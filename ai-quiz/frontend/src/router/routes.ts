import { RouteRecordRaw } from "vue-router";
import ACCESS_ENUM from "@/access/accessEnum";

export const routes: Array<RouteRecordRaw> = [
  {
    path: "/",
    name: "主页",
    component: () => import("@/views/HomePage.vue"),
  },
  {
    path: "/add/app",
    name: "创建应用",
    component: () => import("@/views/add/AddAppPage.vue"),
  },
  {
    path: "/add/app/:id",
    name: "修改应用",
    props: true,
    component: () => import("@/views/add/AddAppPage.vue"),
    meta: {
      hideInMenu: true,
    },
  },
  {
    path: "/add/question/:appId",
    name: "创建题目",
    component: () => import("@/views/add/AddQuestionPage.vue"),
    props: true,
    meta: {
      hideInMenu: true,
    },
  },
  {
    path: "/add/scoring_result/:appId",
    name: "创建评分",
    component: () => import("@/views/add/AddScoringResultPage.vue"),
    props: true,
    meta: {
      hideInMenu: true,
    },
  },
  {
    path: "/app/detail/:id",
    name: "应用详情页",
    props: true,
    component: () => import("@/views/app/AppDetailPage.vue"),
    meta: {
      hideInMenu: true,
    },
  },
  {
    path: "/answer/do/:appId",
    name: "答题",
    component: () => import("@/views/answer/DoAnswerPage.vue"),
    props: true,
    meta: {
      hideInMenu: true,
      access: ACCESS_ENUM.USER,
    },
  },
  {
    path: "/answer/result/:id",
    name: "答题结果",
    component: () => import("@/views/answer/AnswerResultPage.vue"),
    props: true,
    meta: {
      hideInMenu: true,
      access: ACCESS_ENUM.USER,
    },
  },
  {
    path: "/answer/my",
    name: "我的答题",
    component: () => import("@/views/answer/MyAnswerPage.vue"),
    meta: {
      access: ACCESS_ENUM.USER,
    },
  },
  {
    path: "/admin/user",
    name: "用户管理",
    component: () => import("@/views/admin/AdminUserPage.vue"),
    meta: {
      access: ACCESS_ENUM.ADMIN,
    },
  },
  {
    path: "/admin/app",
    name: "应用管理",
    component: () => import("@/views/admin/AdminAppPage.vue"),
    meta: {
      access: ACCESS_ENUM.ADMIN,
    },
  },
  {
    path: "/admin/question",
    name: "题目管理",
    component: () => import("@/views/admin/AdminQuestionPage.vue"),
    meta: {
      access: ACCESS_ENUM.ADMIN,
    },
  },
  {
    path: "/admin/scoring_result",
    name: "评分管理",
    component: () => import("@/views/admin/AdminScoringResultPage.vue"),
    meta: {
      access: ACCESS_ENUM.ADMIN,
    },
  },
  {
    path: "/admin/user_answer",
    name: "回答管理",
    component: () => import("@/views/admin/AdminUserAnswerPage.vue"),
    meta: {
      access: ACCESS_ENUM.ADMIN,
    },
  },
  {
    path: "/app_statistic",
    name: "应用统计",
    component: () => import("@/views/statistic/AppStatisticPage.vue"),
    meta: {
      access: ACCESS_ENUM.ADMIN,
    },
  },
  {
    path: "/noAuth",
    name: "无权限",
    component: () => import("@/views/NoAuthPage.vue"),
    meta: {
      hideInMenu: true,
    },
  },
  {
    path: "/hide",
    name: "隐藏页面",
    component: () => import("@/views/HomePage.vue"),
    meta: {
      hideInMenu: true,
    },
  },
  {
    path: "/:pathMatch(.*)*",
    name: "404",
    component: () => import("@/views/NotFound.vue"),
    meta: {
      hideInMenu: true,
    },
  },
  {
    path: "/user",
    name: "用户",
    component: () => import("@/layouts/UserLayout.vue"),
    children: [
      {
        path: "/user/login",
        name: "用户登录",
        component: () => import("@/views/user/UserLoginPage.vue"),
      },
      {
        path: "/user/register",
        name: "用户注册",
        component: () => import("@/views/user/UserRegisterPage.vue"),
      },
    ],
    meta: {
      hideInMenu: true,
    },
  },
];
