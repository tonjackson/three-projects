<template>
  <div id="userLayout">
    <a-layout style="height: 100vh">
      <a-layout-header class="header">
        <a-space>
          <svg viewBox="0 0 32 32" fill="none" xmlns="http://www.w3.org/2000/svg" class="brand-logo">
            <rect width="32" height="32" rx="8" fill="url(#yudada-user-grad)" />
            <path d="M10 16L14 12L18 16L22 10" stroke="white" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M10 20L14 16L18 20L22 14" stroke="rgba(255,255,255,0.5)" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"/>
            <circle cx="22" cy="10" r="2" fill="white"/>
            <defs>
              <linearGradient id="yudada-user-grad" x1="0" y1="0" x2="32" y2="32" gradientUnits="userSpaceOnUse">
                <stop stop-color="#6366f1"/>
                <stop offset="1" stop-color="#8b5cf6"/>
              </linearGradient>
            </defs>
          </svg>
          <div class="brand-text">
            <span class="brand-name">AI Answer</span>
            <span class="brand-sub">智能答题平台</span>
          </div>
        </a-space>
      </a-layout-header>
      <a-layout-content class="content">
        <router-view />
      </a-layout-content>
      <a-layout-footer class="footer">
        AI Answer - 智能答题平台
      </a-layout-footer>
    </a-layout>
  </div>
</template>

<script setup lang="ts"></script>

<style scoped>
#userLayout {
  text-align: center;
  background: linear-gradient(135deg, #1e1b4b 0%, #312e81 40%, #6366f1 100%);
  min-height: 100vh;
}

.brand-logo {
  width: 40px;
  height: 40px;
  filter: drop-shadow(0 2px 8px rgba(99, 102, 241, 0.4));
}

.brand-text {
  display: flex;
  flex-direction: column;
  text-align: left;
  line-height: 1.2;
}

.brand-name {
  font-family: 'Segoe UI', system-ui, sans-serif;
  font-weight: 700;
  font-size: 18px;
  color: #fff;
  letter-spacing: -0.3px;
}

.brand-sub {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.7);
  font-weight: 500;
  letter-spacing: 0.5px;
}

#userLayout .header {
  margin-top: 16px;
  background: transparent;
}

#userLayout .content {
  margin-bottom: 16px;
  padding: 20px;
}

.footer {
  padding: 16px;
  text-align: center;
  background: rgba(0, 0, 0, 0.1);
  color: rgba(255, 255, 255, 0.6);
  font-size: 13px;
}
</style>


