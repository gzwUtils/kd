<h1 align="center">kd内部学习系统</h1>

基于 Spring Boot 的企业级内部学习平台，集成多种常用中间件与工具，涵盖数据脱敏、加解密、消息队列、定时任务、工作流等核心功能，适用于企业内部培训、知识管理与技术学习。


📦 技术栈
类别	技术组件
核心框架	Spring Boot
数据存储	Redis, Elasticsearch, MySQL（多数据源）
数据同步	Canal
消息队列	RocketMQ
定时任务	Quartz, Spring Schedule, XXL-Job
工作流	Flowable
模板引擎	Freemarker
接口文档	Swagger
数据库版本管理	Flyway
即时通讯	WebSocket
工具与集成	SM3/SM4 加解密、邮件发送、短信服务、文件上传/下载、Excel导入导出


✨ 核心功能
1. 数据安全与加解密
   敏感数据脱敏处理

国密算法支持（SM3 哈希、SM4 对称加解密）

2. Redis 高级应用
   分布式锁实现

自动重试机制

滑动窗口限流

3. 文件与数据操作
   文件上传与下载

Excel 导入导出

异步/同步处理支持

4. 数据同步与搜索
   基于 Canal 实现 MySQL 到 Elasticsearch/Redis 的数据同步

Elasticsearch 高级查询

5. 多数据源与数据库管理
   动态数据源切换

Flyway 数据库版本迁移

6. 消息与通讯
   RocketMQ 消息队列配置与使用

WebSocket 客户端/服务端双向通信

邮件与短信发送集成

7. 定时任务管理
   Timer

Spring Schedule

Quartz

XXL-Job 分布式任务调度

8. 模板与工作流
   Freemarker 动态数据渲染

Flowable 工作流引擎集成

9. 消息推送与微信生态
   实时消息推送

微信公众号与小程序对接支持



🚀 快速开始
环境准备
JDK 8+

Maven 3.6+

MySQL 5.7+

Redis 6+

Elasticsearch 7.x

启动步骤
克隆项目：

bash
git clone https://github.com/gzwUtils/kd.git
导入数据库脚本（Flyway 自动执行）

修改 application.yml 中的相关配置（数据源、Redis、ES 等）
