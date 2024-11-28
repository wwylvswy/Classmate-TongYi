# Classmate-TongYi

TongYi同学 是一款仿照 Kimi 智能助手开发的miniAPP，采用 Android 原生开发，开发语言为Java，支持与阿里云的[通义千问VL](https://help.aliyun.com/zh/model-studio/user-guide/vision?spm=a2c4g.11186623.0.0.74b04823VLt0YX#14d646f5a0owq)模型进行对话。应用提供了文本和图像两种消息类型的支持，并具备新建会话、历史会话恢复等核心功能。

## 特性

- **GPT 聊天**：用户可以与 GPT 进行对话，支持文本和图像消息。
- **历史会话管理**：能够查看和恢复历史会话，轻松回顾之前的对话内容。
- **会话管理**：支持创建新的会话、删除会话以及恢复历史会话。

## 技术栈

- **Android**：应用基于 Android 开发。
- **MVVM 架构**：采用 MVVM 架构模式，使代码更加模块化和可维护。
- **DataBinding**：用于简化 UI 与数据的绑定，提升开发效率。
- **Room**：用于本地存储，管理历史会话和消息。
- **Glide**：用于图片加载和缓存处理。

## 功能概述

1. **用户模块**：
    - 用户登录和注册

2. **会话模块**：
    - 新建会话
    - 查看和恢复历史会话

3. **聊天模块**：
    - 与 GPT 进行聊天
    - 支持文本和图像消息
    - 复制消息

## 开发环境运行

1. 获取通义千问的 [API Key](https://help.aliyun.com/zh/model-studio/developer-reference/get-api-key?spm=a2c4g.11186623.0.0.1967663fTkPuMQ)

2. [将API Key配置到环境变量](https://help.aliyun.com/zh/model-studio/developer-reference/configure-api-key-through-environment-variables?spm=a2c4g.11186623.0.0.196779809sfCW3#e4cd73d544i3r)

