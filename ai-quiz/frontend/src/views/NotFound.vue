<template>
  <div class="not-found">
    <div class="content">
      <h1>404</h1>
      <p>抱歉，您访问的页面不存在</p>
      <a-button type="primary" @click="$router.push('/')">返回首页</a-button>
    </div>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
.not-found {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
}
.content {
  text-align: center;
}
h1 {
  font-size: 96px;
  font-weight: 700;
  color: #6366f1;
  margin: 0;
}
p {
  font-size: 18px;
  color: #666;
  margin: 16px 0 32px;
}
</style>
