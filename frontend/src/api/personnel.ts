import { get, post, put, del } from './request'
import type { Personnel, PersonnelCreateReq, PersonnelUpdateReq } from '@/types'

const BASE_URL = '/personnel'

/**
 * 获取所有人员
 */
export function getAllPersonnel() {
  return get<Personnel[]>(BASE_URL)
}

/**
 * 根据 ID 获取人员
 */
export function getPersonnelById(id: string) {
  return get<Personnel>(`${BASE_URL}/${id}`)
}

/**
 * 创建人员
 */
export function createPersonnel(data: PersonnelCreateReq) {
  return post<Personnel>(BASE_URL, data)
}

/**
 * 更新人员
 */
export function updatePersonnel(id: string, data: PersonnelUpdateReq) {
  return put<Personnel>(`${BASE_URL}/${id}`, data)
}

/**
 * 删除人员
 */
export function deletePersonnel(id: string) {
  return del<unknown>(`${BASE_URL}/${id}`)
}
