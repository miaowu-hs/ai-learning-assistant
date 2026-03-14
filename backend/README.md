# Backend

AI 学习助手后端，基于 Spring Boot 构建，负责认证、文档解析、问答、练习、错题本和学习报告等能力。

## 技术栈

- Spring Boot 3
- Spring Security + JWT
- MyBatis-Plus
- MySQL
- Spring AI

## 启动方式

```bash
mvn spring-boot:run
```

默认端口：`http://localhost:8080`

## 配置说明

- 数据库配置位于 `src/main/resources/application.yml`
- AI 能力默认读取 `DASHSCOPE_API_KEY` 等环境变量
- 上传文件默认保存在 `backend/uploads/`
