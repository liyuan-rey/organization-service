import { requestClient } from '#/api/request';

// 分组数据类型
export interface Group {
  id: string;
  name: string;
  description: string;
}

// 部门数据类型
export interface Department {
  id: string;
  name: string;
  englishName: string;
  shortName: string;
  orgCode: string;
  phone: string;
  fax: string;
  email: string;
  address: string;
  postalCode: string;
  createTime: string;
  updateTime: string;
}

// 分页响应类型
export interface PageResponse<T> {
  items: T[];
  total: number;
}

/**
 * 获取分组列表
 */
export async function getGroupListApi() {
  return requestClient.get<Group[]>('/organization/group/list');
}

/**
 * 获取部门列表
 */
export async function getDepartmentListApi(params?: {
  name?: string;
  page?: number;
  pageSize?: number;
}) {
  return requestClient.get<PageResponse<Department>>(
    '/organization/department/list',
    { params },
  );
}
