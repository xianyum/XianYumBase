import request from '@/utils/request'

// 获取下载url
export function presignedFileUrl(fileId) {
  return request({
    url: '/xym-system/v1/file/presignedUrl',
    method: 'get',
    params: {
      "fileId": fileId
    }
  })
}

// 获取上传预签名凭证（前端直传存储平台）
export function uploadPresignedUrl(filename) {
  return request({
    url: '/xym-system/v1/file/uploadPresignedUrl',
    method: 'get',
    params: {
      "filename": filename
    }
  })
}
