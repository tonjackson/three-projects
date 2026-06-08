<template>
  <div class="home-page">
    <section class="hero">
      <div class="hero-copy">
        <a-tag color="arcoblue" class="hero-tag">AI Powered Assessment</a-tag>
        <h1>AI 智能答题应用平台</h1>
        <p>用 AI 快速生成题目、配置评分策略、发布答题应用，并沉淀用户答题结果与统计分析。</p>
        <a-space>
          <a-button type="primary" size="large" @click="$router.push('/add/app')">
            <template #icon><icon-plus /></template>
            创建应用
          </a-button>
          <a-button size="large" @click="scrollToApps">查看应用广场</a-button>
        </a-space>
      </div>
      <div class="hero-panel">
        <div v-for="item in stats" :key="item.label" class="metric">
          <div class="metric-value">{{ item.value }}</div>
          <div class="metric-label">{{ item.label }}</div>
        </div>
      </div>
    </section>

    <section class="quick-actions">
      <a-card v-for="item in features" :key="item.title" class="action-card" hoverable @click="$router.push(item.path)">
        <div class="action-icon" :style="{ background: item.color }">
          <component :is="item.icon" />
        </div>
        <div>
          <div class="action-title">{{ item.title }}</div>
          <div class="action-desc">{{ item.desc }}</div>
        </div>
      </a-card>
    </section>

    <section ref="appSection" class="content-grid">
      <div class="main-column">
        <div class="section-head">
          <div>
            <h2>热门答题应用</h2>
            <span>来自后端应用接口的真实数据，接口为空时展示真实空状态</span>
          </div>
          <a-input-search
            v-model="keyword"
            :style="{ width: '280px' }"
            placeholder="搜索应用 / 标签"
            button-text="搜索"
            search-button
          />
        </div>

        <a-alert v-if="loadError" type="error" :content="loadError" style="margin-bottom: 16px" />
        <a-spin :loading="loading" style="width: 100%">
        <a-empty v-if="filteredApps.length === 0" description="暂无真实应用数据，请先创建并审核通过应用" />
        <a-row v-else :gutter="[16, 16]">
          <a-col v-for="app in filteredApps" :key="app.id" :xs="24" :sm="12" :lg="8">
            <a-card class="app-card" hoverable @click="$router.push(`/app/detail/${app.id}`)">
              <div class="app-cover" :style="{ background: getAppColor(app) }">
                <span>{{ getAppInitial(app) }}</span>
              </div>
              <div class="app-body">
                <div class="app-title">{{ app.appName || `应用 #${app.id}` }}</div>
                <p>{{ app.appDesc || "暂无应用简介" }}</p>
                <a-space size="mini" wrap>
                  <a-tag :color="app.appType === 0 ? 'green' : 'arcoblue'">
                    {{ app.appType === 0 ? 'AI 测评' : '规则评分' }}
                  </a-tag>
                  <a-tag color="gray">{{ app.scoringStrategy === 0 ? "自定义评分" : "AI 评分" }}</a-tag>
                  <a-tag color="orangered">{{ app.reviewStatus === 1 ? "已通过" : "待审核" }}</a-tag>
                </a-space>
                <div class="app-footer">
                  <span><icon-user /> 创建者：{{ app.user?.userName || app.userId || "-" }}</span>
                  <span><icon-star /> {{ formatTime(app.createTime) }}</span>
                </div>
              </div>
            </a-card>
          </a-col>
        </a-row>
        </a-spin>
      </div>

      <aside class="side-column">
        <a-card title="最新上线" :bordered="false" class="side-card">
          <a-empty v-if="newApps.length === 0" description="暂无真实应用" />
          <div v-for="item in newApps" v-else :key="item.id" class="new-item" @click="$router.push(`/app/detail/${item.id}`)">
            <div class="new-avatar" :style="{ background: getAppColor(item) }">{{ getAppInitial(item) }}</div>
            <div class="new-info">
              <div>{{ item.appName || `应用 #${item.id}` }}</div>
              <span>{{ formatTime(item.createTime) }} · {{ item.appType === 0 ? 'AI 测评' : '规则评分' }}</span>
            </div>
          </div>
        </a-card>

        <a-card title="应用类型分布" :bordered="false" class="side-card">
          <a-empty v-if="apps.length === 0" description="暂无数据" />
          <template v-else>
            <div class="capability">AI 测评类 <a-progress :percent="aiTypePercent" size="small" color="#00b42a" /></div>
            <div class="capability">规则评分类 <a-progress :percent="ruleTypePercent" size="small" color="#165dff" /></div>
            <div class="capability">AI 评分类 <a-progress :percent="aiScorePercent" size="small" color="#722ed1" /></div>
          </template>
        </a-card>
      </aside>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import {
  IconBarChart,
  IconList,
  IconPlus,
  IconRobot,
  IconStar,
  IconUser,
} from "@arco-design/web-vue/es/icon";
import { listAppVoByPageUsingPost } from "@/api/appController";

