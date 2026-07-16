-- ============================================================
-- 博客系统种子数据脚本
-- 用途：插入频道、管理员用户和演示文章
-- 执行方式：在 MySQL 中执行 source docs/seed_posts.sql
-- 注意：已存在的数据会被跳过（使用 INSERT IGNORE）
-- ============================================================

-- 1. 确保频道存在
INSERT IGNORE INTO mto_channel (id, name, key_, thumbnail, status, weight, updated) VALUES
(1, '技术', 'tech', '', 0, 1, NOW()),
(2, '生活', 'life', '', 0, 0, NOW()),
(3, '问答', 'qa',   '', 0, 0, NOW());

-- 2. 确保管理员用户存在（密码为 admin 的 MD5）
INSERT IGNORE INTO mto_user (id, username, password, avatar, name, gender, email, posts, comments, created, last_login, signature, status, updated) VALUES
(1, 'admin', '21232f297a57a5a743894a0e4a801fc3', '/dist/images/avatar/default.png', '管理员', 0, 'admin@mblog.com', 0, 0, NOW(), NOW(), '博客管理员', 0, NOW());

-- ============================================================
-- 3. 插入演示文章（mto_post + mto_post_attribute）
-- ============================================================

-- 文章1：Spring Boot 快速入门
INSERT IGNORE INTO mto_post (id, channel_id, title, summary, thumbnail, tags, author_id, created, comments, views, status, featured, weight, favors, updated) VALUES
(1, 1, 'Spring Boot 3 快速入门指南',
 '从零开始搭建你的第一个 Spring Boot 3 项目，涵盖项目创建、基础配置、热部署与第一个 REST 接口。',
 '', 'Spring Boot,Java,入门', 1, '2026-06-15 10:30:00', 3, 256, 0, 0, 0, 0, '2026-06-15 10:30:00');

