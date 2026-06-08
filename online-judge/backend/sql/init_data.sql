-- OJ 在线判题真实演示数据
-- 登录账号：admin_user
-- 登录密码：12345678

use yuoj;

INSERT IGNORE INTO user (id, userAccount, userPassword, unionId, mpOpenId, userName, userAvatar, userProfile, userRole, createTime, updateTime, isDelete)
VALUES
  (1, 'admin_user', 'c351be499fce691b56cbaaf900ef4d0c', null, null, 'OJ 管理员', 'https://api.dicebear.com/7.x/initials/svg?seed=OJ', '题库维护者', 'admin', now(), now(), 0);

INSERT IGNORE INTO question (id, title, content, tags, answer, submitNum, acceptedNum, judgeCase, judgeConfig, thumbNum, favourNum, userId, createTime, updateTime, isDelete)
VALUES
  (1, '两数之和', '给定一个整数数组 nums 和目标值 target，请返回两数下标，使得两数之和等于目标值。', '["简单","数组","哈希表"]', '使用哈希表记录已遍历数字和下标。', 4230, 3045, '[{"input":"[2,7,11,15]\\n9","output":"[0,1]"}]', '{"timeLimit":1000,"memoryLimit":262144,"stackLimit":262144}', 12, 31, 1, now() - interval 6 day, now() - interval 6 day, 0),
  (2, '无重复字符的最长子串', '给定一个字符串 s，请找出其中不含重复字符的最长子串长度。', '["中等","字符串","滑动窗口","哈希表"]', '使用滑动窗口维护当前无重复区间。', 3891, 2179, '[{"input":"abcabcbb","output":"3"}]', '{"timeLimit":1000,"memoryLimit":262144,"stackLimit":262144}', 9, 22, 1, now() - interval 5 day, now() - interval 5 day, 0),
  (3, '最长回文子串', '给定字符串 s，返回 s 中最长的回文子串。', '["中等","字符串","动态规划"]', '可使用中心扩展或动态规划。', 3102, 1271, '[{"input":"babad","output":"bab"}]', '{"timeLimit":1000,"memoryLimit":262144,"stackLimit":262144}', 7, 18, 1, now() - interval 4 day, now() - interval 4 day, 0),
  (4, '接雨水', '给定非负整数数组 height，计算柱子之间能接多少雨水。', '["困难","数组","双指针","动态规划"]', '双指针维护左右最大高度。', 1987, 536, '[{"input":"[0,1,0,2,1,0,1,3,2,1,2,1]","output":"6"}]', '{"timeLimit":1000,"memoryLimit":262144,"stackLimit":262144}', 14, 29, 1, now() - interval 3 day, now() - interval 3 day, 0),
  (5, '反转链表', '给定单链表头节点，反转链表并返回新的头节点。', '["简单","链表","递归"]', '迭代维护 prev 和 cur 指针。', 3412, 2697, '[{"input":"[1,2,3,4,5]","output":"[5,4,3,2,1]"}]', '{"timeLimit":1000,"memoryLimit":262144,"stackLimit":262144}', 11, 20, 1, now() - interval 2 day, now() - interval 2 day, 0);

INSERT IGNORE INTO question_submit (id, language, code, judgeInfo, status, questionId, userId, createTime, updateTime, isDelete)
VALUES
  (1, 'java', 'class Main {}', '{"message":"Accepted","time":68,"memory":32100}', 2, 1, 1, now() - interval 2 minute, now() - interval 2 minute, 0),
  (2, 'java', 'class Main {}', '{"message":"Wrong Answer","time":51,"memory":31800}', 3, 3, 1, now() - interval 15 minute, now() - interval 15 minute, 0),
  (3, 'java', 'class Main {}', '{"message":"Accepted","time":74,"memory":33000}', 2, 5, 1, now() - interval 1 hour, now() - interval 1 hour, 0),
  (4, 'java', 'class Main {}', '{"message":"Time Limit Exceeded","time":1001,"memory":34000}', 3, 4, 1, now() - interval 3 hour, now() - interval 3 hour, 0);
