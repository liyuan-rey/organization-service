import { get, post, put, del } from './request'
import type { PersonnelPosition, PersonnelPositionReq } from '@/types'

const BASE_URL = '/personnel-positions'

/**
 * 获取所有人员岗位关联
 */
export function getAllPersonnelPositions() {
  return get<PersonnelPosition[]>(BASE_URL)
}

/**
 * 根据人员 ID 获取岗位列表
 */
export function getPositionsByPersonnelId(personnelId: string) {
  return get<PersonnelPosition[]>(`${BASE_URL}/personnel/${personnelId}`)
}

/**
 * 根据岗位 ID 获取人员列表
 */
export function getPersonnelByPositionId(positionId: string) {
  return get<PersonnelPosition[]>(`${BASE_URL}/position/${positionId}`)
}

/**
 * 根据部门 ID 获取人员岗位关联
 */
export function getPersonnelPositionsByDepartmentId(departmentId: string) {
  return get<PersonnelPosition[]>(`${BASE_URL}/department/${departmentId}`)
}

/**
 * 创建人员岗位关联
 */
export function createPersonnelPosition(data: PersonnelPositionReq) {
  return post<PersonnelPosition>(BASE_URL, data)
}

/**
 * 更新人员岗位关联
 */
export function updatePersonnelPosition(id: string, data: PersonnelPositionReq) {
  return put<PersonnelPosition>(`${BASE_URL}/${id}`, data)
}

/**
 * 删除人员岗位关联
 */
export function deletePersonnelPosition(id: string) {
  return del<unknown>(`${BASE_URL}/${id}`)
}
