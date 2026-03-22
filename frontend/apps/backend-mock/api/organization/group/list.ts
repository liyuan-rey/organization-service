import { defineEventHandler } from 'h3';

import { useResponseSuccess } from '~/utils/response';

// 模拟分组数据
const mockGroups = [
  {
    id: '018df1b0-1234-7000-8000-000000000001',
    name: '总公司',
    description: '集团总公司及其直属部门',
  },
  {
    id: '018df1b0-1234-7000-8000-000000000002',
    name: '华东分公司',
    description: '华东地区各分支机构',
  },
  {
    id: '018df1b0-1234-7000-8000-000000000003',
    name: '华南分公司',
    description: '华南地区各分支机构',
  },
  {
    id: '018df1b0-1234-7000-8000-000000000004',
    name: '华北分公司',
    description: '华北地区各分支机构',
  },
  {
    id: '018df1b0-1234-7000-8000-000000000005',
    name: '西南分公司',
    description: '西南地区各分支机构',
  },
];

export default defineEventHandler(async () => {
  return useResponseSuccess(mockGroups);
});