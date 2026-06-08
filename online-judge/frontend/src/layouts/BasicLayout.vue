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
        <a href="#" target="_blank">在线判题系统</a>
      </a-layout-footer>
    </a-layout>
  </div>
</template>

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
  background: #f8f9fb;
  margin: 0;
  padding: 0;
}

#basicLayout .footer {
  background: #f8f9fb;
  padding: 20px;
  text-align: center;
  color: #94a3b8;
  font-size: 13px;
}

#basicLayout .footer a {
  color: #94a3b8;
  text-decoration: none;
  transition: color 0.2s;
}

#basicLayout .footer a:hover {
  color: #0d9488;
}
</style>
<script>
import GlobalHeader from "@/components/GlobalHeader";

export default {
  components: { GlobalHeader },
};
</script>
