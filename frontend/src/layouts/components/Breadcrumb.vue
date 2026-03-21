<template>
  <nav class="flex items-center text-sm">
    <router-link
      to="/dashboard"
      class="flex items-center text-muted-foreground hover:text-foreground transition-colors"
    >
      <Home class="h-4 w-4" />
    </router-link>

    <template v-if="currentRoute.meta?.title && currentRoute.path !== '/dashboard'">
      <ChevronRight class="h-4 w-4 mx-1 text-muted-foreground/50" />
      <span class="text-foreground font-medium">
        {{ currentRoute.meta.title }}
      </span>
    </template>
  </nav>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { Home, ChevronRight } from 'lucide-vue-next'

const route = useRoute()

const currentRoute = computed(() => route)
</script>