INSERT IGNORE INTO mto_post_attribute (id, editor, content) VALUES
(1, 'markdown',
'## 前言

Spring Boot 是目前 Java 生态中最主流的微框架，它通过"约定优于配置"的理念，让开发者可以快速搭建生产级的 Spring 应用。本文将带你从零开始，创建一个 Spring Boot 3 项目。

## 环境准备

- JDK 17+
- Maven 3.8+ 或 Gradle 7+
- IDE：IntelliJ IDEA（推荐）或 VS Code

## 创建项目

### 方式一：Spring Initializr

访问 [start.spring.io](https://start.spring.io)，选择以下配置：

- Project：Maven
- Language：Java
- Spring Boot：3.2.x
- Dependencies：Spring Web, Spring Boot DevTools, Lombok

点击 Generate 下载后解压，用 IDEA 打开即可。

### 方式二：IDEA 内置工具

File → New → Project → Spring Initializr，配置同上。

## 编写第一个接口

```java
@RestController
@RequestMapping("/api")
public class HelloController {

    @GetMapping("/hello")
    public String hello(@RequestParam(defaultValue = "World") String name) {
        return "Hello, " + name + "!";
    }
}
```

启动项目后访问 `http://localhost:8080/api/hello?name=Spring`，页面返回 `Hello, Spring!`。

## 核心配置

`application.yml` 中常见的配置项：

```yaml
server:
  port: 8080

spring:
  application:
    name: my-app
  profiles:
    active: dev
```

## 热部署

引入 DevTools 依赖后，修改代码按 `Ctrl+F9`（IDEA）即可热重载，无需手动重启。

## 小结

本文介绍了 Spring Boot 3 项目的创建方式和基础配置。Spring Boot 的自动配置机制让我们可以专注于业务逻辑，而非繁琐的 XML 配置。后续文章将深入讲解数据库集成、安全认证等高级主题。');

-- 文章2：Java Stream API
INSERT IGNORE INTO mto_post (id, channel_id, title, summary, thumbnail, tags, author_id, created, comments, views, status, featured, weight, favors, updated) VALUES
(2, 1, 'Java 8 Stream API 实战详解',
 '深入理解 Stream 的核心操作：filter、map、reduce、collect，配合实战案例，写出更优雅的 Java 代码。',
 '', 'Java,Stream,函数式编程', 1, '2026-06-20 14:00:00', 5, 389, 0, 1, 0, 0, '2026-06-22 09:15:00');

INSERT IGNORE INTO mto_post_attribute (id, editor, content) VALUES
(2, 'markdown',
'## 为什么需要 Stream？

在 Java 8 之前，处理集合数据通常需要写大量的 for 循环和临时变量，代码冗长且容易出错。Stream API 的引入，让我们可以用声明式的方式处理数据。

## Stream 的核心概念

Stream 不是数据结构，它不存储数据。它更像是一个高级迭代器，提供了一系列流水线操作。

### 操作分类

| 类型 | 说明 | 示例 |
|------|------|------|
| 中间操作 | 返回新的 Stream，惰性执行 | `filter`, `map`, `sorted` |
| 终端操作 | 触发计算，关闭 Stream | `collect`, `forEach`, `reduce` |

## 常用操作实战

### filter — 过滤

```java
List<String> names = Arrays.asList("张三", "李四", "王五", "赵六");
List<String> result = names.stream()
    .filter(name -> name.startsWith("张"))
    .collect(Collectors.toList());
// 结果: ["张三"]
```

### map — 转换

```java
List<Integer> lengths = names.stream()
    .map(String::length)
    .collect(Collectors.toList());
// 结果: [2, 2, 2, 2]
```

### reduce — 归约

```java
int sum = IntStream.rangeClosed(1, 100)
    .reduce(0, Integer::sum);
// 结果: 5050
```

### collect — 收集

```java
Map<Integer, List<String>> grouped = names.stream()
    .collect(Collectors.groupingBy(String::length));
```

## 性能注意事项

- 对于简单循环（<1000 条），for 循环和 Stream 性能差异不大
- 对于大数据量，并行流 `parallelStream()` 可以显著提升性能
- 避免在 Stream 中修改外部变量，这违背了函数式编程的原则

## 小结

Stream API 让 Java 代码更加简洁和可读。掌握 `filter`、`map`、`reduce`、`collect` 这四个核心操作，就能应对 80% 的数据处理场景。');

-- 文章3：MySQL 索引优化
INSERT IGNORE INTO mto_post (id, channel_id, title, summary, thumbnail, tags, author_id, created, comments, views, status, featured, weight, favors, updated) VALUES
(3, 1, 'MySQL 索引优化实战：从原理到调优',
 '索引是数据库性能优化的核心。本文从 B+Tree 原理出发，讲解索引类型、最左前缀原则、覆盖索引与慢查询优化。',
 '', 'MySQL,数据库,索引,性能优化', 1, '2026-06-25 08:45:00', 7, 512, 0, 1, 0, 0, '2026-07-01 11:20:00');

INSERT IGNORE INTO mto_post_attribute (id, editor, content) VALUES
(3, 'markdown',
'## 索引的本质

索引是帮助 MySQL 高效获取数据的**排好序的数据结构**。MySQL 默认使用 InnoDB 引擎，其索引结构为 B+Tree。

## B+Tree 的核心特点

1. 非叶子节点只存储键值，不存储数据
2. 叶子节点包含所有键值和数据，且通过双向链表连接
3. 所有查询最终都会落到叶子节点

这意味着 B+Tree 的高度通常只有 2-4 层，每次查询只需 2-4 次磁盘 IO。

## 索引类型

### 主键索引（聚簇索引）

叶子节点存储的是整行数据。InnoDB 表必须有一个主键，建议使用自增 ID。

### 普通索引（二级索引）

叶子节点存储的是主键值。查询时需要**回表**——先通过二级索引找到主键，再通过主键索引找到完整数据。

## 最左前缀原则

对于联合索引 `(a, b, c)`：

```sql
-- 走索引
WHERE a = 1
WHERE a = 1 AND b = 2
WHERE a = 1 AND b = 2 AND c = 3

-- 不走索引
WHERE b = 2          -- 跳过了 a
WHERE a = 1 AND c = 3 -- 跳过了 b
```

## 覆盖索引

如果查询的列都在索引中，则不需要回表，性能大幅提升。

```sql
-- 假设有联合索引 (name, age)
SELECT name, age FROM users WHERE name = ''张三'';  -- 覆盖索引，无需回表
SELECT * FROM users WHERE name = ''张三'';           -- 需要回表
```

## 慢查询优化思路

1. 开启慢查询日志：`SET GLOBAL slow_query_log = ON;`
2. 使用 `EXPLAIN` 分析执行计划，关注 `type` 字段（至少达到 `range` 级别）
3. 检查是否走了索引，避免 `filesort` 和 `temporary`
4. 对于大数据量分页，使用"延迟关联"优化

## 小结

索引优化的核心是减少磁盘 IO。理解 B+Tree 的结构和最左前缀原则，是写好 SQL 的基础。记住：**索引不是越多越好**，每个索引都会增加写操作的开销。');

-- 文章4：Vue3 组合式 API
INSERT IGNORE INTO mto_post (id, channel_id, title, summary, thumbnail, tags, author_id, created, comments, views, status, featured, weight, favors, updated) VALUES
(4, 1, 'Vue 3 组合式 API 入门与实践',
 '从 Options API 到 Composition API 的平滑过渡指南，详解 ref、reactive、computed、watch 和生命周期钩子。',
 '', 'Vue,前端,JavaScript,Composition API', 1, '2026-06-28 16:00:00', 4, 298, 0, 0, 0, 0, '2026-06-28 16:00:00');

INSERT IGNORE INTO mto_post_attribute (id, editor, content) VALUES
(4, 'markdown',
'## 为什么需要组合式 API？

Vue 2 的 Options API（`data`、`methods`、`computed`）在小型组件中很直观，但在大型组件中，同一逻辑关注点的代码被分散在不同选项中，维护困难。

Vue 3 的 Composition API 允许我们将相关逻辑组织在一起，并且可以轻松地在组件间复用。

## setup 函数

`setup` 是组合式 API 的入口，在组件创建之前执行：

```vue
<script>
import { ref } from ''vue''

export default {
  setup() {
    const count = ref(0)
    const increment = () => count.value++

    return { count, increment }
  }
}
</script>

<template>
  <button @click="increment">{{ count }}</button>
</template>
```

## 响应式核心

| API | 用途 | 访问方式 |
|-----|------|----------|
| `ref()` | 基本类型响应式 | `.value` |
| `reactive()` | 对象响应式 | 直接访问属性 |
| `computed()` | 计算属性 | `.value` |
| `watch()` | 监听变化 | — |

## 实战示例：搜索组件

```vue
<script setup>
import { ref, watch } from ''vue''

const keyword = ref('''')
const results = ref([])

watch(keyword, async (newVal) => {
  if (newVal.length < 2) return
  const res = await fetch(`/api/search?q=${newVal}`)
  results.value = await res.json()
})
</script>
```

## 与 Options API 的对比

| 维度 | Options API | Composition API |
|------|------------|-----------------|
| 逻辑组织 | 按选项类型分散 | 按功能聚合 |
| 逻辑复用 | Mixin（命名冲突） | 自定义 Hook |
| TypeScript 支持 | 一般 | 优秀 |
| 学习曲线 | 低 | 中 |

## 小结

Composition API 不是要取代 Options API，两者可以共存。对于新项目，建议使用 `<script setup>` + Composition API，代码更简洁，TypeScript 支持更好。');

-- 文章5：Git 常用命令速查
INSERT IGNORE INTO mto_post (id, channel_id, title, summary, thumbnail, tags, author_id, created, comments, views, status, featured, weight, favors, updated) VALUES
(5, 1, 'Git 常用命令速查手册',
 '整理日常开发中最常用的 Git 命令，涵盖分支管理、版本回退、合并冲突解决与远程仓库操作。',
 '', 'Git,版本控制,命令行', 1, '2026-07-02 09:00:00', 2, 178, 0, 0, 0, 0, '2026-07-02 09:00:00');

INSERT IGNORE INTO mto_post_attribute (id, editor, content) VALUES
(5, 'markdown',
'## 基础配置

```bash
git config --global user.name "Your Name"
git config --global user.email "email@example.com"
git config --global core.editor "code --wait"  # 设置 VS Code 为默认编辑器
```

## 日常工作流

```bash
git status              # 查看当前状态
git add <file>          # 暂存文件
git add -p              # 交互式暂存（推荐）
git commit -m "message" # 提交
git push origin main    # 推送到远程
git pull origin main    # 拉取远程更新
```

## 分支操作

```bash
git branch                  # 查看本地分支
git branch -r               # 查看远程分支
git branch feature-login    # 创建分支
git checkout feature-login  # 切换分支
git checkout -b feature-pay # 创建并切换
git merge feature-login     # 合并分支
git branch -d feature-login # 删除本地分支
```

## 版本回退

```bash
git log --oneline           # 查看提交日志
git reset --soft HEAD~1     # 撤销 commit，保留修改
git reset --hard HEAD~1     # 撤销 commit，丢弃修改
git revert <commit-hash>    # 创建新 commit 来撤销（安全）
```

## 暂存与恢复

```bash
git stash                   # 暂存当前修改
git stash list              # 查看暂存列表
git stash pop               # 恢复最近暂存
git stash drop              # 删除最近暂存
```

## 合并冲突解决

冲突发生时，Git 会在文件中标记冲突区域：

```
<<<<<<< HEAD
你的修改
=======
他人的修改
>>>>>>> feature-branch
```

手动编辑后，执行 `git add` 和 `git commit` 完成合并。

## 实用技巧

```bash
git log --oneline --graph --all  # 可视化分支图
git cherry-pick <commit-hash>    # 摘取某个提交
git rebase -i HEAD~3             # 交互式合并最近 3 个提交
git blame <file>                 # 查看每行代码的作者
```

## 小结

Git 命令不需要死记硬背，遇到问题查手册即可。建议使用 `git log --oneline --graph` 养成查看历史的习惯，理解分支拓扑图是解决大多数问题的关键。');

-- 文章6：Docker 部署实战
INSERT IGNORE INTO mto_post (id, channel_id, title, summary, thumbnail, tags, author_id, created, comments, views, status, featured, weight, favors, updated) VALUES
(6, 1, 'Docker 容器化部署 Spring Boot 应用',
 '从编写 Dockerfile 到使用 Docker Compose 编排多服务，完整演示 Spring Boot + MySQL + Redis 的容器化部署流程。',
 '', 'Docker,Spring Boot,DevOps,部署', 1, '2026-07-05 11:00:00', 6, 432, 0, 1, 0, 0, '2026-07-06 15:30:00');

INSERT IGNORE INTO mto_post_attribute (id, editor, content) VALUES
(6, 'markdown',
'## 为什么需要容器化？

传统部署方式中，"在我机器上能跑"是永恒的痛点。Docker 将应用和环境打包在一起，确保在任何机器上运行结果一致。

## 编写 Dockerfile

```dockerfile
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 优化技巧

1. 使用多阶段构建减小镜像体积
2. 基础镜像选择 Alpine 版本（体积小）
3. 将不常变的依赖层放在前面，利用 Docker 缓存

## Docker Compose 编排

```yaml
version: ''3.8''
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: db_mblog
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"

  app:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - mysql
      - redis
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/db_mblog

volumes:
  mysql_data:
```

## 常用命令

```bash
docker build -t mblog:latest .           # 构建镜像
docker-compose up -d                      # 启动所有服务
docker-compose logs -f app               # 查看应用日志
docker-compose down                       # 停止并删除容器
docker exec -it <container> sh           # 进入容器
```

## 生产环境注意事项

- 不要将敏感信息写在 Dockerfile 或 docker-compose.yml 中
- 使用 `.env` 文件管理环境变量
- 配置健康检查：`HEALTHCHECK` 指令
- 限制容器资源：`deploy.resources.limits`

## 小结

Docker 让部署变得简单可靠。对于小型项目，Docker Compose 足以满足需求；对于大型项目，可以进一步学习 Kubernetes 进行容器编排。');

-- 文章7：RESTful API 设计
INSERT IGNORE INTO mto_post (id, channel_id, title, summary, thumbnail, tags, author_id, created, comments, views, status, featured, weight, favors, updated) VALUES
(7, 1, 'RESTful API 设计最佳实践',
 '从 URL 命名、HTTP 方法选择、状态码使用到版本管理和错误处理，系统梳理 RESTful API 设计规范。',
 '', 'RESTful,API,设计规范,后端', 1, '2026-07-08 13:00:00', 3, 201, 0, 0, 0, 0, '2026-07-08 13:00:00');

INSERT IGNORE INTO mto_post_attribute (id, editor, content) VALUES
(7, 'markdown',
'## 核心原则

REST（Representational State Transfer）是一种架构风格，不是标准。设计良好的 RESTful API 应当遵循以下原则：

1. **资源导向**：URL 表示资源，而非操作
2. **无状态**：每个请求包含所有必要信息
3. **统一接口**：使用标准 HTTP 方法

## URL 设计规范

```
GET    /api/posts          # 获取文章列表
GET    /api/posts/1        # 获取单篇文章
POST   /api/posts          # 创建文章
PUT    /api/posts/1        # 更新文章（全量）
PATCH  /api/posts/1        # 更新文章（部分）
DELETE /api/posts/1        # 删除文章
```

### 命名约定

- 使用名词复数形式：`/api/users` 而非 `/api/getUsers`
- 层级关系用路径表示：`/api/posts/1/comments`
- 使用短横线分隔：`/api/post-categories` 而非 `/api/postCategories`
- 避免深层嵌套（不超过 3 层）

## HTTP 状态码

| 状态码 | 含义 | 使用场景 |
|--------|------|----------|
| 200 | OK | 请求成功 |
| 201 | Created | 资源创建成功 |
| 204 | No Content | 删除成功 |
| 400 | Bad Request | 参数校验失败 |
| 401 | Unauthorized | 未认证 |
| 403 | Forbidden | 无权限 |
| 404 | Not Found | 资源不存在 |
| 500 | Internal Server Error | 服务器内部错误 |

## 统一响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

## 分页与过滤

```
GET /api/posts?page=1&size=20&sort=created,desc&channel=tech
```

## 版本管理

推荐方式：URL 前缀版本号

```
GET /api/v1/posts
GET /api/v2/posts
```

## 小结

好的 API 设计应该让调用方感觉"自然"——不需要查文档就能猜出用法。统一的命名、一致的响应格式和清晰的错误信息，是 API 设计的基础功。');

-- 文章8：Redis 缓存实战
INSERT IGNORE INTO mto_post (id, channel_id, title, summary, thumbnail, tags, author_id, created, comments, views, status, featured, weight, favors, updated) VALUES
(8, 1, 'Redis 缓存实战：从入门到高可用',
 '全面介绍 Redis 核心数据结构、缓存策略（Cache-Aside、Read/Write Through）、缓存穿透/击穿/雪崩的解决方案。',
 '', 'Redis,缓存,性能优化,NoSQL', 1, '2026-07-10 10:00:00', 8, 567, 0, 1, 0, 0, '2026-07-12 08:40:00');

INSERT IGNORE INTO mto_post_attribute (id, editor, content) VALUES
(8, 'markdown',
'## Redis 是什么？

Redis（Remote Dictionary Server）是一个基于内存的高性能键值数据库。它支持丰富的数据结构，广泛用于缓存、消息队列、排行榜等场景。

## 核心数据结构

| 类型 | 适用场景 | 常用命令 |
|------|----------|----------|
| String | 缓存、计数器 | `SET`, `GET`, `INCR` |
| Hash | 对象存储 | `HSET`, `HGET`, `HGETALL` |
| List | 消息队列、时间线 | `LPUSH`, `RPOP`, `LRANGE` |
| Set | 标签、去重 | `SADD`, `SMEMBERS`, `SINTER` |
| Sorted Set | 排行榜 | `ZADD`, `ZRANGE`, `ZREVRANK` |

## 缓存策略

### Cache-Aside（旁路缓存）

```
读：先查缓存 → 命中返回，未命中查 DB → 写缓存 → 返回
写：先更新 DB → 删除缓存
```

这是最常用的策略，实现简单，但存在短暂的数据不一致窗口。

### Read/Write Through

缓存层代理所有数据库操作，应用只与缓存交互。

## 三大缓存问题

### 缓存穿透

查询不存在的数据，请求直接打到数据库。

**解决方案**：
- 布隆过滤器（Bloom Filter）
- 缓存空值（设置短过期时间）

### 缓存击穿

热点 key 过期，大量并发请求同时打到数据库。

**解决方案**：
- 互斥锁：只让一个线程去加载数据
- 逻辑过期（永不过期 + 异步刷新）

### 缓存雪崩

大量 key 同时过期，数据库瞬间承受巨大压力。

**解决方案**：
- 过期时间加随机值
- 多级缓存（本地缓存 + Redis）
- 限流降级

## Spring Boot 集成

```java
@Cacheable(value = "posts", key = "#id")
public Post getPostById(Long id) {
    return postRepository.findById(id).orElse(null);
}
```

## 小结

Redis 是后端开发必备技能。掌握五种基础数据结构和三种缓存问题的解决方案，就能应对大多数场景。使用时注意设置合理的过期时间和内存淘汰策略。');

-- 文章9：Linux 常用命令
INSERT IGNORE INTO mto_post (id, channel_id, title, summary, thumbnail, tags, author_id, created, comments, views, status, featured, weight, favors, updated) VALUES
(9, 1, 'Linux 常用命令速查：开发必备',
 '精选开发中最常用的 Linux 命令，涵盖文件操作、进程管理、文本处理、网络诊断和权限管理。',
 '', 'Linux,命令行,运维', 1, '2026-07-12 15:00:00', 1, 145, 0, 0, 0, 0, '2026-07-12 15:00:00');

INSERT IGNORE INTO mto_post_attribute (id, editor, content) VALUES
(9, 'markdown',
'## 文件操作

```bash
ls -la              # 列出所有文件（含隐藏文件）
cd /path/to/dir     # 切换目录
pwd                 # 显示当前路径
mkdir -p a/b/c      # 递归创建目录
cp -r src dest      # 递归复制
mv old new          # 移动/重命名
rm -rf dir          # 强制递归删除（慎用）
find . -name "*.java"  # 查找文件
```

## 文件内容查看

```bash
cat file.txt        # 查看全部内容
head -n 20 file.txt # 查看前 20 行
tail -f app.log     # 实时跟踪日志
less file.txt       # 分页查看（支持搜索）
wc -l file.txt      # 统计行数
```

## 文本处理三剑客

```bash
# grep — 文本搜索
grep "ERROR" app.log                    # 搜索 ERROR
grep -r "TODO" src/                     # 递归搜索
grep -v "DEBUG" app.log                 # 排除 DEBUG

# sed — 文本替换
sed -i ''s/foo/bar/g'' file.txt          # 替换所有 foo 为 bar

# awk — 文本分析
awk ''{print $1, $3}'' access.log       # 打印第 1、3 列
```

## 进程与系统

```bash
ps aux | grep java      # 查找 Java 进程
top                     # 实时进程监控
htop                    # 更友好的 top（需安装）
kill -9 <PID>           # 强制终止进程
df -h                   # 磁盘使用情况
free -h                 # 内存使用情况
```

## 网络诊断

```bash
ping baidu.com              # 测试连通性
curl -I https://api.com    # 查看响应头
netstat -tlnp | grep 8080  # 查看端口占用
lsof -i :8080               # 查看端口被哪个进程占用
```

## 权限管理

```bash
chmod 755 script.sh     # rwxr-xr-x
chown user:group file   # 修改所属用户和组
sudo <command>          # 以 root 身份执行
```

## 小结

不需要记住所有命令，掌握 `man <command>` 和 `--help` 的用法更重要。建议把常用命令做成 alias，提高效率。');

-- 文章10：前端性能优化
INSERT IGNORE INTO mto_post (id, channel_id, title, summary, thumbnail, tags, author_id, created, comments, views, status, featured, weight, favors, updated) VALUES
(10, 1, '前端性能优化实战策略',
 '从网络、渲染、构建三个层面，介绍前端性能优化的核心策略：代码分割、懒加载、CDN、缓存策略与关键渲染路径优化。',
 '', '前端,性能优化,Webpack,Vite', 1, '2026-07-13 17:00:00', 4, 220, 0, 0, 0, 0, '2026-07-14 09:00:00');

INSERT IGNORE INTO mto_post_attribute (id, editor, content) VALUES
(10, 'markdown',
'## 性能优化的重要性

Google 研究表明，页面加载时间超过 3 秒，53% 的移动用户会离开。性能直接影响用户体验和 SEO 排名。

## 核心指标

- **FCP**（First Contentful Paint）：首次内容绘制
- **LCP**（Largest Contentful Paint）：最大内容绘制，应 < 2.5s
- **FID**（First Input Delay）：首次输入延迟，应 < 100ms
- **CLS**（Cumulative Layout Shift）：累计布局偏移，应 < 0.1

## 网络层面

### 资源压缩

```javascript
// vite.config.js — 启用 Gzip 压缩
import viteCompression from ''vite-plugin-compression''

export default {
  plugins: [viteCompression()]
}
```

### CDN 加速

静态资源（JS、CSS、图片）部署到 CDN，利用边缘节点加速访问。

### HTTP 缓存

```
Cache-Control: max-age=31536000  # 强缓存，一年
ETag: "abc123"                    # 协商缓存标识
```

## 渲染层面

### 图片优化

- 使用 WebP 格式（体积比 JPEG 小 25%-35%）
- 响应式图片：`<img srcset="..." sizes="...">`
- 图片懒加载：`<img loading="lazy">`

### 代码分割

```javascript
// 路由懒加载
const PostPage = () => import(''./views/PostPage.vue'')
```

### 虚拟列表

对于长列表（>1000 项），只渲染可视区域内的 DOM 节点。

## 构建层面

### Tree Shaking

确保使用 ES Module 语法（`import/export`），打包工具会自动删除未使用的代码。

### 依赖分析

```bash
npx vite-bundle-visualizer  # 可视化分析包体积
```

## 小结

性能优化是持续的过程，建议在项目中集成 Lighthouse CI，在每次 PR 时自动检测性能回归。优先优化 LCP 和 CLS，这两个指标对用户体验影响最大。');

-- ============================================================
-- 更新用户的文章计数
-- ============================================================
UPDATE mto_user SET posts = 10 WHERE id = 1;

-- ============================================================
-- 执行完毕
-- ============================================================
SELECT '种子数据插入完成！' AS message;
SELECT COUNT(*) AS total_posts FROM mto_post WHERE status = 0;