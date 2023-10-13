import request from '@/utils/request'

// 查询【请填写功能名称】列表
export function getHaoKaLotPage(query) {
  return request({
    url: '/xianyum-analysis/v1/haoKaLot/getPage',
    method: 'get',
    params: query
  })
}
