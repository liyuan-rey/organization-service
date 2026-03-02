// API 响应结构
export interface ApiResult<T> {
  status: number
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
  personCount?: number
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

// 岗位实体
export interface Position {
  id: string
  name: string
  code: string
  description: string
  jobLevel: string
  jobCategory: string
  minSalary: number | null
  maxSalary: number | null
  status: number
  createTime: string
  updateTime: string
}

// 岗位创建请求
export interface PositionCreateReq {
  name: string
  code: string
  description?: string
  jobLevel?: string
  jobCategory?: string
  minSalary?: number | null
  maxSalary?: number | null
  status?: number
}

// 岗位更新请求
export interface PositionUpdateReq {
  name?: string
  code?: string
  description?: string
  jobLevel?: string
  jobCategory?: string
  minSalary?: number | null
  maxSalary?: number | null
  status?: number
}

// 部门岗位关联实体
export interface DepartmentPosition {
  id: string
  departmentId: string
  departmentName: string
  positionId: string
  positionName: string
  isPrimary: boolean
  sortOrder: number
  createTime: string
  updateTime: string
}

// 部门岗位关联请求
export interface DepartmentPositionReq {
  departmentId: string
  positionId: string
  isPrimary?: boolean
  sortOrder?: number
}

// 人员岗位关联实体
export interface PersonnelPosition {
  id: string
  personnelId: string
  personnelName: string
  positionId: string
  positionName: string
  departmentId: string | null
  departmentName: string | null
  isPrimary: boolean
  startDate: string | null
  endDate: string | null
  status: number
  createTime: string
  updateTime: string
}

// 人员岗位关联请求
export interface PersonnelPositionReq {
  personnelId: string
  positionId: string
  departmentId?: string | null
  isPrimary?: boolean
  startDate?: string | null
  endDate?: string | null
  status?: number
}
