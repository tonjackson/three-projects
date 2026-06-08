import { listUserVOByPageUsingPOST } from '@/services/yubi/userController';
import { PageContainer } from '@ant-design/pro-components';
import { history } from '@umijs/max';
import { Alert, Button, Card, Col, Row, Space, Spin, Table, Tag, Typography } from 'antd';
import React, { useEffect, useState } from 'react';

const { Text } = Typography;

const Admin: React.FC = () => {
  const [users, setUsers] = useState<API.UserVO[]>([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  const [loadError, setLoadError] = useState('');
  const [current, setCurrent] = useState(1);
  const pageSize = 10;

  const loadUsers = async (page: number = 1) => {
    setLoading(true);
    setLoadError('');
    try {
      const res = await listUserVOByPageUsingPOST({
        current: page,
        pageSize,
        sortField: 'createTime',
        sortOrder: 'descend',
      });
      if (res.code === 0 && res.data) {
        setUsers(res.data.records || []);
        setTotal(res.data.total || 0);
      } else {
        setUsers([]);
        setTotal(0);
        setLoadError(res.message || '用户接口返回异常');
      }
    } catch {
      setUsers([]);
      setTotal(0);
      setLoadError('无法连接后端接口，请确认 yubi-backend 已启动。');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadUsers(1);
  }, []);

  const columns = [
    {
      title: 'ID',
      dataIndex: 'id',
      width: 80,
    },
    {
      title: '用户名',
      dataIndex: 'userName',
      render: (text: string) => text || '-',
    },
    {
      title: '账号',
      dataIndex: 'userAccount',
      render: (text: string) => text || '-',
    },
    {
      title: '头像',
      dataIndex: 'userAvatar',
      width: 80,
      render: (url: string) =>
        url ? (
          <img src={url} alt="avatar" style={{ width: 32, height: 32, borderRadius: '50%' }} />
        ) : (
          <div
            style={{
              width: 32,
              height: 32,
              borderRadius: '50%',
              background: 'linear-gradient(135deg, #2563eb, #3b82f6)',
              color: '#fff',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              fontSize: 14,
              fontWeight: 600,
            }}
          >
            U
          </div>
        ),
    },
    {
      title: '角色',
      dataIndex: 'userRole',
      render: (role: string) => {
        const isadmin = role === 'admin';
        return <Tag color={isadmin ? 'red' : 'blue'}>{isadmin ? '管理员' : '用户'}</Tag>;
      },
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      render: (time: string) => (time ? new Date(time).toLocaleString() : '-'),
    },
  ];

  const quickEntries = [
    { title: '智能分析', desc: '上传 CSV，AI 自动生成图表', path: '/add_chart' },
    { title: '异步分析', desc: '大数据量异步生成', path: '/add_chart_async' },
    { title: '我的图表', desc: '查看和管理已生成图表', path: '/my_chart' },
  ];

  return (
    <PageContainer
      header={{
        title: '管理后台',
        subTitle: '用户管理与平台运维',
      }}
    >
      {loadError && (
        <Alert
          showIcon
          type="warning"
          message={loadError}
          style={{ marginBottom: 16 }}
          action={<Button size="small" onClick={() => loadUsers(current)}>重试</Button>}
        />
      )}

      <Row gutter={16} style={{ marginBottom: 16 }}>
        {quickEntries.map((item) => (
          <Col span={8} key={item.path}>
            <Card
              hoverable
              size="small"
              onClick={() => history.push(item.path)}
              style={{ borderRadius: 8 }}
            >
              <Text strong style={{ fontSize: 15 }}>{item.title}</Text>
              <br />
              <Text type="secondary" style={{ fontSize: 12 }}>{item.desc}</Text>
            </Card>
          </Col>
        ))}
      </Row>

      <Card title="用户管理" style={{ borderRadius: 8 }}>
        <Spin spinning={loading}>
          <Table
            rowKey="id"
            columns={columns}
            dataSource={users}
            pagination={{
              current,
              pageSize,
              total,
              showTotal: (t) => `共 ${t} 条`,
              onChange: (page) => {
                setCurrent(page);
                loadUsers(page);
              },
            }}
            locale={{ emptyText: loadError ? '接口不可用' : '暂无用户数据' }}
          />
        </Spin>
      </Card>
    </PageContainer>
  );
};

export default Admin;
