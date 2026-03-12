<div align="center"> 

# 社区智慧养老服务系统（后端）

🚧 _注意：该项目尚未完善_ 🚧

> ℹ️ 本仓库为社区智慧养老服务系统后端仓库，访问前端仓库请点击[这里](https://github.com/TMyxGames/community-care-system-frontend)

</div>

---

## 💭 项目简介

本项目为作者的毕设项目，由作者一人独立开发，历经两个月的探索，最终完成了系统的搭建。 另外，本项目的作者是个编程新手，代码质量较低，如有问题敬请谅解 ๐°(৹˃﹏˂̵৹)°๐

#### 功能特色：

- **🔐 账户管理**

    提供**用户注册**、**登录**、**找回密码**、**修改头像**、**修改个人资料**等功能。基于角色权限将用户分为**家属**、**老人**、**社区服务人员**、
**系统管理员**四类角色。采用基于**SpringBoot Mail**的**邮箱验证码**与**JWT认证**保证账户安全。


- **📋 健康档案**

    提供**BMI**、**心率**、**血压**、**血糖**等体征数据的存储。采用 **Vue Data UI** 实现数据可视化展示，并接入 **DeepSeek API** 实现**健康数据分析**。


- **🏥 社区服务**

    提供社区服务项目的**浏览**与**预约**，以及对已完成的服务订单进行**评价**。


- **🚨 安全监控**

    接入**高德地图API**实现对老人的**实时定位**与**电子围栏**绘制。设计了**多级告警机制**并采用 **WebSocket** 实现老人越界实时告警推送。



- **📦 订单接收**

    为服务人员提供的**订单接收**功能，提供从**接收订单**、**开始服务**到**完成服务**的全流程追踪。设计了基于 **JTS** 的人员筛选方法实现**就近派单**。


- **⚙️ 后台管理**

    为系统管理员提供**一站式管理中心**。包含**文章管理**、**服务项目管理**、**服务人员管理**等核心管理功能。

## 🛠️ 技术栈

- **开发语言：** Java 21
- **核心框架：** SpringBoot 3.5.7
- **持久层框架：** MyBatis Plus
- **关系数据库：** MySQL 8.0.22
- **缓存数据库：** Redis
- **构建工具：** Maven 3.9.9

## 🌐 运行环境

- **操作系统：** Windows 10 / 11
- **IDE：** IntelliJ IDEA 2025.1.1.1
- **JDK：** Java 21 (Amazon Corretto 21.0.5)
- **关系数据库：** MySQL 8.0+
- **缓存数据库：** Redis 6.x+

## 📁 项目结构
```
community-care-system/                  # 项目根目录
├─ community-care-system-frontend/      # 前端项目
├─ community-care-system-backend/       # 后端项目
│   ├─ sql/script.sql                       # 数据库脚本
│   └─ src/main/                        
│       ├─ java/com.tmyx.backend/
│       │   ├─ common/                          # 公共类
│       │   ├─ config/                          # 配置类
│       │   ├─ handler/                         # ws处理
│       │   ├─ interceptor/                     # 拦截器
│       │   ├─ controller/                      # 控制器
│       │   ├─ service/                         # 服务
│       │   ├─ mapper/                          # 数据库映射
│       │   ├─ entity/                          # 实体类
│       │   ├─ dto/                             # 数据传输对象
│       │   ├─ vo/                              # 视图类
│       │   ├─ util/                            # 工具类
│       │   └─ BackendApplication.java          # 启动类
│       └─ resources/
│           ├─ application.properties           # 配置文件
│           ├─ Mapper/                          # 数据库映射
│           └─ static/                          # 静态资源
└─ uploads/                             # 上传文件保存目录
    ├─ user/                                # 用户
    │   └─ avatar/                              # 用户头像
    ├─ carousel/                            # 轮播图
    │   └─ images/                              # 轮播图图片
    ├─ article/                             # 文章
    │   └─ {articleId}/                         # 文章包
    │       ├─ index.md                         # 文章内容
    │       └─ images/                          # 文章图片
    └─ service/                             # 服务
        ├─ contents/                            # 服务详情
        └─ images/                              # 服务图片
```

## 🚀 开始

#### 一、创建数据库表
使用你喜欢的数据库管理工具运行项目根目录下sql文件夹内的`script.sql`脚本创建数据库表结构

#### 二、创建目录结构
创建一个文件夹，将前端项目和后端项目放进去：
```
community-care-system/                  # 项目根目录
├─ community-care-system-backend/       # 后端项目
└─ community-care-system-frontend/      # 前端项目
```
> ℹ️ `uploads` 目录在项目运行时将自动创建，内部的分类目录也会在相关功能被初次调用时自动创建，无需手动创建

#### 三、`application.properties`配置

**MySQL 连接配置**
- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`

**Redis 连接配置**
- `spring.data.redis.host`
- `spring.data.redis.port`
- `spring.data.redis.database`
- `spring.data.redis.password`

**服务器地址与端口**
- `server.port`
- `server.address`
> ℹ️ 如果你想在移动设备上访问，请将 `server.address` 设置为 `0.0.0.0`

**资源上传路径**
- `file.upload-path`
> ℹ️ 必须使用相对路径： `../uploads/`

**Spring Mail 配置**
- `spring.mail.host`
- `spring.mail.port`
- `spring.mail.username`
- `spring.mail.password`
- `spring.mail.protocol`
- `spring.mail.properties.mail.smtp.auth`
- `spring.mail.properties.mail.smtp.starttls.enable`
- `spring.mail.properties.mail.smtp.starttls.required`
- `spring.mail.properties.mail.display.sendmail`

**DeepSeek API (可选)**
- `deepseek.api.key`
> ⚠️ **健康数据分析功能**需要配置 **DeepSeek API** 密钥，如需使用请自行配置。

#### 四、启动项目

启动后端项目。

## 📄 未来打算

如果未来还有精力折腾这个项目的话（或许很难有x_x）会更新以下内容：

**健康档案**
- 查看历史健康数据
- 查看特定时间段健康数据
- 每周 / 每月自动生成健康周报 / 月报
- ~~接入智能穿戴设备实时采集健康数据~~

**消息**
- 增加系统通知、告警提醒消息
- 增加未读数角标
- 增加直接与用户对话的功能（在线联系客服、服务人员等）

**安全监控**
- 记录老人特定时间段内的活动轨迹

**社区服务**
- 增加服务存量机制（根据当前空闲服务人员数量计算存量）
- 增加提前预约功能

**订单接收**
- 优化接单机制（不再自动派单给服务人员）
- 增加安全码机制（防止）
- 增加异常情况处理机制