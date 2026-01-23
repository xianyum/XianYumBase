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