const appSection = ref<HTMLElement>();
const keyword = ref("");
const apps = ref<API.AppVO[]>([]);
const total = ref(0);
const loading = ref(false);
const loadError = ref("");

const features = [
  { title: "AI 生成题目", desc: "输入主题和题型，快速生成题目草稿", icon: IconRobot, color: "#165dff", path: "/add/app" },
  { title: "应用广场", desc: "浏览已发布应用，直接进入答题体验", icon: IconList, color: "#0fc6c2", path: "/" },
  { title: "结果统计", desc: "查看答题结果分布和参与数据", icon: IconBarChart, color: "#722ed1", path: "/answer/my" },
];

const stats = computed(() => {
  const aiCount = apps.value.filter((item) => item.appType === 0).length;
  const scoreCount = apps.value.filter((item) => item.appType !== 0).length;
  return [
    { label: "发布应用", value: String(total.value) },
    { label: "AI 测评", value: String(aiCount) },
    { label: "规则评分", value: String(scoreCount) },
  ];
});

const filteredApps = computed(() => {
  const key = keyword.value.trim();
  if (!key) return apps.value;
  return apps.value.filter((item) => `${item.appName}${item.appDesc}`.includes(key));
});

const newApps = computed(() => apps.value.slice(0, 4));

const aiTypePercent = computed(() => {
  if (apps.value.length === 0) return 0;
  return Math.round((apps.value.filter((a) => a.appType === 0).length / apps.value.length) * 100) / 100;
});
const ruleTypePercent = computed(() => {
  if (apps.value.length === 0) return 0;
  return Math.round((apps.value.filter((a) => a.appType !== 0).length / apps.value.length) * 100) / 100;
});
const aiScorePercent = computed(() => {
  if (apps.value.length === 0) return 0;
  return Math.round((apps.value.filter((a) => a.scoringStrategy === 0).length / apps.value.length) * 100) / 100;
});

const colorList = [
  "linear-gradient(135deg,#165dff,#4e7cff)",
  "linear-gradient(135deg,#0fc6c2,#33d6c8)",
  "linear-gradient(135deg,#722ed1,#9b5cff)",
  "linear-gradient(135deg,#f7ba1e,#ff9a2e)",
  "linear-gradient(135deg,#f53f3f,#ff7a45)",
  "linear-gradient(135deg,#00b42a,#23c343)",
];

const getAppColor = (app: API.AppVO) => colorList[Number(app.id || 0) % colorList.length];
const getAppInitial = (app: API.AppVO) => (app.appName || "应用").slice(0, 2).toUpperCase();
const formatTime = (time?: string) => (time ? new Date(time).toLocaleDateString() : "-");

const loadApps = async () => {
  loading.value = true;
  loadError.value = "";
  try {
    const res = await listAppVoByPageUsingPost({
      current: 1,
      pageSize: 12,
      sortField: "createTime",
      sortOrder: "descend",
    });
    if (res.data.code === 0 && res.data.data) {
      apps.value = res.data.data.records || [];
      total.value = res.data.data.total || 0;
    } else {
      apps.value = [];
      total.value = 0;
      loadError.value = res.data.message || "应用接口返回异常";
    }
  } catch (error) {
    apps.value = [];
    total.value = 0;
    loadError.value = "无法连接后端应用接口，请确认 yudada-backend 已启动并完成数据库初始化。";
  } finally {
    loading.value = false;
  }
};

