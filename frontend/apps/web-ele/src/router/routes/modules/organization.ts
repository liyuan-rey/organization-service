import type { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    meta: {
      icon: 'lucide:building-2',
      order: 100,
      title: '组织机构',
    },
    name: 'Organization',
    path: '/organization',
    children: [
      {
        name: 'AddressBook',
        path: '/organization/address-book',
        component: () => import('#/views/organization/address-book/index.vue'),
        meta: {
          title: '通讯录',
          icon: 'lucide:book-user',
        },
      },
      {
        name: 'StructureMaintenance',
        path: '/organization/structure',
        component: () => import('#/views/organization/structure/index.vue'),
        meta: {
          title: '结构维护',
          icon: 'lucide:git-branch',
        },
      },
    ],
  },
];

export default routes;