// API 响应结构
export interface ApiResult<T> {
  code: number
  message: string
  data: T
}

// 部门实体
export interface Department {
  id: string
  name: string
  code: string
  description: string
  parentId: string | null
  sortOrder: number
  status: number
  createTime: string
  updateTime: string
}

// 部门创建请求
export interface DepartmentCreateReq {
  name: string
  code: string
  description?: string
  parentId?: string | null
  sortOrder?: number
  status?: number
}

// 部门更新请求
export interface DepartmentUpdateReq {
  name: string
  code: string
  description?: string
  parentId?: string | null
  sortOrder?: number
  status?: number
}

// 人员实体
export interface Personnel {
  id: string
  name: string
  code: string
  email: string
  phone: string
  departmentId: string
  position: string
  status: number
  createTime: string
  updateTime: string
}

// 人员创建请求
export interface PersonnelCreateReq {
  name: string
  code: string
  email?: string
  phone?: string
  departmentId: string
  position?: string
  status?: number
}

// 人员更新请求
export interface PersonnelUpdateReq {
  name: string
  code: string
  email?: string
  phone?: string
  departmentId: string
  position?: string
  status?: number
}

// 分页参数
export interface PageParams {
  page: number
  size: number
}

// 分页响应
export interface PageResult<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}
