use yubi;

INSERT IGNORE INTO user_info (id, userAccount, userPassword, userName, userAvatar, userRole, createTime, updateTime, isDelete)
VALUES
  (1, 'admin_user', '4dac5576637f2d6ea9f902a5df25f061', 'BI 管理员', 'https://api.dicebear.com/7.x/initials/svg?seed=BI', 'admin', now(), now(), 0);

INSERT IGNORE INTO chart (id, goal, `name`, chartData, chartType, genChart, genResult, status, execMessage, userId, createTime, updateTime, isDelete)
VALUES
  (1, '分析近 6 个月销售额变化趋势', '月度销售趋势分析', '月份,销售额\n1月,128000\n2月,156000\n3月,149000\n4月,188000\n5月,205000\n6月,236000', '折线图', '{"xAxis":{"type":"category","data":["1月","2月","3月","4月","5月","6月"]},"yAxis":{"type":"value"},"series":[{"type":"line","data":[128000,156000,149000,188000,205000,236000]}]}', '近 6 个月销售额整体上升，6 月达到最高值，4 月后增长速度明显加快。', 'succeed', null, 1, now() - interval 5 minute, now() - interval 5 minute, 0),
  (2, '展示不同渠道新增用户占比', '用户渠道占比分析', '渠道,新增用户\n自然流量,420\n广告投放,360\n内容运营,210\n渠道合作,170', '饼图', '{"series":[{"type":"pie","data":[{"name":"自然流量","value":420},{"name":"广告投放","value":360},{"name":"内容运营","value":210},{"name":"渠道合作","value":170}]}]}', '自然流量和广告投放贡献最高，合计占新增用户主要来源。', 'succeed', null, 1, now() - interval 32 minute, now() - interval 32 minute, 0),
  (3, '对比四季度营收数据', '季度营收对比模型', '季度,营收\nQ1,560000\nQ2,630000\nQ3,710000\nQ4,860000', '柱状图', null, null, 'running', null, 1, now() - interval 1 hour, now() - interval 1 hour, 0),
  (4, '识别库存周转率异常波动', '库存周转异常监控', '日期,周转率\n周一,1.8\n周二,1.6\n周三,0.7\n周四,1.5\n周五,1.9', '折线图', null, null, 'failed', '样本数据存在异常低值，请核对库存录入。', 1, now() - interval 2 hour, now() - interval 2 hour, 0),
  (5, '分析各地区销售贡献', '区域销售分布分析', '地区,销售额\n华东,420000\n华南,360000\n华北,280000\n西南,190000', '热力图', '{"series":[{"type":"bar","data":[420000,360000,280000,190000]}]}', '华东和华南区域贡献最高，西南区域仍有增长空间。', 'succeed', null, 1, now() - interval 1 day, now() - interval 1 day, 0);
