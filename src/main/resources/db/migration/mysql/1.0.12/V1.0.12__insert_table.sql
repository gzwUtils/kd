-- 消息接收者详情表（只存需要跟踪状态的，如站内信）
CREATE TABLE `message_receiver_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `record_id` bigint(20) NOT NULL COMMENT '记录ID',
  `receiver` varchar(100) NOT NULL COMMENT '接收者',

  -- 状态字段（简化）
  `read_status` tinyint(4) DEFAULT '0' COMMENT '阅读状态：0-未读 1-已读',
  `read_time` datetime DEFAULT NULL COMMENT '阅读时间',

  -- 基础字段
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

  -- 索引
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_record_receiver` (`record_id`,`receiver`),
  KEY `idx_receiver` (`receiver`),
  KEY `idx_read_status` (`read_status`)
) COMMENT='消息接收者详情表';