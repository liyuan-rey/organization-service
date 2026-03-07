<script setup lang="ts">
import type { HTMLAttributes } from 'vue'
import { DialogTrigger, type DialogTriggerProps } from 'radix-vue'

const props = defineProps<DialogTriggerProps & { class?: HTMLAttributes['class'] }>()
</script>

<template>
  <DialogTrigger v-bind="props">
    <slot />
  </DialogTrigger>
</template>