onMounted(loadApps);

const scrollToApps = () => appSection.value?.scrollIntoView({ behavior: "smooth" });
</script>

<style scoped>
.home-page { min-height: 100vh; background: #f5f6fa; }
.hero {
  display: flex; justify-content: space-between; align-items: center; gap: 32px;
  padding: 44px 56px; background: linear-gradient(135deg, #10204f 0%, #165dff 100%); color: #fff;
}
.hero h1 { margin: 10px 0 12px; font-size: 34px; line-height: 1.2; }
.hero p { max-width: 620px; margin: 0 0 24px; color: rgba(255,255,255,.82); font-size: 15px; }
.hero-tag { background: rgba(255,255,255,.16); color: #fff; border: 1px solid rgba(255,255,255,.28); }
.hero-panel {
  display: grid; grid-template-columns: repeat(3, 1fr); min-width: 420px; padding: 20px 24px;
  border-radius: 12px; background: rgba(255,255,255,.12); box-shadow: 0 12px 32px rgba(8,20,60,.22);
}
.metric { text-align: center; }
.metric-value { font-size: 26px; font-weight: 700; }
.metric-label { margin-top: 4px; color: rgba(255,255,255,.72); font-size: 12px; }
.quick-actions {
  max-width: 1200px; margin: -24px auto 0; padding: 0 24px;
  display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px;
}
.action-card { border-radius: 10px; cursor: pointer; box-shadow: 0 8px 24px rgba(29,33,41,.08); }
.action-card :deep(.arco-card-body) { display: flex; align-items: center; gap: 14px; padding: 20px; }
.action-icon { width: 46px; height: 46px; border-radius: 12px; color: #fff; display: flex; align-items: center; justify-content: center; font-size: 22px; }
.action-title { font-weight: 600; color: #1d2129; margin-bottom: 4px; }
.action-desc { font-size: 12px; color: #86909c; }
.content-grid {
  max-width: 1200px; margin: 28px auto 0; padding: 0 24px 40px;
  display: grid; grid-template-columns: minmax(0, 1fr) 300px; gap: 20px;
}
.section-head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.section-head h2 { margin: 0; font-size: 20px; }
.section-head span { display: block; margin-top: 4px; color: #86909c; font-size: 12px; }
.app-card { border-radius: 10px; overflow: hidden; cursor: pointer; }
.app-cover { height: 92px; display: flex; align-items: center; justify-content: center; color: #fff; font-weight: 700; font-size: 26px; }
.app-title { margin-bottom: 8px; color: #1d2129; font-weight: 600; }
.app-body p { height: 40px; margin: 0 0 12px; color: #86909c; font-size: 13px; line-height: 1.55; overflow: hidden; }
.app-footer { display: flex; justify-content: space-between; margin-top: 14px; color: #86909c; font-size: 12px; }
.side-card { border-radius: 10px; margin-bottom: 16px; }
.new-item { display: flex; gap: 10px; padding: 10px 0; cursor: pointer; border-bottom: 1px solid #f2f3f5; }
.new-item:last-child { border-bottom: none; }
.new-avatar { width: 38px; height: 38px; border-radius: 10px; color: #fff; display: flex; align-items: center; justify-content: center; font-weight: 700; }
.new-info div { color: #1d2129; font-weight: 500; }
.new-info span { color: #86909c; font-size: 12px; }
.capability { margin-bottom: 14px; color: #4e5969; font-size: 13px; }
@media (max-width: 900px) {
  .hero, .section-head { flex-direction: column; align-items: flex-start; }
  .hero-panel, .quick-actions, .content-grid { grid-template-columns: 1fr; min-width: 0; }
}
</style>
