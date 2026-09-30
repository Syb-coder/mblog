# mblog

一个基于 Spring Boot 的轻量级博客系统，支持 Markdown 写作、文章管理、评论、用户注册和后台配置，适合作为个人博客、Java Web 学习及二次开发的起点。

## 功能概览

- Markdown 文章编辑与渲染
- 文章、分类、标签及评论管理
- 用户注册、登录和后台管理
- 图片/文件上传
- 邮件相关配置与 OAuth 扩展
- MySQL 生产配置与 H2 本地开发配置
- Docker Compose 一键启动
- 通过 AdminLTE、PJAX 等前端资源提供管理界面

## 技术栈

- Java 17+
- Spring Boot 3.5
- Maven
- Apache Shiro
- MySQL / H2
- CommonMark、Jsoup、Jackson、FastJSON2
- Thymeleaf、HTML、JavaScript
- Docker Compose（可选）

## 环境要求

- JDK 17 或更高版本
- Maven 3.8+（或使用 IDE 内置 Maven）
- MySQL 8（使用 MySQL 配置时）
- Docker Desktop（可选）

## 快速开始

### 方式一：本地运行

```bash
git clone https://github.com/Syb-coder/mblog.git
cd mblog
```

使用 IntelliJ IDEA 打开项目，等待 Maven 依赖下载完成，然后运行 `com.sunblog.BootApplication` 的 `main` 方法。启动后访问：

- 前台：http://localhost:8080
- 后台：http://localhost:8080/admin

默认管理员账号信息请以当前配置和初始化脚本为准；首次运行后请立即修改默认密码。

### 方式二：Maven 启动

```bash
./mvnw spring-boot:run
# Windows
mvnw.cmd spring-boot:run
```

也可以执行：

```bash
./mvnw clean package
java -jar target/*.jar
```

### 方式三：Docker Compose

```bash
docker compose up -d
docker compose logs -f server
```

停止服务：

```bash
docker compose down
```

Docker 配置和镜像行为请以 `docker-compose.yml` 为准；修改源码后如需重建，可使用 `docker compose up -d --build`。

## 数据库配置

本地开发可使用 H2 profile；使用 MySQL 时，先创建 `db_mblog` 数据库，并修改 `src/main/resources/application-mysql.yml` 中的连接信息：

```yaml
spring.datasource.url: jdbc:mysql://localhost/db_mblog?useSSL=false&characterEncoding=utf8&serverTimezone=GMT%2B8
spring.datasource.username: your_username
spring.datasource.password: your_password
```

不要将真实密码、邮件凭据或 OAuth 密钥提交到 Git。更多配置说明请查看 [`docs/`](docs/)：

- [快速开始](docs/getting-started.md)
- [基础配置](docs/base.md)
- [上传配置](docs/upload.md)
- [OAuth 配置](docs/oauth.md)
- [邮件配置](docs/email.md)

## 开发说明

项目源码位于 `src/`，数据库初始化脚本及产品文档位于 `docs/`。配置注册、发布、评论等开关可在 `application.yml` 中调整，站点运行后的更多选项可通过后台配置页面管理。

## 许可证

本项目包含 [`LICENSE`](LICENSE)。使用、修改和分发时请遵循许可证及项目所使用第三方组件的许可条款。
