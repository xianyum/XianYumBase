import request from '@/utils/request'

export function saveOtpNetworkAuth(data) {
    return request({
        url: '/xym-extension/v1/otp-network-auth/save',
        method: 'post',
        data: data
    })
}


export function getOtpNetworkAuthList() {
    return request({
        url: '/xym-extension/v1/otp-network-auth/getList',
        method: 'get'
    })
}

export function deleteOtpNetworkAuthList(id) {
    return request({
        url: '/xym-extension/v1/otp-network-auth/delete',
        method: 'delete',
        params: {
            id: id
        }
    })
}

export function updateOtpNetworkAuth(data) {
    return request({
        url: '/xym-extension/v1/otp-network-auth/update',
        method: 'put',
        data: data
    })
}