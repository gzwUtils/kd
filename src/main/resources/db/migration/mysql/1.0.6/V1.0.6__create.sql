create table if not exists doc.message_template
(
    id               bigint auto_increment
    primary key,
    name             varchar(100)  default ''  not null comment '标题',
    msg_status       tinyint       default 0   not null comment '当前消息状态：10.新建 20.停用 30.启用 40.等待发送 50.发送中 60.发送成功 70.发送失败',
    cron_task_id     bigint                    null comment '定时任务Id (xxl-job-admin返回)',
    cron_crowd_path  varchar(500)              null comment '定时发送人群的文件路径',
    expect_push_time varchar(100)              null comment '期望发送时间：0:立即发送 定时任务以及周期任务:cron表达式',
    id_type          tinyint       default 0   not null comment '消息的发送ID类型：10. userId 20.did 30.手机号 40.openId 50.email 60.企业微信userId',
    send_channel     int           default 0   not null comment '消息发送渠道：10.IM 20.Push 30.短信 40.Email 50.公众号 60.小程序 70.企业微信 80.钉钉机器人 90.钉钉工作通知 100.企业微信机器人 110.飞书机器人 110. 飞书应用消息 ',
    template_type    tinyint       default 0   not null comment '10.运营类 20.技术类接口调用',
    msg_type         tinyint       default 0   not null comment '10.通知类消息 20.营销类消息 30.验证码类消息',
    shield_type      tinyint       default 0   not null comment '10.夜间不屏蔽 20.夜间屏蔽 30.夜间屏蔽(次日早上9点发送)',
    msg_content      varchar(4096) default ''  not null comment '消息内容 占位符用{$var}表示',
    send_account     varchar(20)   default '0' not null comment '发送账号 一个渠道下可存在多个账号',
    creator          varchar(45)   default ''  not null comment '创建者',
    updator          varchar(45)   default ''  null comment '更新者',
    is_deleted       tinyint       default 0   not null comment '是否删除：0.不删除 1.删除',
    created          int           default 0   not null comment '创建时间',
    updated          int           default 0   null comment '更新时间'
    )
    comment '消息模板信息' collate = utf8mb4_unicode_ci;

create index idx_channel
    on doc.message_template (send_channel);

create table if not exists doc.learn
(
    id     int(10)      null,
    name   varchar(400) not null,
    `desc` varchar(400) null
    );

create table if not exists doc.llll
(
    id           int(1) auto_increment comment '主键自增id'
    primary key,
    id_name      varchar(200)  null comment 'number',
    English_id   varchar(30)   null comment 'English',
    name         varchar(30)   null comment '名称',
    nn_date      date          null comment '日期',
    home_page    varchar(100)  null comment '主页',
    brief        varchar(1000) null comment '简介',
    introduction varchar(1000) null comment '详情',
    reading      int           null comment '阅读量',
    engageMent   int           null comment '互动',
    fans         int           null comment '粉丝',
    sex          char(2)       null comment '性别'
    );

