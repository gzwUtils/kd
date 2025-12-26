-- 消息记录表（简化版）
CREATE TABLE `message_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `biz_id` varchar(64) NOT NULL COMMENT '业务ID',
  `template_id` bigint(20) DEFAULT NULL COMMENT '模板ID',

  -- 核心字段
  `channel_type` varchar(20) NOT NULL COMMENT '渠道类型：SMS/EMAIL/WEBSITE',
  `message_type` tinyint(4) NOT NULL DEFAULT '2' COMMENT '消息类型：1-广播 2-批量 3-单条',

  -- 接收者（简化：只存主要接收者或第一个）
  `receiver` varchar(200) DEFAULT NULL COMMENT '接收者标识',
  `receiver_count` int(11) DEFAULT '0' COMMENT '接收者总数',

  -- 消息内容
  `title` varchar(200) DEFAULT '' COMMENT '消息标题',
  `content` text NOT NULL COMMENT '消息内容',

  -- 延迟相关（简化）
  `delay_minutes` int(11) DEFAULT '0' COMMENT '延迟分钟数',
  `schedule_time` datetime DEFAULT NULL COMMENT '计划发送时间',
  `retry_count` int(11) DEFAULT '0' COMMENT '重试次数',

  -- 发送状态（简化）
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态：1-发送中 2-成功 3-失败',
  `success_count` int(11) DEFAULT '0' COMMENT '成功数量',
  `fail_count` int(11) DEFAULT '0' COMMENT '失败数量',

  -- 基础时间字段
  `send_time` datetime DEFAULT NULL COMMENT '发送时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

  -- 索引
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_biz_id` (`biz_id`),
  KEY `idx_channel_type` (`channel_type`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_schedule_time` (`schedule_time`)
) COMMENT='消息记录表';