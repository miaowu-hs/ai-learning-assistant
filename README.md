# AI Learning Assistant

基于 Spring Boot + Vue 3 构建的 AI 学习助手，围绕“文档学习 - 问答 - 练习 - 错题复练 - 学习报告”提供完整学习闭环。

## 项目结构

```text
ailearningassistant/
├─ backend/   # Spring Boot 后端
└─ front/     # Vue 3 + Vite 前端
```

## 核心功能

- 用户注册、登录、当前用户信息获取
- 文档上传、分页列表、详情、解析、摘要查看
- 基于文档的 RAG 问答
- 基于文档生成练习、提交答案、查看练习结果
- 错题本查看、删除、按文档重新生成练习
- 学习报告统计与建议展示

## 技术栈

### 后端

- Spring Boot 3
- Spring Security + JWT
- MyBatis-Plus
- MySQL
- Spring AI / DashScope compatible API

### 前端

- Vue 3
- Vite
- TypeScript
- Vue Router
- Pinia
- Axios
- Element Plus

## 本地启动

### 1. 启动后端

在 `backend/` 目录执行：

```bash
mvn spring-boot:run
```

默认端口：`http://localhost:8080`

### 2. 启动前端

在 `front/` 目录执行：

```bash
npm install
npm run dev
```

默认端口：`http://localhost:5173`

前端开发环境已配置 Vite 代理，请求会通过 `/api` 转发到本地后端。

## 说明

- 文档下载 / 在线预览接口当前未在后端开放，因此前端未实现独立预览页。
- 问答会话列表、练习列表、历史练习列表接口当前未开放，因此前端仅实现后端已有能力。
