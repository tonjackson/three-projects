<template>
  <div class="questions-page">
    <section class="overview">
      <div class="overview-title">
        <a-tag color="arcoblue">Online Judge</a-tag>
        <h1>题库与判题工作台</h1>
        <p>用高信息密度题库、最近提交和难度分布展示在线判题系统的核心业务闭环。</p>
      </div>
      <div class="stats">
        <div v-for="item in stats" :key="item.label" class="stat">
          <div class="stat-value">{{ item.value }}</div>
          <div class="stat-label">{{ item.label }}</div>
        </div>
      </div>
    </section>

    <div class="page-layout">
      <main class="questions-main">
        <a-card :bordered="false" class="filter-card">
          <a-row :gutter="12" align="center">
            <a-col flex="1">
              <a-input-search v-model="searchKey" placeholder="搜索题目标题或标签" allow-clear search-button />
            </a-col>
            <a-col>
              <a-select v-model="filterDifficulty" placeholder="难度" allow-clear style="width: 110px">
                <a-option value="easy">简单</a-option>
                <a-option value="medium">中等</a-option>
                <a-option value="hard">困难</a-option>
                <a-option value="unknown">未标注</a-option>
              </a-select>
            </a-col>
            <a-col>
              <a-select v-model="filterTag" placeholder="标签" allow-clear style="width: 120px">
                <a-option v-for="tag in allTags" :key="tag" :value="tag">{{ tag }}</a-option>
              </a-select>
            </a-col>
            <a-col>
              <a-select v-model="filterStatus" placeholder="提交状态" allow-clear style="width: 120px">
                <a-option value="ac">已通过</a-option>
                <a-option value="tried">尝试过</a-option>
                <a-option value="todo">未开始</a-option>
              </a-select>
            </a-col>
          </a-row>
        </a-card>

        <a-card :bordered="false" class="table-card">
          <template #title>
            <div class="table-title">
              <span>真实题库</span>
              <a-tag color="gray">{{ filteredQuestions.length }} 题</a-tag>
            </div>
          </template>
          <a-alert v-if="loadError" type="error" :content="loadError" style="margin-bottom: 16px" />
          <a-table
            :data="filteredQuestions"
            :loading="loading"
            :pagination="{ pageSize: 10, showTotal: true }"
            :bordered="false"
            row-key="id"
            @row-click="goToQuestion"
          >
            <template #columns>
              <a-table-column title="状态" :width="70">
                <template #cell="{ record }">
                  <icon-check-circle-fill v-if="record.status === 'ac'" class="status-ac" />
                  <icon-minus-circle-fill v-else-if="record.status === 'tried'" class="status-tried" />
                  <span v-else class="status-todo">-</span>
                </template>
              </a-table-column>
              <a-table-column title="#" data-index="id" :width="70" />
              <a-table-column title="题目" data-index="title">
                <template #cell="{ record }">
                  <a-link @click.stop="goToQuestion(record)">{{ record.title }}</a-link>
                </template>
              </a-table-column>
              <a-table-column title="标签" :width="210">
                <template #cell="{ record }">
                  <a-space size="mini" wrap>
                    <a-tag v-for="tag in record.tags" :key="tag" size="small" color="arcoblue">{{ tag }}</a-tag>
                  </a-space>
                </template>
              </a-table-column>
              <a-table-column title="难度" :width="90">
                <template #cell="{ record }">
                  <a-tag :color="difficultyColor(record.difficulty)" size="small">
                    {{ difficultyLabel(record.difficulty) }}
                  </a-tag>
                </template>
              </a-table-column>
              <a-table-column title="通过率" :width="150">
                <template #cell="{ record }">
                  <div class="rate-cell">
                    <a-progress :percent="record.acceptRate / 100" :color="acceptRateColor(record.acceptRate)" :show-text="false" size="small" />
                    <span>{{ record.acceptRate }}%</span>
                  </div>
                </template>
              </a-table-column>
              <a-table-column title="提交数" :width="90">
                <template #cell="{ record }">{{ record.submitNum || 0 }}</template>
              </a-table-column>
              <a-table-column title="操作" :width="100">
                <template #cell="{ record }">
                  <a-button type="outline" size="small" @click.stop="goToQuestion(record)">去作答</a-button>
                </template>
              </a-table-column>
            </template>
          </a-table>
        </a-card>
      </main>

      <aside class="sidebar">
        <a-card title="最近提交" :bordered="false" class="side-card">
          <template #extra>
            <a-link @click="$router.push('/question_submit')">全部</a-link>
          </template>
          <a-empty v-if="recentSubmits.length === 0" description="暂无真实提交记录" />
          <div v-for="item in recentSubmits" v-else :key="item.id" class="recent-item">
            <a-link @click="$router.push(`/view/question/${item.questionId}`)">{{ item.title }}</a-link>
            <div class="recent-meta">
              <a-tag :color="judgeStatusColor(item.statusText)" size="small">{{ item.statusText }}</a-tag>
              <span>{{ item.time }}</span>
            </div>
          </div>
        </a-card>

        <a-card title="难度分布" :bordered="false" class="side-card">
          <div v-for="item in diffDistribution" :key="item.label" class="diff-item">
            <div class="diff-head">
              <a-tag :color="item.color" size="small">{{ item.label }}</a-tag>
              <span>{{ item.count }} 题</span>
            </div>
            <a-progress :percent="item.percent / 100" :color="item.hex" :show-text="false" size="small" />
          </div>
        </a-card>

        <a-card title="推荐练习" :bordered="false" class="side-card">
          <div v-for="item in recommended" :key="item.id" class="recommend-item" @click="$router.push(`/view/question/${item.id}`)">
            <span>{{ item.title }}</span>
            <a-tag :color="difficultyColor(item.difficulty)" size="small">{{ difficultyLabel(item.difficulty) }}</a-tag>
          </div>
        </a-card>
      </aside>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import { IconCheckCircleFill, IconMinusCircleFill } from "@arco-design/web-vue/es/icon";
