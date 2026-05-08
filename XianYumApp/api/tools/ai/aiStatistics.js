import request from "@/utils/request";

export function getAiModels() {
    return request({
        url: '/xym-extension/v1/openai/models',
        method: 'get'
    })
}


export function getAiTokenUsage() {
    return request({
        url: '/xym-extension/v1/openai/token/usage',
        method: 'get'
    })
}


export function getAiTokenLog() {
    return request({
        url: '/xym-extension/v1/openai/token/log',
        method: 'get'
    })
}
