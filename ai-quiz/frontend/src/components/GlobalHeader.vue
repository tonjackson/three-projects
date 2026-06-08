<template>
  <a-row id="globalHeader" align="center" :wrap="false">
    <a-col flex="auto">
      <a-menu
        mode="horizontal"
        :selected-keys="selectedKeys"
        @menu-item-click="doMenuClick"
      >
        <a-menu-item
          key="0"
          :style="{ padding: 0, marginRight: '32px' }"
          disabled
        >
          <div class="brand">
            <div class="brand-mark">
              <svg viewBox="0 0 32 32" fill="none" xmlns="http://www.w3.org/2000/svg" class="brand-svg">
                <rect width="32" height="32" rx="8" fill="url(#yudada-brand-grad)" />
                <path d="M10 16L14 12L18 16L22 10" stroke="white" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M10 20L14 16L18 20L22 14" stroke="rgba(255,255,255,0.5)" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"/>
                <circle cx="22" cy="10" r="2" fill="white"/>
                <defs>
                  <linearGradient id="yudada-brand-grad" x1="0" y1="0" x2="32" y2="32" gradientUnits="userSpaceOnUse">
                    <stop stop-color="#6366f1"/>
                    <stop offset="1" stop-color="#8b5cf6"/>
                  </linearGradient>
                </defs>
              </svg>
            </div>
            <div class="brand-text">
              <span class="brand-name">AI Answer</span>
              <span class="brand-sub">智能答题平台</span>
            </div>
          </div>
        </a-menu-item>
        <a-menu-item v-for="item in visibleRoutes" :key="item.path">
          {{ item.name }}
        </a-menu-item>
      </a-menu>
    </a-col>
    <a-col flex="160px" style="text-align: right; padding-right: 20px;">
      <div v-if="loginUserStore.loginUser.id" class="user-info">
        <div class="user-avatar">{{ (loginUserStore.loginUser.userName ?? "无")[0] }}</div>
        <span class="user-name">{{ loginUserStore.loginUser.userName ?? "无名" }}</span>
      </div>
      <div v-else>
        <a-button type="primary" href="/user/login" class="login-btn">登录</a-button>
      </div>
    </a-col>
  </a-row>
</template>

<script setup lang="ts">
import { routes } from "@/router/routes";
import { useRouter } from "vue-router";
import { computed, ref } from "vue";
import { useLoginUserStore } from "@/store/userStore";
import checkAccess from "@/access/checkAccess";

const loginUserStore = useLoginUserStore();

const router = useRouter();
const selectedKeys = ref(["/"]);
router.afterEach((to, from, failure) => {
  selectedKeys.value = [to.path];
});

const visibleRoutes = computed(() => {
  return routes.filter((item) => {
    if (item.meta?.hideInMenu) {
      return false;
    }
    if (!checkAccess(loginUserStore.loginUser, item.meta?.access as string)) {
      return false;
    }
    return true;
  });
});

const doMenuClick = (key: string) => {
  router.push({ path: key });
};
</script>

<style scoped>
#globalHeader {
  background: #fff;
  border-bottom: 1px solid rgba(0, 0, 0, 0.04);
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
}

.brand-svg {
  width: 34px;
  height: 34px;
  filter: drop-shadow(0 2px 8px rgba(99, 102, 241, 0.3));
}

.brand-text {
  display: flex;
  flex-direction: column;
  line-height: 1.2;
}

.brand-name {
  font-family: 'Segoe UI', system-ui, sans-serif;
  font-weight: 700;
  font-size: 15px;
  color: #1e1b4b;
  letter-spacing: -0.3px;
}

.brand-sub {
  font-size: 11px;
  color: #8b5cf6;
  font-weight: 500;
  letter-spacing: 0.5px;
}

.user-info {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.user-avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  color: #fff;
  font-size: 12px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-name {
  font-size: 14px;
  color: #4e5969;
  font-weight: 500;
}

.login-btn {
  border-radius: 8px;
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  border: none;
  font-weight: 500;
}
</style>
