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


export function getAiCurrentModel() {
    return request({
        url: '/xym-extension/v1/openai/getCurrentModel',
        method: 'get'
    })
}


export function setAiCurrentModel(modelName) {
    return request({
        url: '/xym-extension/v1/openai/setCurrentModel',
        method: 'put',
        params: {
            modelName: modelName
        }
    })
}