import { QuestionControllerService } from "../../../generated";
import type { QuestionSubmitVO, QuestionVO } from "../../../generated";

type Difficulty = "easy" | "medium" | "hard" | "unknown";
type QuestionStatus = "ac" | "tried" | "todo";

type QuestionRow = QuestionVO & {
  difficulty: Difficulty;
  acceptRate: number;
  status: QuestionStatus;
};

const router = useRouter();
const searchKey = ref("");
const filterDifficulty = ref<Difficulty | undefined>();
const filterTag = ref<string | undefined>();
const filterStatus = ref<QuestionStatus | undefined>();
const questions = ref<QuestionRow[]>([]);
const recentSubmitsRaw = ref<QuestionSubmitVO[]>([]);
const total = ref(0);
const loading = ref(false);
const loadError = ref("");

const getDifficulty = (tags?: string[]): Difficulty => {
  const text = (tags || []).join(",");
  if (text.includes("简单") || text.toLowerCase().includes("easy")) return "easy";
  if (text.includes("中等") || text.toLowerCase().includes("medium")) return "medium";
  if (text.includes("困难") || text.toLowerCase().includes("hard")) return "hard";
  return "unknown";
};

const getAcceptRate = (question: QuestionVO) => {
  const submitNum = question.submitNum || 0;
  if (!submitNum) return 0;
  return Math.round(((question.acceptedNum || 0) / submitNum) * 100);
};

const submittedStatusMap = computed<Record<number, QuestionStatus>>(() => {
  const map: Record<number, QuestionStatus> = {};
  recentSubmitsRaw.value.forEach((item) => {
    if (!item.questionId) return;
    if (item.status === 2) {
      map[item.questionId] = "ac";
    } else if (!map[item.questionId]) {
      map[item.questionId] = "tried";
    }
  });
  return map;
});

