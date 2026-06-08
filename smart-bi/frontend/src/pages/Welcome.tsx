import { PageContainer, ProCard } from '@ant-design/pro-components';
import {
  BarChartOutlined,
  ClockCircleOutlined,
  CloudUploadOutlined,
  ExclamationCircleOutlined,
  PlusOutlined,
  RightOutlined,
  SyncOutlined,
} from '@ant-design/icons';
import { history } from '@umijs/max';
import { Alert, Badge, Button, Col, Empty, List, Progress, Row, Space, Spin, Tag, Typography } from 'antd';
import React, { useEffect, useMemo, useState } from 'react';
import { listChartByPageUsingPOST } from '@/services/yubi/chartController';

const { Text } = Typography;

const statusConfig: Record<string, { color: 'success' | 'processing' | 'error' | 'warning'; text: string }> = {
  succeed: { color: 'success', text: '已完成' },
  running: { color: 'processing', text: '生成中' },
  failed: { color: 'error', text: '生成失败' },
  wait: { color: 'warning', text: '等待中' },
};

const typeColors = ['#1677ff', '#52c41a', '#fa8c16', '#722ed1', '#8c8c8c'];

const formatTime = (time?: string) => {
  if (!time) {
    return '-';
  }
  return new Date(time).toLocaleString();
};

