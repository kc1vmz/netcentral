<script setup lang="ts">
defineProps<{
  title: string,
  description: string
}>()
</script>

<template>
  <!-- main page -->
  <div>
    <div class="mobilepageheader">{{ title }}</div>
    <i>{{ description }}</i>
    <div class="line"><hr/></div>
  </div>
</template>

<style scoped>
.line {
  margin : 0px;
}
</style>
