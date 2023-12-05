DROP TABLE IF EXISTS `assign`;

CREATE TABLE `assign`
(
    `id`             int(10) unsigned NOT NULL AUTO_INCREMENT,
    `customer_name`  varchar(100) NOT NULL COMMENT '客户姓名',
    `address`        varchar(255)   DEFAULT NULL COMMENT '客户地址',
    `phone`          varchar(100)   DEFAULT NULL COMMENT '客户手机号',
    `create_time`    datetime       DEFAULT NULL COMMENT '注册日期',
    `update_time`    datetime       DEFAULT NULL COMMENT '更新时间',
    `status`         int(11) DEFAULT '0' COMMENT ' 0 未支付 1  支付',
    `number`         int(1) DEFAULT '0' COMMENT '消费次数',
    `balance`        decimal(10, 2) DEFAULT '0.00' COMMENT '余额',
    `service_id`     varchar(6)     DEFAULT NULL COMMENT '订阅服务ID',
    `coupon`         int(1) DEFAULT '0' COMMENT '是否拥有优惠劵 0 没有 1 有',
    `coupon_balance` decimal(10, 2) DEFAULT '0.00' COMMENT '优惠劵额度',
    `openid`         varchar(100)   DEFAULT NULL COMMENT 'openId',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='合同';



DROP TABLE IF EXISTS `async_task`;

CREATE TABLE `async_task`
(
    `id`                int(10) unsigned NOT NULL AUTO_INCREMENT,
    `params`            varchar(400) NOT NULL COMMENT '任务参数',
    `status`            int(11) NOT NULL COMMENT '下载状态',
    `errorMsg`          varchar(400) DEFAULT NULL COMMENT '处理失败原因',
    `type`              int(11) DEFAULT NULL COMMENT '异步任务类型',
    `fromMenu`          varchar(400) DEFAULT NULL COMMENT '导出来源菜单',
    `creator`           varchar(500) DEFAULT NULL COMMENT '创建者',
    `create_time`       datetime     DEFAULT NULL COMMENT '创建时间',
    `updater`           varchar(500) DEFAULT NULL COMMENT '更新者',
    `update_time`       datetime     DEFAULT NULL COMMENT '更新时间',
    `exportName`        varchar(100) DEFAULT NULL COMMENT '导出名称',
    `numberOfSuccesses` int(11) DEFAULT NULL COMMENT '处理成功的条数',
    `numberOfFailed`    int(11) DEFAULT NULL COMMENT '处理失败的条数',
    `file_path`         varchar(200) null comment '文件路径',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='async_task';



DROP TABLE IF EXISTS `config`;

CREATE TABLE `config`
(
    `id`             int(10) unsigned NOT NULL AUTO_INCREMENT,
    `annual_balance` decimal(10, 2) NOT NULL COMMENT '每月服务费',
    `year_balance`   decimal(10, 2) DEFAULT NULL COMMENT '全年服务费',
    `extra_balance`  decimal(10, 2) DEFAULT NULL COMMENT '加项服务费',
    `other_balance`  decimal(10, 2) DEFAULT NULL COMMENT '其他服务费',
    `create_time`    datetime       null comment '创建时间',
    `update_time`    datetime       null comment '更新时间'
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务配置';


DROP TABLE IF EXISTS `doc`;

CREATE TABLE `doc`
(
    `id`            int(10) unsigned NOT NULL AUTO_INCREMENT,
    `status`        int(11) NOT NULL COMMENT '状态',
    `dispatch`      varchar(400) NOT NULL COMMENT '派单人',
    `appoint`       varchar(400) NOT NULL COMMENT '指派人',
    `desc`          varchar(400) DEFAULT NULL COMMENT '描述',
    `issue_date`    datetime     DEFAULT NULL COMMENT '发行日期',
    `appoint_date`  datetime     DEFAULT NULL COMMENT '指派日期',
    `audit`         varchar(100) DEFAULT NULL COMMENT '审批人',
    `verify`        varchar(100) DEFAULT NULL COMMENT '核稿人',
    `result`        varchar(20)  DEFAULT NULL,
    `msgId`         varchar(100) DEFAULT NULL,
    `remark`        varchar(500) DEFAULT NULL COMMENT '备注',
    `address`       varchar(500) DEFAULT NULL COMMENT '客户地址',
    `consumer_Name` varchar(500) DEFAULT NULL COMMENT '客户姓名',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='doc';



DROP TABLE IF EXISTS `fly_learn`;

CREATE TABLE `fly_learn`
(
    `id`   int(10) unsigned NOT NULL AUTO_INCREMENT,
    `type` int(11) NOT NULL COMMENT '类型',
    `name` varchar(400) NOT NULL COMMENT '名称',
    `desc` varchar(400) DEFAULT NULL COMMENT '描述',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='fly_learn';


DROP TABLE IF EXISTS `flyway_schema_history`;

CREATE TABLE `flyway_schema_history`
(
    `installed_rank` int(11) NOT NULL,
    `version`        varchar(50)            DEFAULT NULL,
    `description`    varchar(200)  NOT NULL,
    `type`           varchar(20)   NOT NULL,
    `script`         varchar(1000) NOT NULL,
    `checksum`       int(11) DEFAULT NULL,
    `installed_by`   varchar(100)  NOT NULL,
    `installed_on`   timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `execution_time` int(11) NOT NULL,
    `success`        tinyint(1) NOT NULL,
    PRIMARY KEY (`installed_rank`),
    KEY              `flyway_schema_history_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



DROP TABLE IF EXISTS `log`;

CREATE TABLE `log`
(
    `id`          int(10) unsigned NOT NULL AUTO_INCREMENT,
    `username`    varchar(400) NOT NULL COMMENT '用户',
    `operation`   varchar(400) NOT NULL COMMENT '操作',
    `desc`        varchar(400) DEFAULT NULL COMMENT '描述',
    `time`        mediumint(9) DEFAULT NULL COMMENT '执行时间',
    `method`      varchar(400) DEFAULT NULL COMMENT '方法',
    `params`      varchar(500) DEFAULT NULL COMMENT '参数',
    `ip`          varchar(100) DEFAULT NULL COMMENT 'ip',
    `create_time` datetime     DEFAULT NULL COMMENT '创建时间',
    `location`    varchar(100) DEFAULT NULL COMMENT '地址',
    `result`      varchar(200) DEFAULT NULL COMMENT '操作结果',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='log';



DROP TABLE IF EXISTS `service`;

CREATE TABLE `service`
(
    `id`                 int(10) unsigned NOT NULL AUTO_INCREMENT,
    `service_year`       int(3) NOT NULL COMMENT '服务年限',
    `start_time`         datetime       DEFAULT NULL COMMENT '起始时间',
    `end_time`           datetime       DEFAULT NULL COMMENT '结束时间',
    `floor_area`         varchar(100)   DEFAULT NULL COMMENT '房屋建筑面积',
    `style`              int(11) DEFAULT '0' COMMENT ' 0 小保洁 1  大保洁',
    `number`             int(1) DEFAULT '0' COMMENT '保洁次数',
    `first_service_time` varchar(100)   DEFAULT NULL COMMENT '第一次保洁服务时间',
    `service_time`       varchar(100)   DEFAULT NULL COMMENT '固定服务时间',
    `extra_desc`         varchar(400)   DEFAULT NULL COMMENT '特许附加服务项目',
    `service_balance`    decimal(10, 2) DEFAULT '0.00' COMMENT '服务费用',
    `openid`             varchar(100)   DEFAULT NULL COMMENT 'openId',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务';


DROP TABLE IF EXISTS `user`;

CREATE TABLE `user`
(
    `id`          int(10) unsigned NOT NULL AUTO_INCREMENT,
    `password`    varchar(400) NOT NULL COMMENT '密码',
    `account`     varchar(255) DEFAULT NULL COMMENT '账户',
    `phone`       varchar(400) DEFAULT NULL COMMENT '手机号',
    `create_time` datetime     DEFAULT NULL COMMENT '注册日期',
    `status`      int(11) DEFAULT '0' COMMENT '是否启用 0 启用 1  禁用',
    `update_time` datetime     DEFAULT NULL COMMENT '更新时间',
    `is_admin`    int(1) DEFAULT '0' COMMENT '是否管理员',
    `error_retry` int(1) DEFAULT '0' COMMENT '错误次数',
    `email`       varchar(60)   null comment 'email',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='user';



DROP TABLE IF EXISTS `wx_template_msg`;

CREATE TABLE `wx_template_msg`
(
    `id`            int(10) unsigned NOT NULL AUTO_INCREMENT,
    `template_id`   varchar(400) DEFAULT NULL COMMENT '模板ID',
    `template_name` varchar(400) NOT NULL COMMENT '模板名称',
    `color`         varchar(20)  NOT NULL COMMENT '模板颜色',
    `status`        int(2) DEFAULT '0' COMMENT '0 在用 1 停用',
    `role`          int(2) DEFAULT '0' COMMENT '0 关注 1 代办 ',
    `url`           varchar(100) DEFAULT NULL COMMENT '跳转url',
    `data`          varchar(100) DEFAULT NULL COMMENT '模板数据',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='wx_template_msg';


DROP TABLE IF EXISTS `wx_user`;

CREATE TABLE `wx_user`
(
    `id`              int(10) unsigned NOT NULL AUTO_INCREMENT,
    `subscribe`       int(11) DEFAULT '0' COMMENT '是否订阅',
    `openId`          varchar(400) NOT NULL COMMENT 'openid',
    `nickname`        varchar(400) NOT NULL COMMENT 'nickname',
    `sex`             varchar(400) NOT NULL COMMENT 'sex',
    `headimgurl`      varchar(400) NOT NULL COMMENT 'headimgurl',
    `phone`           varchar(400) DEFAULT NULL COMMENT '手机号',
    `language`        varchar(400) NOT NULL COMMENT '语言',
    `subscribe_time`  mediumtext COMMENT '订阅时间',
    `unionid`         varchar(400) DEFAULT NULL,
    `remark`          varchar(400) DEFAULT NULL,
    `subscribe_scene` varchar(400) DEFAULT NULL COMMENT '来源',
    `groupid`         int(11) DEFAULT '0',
    `tagid_list`      varchar(400) DEFAULT NULL,
    `name`            varchar(200) DEFAULT NULL COMMENT '真实姓名',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='wx_user';




DROP TABLE IF EXISTS `qrtz_job_details`;

CREATE TABLE `qrtz_job_details`
(
    `SCHED_NAME`        varchar(120) NOT NULL COMMENT '计划名称',
    `JOB_NAME`          varchar(200) NOT NULL COMMENT '作业名称',
    `JOB_GROUP`         varchar(200) NOT NULL COMMENT '作业组',
    `DESCRIPTION`       varchar(250) DEFAULT NULL COMMENT '描述',
    `JOB_CLASS_NAME`    varchar(250) NOT NULL COMMENT '作业程序集名称',
    `IS_DURABLE`        varchar(1)   NOT NULL COMMENT '是否持久',
    `IS_NONCONCURRENT`  varchar(1)   NOT NULL COMMENT '是否并行',
    `IS_UPDATE_DATA`    varchar(1)   NOT NULL COMMENT '是否更新',
    `REQUESTS_RECOVERY` varchar(1)   NOT NULL COMMENT '是否要求唤醒',
    `JOB_DATA`          blob,
    PRIMARY KEY (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='自定义触发器';



DROP TABLE IF EXISTS `qrtz_triggers`;

CREATE TABLE `qrtz_triggers`
(
    `SCHED_NAME`     varchar(120) NOT NULL COMMENT '计划名称',
    `TRIGGER_NAME`   varchar(200) NOT NULL COMMENT '触发器名称',
    `TRIGGER_GROUP`  varchar(200) NOT NULL COMMENT '触发器组',
    `JOB_NAME`       varchar(200) NOT NULL COMMENT '作业名称',
    `JOB_GROUP`      varchar(200) NOT NULL COMMENT '作业组',
    `DESCRIPTION`    varchar(250) DEFAULT NULL COMMENT '描述',
    `NEXT_FIRE_TIME` bigint(13) DEFAULT NULL COMMENT '下次执行时间',
    `PREV_FIRE_TIME` bigint(13) DEFAULT NULL COMMENT '前一次执行时间',
    `PRIORITY`       int(11) DEFAULT NULL COMMENT '优先权',
    `TRIGGER_STATE`  varchar(16)  NOT NULL COMMENT '触发器状态',
    `TRIGGER_TYPE`   varchar(8)   NOT NULL COMMENT '触发器类型',
    `START_TIME`     bigint(13) NOT NULL COMMENT '开始时间',
    `END_TIME`       bigint(13) DEFAULT NULL COMMENT '结束时间',
    `CALENDAR_NAME`  varchar(200) DEFAULT NULL COMMENT '日历名称',
    `MISFIRE_INSTR`  smallint(2) DEFAULT NULL COMMENT '失败次数',
    `JOB_DATA`       blob COMMENT '作业数据',
    PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`),
    KEY              `IDX_QRTZ_T_J` (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`),
    KEY              `IDX_QRTZ_T_JG` (`SCHED_NAME`,`JOB_GROUP`),
    KEY              `IDX_QRTZ_T_C` (`SCHED_NAME`,`CALENDAR_NAME`),
    KEY              `IDX_QRTZ_T_G` (`SCHED_NAME`,`TRIGGER_GROUP`),
    KEY              `IDX_QRTZ_T_STATE` (`SCHED_NAME`,`TRIGGER_STATE`),
    KEY              `IDX_QRTZ_T_N_STATE` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`,`TRIGGER_STATE`),
    KEY              `IDX_QRTZ_T_N_G_STATE` (`SCHED_NAME`,`TRIGGER_GROUP`,`TRIGGER_STATE`),
    KEY              `IDX_QRTZ_T_NEXT_FIRE_TIME` (`SCHED_NAME`,`NEXT_FIRE_TIME`),
    KEY              `IDX_QRTZ_T_NFT_ST` (`SCHED_NAME`,`TRIGGER_STATE`,`NEXT_FIRE_TIME`),
    KEY              `IDX_QRTZ_T_NFT_MISFIRE` (`SCHED_NAME`,`MISFIRE_INSTR`,`NEXT_FIRE_TIME`),
    KEY              `IDX_QRTZ_T_NFT_ST_MISFIRE` (`SCHED_NAME`,`MISFIRE_INSTR`,`NEXT_FIRE_TIME`,`TRIGGER_STATE`),
    KEY              `IDX_QRTZ_T_NFT_ST_MISFIRE_GRP` (`SCHED_NAME`,`MISFIRE_INSTR`,`NEXT_FIRE_TIME`,`TRIGGER_GROUP`,`TRIGGER_STATE`),
    CONSTRAINT `qrtz_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) REFERENCES `qrtz_job_details` (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='触发器的基本信息';



DROP TABLE IF EXISTS `qrtz_blob_triggers`;

CREATE TABLE `qrtz_blob_triggers`
(
    `SCHED_NAME`    varchar(120) NOT NULL COMMENT '计划名称',
    `TRIGGER_NAME`  varchar(200) NOT NULL COMMENT '触发器名称',
    `TRIGGER_GROUP` varchar(200) NOT NULL COMMENT '触发器组',
    `BLOB_DATA`     blob COMMENT '保存triggers 一些信息',
    PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`),
    KEY             `SCHED_NAME` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
    CONSTRAINT `qrtz_blob_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='自定义触发器';


DROP TABLE IF EXISTS `qrtz_calendars`;

CREATE TABLE `qrtz_calendars`
(
    `SCHED_NAME`    varchar(120) NOT NULL COMMENT '计划名称',
    `CALENDAR_NAME` varchar(200) NOT NULL COMMENT '触发器名称',
    `CALENDAR`      blob         NOT NULL,
    PRIMARY KEY (`SCHED_NAME`, `CALENDAR_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='以 Blob 类型存储 Quartz 的 Calendar 信息';



DROP TABLE IF EXISTS `qrtz_cron_triggers`;

CREATE TABLE `qrtz_cron_triggers`
(
    `SCHED_NAME`      varchar(120) NOT NULL COMMENT '计划名称',
    `TRIGGER_NAME`    varchar(200) NOT NULL COMMENT '触发器名称',
    `TRIGGER_GROUP`   varchar(200) NOT NULL COMMENT '触发器组',
    `CRON_EXPRESSION` varchar(120) NOT NULL COMMENT '时间表达式',
    `TIME_ZONE_ID`    varchar(80) DEFAULT NULL COMMENT '时区ID',
    PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`),
    CONSTRAINT `qrtz_cron_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='存储 Cron Trigger，包括Cron表达式和时区信息';


DROP TABLE IF EXISTS `qrtz_fired_triggers`;

CREATE TABLE `qrtz_fired_triggers`
(
    `SCHED_NAME`        varchar(120) NOT NULL COMMENT '计划名称',
    `ENTRY_ID`          varchar(95)  NOT NULL COMMENT '组标识',
    `TRIGGER_NAME`      varchar(200) NOT NULL COMMENT '触发器名称',
    `TRIGGER_GROUP`     varchar(200) NOT NULL COMMENT '触发器组',
    `INSTANCE_NAME`     varchar(200) NOT NULL COMMENT '当前实例的名称',
    `FIRED_TIME`        bigint(13) NOT NULL COMMENT '当前执行时间',
    `SCHED_TIME`        bigint(13) NOT NULL COMMENT '计划时间',
    `PRIORITY`          int(11) NOT NULL COMMENT '权重',
    `STATE`             varchar(16)  NOT NULL COMMENT '状态',
    `JOB_NAME`          varchar(200) DEFAULT NULL COMMENT '作业名称',
    `JOB_GROUP`         varchar(200) DEFAULT NULL COMMENT '作业组',
    `IS_NONCONCURRENT`  varchar(1)   DEFAULT NULL COMMENT '是否并行',
    `REQUESTS_RECOVERY` varchar(1)   DEFAULT NULL COMMENT '是否要求唤醒',
    PRIMARY KEY (`SCHED_NAME`, `ENTRY_ID`),
    KEY                 `IDX_QRTZ_FT_TRIG_INST_NAME` (`SCHED_NAME`,`INSTANCE_NAME`),
    KEY                 `IDX_QRTZ_FT_INST_JOB_REQ_RCVRY` (`SCHED_NAME`,`INSTANCE_NAME`,`REQUESTS_RECOVERY`),
    KEY                 `IDX_QRTZ_FT_J_G` (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`),
    KEY                 `IDX_QRTZ_FT_JG` (`SCHED_NAME`,`JOB_GROUP`),
    KEY                 `IDX_QRTZ_FT_T_G` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
    KEY                 `IDX_QRTZ_FT_TG` (`SCHED_NAME`,`TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='存储与已触发的 Trigger 相关的状态信息，以及相联 Job的执行信息';



DROP TABLE IF EXISTS `qrtz_locks`;

CREATE TABLE `qrtz_locks`
(
    `SCHED_NAME` varchar(120) NOT NULL COMMENT '计划名称',
    `LOCK_NAME`  varchar(40)  NOT NULL COMMENT '锁名称',
    PRIMARY KEY (`SCHED_NAME`, `LOCK_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='存储程序的悲观锁的信息(假如使用了悲观锁)';



DROP TABLE IF EXISTS `qrtz_paused_trigger_grps`;

CREATE TABLE `qrtz_paused_trigger_grps`
(
    `SCHED_NAME`    varchar(120) NOT NULL COMMENT '计划名称',
    `TRIGGER_GROUP` varchar(200) NOT NULL COMMENT '触发器组',
    PRIMARY KEY (`SCHED_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='存储已暂停的 Trigger组的信息';


DROP TABLE IF EXISTS `qrtz_scheduler_state`;

CREATE TABLE `qrtz_scheduler_state`
(
    `SCHED_NAME`        varchar(120) NOT NULL COMMENT '计划名称',
    `INSTANCE_NAME`     varchar(200) NOT NULL COMMENT '实例名称',
    `LAST_CHECKIN_TIME` bigint(13) NOT NULL COMMENT '最后的检查时间',
    `CHECKIN_INTERVAL`  bigint(13) NOT NULL COMMENT '检查间隔',
    PRIMARY KEY (`SCHED_NAME`, `INSTANCE_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='存储少量的有关 Scheduler 的状态信息，和别的Scheduler实例(假如是用于一个集群中)';


DROP TABLE IF EXISTS `qrtz_simple_triggers`;

CREATE TABLE `qrtz_simple_triggers`
(
    `SCHED_NAME`      varchar(120) NOT NULL COMMENT '计划名称',
    `TRIGGER_NAME`    varchar(200) NOT NULL COMMENT '触发器名称',
    `TRIGGER_GROUP`   varchar(200) NOT NULL COMMENT '触发器组',
    `REPEAT_COUNT`    bigint(7) NOT NULL COMMENT '重复次数',
    `REPEAT_INTERVAL` bigint(12) NOT NULL COMMENT '触发次数',
    `TIMES_TRIGGERED` bigint(10) NOT NULL COMMENT '重复间隔',
    PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`),
    CONSTRAINT `qrtz_simple_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='存储简单的Trigger，包括重复次数，间隔，以及已触的次数';


DROP TABLE IF EXISTS `qrtz_simprop_triggers`;

CREATE TABLE `qrtz_simprop_triggers`
(
    `SCHED_NAME`    varchar(120) NOT NULL COMMENT '计划名称',
    `TRIGGER_NAME`  varchar(200) NOT NULL COMMENT '触发器名称',
    `TRIGGER_GROUP` varchar(200) NOT NULL COMMENT '触发器组',
    `STR_PROP_1`    varchar(512)   DEFAULT NULL COMMENT '根据不同的trigger类型存放各自的参数',
    `STR_PROP_2`    varchar(512)   DEFAULT NULL COMMENT '根据不同的trigger类型存放各自的参数',
    `STR_PROP_3`    varchar(512)   DEFAULT NULL COMMENT '根据不同的trigger类型存放各自的参数',
    `INT_PROP_1`    int(11) DEFAULT NULL COMMENT '根据不同的trigger类型存放各自的参数',
    `INT_PROP_2`    int(11) DEFAULT NULL COMMENT '根据不同的trigger类型存放各自的参数',
    `LONG_PROP_1`   bigint(20) DEFAULT NULL COMMENT '根据不同的trigger类型存放各自的参数',
    `LONG_PROP_2`   bigint(20) DEFAULT NULL COMMENT '根据不同的trigger类型存放各自的参数',
    `DEC_PROP_1`    decimal(13, 4) DEFAULT NULL COMMENT '根据不同的trigger类型存放各自的参数',
    `DEC_PROP_2`    decimal(13, 4) DEFAULT NULL COMMENT '根据不同的trigger类型存放各自的参数',
    `BOOL_PROP_1`   varchar(1)     DEFAULT NULL COMMENT '根据不同的trigger类型存放各自的参数',
    `BOOL_PROP_2`   varchar(1)     DEFAULT NULL COMMENT '根据不同的trigger类型存放各自的参数',
    PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`),
    CONSTRAINT `qrtz_simprop_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='存储CalendarIntervalTrigger和DailyTimeIntervalTrigger两种类型的触发器';


