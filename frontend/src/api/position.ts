import { get, post, put, del } from './request'
import type { Position, PositionCreateReq, PositionUpdateReq } from '@/types'

const BASE_URL = '/positions'

/**
 * 获取所有岗位
 */
export function getAllPositions() {
  return get<Position[]>(BASE_URL)
}

/**
 * 根据 ID 获取岗位
 */
export function getPositionById(id: string) {
  return get<Position>(`${BASE_URL}/${id}`)
}

/**
 * 创建岗位
 */
export function createPosition(data: PositionCreateReq) {
  return post<Position>(BASE_URL, data)
}

/**
 * 更新岗位
 */
export function updatePosition(id: string, data: PositionUpdateReq) {
  return put<Position>(`${BASE_URL}/${id}`, data)
}

/**
 * 删除岗位
 */
export function deletePosition(id: string) {
  return del<unknown>(`${BASE_URL}/${id}`)
}
