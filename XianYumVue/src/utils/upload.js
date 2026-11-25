import { uploadPresignedUrl } from '@/api/system/file'

/**
 * 前端直传文件到存储平台
 * 流程：后端生成上传凭证（同时创建文件记录并返回 fileId）-> 前端直传存储平台
 *
 * @param {File} file 待上传文件
 * @param {Object} [options] 可选配置
 * @param {Function} [options.onProgress] 上传进度回调，参数为百分比（0-100）
 * @returns {Promise<Object>} 后端凭证信息，包含 fileId、url、token、key、filename、originalFilename
 */
export function uploadFileDirectly(file, options = {}) {
  if (!file) {
    return Promise.reject(new Error('上传文件不能为空'))
  }
  const { onProgress } = options
  return uploadPresignedUrl(file.name).then(response => {
    const presign = response.data
    if (!presign || !presign.url || !presign.token || !presign.key) {
      return Promise.reject(new Error('获取上传凭证失败'))
    }
    const uploadUrl = /^https?:\/\//i.test(presign.url) ? presign.url : 'https://' + presign.url
    const formData = new FormData()
    formData.append('token', presign.token)
    formData.append('key', presign.key)
    formData.append('file', file)
    return sendToStorage(uploadUrl, formData, onProgress).then(() => presign)
  })
}

/**
 * 通过 XMLHttpRequest 直传存储平台
 * 不使用业务 axios 实例，避免携带 Authorization 头导致跨域或鉴权失败
 *
 * @param {String} url 存储平台上传地址
 * @param {FormData} formData 表单数据（token、key、file）
 * @param {Function} [onProgress] 上传进度回调
 * @returns {Promise<Object>} 存储平台返回结果
 */
function sendToStorage(url, formData, onProgress) {
  return new Promise((resolve, reject) => {
    const xhr = new XMLHttpRequest()
    xhr.open('POST', url, true)
    xhr.upload.onprogress = e => {
      if (e.lengthComputable && onProgress) {
        onProgress(Math.round((e.loaded / e.total) * 100))
      }
    }
    xhr.onload = () => {
      let body = {}
      try {
        body = JSON.parse(xhr.responseText)
      } catch (e) {
        body = {}
      }
      if (xhr.status >= 200 && xhr.status < 300) {
        resolve(body)
      } else {
        reject(new Error(body.error || ('上传失败(' + xhr.status + ')')))
      }
    }
    xhr.onerror = () => reject(new Error('网络异常，请检查存储空间跨域配置'))
    xhr.send(formData)
  })
}
