import axios from "axios";
import { Message } from "@arco-design/web-vue";

axios.defaults.withCredentials = true;

// 请求拦截器
axios.interceptors.request.use(
  function (config) {
    return config;
  },
  function (error) {
    return Promise.reject(error);
  }
);

// 响应拦截器 - 统一错误码处理
axios.interceptors.response.use(
  function (response) {
    const { data } = response;

    // 未登录
    if (data.code === 40100) {
      if (
        !response.request.responseURL.includes("user/get/login") &&
        !window.location.pathname.includes("/user/login")
      ) {
        Message.warning("请先登录");
        window.location.href = `/user/login?redirect=${window.location.href}`;
      }
    }

    // 无权限
    if (data.code === 40101) {
      Message.error("无权限访问");
    }

    // 禁止操作
    if (data.code === 40300) {
      Message.error(data.message || "禁止操作");
    }

    return response;
  },
  function (error) {
    if (error.code === "ERR_NETWORK") {
      Message.error("网络异常，请检查后端是否启动");
    } else if (error.response) {
      const status = error.response.status;
      if (status === 401) {
        Message.warning("登录已过期，请重新登录");
        window.location.href = `/user/login?redirect=${window.location.href}`;
      } else if (status === 403) {
        Message.error("无权限访问");
      } else if (status >= 500) {
        Message.error("服务器异常，请稍后重试");
      }
    }
    return Promise.reject(error);
  }
);