const stats = computed(() => {
  const submitTotal = questions.value.reduce((sum, item) => sum + (item.submitNum || 0), 0);
  const acceptedTotal = questions.value.reduce((sum, item) => sum + (item.acceptedNum || 0), 0);
  const solved = Object.values(submittedStatusMap.value).filter((item) => item === "ac").length;
  const passRate = submitTotal ? `${Math.round((acceptedTotal / submitTotal) * 100)}%` : "0%";
  return [
    { label: "题目总数", value: String(total.value) },
    { label: "提交总数", value: String(submitTotal) },
    { label: "AC 总数", value: String(acceptedTotal) },
    { label: "已解决", value: String(solved) },
    { label: "通过率", value: passRate },
  ];
});

const allTags = computed(() => Array.from(new Set(questions.value.flatMap((item) => item.tags || []))));

const filteredQuestions = computed(() => {
  const key = searchKey.value.trim();
  return questions.value.filter((item) => {
    const tags = item.tags || [];
    if (key && !(item.title || "").includes(key) && !tags.some((tag) => tag.includes(key))) return false;
    if (filterDifficulty.value && item.difficulty !== filterDifficulty.value) return false;
    if (filterTag.value && !tags.includes(filterTag.value)) return false;
    if (filterStatus.value && item.status !== filterStatus.value) return false;
    return true;
  });
});

const recentSubmits = computed(() =>
  recentSubmitsRaw.value.map((item) => ({
    id: item.id,
    questionId: item.questionId,
    title: item.questionVO?.title || `题目 #${item.questionId}`,
    statusText: submitStatusLabel(item.status),
    time: formatTime(item.createTime),
  })),
);

const diffDistribution = computed(() => {
  const values: Array<{ key: Difficulty; label: string; color: string; hex: string }> = [
    { key: "easy", label: "简单", color: "green", hex: "#00b42a" },
    { key: "medium", label: "中等", color: "orange", hex: "#ff7d00" },
    { key: "hard", label: "困难", color: "red", hex: "#f53f3f" },
    { key: "unknown", label: "未标注", color: "gray", hex: "#86909c" },
  ];
  return values.map((item) => {
    const count = questions.value.filter((question) => question.difficulty === item.key).length;
    return {
      ...item,
      count,
      percent: questions.value.length ? Math.round((count / questions.value.length) * 100) : 0,
    };
  });
});

const recommended = computed(() => questions.value.slice(0, 4));

const goToQuestion = (record: QuestionRow) => {
  router.push(`/view/question/${record.id}`);
};

const difficultyLabel = (value: Difficulty) => ({ easy: "简单", medium: "中等", hard: "困难", unknown: "未标注" }[value]);
const difficultyColor = (value: Difficulty) => ({ easy: "green", medium: "orange", hard: "red", unknown: "gray" }[value]);
const acceptRateColor = (rate: number) => (rate >= 60 ? "#00b42a" : rate >= 40 ? "#ff7d00" : "#f53f3f");
const judgeStatusColor = (status: string) => {
  if (status === "成功") return "green";
  if (status === "失败") return "red";
  if (status === "等待中") return "orange";
  return "gray";
};

const submitStatusLabel = (status?: number) => {
  if (status === 2) return "成功";
  if (status === 3) return "失败";
  if (status === 0) return "等待中";
  return "未知";
};

const formatTime = (time?: string) => (time ? new Date(time).toLocaleString() : "-");

const normalizeQuestion = (question: QuestionVO): QuestionRow => {
  const difficulty = getDifficulty(question.tags);
  return {
    ...question,
    difficulty,
    acceptRate: getAcceptRate(question),
    status: submittedStatusMap.value[question.id || 0] || "todo",
  };
};

