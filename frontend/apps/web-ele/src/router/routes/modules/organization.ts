import type { RouteRecordRaw } from 'vue-router';

import { $t } from '#/locales';

const routes: RouteRecordRaw[] = [
  {
    meta: {
      icon: 'lucide:building-2',
      order: 100,
      title: $t('page.organization.title'),
    },
    name: 'Organization',
    path: '/organization',
    children: [
      {
        name: 'AddressBook',
        path: '/organization/address-book',
        component: () => import('#/views/organization/address-book/index.vue'),
        meta: {
          title: $t('page.organization.addressBook.title'),
          icon: 'lucide:book-user',
        },
      },
      {
        name: 'StructureMaintenance',
        path: '/organization/structure',
        component: () => import('#/views/organization/structure/index.vue'),
        meta: {
          title: $t('page.organization.structure.title'),
          icon: 'lucide:git-branch',
        },
      },
    ],
  },
];

export default routes;
