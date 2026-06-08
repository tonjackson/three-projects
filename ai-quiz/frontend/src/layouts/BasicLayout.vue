<template>
  <div id="basicLayout">
    <a-layout style="min-height: 100vh">
      <a-layout-header class="header">
        <GlobalHeader />
      </a-layout-header>
      <a-layout-content class="content">
        <router-view />
      </a-layout-content>
      <a-layout-footer class="footer">
        <a href="#" target="_blank">智能答题平台</a>
      </a-layout-footer>
    </a-layout>
  </div>
</template>

<script setup lang="ts">
import GlobalHeader from "@/components/GlobalHeader.vue";
</script>

<style scoped>
#basicLayout {
}

#basicLayout .header {
  position: sticky;
  top: 0;
  z-index: 20;
  background: #fff;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

#basicLayout .content {
  width: 100%;
  box-sizing: border-box;
  flex: 1;
  margin: 0;
  padding: 0;
  background: #f8f9fc;
}

.footer {
  padding: 20px;
  text-align: center;
  background: #f8f9fc;
  color: #94a3b8;
  font-size: 13px;
}

.footer a {
  color: #94a3b8;
  text-decoration: none;
  transition: color 0.2s;
}

.footer a:hover {
  color: #6366f1;
}
</style>
