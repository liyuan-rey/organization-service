import { get, post, put, del } from './request'
import type { Department, DepartmentCreateReq, DepartmentUpdateReq } from '@/types'

const BASE_URL = '/departments'

/**
 * 获取所有部门
 */
export function getAllDepartments() {
  return get<Department[]>(BASE_URL)
}

/**
 * 根据 ID 获取部门
 */
export function getDepartmentById(id: string) {
  return get<Department>(`${BASE_URL}/${id}`)
}

/**
 * 创建部门
 */
export function createDepartment(data: DepartmentCreateReq) {
  return post<Department>(BASE_URL, data)
}

/**
 * 更新部门
 */
export function updateDepartment(id: string, data: DepartmentUpdateReq) {
  return put<Department>(`${BASE_URL}/${id}`, data)
}

/**
 * 删除部门
 */
export function deleteDepartment(id: string) {
  return del<unknown>(`${BASE_URL}/${id}`)
}
