<script setup lang="ts">
import type { HTMLAttributes } from 'vue'
import { AlertDialogTrigger, type AlertDialogTriggerProps } from 'radix-vue'

const props = defineProps<AlertDialogTriggerProps & { class?: HTMLAttributes['class'] }>()
</script>

<template>
  <AlertDialogTrigger v-bind="props">
    <slot />
  </AlertDialogTrigger>
</template>