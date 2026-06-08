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
                <rect width="32" height="32" rx="8" fill="url(#yuoj-brand-grad)" />
                <path d="M11 11L8 16L11 21" stroke="white" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M21 11L24 16L21 21" stroke="white" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M18 10L14 22" stroke="rgba(255,255,255,0.6)" stroke-width="2" stroke-linecap="round"/>
                <defs>
                  <linearGradient id="yuoj-brand-grad" x1="0" y1="0" x2="32" y2="32" gradientUnits="userSpaceOnUse">
                    <stop stop-color="#0d9488"/>
                    <stop offset="1" stop-color="#14b8a6"/>
                  </linearGradient>
                </defs>
              </svg>
            </div>
            <div class="brand-text">
              <span class="brand-name">YuOJ</span>
              <span class="brand-sub">在线判题系统</span>
            </div>
          </div>
        </a-menu-item>
        <a-menu-item v-for="item in visibleRoutes" :key="item.path">
          {{ item.name }}
        </a-menu-item>
      </a-menu>
    </a-col>
    <a-col flex="160px" style="text-align: right; padding-right: 20px;">
      <div class="user-info">
        <div class="user-avatar">{{ (store.state.user?.loginUser?.userName ?? "未")[0] }}</div>
        <span class="user-name">{{ store.state.user?.loginUser?.userName ?? "未登录" }}</span>
      </div>
    </a-col>
  </a-row>
</template>

<script setup lang="ts">
import { routes } from "../router/routes";
import { useRouter } from "vue-router";
import { computed, ref } from "vue";
import { useStore } from "vuex";
import checkAccess from "@/access/checkAccess";

const router = useRouter();
const store = useStore();

const visibleRoutes = computed(() => {
  return routes.filter((item, index) => {
    if (item.meta?.hideInMenu) {
      return false;
    }
    if (
      !checkAccess(store.state.user.loginUser, item?.meta?.access as string)
    ) {
      return false;
    }
    return true;
  });
});

const selectedKeys = ref(["/"]);

router.afterEach((to, from, failure) => {
  selectedKeys.value = [to.path];
});

const doMenuClick = (key: string) => {
  router.push({
    path: key,
  });
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
  filter: drop-shadow(0 2px 8px rgba(13, 148, 136, 0.3));
}

.brand-text {
  display: flex;
  flex-direction: column;
  line-height: 1.2;
}

.brand-name {
  font-family: 'Consolas', 'SF Mono', 'Fira Code', monospace;
  font-weight: 700;
  font-size: 16px;
  color: #0f172a;
  letter-spacing: -0.5px;
}

.brand-sub {
  font-size: 11px;
  color: #0d9488;
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
  background: linear-gradient(135deg, #0d9488, #14b8a6);
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
</style>