const loadQuestions = async () => {
  loading.value = true;
  loadError.value = "";
  try {
    const [questionRes, submitRes] = await Promise.all([
      QuestionControllerService.listQuestionVoByPageUsingPost({
        current: 1,
        pageSize: 20,
        sortField: "createTime",
        sortOrder: "descend",
      }),
      QuestionControllerService.listQuestionSubmitByPageUsingPost({
        current: 1,
        pageSize: 10,
        sortField: "createTime",
        sortOrder: "descend",
      }),
    ]);
    if (questionRes.code === 0 && questionRes.data) {
      total.value = questionRes.data.total || 0;
      recentSubmitsRaw.value = submitRes.code === 0 && submitRes.data ? submitRes.data.records || [] : [];
      questions.value = (questionRes.data.records || []).map(normalizeQuestion);
    } else {
      total.value = 0;
      questions.value = [];
      recentSubmitsRaw.value = [];
      loadError.value = questionRes.message || "题目接口返回异常";
    }
  } catch (error) {
    total.value = 0;
    questions.value = [];
    recentSubmitsRaw.value = [];
    loadError.value = "无法连接后端题目接口，请确认 yuoj-backend 已启动并完成数据库初始化。";
  } finally {
    loading.value = false;
  }
};

onMounted(loadQuestions);
</script>

<style scoped>
.questions-page { min-height: 100vh; background: #f5f6fa; padding: 22px 24px 40px; }
.overview {
  display: flex; justify-content: space-between; gap: 24px; padding: 28px 32px; margin-bottom: 20px;
  border-radius: 12px; background: linear-gradient(135deg, #101828 0%, #243b74 100%); color: #fff;
}
.overview h1 { margin: 10px 0 8px; font-size: 28px; }
.overview p { margin: 0; color: rgba(255,255,255,.72); }
.stats { display: grid; grid-template-columns: repeat(5, 92px); gap: 10px; align-items: stretch; }
.stat { padding: 14px 8px; border-radius: 10px; text-align: center; background: rgba(255,255,255,.1); }
.stat-value { font-size: 21px; font-weight: 700; }
.stat-label { margin-top: 4px; font-size: 12px; color: rgba(255,255,255,.68); }
.page-layout { display: grid; grid-template-columns: minmax(0, 1fr) 300px; gap: 18px; }
.questions-main, .sidebar { min-width: 0; }
.filter-card, .table-card, .side-card { border-radius: 10px; box-shadow: 0 1px 4px rgba(29,33,41,.05); }
.filter-card { margin-bottom: 16px; }
.table-title { display: flex; align-items: center; gap: 8px; font-weight: 600; }
.table-card :deep(.arco-table-tr) { cursor: pointer; }
.table-card :deep(.arco-table-tr:hover .arco-table-td) { background: #f5f7ff; }
.status-ac, .status-tried { font-size: 17px; }
.status-ac { color: #00b42a; }
.status-tried { color: #ff7d00; }
.status-todo { color: #c9cdd4; }
.rate-cell { display: grid; grid-template-columns: 1fr 44px; align-items: center; gap: 8px; }
.rate-cell span { color: #4e5969; font-size: 12px; }
.side-card { margin-bottom: 16px; }
.recent-item { padding: 10px 0; border-bottom: 1px solid #f2f3f5; }
.recent-item:last-child { border-bottom: none; }
.recent-meta { display: flex; align-items: center; gap: 8px; margin-top: 6px; }
.recent-meta span { color: #86909c; font-size: 12px; }
.diff-item { margin-bottom: 14px; }
.diff-head, .recommend-item { display: flex; align-items: center; justify-content: space-between; }
.diff-head { margin-bottom: 6px; }
.diff-head span { color: #86909c; font-size: 12px; }
.recommend-item { padding: 9px 0; cursor: pointer; border-bottom: 1px solid #f2f3f5; }
.recommend-item:last-child { border-bottom: none; }
.recommend-item:hover { background: #f7f8fa; }
@media (max-width: 1000px) {
  .overview { display: block; }
  .page-layout { grid-template-columns: 1fr; }
  .stats { grid-template-columns: repeat(2, 1fr); margin-top: 20px; }
}
</style>
