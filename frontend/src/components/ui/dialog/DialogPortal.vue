<script setup lang="ts">
import type { HTMLAttributes } from 'vue'
import { DialogPortal, type DialogPortalProps } from 'radix-vue'

const props = defineProps<DialogPortalProps & { class?: HTMLAttributes['class'] }>()
</script>

<template>
  <DialogPortal v-bind="props">
    <slot />
  </DialogPortal>
</template>