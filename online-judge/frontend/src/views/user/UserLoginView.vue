<template>
  <div class="login-page">
    <div class="login-bg">
      <div class="bg-orb bg-orb-1"></div>
      <div class="bg-orb bg-orb-2"></div>
      <div class="bg-orb bg-orb-3"></div>
    </div>
    <div class="login-container">
      <div class="login-brand">
        <div class="brand-icon">
          <icon-code />
        </div>
        <h1>OJ 在线判题</h1>
        <p>在线编程、实时判题、多语言支持的算法竞赛平台</p>
        <div class="brand-features">
          <div class="feature-dot"><icon-check-circle /> 多语言代码提交</div>
          <div class="feature-dot"><icon-check-circle /> 实时判题反馈</div>
          <div class="feature-dot"><icon-check-circle /> 题库难度分级</div>
        </div>
      </div>
      <div class="login-card">
        <div class="card-header">
          <h2>欢迎回来</h2>
          <span>登录你的账号继续刷题</span>
        </div>
        <a-form
          :model="form"
          layout="vertical"
          @submit="handleSubmit"
        >
          <a-form-item field="userAccount" hide-label>
            <a-input v-model="form.userAccount" placeholder="请输入账号" size="large">
              <template #prefix><icon-user /></template>
            </a-input>
          </a-form-item>
          <a-form-item field="userPassword" hide-label>
            <a-input-password
              v-model="form.userPassword"
              placeholder="请输入密码"
              size="large"
            >
              <template #prefix><icon-lock /></template>
            </a-input-password>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" html-type="submit" long size="large" :loading="submitting">
              登录
            </a-button>
          </a-form-item>
        </a-form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from "vue";
import { UserControllerService, UserLoginRequest } from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import { useRouter } from "vue-router";
import { useStore } from "vuex";

const form = reactive({
  userAccount: "",
  userPassword: "",
} as UserLoginRequest);

const router = useRouter();
const store = useStore();
const submitting = ref(false);

const handleSubmit = async () => {
  if (!form.userAccount || !form.userPassword) {
    message.warning("请输入账号和密码");
    return;
  }
  submitting.value = true;
  try {
    const res = await UserControllerService.userLoginUsingPost(form);
    if (res.code === 0) {
      await store.dispatch("user/getLoginUser");
      message.success("登录成功");
      router.push({ path: "/", replace: true });
    } else {
      message.error("登录失败，" + res.message);
    }
  } finally {
    submitting.value = false;
  }
};
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #021019 0%, #0c2d48 40%, #145374 70%, #1a7a8a 100%);
  position: relative;
  overflow: hidden;
}

.login-bg {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.bg-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.3;
}

.bg-orb-1 {
  width: 500px;
  height: 500px;
  background: #0ea5e9;
  top: -120px;
  right: -100px;
  animation: float1 8s ease-in-out infinite;
}

.bg-orb-2 {
  width: 400px;
  height: 400px;
  background: #06b6d4;
  bottom: -80px;
  left: -80px;
  animation: float2 10s ease-in-out infinite;
}

.bg-orb-3 {
  width: 300px;
  height: 300px;
  background: #2dd4bf;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation: float3 12s ease-in-out infinite;
}

@keyframes float1 {
  0%, 100% { transform: translate(0, 0); }
  50% { transform: translate(-30px, 20px); }
}

@keyframes float2 {
  0%, 100% { transform: translate(0, 0); }
  50% { transform: translate(20px, -30px); }
}

@keyframes float3 {
  0%, 100% { transform: translate(-50%, -50%) scale(1); }
  50% { transform: translate(-50%, -50%) scale(1.15); }
}

.login-container {
  display: flex;
  align-items: center;
  gap: 80px;
  position: relative;
  z-index: 1;
  padding: 40px;
}

.login-brand {
  color: #fff;
  max-width: 400px;
}

.brand-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  background: linear-gradient(135deg, #0ea5e9, #06b6d4);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  margin-bottom: 24px;
  box-shadow: 0 8px 32px rgba(14, 165, 233, 0.4);
}

.login-brand h1 {
  font-size: 40px;
  font-weight: 800;
  margin: 0 0 12px;
  letter-spacing: -0.5px;
  background: linear-gradient(135deg, #fff 0%, #a5f3fc 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.login-brand p {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.7);
  margin: 0 0 32px;
  line-height: 1.6;
}

.brand-features {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.feature-dot {
  display: flex;
  align-items: center;
  gap: 8px;
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
}

.feature-dot :deep(.arco-icon) {
  color: #34d399;
  font-size: 16px;
}

.login-card {
  width: 400px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 40px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.card-header {
  margin-bottom: 32px;
}

.card-header h2 {
  font-size: 26px;
  font-weight: 700;
  color: #0c2d48;
  margin: 0 0 8px;
}

.card-header span {
  color: #86909c;
  font-size: 14px;
}

.login-card :deep(.arco-form-item) {
  margin-bottom: 20px;
}

.login-card :deep(.arco-input-wrapper),
.login-card :deep(.arco-input-password) {
  border-radius: 10px;
}

.login-card :deep(.arco-btn-primary) {
  border-radius: 10px;
  height: 44px;
  font-size: 15px;
  font-weight: 600;
  background: linear-gradient(135deg, #0ea5e9, #06b6d4);
  border: none;
  box-shadow: 0 4px 16px rgba(14, 165, 233, 0.3);
}

.login-card :deep(.arco-btn-primary:hover) {
  background: linear-gradient(135deg, #0284c7, #0891b2);
}

@media (max-width: 900px) {
  .login-container {
    flex-direction: column;
    gap: 32px;
    padding: 24px;
  }
  .login-brand {
    text-align: center;
    max-width: 100%;
  }
  .brand-icon {
    margin: 0 auto 16px;
  }
  .brand-features {
    align-items: center;
  }
  .login-card {
    width: 100%;
    max-width: 400px;
  }
}
</style>