const Welcome: React.FC = () => {
  const [charts, setCharts] = useState<API.Chart[]>([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  const [loadError, setLoadError] = useState('');

  const loadCharts = async () => {
    setLoading(true);
    setLoadError('');
    try {
      const res = await listChartByPageUsingPOST({
        current: 1,
        pageSize: 10,
        sortField: 'createTime',
        sortOrder: 'descend',
      });
      if (res.code === 0 && res.data) {
        setCharts(res.data.records || []);
        setTotal(res.data.total || 0);
      } else {
        setCharts([]);
        setTotal(0);
        setLoadError(res.message || '图表接口返回异常');
      }
    } catch (error) {
      setCharts([]);
      setTotal(0);
      setLoadError('无法连接后端图表接口，请确认 yubi-backend 已启动并完成数据库初始化。');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadCharts();
  }, []);

  const overviewStats = useMemo(() => {
    const today = new Date().toDateString();
    const todayCount = charts.filter((item) => item.createTime && new Date(item.createTime).toDateString() === today).length;
    const runningCount = charts.filter((item) => item.status === 'running' || item.status === 'wait').length;
    const failedCount = charts.filter((item) => item.status === 'failed').length;
    return [
      { title: '图表总数', value: total, suffix: '个', icon: <BarChartOutlined style={{ color: '#1677ff' }} /> },
      { title: '今日新增', value: todayCount, suffix: '个', icon: <PlusOutlined style={{ color: '#52c41a' }} /> },
      { title: '处理中', value: runningCount, suffix: '个', icon: <SyncOutlined spin style={{ color: '#fa8c16' }} /> },
      { title: '失败任务', value: failedCount, suffix: '个', icon: <ExclamationCircleOutlined style={{ color: '#ff4d4f' }} /> },
    ];
  }, [charts, total]);

  const failedCount = overviewStats[3].value;

  const typeDistribution = useMemo(() => {
    const countMap = charts.reduce<Record<string, number>>((map, item) => {
      const type = item.chartType || '未指定';
      map[type] = (map[type] || 0) + 1;
      return map;
    }, {});
    return Object.entries(countMap).map(([type, count], index) => ({
      type,
      count,
      percent: charts.length ? Math.round((count / charts.length) * 100) : 0,
      color: typeColors[index % typeColors.length],
    }));
  }, [charts]);

  return (
    <PageContainer
      header={{
        title: '智能 BI 工作台',
        subTitle: '上传数据，AI 自动分析并生成可视化图表',
        extra: [
          <Button key="async" onClick={() => history.push('/add_chart_async')}>
            异步生成
          </Button>,
          <Button key="create" type="primary" icon={<CloudUploadOutlined />} onClick={() => history.push('/add_chart')}>
            上传数据 / 智能分析
          </Button>,
        ],
      }}
    >
      {loadError && (
        <Alert
          showIcon
          type="error"
          message={loadError}
          style={{ marginBottom: 16 }}
          action={<Button size="small" onClick={loadCharts}>重试</Button>}
        />
      )}

      {failedCount > 0 && (
        <ProCard
          style={{
            marginBottom: 16,
            background: '#fff2f0',
            border: '1px solid #ffccc7',
            borderRadius: 8,
          }}
          bodyStyle={{ padding: '12px 16px' }}
        >
          <Space>
            <ExclamationCircleOutlined style={{ color: '#ff4d4f' }} />
            <Text>
              有 <Text strong style={{ color: '#ff4d4f' }}>{failedCount} 个</Text> 图表生成失败，请检查数据格式后重新提交。
            </Text>
            <Button type="link" size="small" onClick={() => history.push('/my_chart')}>
              查看详情 <RightOutlined />
            </Button>
          </Space>
        </ProCard>
      )}

      <Row gutter={16} style={{ marginBottom: 16 }}>
        {overviewStats.map((item) => (
          <Col span={6} key={item.title}>
            <ProCard style={{ borderRadius: 8 }} bodyStyle={{ padding: '20px 24px' }}>
              <Space align="start" size={16}>
                <div
                  style={{
                    width: 48,
                    height: 48,
                    borderRadius: 10,
                    background: '#f5f5f5',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    fontSize: 22,
                  }}
                >
                  {item.icon}
                </div>
                <div>
                  <div style={{ fontSize: 12, color: '#8c8c8c', marginBottom: 4 }}>{item.title}</div>
                  <div style={{ fontSize: 28, fontWeight: 700, lineHeight: 1 }}>
                    {item.value}
                    <span style={{ fontSize: 13, color: '#8c8c8c', fontWeight: 400, marginLeft: 4 }}>
                      {item.suffix}
                    </span>
                  </div>
                </div>
              </Space>
            </ProCard>
          </Col>
        ))}
      </Row>

      <Row gutter={16}>
        <Col span={16}>
          <ProCard
            title="最近图表"
            extra={
              <Button type="link" onClick={() => history.push('/my_chart')}>
                全部图表 <RightOutlined />
              </Button>
            }
            style={{ borderRadius: 8 }}
          >
            <Spin spinning={loading}>
              <List
                locale={{ emptyText: <Empty description="暂无真实图表数据，请先上传数据生成图表" /> }}
                dataSource={charts}
                renderItem={(item) => {
                  const status = statusConfig[item.status || 'wait'] || statusConfig.wait;
                  return (
                    <List.Item
                      style={{ cursor: 'pointer', padding: '12px 0' }}
                      actions={[
                        <Badge key="status" status={status.color} text={status.text} />,
                        <Text key="time" type="secondary" style={{ fontSize: 12 }}>
                          {formatTime(item.createTime)}
                        </Text>,
                      ]}
                    >
                      <List.Item.Meta
                        title={
                          <Space>
                            <Text strong style={{ fontSize: 14 }}>{item.name || `图表 #${item.id}`}</Text>
                            <Tag color="blue">{item.chartType || '未指定'}</Tag>
                          </Space>
                        }
                        description={
                          <Text type="secondary" style={{ fontSize: 12 }}>
                            分析目标：{item.goal || '-'}
                          </Text>
                        }
                      />
                    </List.Item>
                  );
                }}
              />
            </Spin>
          </ProCard>
        </Col>

        <Col span={8}>
          <Space direction="vertical" style={{ width: '100%' }} size={16}>
            <ProCard title="图表类型分布" style={{ borderRadius: 8 }}>
              {typeDistribution.length === 0 ? (
                <Empty description="暂无类型统计" />
              ) : (
                typeDistribution.map((item) => (
                  <div key={item.type} style={{ marginBottom: 14 }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 5 }}>
                      <Text style={{ fontSize: 13 }}>{item.type}</Text>
                      <Text type="secondary" style={{ fontSize: 12 }}>{item.count} 个</Text>
                    </div>
                    <Progress percent={item.percent} strokeColor={item.color} showInfo={false} size="small" />
                  </div>
                ))
              )}
            </ProCard>

            <ProCard title="快捷操作" style={{ borderRadius: 8 }}>
              <Space direction="vertical" style={{ width: '100%' }}>
                <Button block icon={<CloudUploadOutlined />} type="primary" onClick={() => history.push('/add_chart')}>
                  上传 Excel / CSV 分析
                </Button>
                <Button block icon={<ClockCircleOutlined />} onClick={() => history.push('/add_chart_async')}>
                  异步任务分析
                </Button>
                <Button block icon={<BarChartOutlined />} onClick={() => history.push('/my_chart')}>
                  我的图表库
                </Button>
              </Space>
            </ProCard>
          </Space>
        </Col>
      </Row>
    </PageContainer>
  );
};

export default Welcome;
