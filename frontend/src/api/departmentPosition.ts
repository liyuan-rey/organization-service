import { get, post, del } from './request'
import type { DepartmentPosition, DepartmentPositionReq } from '@/types'

const BASE_URL = '/department-positions'

/**
 * 获取所有部门岗位关联
 */
export function getAllDepartmentPositions() {
  return get<DepartmentPosition[]>(BASE_URL)
}

/**
 * 根据部门 ID 获取岗位列表
 */
export function getPositionsByDepartmentId(departmentId: string) {
  return get<DepartmentPosition[]>(`${BASE_URL}/department/${departmentId}`)
}

/**
 * 根据岗位 ID 获取部门列表
 */
export function getDepartmentsByPositionId(positionId: string) {
  return get<DepartmentPosition[]>(`${BASE_URL}/position/${positionId}`)
}

/**
 * 创建部门岗位关联
 */
export function createDepartmentPosition(data: DepartmentPositionReq) {
  return post<DepartmentPosition>(BASE_URL, data)
}

/**
 * 删除部门岗位关联
 */
export function deleteDepartmentPosition(departmentId: string, positionId: string) {
  return del<unknown>(`${BASE_URL}/${departmentId}/${positionId}`)
}
