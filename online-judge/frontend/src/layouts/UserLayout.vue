<template>
  <div id="userLayout">
    <a-layout style="min-height: 100vh">
      <a-layout-header class="header">
        <a-space>
          <svg viewBox="0 0 32 32" fill="none" xmlns="http://www.w3.org/2000/svg" class="brand-logo">
            <rect width="32" height="32" rx="8" fill="url(#yuoj-user-grad)" />
            <path d="M11 11L8 16L11 21" stroke="white" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M21 11L24 16L21 21" stroke="white" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M18 10L14 22" stroke="rgba(255,255,255,0.6)" stroke-width="2" stroke-linecap="round"/>
            <defs>
              <linearGradient id="yuoj-user-grad" x1="0" y1="0" x2="32" y2="32" gradientUnits="userSpaceOnUse">
                <stop stop-color="#0d9488"/>
                <stop offset="1" stop-color="#14b8a6"/>
              </linearGradient>
            </defs>
          </svg>
          <div class="brand-text">
            <span class="brand-name">YuOJ</span>
            <span class="brand-sub">在线判题系统</span>
          </div>
        </a-space>
      </a-layout-header>
      <a-layout-content class="content">
        <router-view />
      </a-layout-content>
      <a-layout-footer class="footer">
        YuOJ - 在线判题系统
      </a-layout-footer>
    </a-layout>
  </div>
</template>

<style scoped>
#userLayout {
  text-align: center;
  background: linear-gradient(135deg, #042f2e 0%, #0f766e 40%, #14b8a6 100%);
  min-height: 100vh;
}

.brand-logo {
  width: 40px;
  height: 40px;
  filter: drop-shadow(0 2px 8px rgba(13, 148, 136, 0.4));
}

.brand-text {
  display: flex;
  flex-direction: column;
  text-align: left;
  line-height: 1.2;
}

.brand-name {
  font-family: 'Consolas', 'SF Mono', 'Fira Code', monospace;
  font-weight: 700;
  font-size: 18px;
  color: #fff;
  letter-spacing: -0.5px;
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

#userLayout .footer {
  padding: 16px;
  position: sticky;
  bottom: 0;
  left: 0;
  right: 0;
  text-align: center;
  background: rgba(0, 0, 0, 0.1);
  color: rgba(255, 255, 255, 0.6);
  font-size: 13px;
}
</style>
<script></script>
