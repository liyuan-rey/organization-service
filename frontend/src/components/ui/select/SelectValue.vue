<script setup lang="ts">
import type { HTMLAttributes } from 'vue'
import { SelectValue, type SelectValueProps } from 'radix-vue'

const props = defineProps<SelectValueProps & { class?: HTMLAttributes['class'] }>()
</script>

<template>
  <SelectValue v-bind="props">
    <slot />
  </SelectValue>
</template>