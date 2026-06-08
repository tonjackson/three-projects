<template>
  <div id="userRegisterView">
    <div class="login-card">
      <h2 style="margin-bottom: 16px">用户注册</h2>
      <a-form
        style="max-width: 400px"
        label-align="left"
        auto-label-width
        :model="form"
        @submit="handleSubmit"
      >
        <a-form-item field="userAccount" label="账号">
          <a-input v-model="form.userAccount" placeholder="请输入账号" />
        </a-form-item>
        <a-form-item field="userPassword" tooltip="密码不少于 8 位" label="密码">
          <a-input-password
            v-model="form.userPassword"
            placeholder="请输入密码"
          />
        </a-form-item>
        <a-form-item field="checkPassword" tooltip="确认密码不少于 8 位" label="确认密码">
          <a-input-password
            v-model="form.checkPassword"
            placeholder="请输入确认密码"
          />
        </a-form-item>
        <a-form-item>
          <a-space>
            <a-button type="primary" html-type="submit" style="width: 120px">
              注册
            </a-button>
            <a-link href="/user/login">已有账号？去登录</a-link>
          </a-space>
        </a-form-item>
      </a-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive } from "vue";
import { UserControllerService, UserRegisterRequest } from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import { useRouter } from "vue-router";

const form = reactive({
  userAccount: "",
  userPassword: "",
  checkPassword: "",
} as UserRegisterRequest);

const router = useRouter();

const handleSubmit = async () => {
  if (form.checkPassword !== form.userPassword) {
    message.error("两次输入的密码不一致");
    return;
  }
  const res = await UserControllerService.userRegisterUsingPost(form);
  if (res.code === 0) {
    message.success("注册成功");
    router.push({
      path: "/user/login",
      replace: true,
    });
  } else {
    message.error("注册失败，" + res.message);
  }
};
</script>

<style scoped>
#userRegisterView {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 70vh;
}

.login-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(12px);
  border-radius: 16px;
  padding: 40px 48px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
  width: fit-content;
}

.login-card h2 {
  color: #0f172a;
  font-size: 22px;
  font-weight: 700;
}
</style>
