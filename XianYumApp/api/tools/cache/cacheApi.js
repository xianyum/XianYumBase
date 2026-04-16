import request from '@/utils/request'


export function deleteCacheByKey(redisKey) {
    return request({
        url: '/xym-extension/v1/cache/deleteByKey',
        method: 'delete',
        params: {
            redisKey: redisKey
        }
    })
}