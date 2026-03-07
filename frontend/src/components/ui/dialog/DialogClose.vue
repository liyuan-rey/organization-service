<script setup lang="ts">
import type { HTMLAttributes } from 'vue'
import { DialogClose, type DialogCloseProps } from 'radix-vue'

const props = defineProps<DialogCloseProps & { class?: HTMLAttributes['class'] }>()
</script>

<template>
  <DialogClose v-bind="props">
    <slot />
  </DialogClose>
</template>