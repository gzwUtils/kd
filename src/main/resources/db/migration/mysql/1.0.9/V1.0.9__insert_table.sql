-- 公告表
CREATE TABLE `notice` (
    `id`            bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `context`       text            NOT NULL COMMENT '公告内容',
    `operator_name` varchar(50)     NOT NULL DEFAULT '' COMMENT '操作人',
    `create_time`   datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       tinyint(1)      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-正常 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = '公告表';