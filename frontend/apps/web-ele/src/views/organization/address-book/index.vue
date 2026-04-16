<script lang="ts" setup>
import { Page } from '@vben/common-ui';
import { $t } from '@vben/locales';
</script>

<template>
  <Page
    :title="$t('page.organization.addressBook.title')"
    :description="$t('page.organization.addressBook.description')"
  >
    <div class="flex h-full items-center justify-center">
      <span class="text-lg text-muted-foreground">
        {{ $t('page.organization.addressBook.title') }}（开发中）
      </span>
    </div>
  </Page>
</template>
