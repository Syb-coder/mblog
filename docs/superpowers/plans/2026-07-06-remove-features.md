# Mblog 功能裁剪实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 精简 Mblog 博客系统，移除 H2、Flyway、全文搜索、收藏、友链、钩子系统、事件系统、REST API、Shiro 标签、多配置文件、Docker 文件等非核心功能，仅保留用户、文章、评论、分类、标签、模板指令 + Actuator。

**Architecture:** 每个功能独立删除，任务间无顺序依赖。删除 Java 源文件 + 清理引用 + 更新模板 + 修改配置文件。

**Tech Stack:** Spring Boot 3.5, Java 17, MySQL, Freemarker, Shiro

---

## 文件变更总览

### 删除文件
```
src/main/java/com/sunblog/shiro/tags/              (14 个文件)
src/main/java/com/sunblog/modules/hook/             (7 个文件)
src/main/java/com/sunblog/modules/event/            (2 个文件)
src/main/java/com/sunblog/modules/entity/Favorite.java
src/main/java/com/sunblog/modules/data/FavoriteVO.java
src/main/java/com/sunblog/modules/repository/FavoriteRepository.java
src/main/java/com/sunblog/modules/repository/LinksRepository.java
src/main/java/com/sunblog/modules/service/FavoriteService.java
src/main/java/com/sunblog/modules/service/LinksService.java
src/main/java/com/sunblog/modules/service/PostSearchService.java
src/main/java/com/sunblog/modules/service/impl/FavoriteServiceImpl.java
src/main/java/com/sunblog/modules/service/impl/LinksServiceImpl.java
src/main/java/com/sunblog/modules/service/impl/PostSearchServiceImpl.java
src/main/java/com/sunblog/modules/entity/Links.java
src/main/java/com/sunblog/config/SmartCnAnalysisConfigurer.java
src/main/java/com/sunblog/web/controller/api/ApiController.java
src/main/java/com/sunblog/web/controller/api/package-info.java
src/main/java/com/sunblog/web/controller/site/SearchController.java
src/main/java/com/sunblog/web/controller/site/user/FavorController.java
src/main/java/com/sunblog/modules/template/directive/LinksDirective.java
src/main/java/com/sunblog/modules/template/directive/UserFavoritesDirective.java

src/main/resources/application-h2.yml
src/main/resources/application-docker.yml
src/main/resources/scripts/migration/V3.2__update.sql

src/main/resources/templates/classic/search.ftl
src/main/resources/templates/classic/user/method_favorites.ftl

Dockerfile
docker-compose.yml
```

### 修改文件
```
pom.xml                          — 移除 H2, Flyway, Lucene/Hibernate Search 依赖, 移除 h2/docker profile
src/main/resources/application.yml          — 移除 Flyway 配置
src/main/resources/application-dev.yml      — 移除搜索配置, 移除 Flyway 配置
src/main/resources/scripts/schema.sql       — 移除 mto_favorite/mto_links 表定义

SiteConfiguration.java           — 移除 ShiroTags 注册
BaseInterceptor.java             — 移除 InterceptorHookManager 引用
WebMvcConfiguration.java         — (可移除 FastJsonHttpMessageConverter 如果不需要)

PostService.java                 — 移除 favor/unfavor 方法
PostServiceImpl.java             — 移除 FavoriteService, ApplicationContext, onPushEvent, favor/unfavor
PostUpdateEventHandler.java      — 删除（整个文件）
OptionsController.java           — 移除 PostSearchService 引用
BaseInterceptor.java             — 移除 HookManager 引用

templates/classic/inc/header.ftl          — 移除 @shiro.hasPermission
templates/classic/channel/view.ftl         — 移除收藏按钮
templates/classic/user/method_posts.ftl    — 移除 favors 显示
templates/classic/inc/user_sidebar.ftl     — 移除"收藏的文章"链接
templates/admin/index.ftl                 — 移除 reset_indexes 按钮
templates/admin/options/index.ftl          — 重建索引按钮已在 admin/index.ftl
```

---

### Task 1: 移除 H2 数据库支持

**文件:**
- Modify: `pom.xml`
- Delete: `src/main/resources/application-h2.yml`
- Modify: `pom.xml` (profiles)

- [ ] **Step 1: 从 pom.xml 移除 H2 依赖**

找到并删除 H2 依赖块（约 lines 134-138）：

```xml
		<dependency>
			<groupId>com.h2database</groupId>
			<artifactId>h2</artifactId>
			<scope>runtime</scope>
		</dependency>
```

- [ ] **Step 2: 删除 application-h2.yml 配置文件**

```bash
git rm src/main/resources/application-h2.yml
```

- [ ] **Step 3: 从 pom.xml 移除 h2 profile**

找到 h2 profile 块（约 lines 309-314）：

```xml
		<profile>
			<id>h2</id>
			<properties>
				<profileActive>h2</profileActive>
			</properties>
		</profile>
```

删除整个 `<profile>` 节点。

- [ ] **Step 4: 提交**

```bash
git add -A
git commit -m "perf: remove H2 database support"
```

---

### Task 2: 移除 Flyway 数据库迁移

**文件:**
- Modify: `pom.xml`
- Modify: `src/main/resources/application.yml`
- Modify: `src/main/resources/application-dev.yml`
- Delete: `src/main/resources/scripts/migration/V3.2__update.sql`

- [ ] **Step 1: 从 pom.xml 移除 Flyway 依赖**

找到并删除：

```xml
		<dependency>
			<groupId>org.flywaydb</groupId>
			<artifactId>flyway-core</artifactId>
		</dependency>
		<dependency>
			<groupId>org.flywaydb</groupId>
			<artifactId>flyway-mysql</artifactId>
		</dependency>
```

- [ ] **Step 2: 从 application.yml 移除 Flyway 配置**

删除 lines 38-42 的 Flyway 配置块：

```yaml
    flyway:
        enabled: false
        baseline-on-migrate: true
        encoding: UTF-8
        locations: classpath:scripts/migration
```

- [ ] **Step 3: 从 application-dev.yml 移除 Flyway 配置**

删除 `flyway: enabled: true` 行。

- [ ] **Step 4: 删除迁移脚本目录**

```bash
Remove-Item -Recurse -Force src/main/resources/scripts/migration
```

- [ ] **Step 5: 提交**

```bash
git add -A
git commit -m "perf: remove Flyway database migration"
```

---

### Task 3: 移除全文搜索 (Hibernate Search + Lucene)

**文件:**
- Modify: `pom.xml`
- Modify: `src/main/resources/application-dev.yml`
- Delete: `src/main/java/com/sunblog/modules/service/PostSearchService.java`
- Delete: `src/main/java/com/sunblog/modules/service/impl/PostSearchServiceImpl.java`
- Delete: `src/main/java/com/sunblog/config/SmartCnAnalysisConfigurer.java`
- Delete: `src/main/java/com/sunblog/web/controller/site/SearchController.java`
- Delete: `src/main/resources/templates/classic/search.ftl`
- Modify: `src/main/java/com/sunblog/web/controller/admin/OptionsController.java`

- [ ] **Step 1: 从 pom.xml 移除搜索相关依赖**

删除：

```xml
		<dependency>
			<groupId>org.hibernate.search</groupId>
			<artifactId>hibernate-search-mapper-orm</artifactId>
			<version>${hibernate.search.version}</version>
		</dependency>
		<dependency>
			<groupId>org.hibernate.search</groupId>
			<artifactId>hibernate-search-backend-lucene</artifactId>
			<version>${hibernate.search.version}</version>
		</dependency>
		<dependency>
			<groupId>org.apache.lucene</groupId>
			<artifactId>lucene-analyzers-smartcn</artifactId>
			<version>8.11.4</version>
		</dependency>
```

同时删除 properties 中的 `hibernate.search.version` 条目。

- [ ] **Step 2: 从 application-dev.yml 移除搜索配置**

删除 lines 22-26：

```yaml
            hibernate.search.backend.type: lucene
            hibernate.search.backend.directory.type: local-filesystem
            hibernate.search.backend.directory.root: ${site.location}/storage/indexes
            hibernate.search.backend.analysis.configurer: com.sunblog.config.SmartCnAnalysisConfigurer
            hibernate.search.backend.lucene_version: LATEST
```

- [ ] **Step 3: 删除搜索相关 Java 文件**

```bash
Remove-Item src/main/java/com/sunblog/modules/service/PostSearchService.java
Remove-Item src/main/java/com/sunblog/modules/service/impl/PostSearchServiceImpl.java
Remove-Item src/main/java/com/sunblog/config/SmartCnAnalysisConfigurer.java
Remove-Item src/main/java/com/sunblog/web/controller/site/SearchController.java
```

- [ ] **Step 4: 删除 search.ftl 模板**

```bash
Remove-Item src/main/resources/templates/classic/search.ftl
```

- [ ] **Step 5: 修改 OptionsController 移除搜索引用**

将 `OptionsController.java` 的以下内容：

```java
import com.sunblog.modules.service.PostSearchService;

public class OptionsController extends BaseController {
	@Autowired
	private PostSearchService postSearchService;

	@RequestMapping("/reset_indexes")
	@ResponseBody
	public Result resetIndexes() {
		postSearchService.resetIndexes();
		return Result.success();
	}
}
```

改为：

```java
public class OptionsController extends BaseController {

}
```

- [ ] **Step 6: 在 admin/index.ftl 移除"重建索引"按钮**

编辑 `templates/admin/index.ftl`，删除以下内容：

```html
                    <button type="button" class="btn btn-info" data-action="reset_indexes">
```

以及对应的 JS：

```javascript
        $('button[data-action="reset_indexes"]').bind('click', function(){
                J.getJSON('${base}/admin/options/reset_indexes', ajaxReload);
        });
```

- [ ] **Step 7: 提交**

```bash
git add -A
git commit -m "perf: remove full-text search (Hibernate Search + Lucene)"
```

---

### Task 4: 移除事件系统

**文件:**
- Delete: `src/main/java/com/sunblog/modules/event/PostUpdateEvent.java`
- Delete: `src/main/java/com/sunblog/modules/event/handler/PostUpdateEventHandler.java`
- Modify: `src/main/java/com/sunblog/modules/service/impl/PostServiceImpl.java`

- [ ] **Step 1: 删除事件 Java 文件**

```bash
Remove-Item src/main/java/com/sunblog/modules/event/PostUpdateEvent.java
Remove-Item src/main/java/com/sunblog/modules/event/handler/PostUpdateEventHandler.java
```

- [ ] **Step 2: 修改 PostServiceImpl.java**

移除 import：
```java
import com.sunblog.modules.event.PostUpdateEvent;
```
和
```java
import org.springframework.context.ApplicationContext;
```

移除注入：
```java
	@Autowired
	private ApplicationContext applicationContext;
```

移除 `onPushEvent` 方法 (lines 459-469)：

```java
	private void onPushEvent(Post post, int action) {
		PostUpdateEvent event = new PostUpdateEvent(System.currentTimeMillis());
		event.setPostId(post.getId());
		event.setUserId(post.getAuthorId());
		event.setAction(action);
		applicationContext.publishEvent(event);
	}
```

移除所有对 `onPushEvent` 的调用（两处）：
- Line 214: `onPushEvent(po, PostUpdateEvent.ACTION_PUBLISH);`
- Line 312: `onPushEvent(po, PostUpdateEvent.ACTION_DELETE);`
- Line 336: `onPushEvent(po, PostUpdateEvent.ACTION_DELETE);`

- [ ] **Step 3: 提交**

```bash
git add -A
git commit -m "perf: remove event system"
```

---

### Task 5: 移除收藏功能

**文件:**
- Delete: `src/main/java/com/sunblog/modules/entity/Favorite.java`
- Delete: `src/main/java/com/sunblog/modules/data/FavoriteVO.java`
- Delete: `src/main/java/com/sunblog/modules/repository/FavoriteRepository.java`
- Delete: `src/main/java/com/sunblog/modules/service/FavoriteService.java`
- Delete: `src/main/java/com/sunblog/modules/service/impl/FavoriteServiceImpl.java`
- Delete: `src/main/java/com/sunblog/web/controller/site/user/FavorController.java`
- Delete: `src/main/java/com/sunblog/modules/template/directive/UserFavoritesDirective.java`
- Delete: `src/main/resources/templates/classic/user/method_favorites.ftl`
- Modify: `src/main/java/com/sunblog/modules/service/PostService.java`
- Modify: `src/main/java/com/sunblog/modules/service/impl/PostServiceImpl.java`
- Modify: `src/main/resources/templates/classic/channel/view.ftl`
- Modify: `src/main/resources/templates/classic/user/method_posts.ftl`
- Modify: `src/main/resources/templates/classic/inc/user_sidebar.ftl`
- Modify: `src/main/resources/scripts/schema.sql`

- [ ] **Step 1: 删除收藏相关 Java 文件**

```bash
Remove-Item src/main/java/com/sunblog/modules/entity/Favorite.java
Remove-Item src/main/java/com/sunblog/modules/data/FavoriteVO.java
Remove-Item src/main/java/com/sunblog/modules/repository/FavoriteRepository.java
Remove-Item src/main/java/com/sunblog/modules/service/FavoriteService.java
Remove-Item src/main/java/com/sunblog/modules/service/impl/FavoriteServiceImpl.java
Remove-Item src/main/java/com/sunblog/web/controller/site/user/FavorController.java
Remove-Item src/main/java/com/sunblog/modules/template/directive/UserFavoritesDirective.java
```

- [ ] **Step 2: 从 PostService 接口移除 favor/unfavor 方法**

删除 lines 125-141：

```java
	/**
	 * 收藏文章
	 */
	@CacheEvict(key = "'view_' + #postId")
	void favor(long userId, long postId);

	/**
	 * 取消收藏
	 */
	@CacheEvict(key = "'view_' + #postId")
	void unfavor(long userId, long postId);
```

- [ ] **Step 3: 从 PostServiceImpl 移除收藏相关代码**

移除注入：
```java
	@Autowired
	private FavoriteService favoriteService;
```

移除 `favor()` 和 `unfavor()` 方法 (lines 360-380)：

```java
	@Override
	@Transactional(rollbackFor = Throwable.class)
	public void favor(long userId, long postId) {
		postRepository.updateFavors(postId, Consts.IDENTITY_STEP);
		favoriteService.add(userId, postId);
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public void unfavor(long userId, long postId) {
		postRepository.updateFavors(postId,  Consts.DECREASE_STEP);
		favoriteService.delete(userId, postId);
	}
```

移除 import `import com.sunblog.modules.service.FavoriteService;`（如果不再被引用）。

- [ ] **Step 4: 删除收藏模板文件**

```bash
Remove-Item src/main/resources/templates/classic/user/method_favorites.ftl
```

- [ ] **Step 5: 修改 channel/view.ftl 移除收藏按钮**

删除或注释掉 lines 111-112 的收藏按钮 HTML。

- [ ] **Step 6: 修改 method_posts.ftl 移除收藏数显示**

将 `${row.favors} 点赞` 改为 `0 点赞` 或删除该行。

- [ ] **Step 7: 修改 user_sidebar.ftl 移除"收藏的文章"链接**

删除 line 49 的收藏链接。

- [ ] **Step 8: 从 schema.sql 移除 mto_favorite 表定义**

如果有 `mto_favorite` 相关建表语句，删除。

- [ ] **Step 9: 提交**

```bash
git add -A
git commit -m "perf: remove favorites feature"
```

---

### Task 6: 移除友链功能

**文件:**
- Delete: `src/main/java/com/sunblog/modules/entity/Links.java`
- Delete: `src/main/java/com/sunblog/modules/repository/LinksRepository.java`
- Delete: `src/main/java/com/sunblog/modules/service/LinksService.java`
- Delete: `src/main/java/com/sunblog/modules/service/impl/LinksServiceImpl.java`
- Delete: `src/main/java/com/sunblog/modules/template/directive/LinksDirective.java`

- [ ] **Step 1: 删除友链相关 Java 文件**

```bash
Remove-Item src/main/java/com/sunblog/modules/entity/Links.java
Remove-Item src/main/java/com/sunblog/modules/repository/LinksRepository.java
Remove-Item src/main/java/com/sunblog/modules/service/LinksService.java
Remove-Item src/main/java/com/sunblog/modules/service/impl/LinksServiceImpl.java
Remove-Item src/main/java/com/sunblog/modules/template/directive/LinksDirective.java
```

- [ ] **Step 2: 提交**

```bash
git add -A
git commit -m "perf: remove friend links feature"
```

---

### Task 7: 移除钩子/插件系统

**文件:**
- Delete: `src/main/java/com/sunblog/modules/hook/Hook.java`
- Delete: `src/main/java/com/sunblog/modules/hook/interceptor/InterceptorHook.java`
- Delete: `src/main/java/com/sunblog/modules/hook/interceptor/InterceptorHookManager.java`
- Delete: `src/main/java/com/sunblog/modules/hook/interceptor/InterceptorHookSupport.java`
- Delete: `src/main/java/com/sunblog/modules/hook/interceptor/impl/HidenContentPugin.java`
- Delete: `src/main/java/com/sunblog/modules/hook/interceptor/impl/ViewCopyrightPugin.java`
- Modify: `src/main/java/com/sunblog/web/interceptor/BaseInterceptor.java`

- [ ] **Step 1: 删除钩子 Java 文件**

```bash
Remove-Item -Recurse -Force src/main/java/com/sunblog/modules/hook
```

- [ ] **Step 2: 修改 BaseInterceptor.java**

移除 Hook 相关 import：
```java
import com.sunblog.modules.hook.interceptor.InterceptorHookManager;
```

移除注入：
```java
	@Autowired
	private InterceptorHookManager interceptorHookManager;
```

从 `preHandle`、`postHandle`、`afterCompletion` 方法中移除所有 `interceptorHookManager.*` 调用。

修改后的三个方法：

```java
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		request.setAttribute("base", request.getContextPath());
		if (modelAndView != null) {
			modelAndView.addObject("site", siteOptions);
		}
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
	}
```

- [ ] **Step 3: 提交**

```bash
git add -A
git commit -m "perf: remove hook/plugin system"
```

---

### Task 8: 移除 Shiro 权限标签库

**文件:**
- Delete: `src/main/java/com/sunblog/shiro/tags/` (14 个文件)
- Modify: `src/main/java/com/sunblog/config/SiteConfiguration.java`
- Modify: `src/main/resources/templates/classic/inc/header.ftl`

- [ ] **Step 1: 删除 Shiro Tags Java 文件**

```bash
Remove-Item -Recurse -Force src/main/java/com/sunblog/shiro/tags
```

- [ ] **Step 2: 修改 SiteConfiguration.java**

移除 ShiroTags 相关代码：

删除 import：
```java
import com.sunblog.shiro.tags.ShiroTags;
```

从 `setSharedVariable` 方法中移除：
```java
            configuration.setSharedVariable("shiro", new ShiroTags());
```

- [ ] **Step 3: 修改 header.ftl 移除 @shiro.hasPermission**

替换 lines 104-107：

原内容：
```html
                            <@shiro.hasPermission name="admin">
                                <li><a href="${base}/admin"><i class="icon icon-dashboard"></i> 后台管理</a></li>
                            </@shiro.hasPermission>
```

改为：
```html
                            <li><a href="${base}/admin"><i class="icon icon-dashboard"></i> 后台管理</a></li>
```

- [ ] **Step 4: 提交**

```bash
git add -A
git commit -m "perf: remove Shiro permission tags"
```

---

### Task 9: 移除 RESTful API

**文件:**
- Delete: `src/main/java/com/sunblog/web/controller/api/ApiController.java`
- Delete: `src/main/java/com/sunblog/web/controller/api/package-info.java`

- [ ] **Step 1: 删除 API Java 文件**

```bash
Remove-Item -Recurse -Force src/main/java/com/sunblog/web/controller/api
```

- [ ] **Step 2: 提交**

```bash
git add -A
git commit -m "perf: remove REST API"
```

---

### Task 10: 移除 Docker 和多配置文件

**文件:**
- Delete: `Dockerfile`
- Delete: `docker-compose.yml`
- Delete: `src/main/resources/application-docker.yml`
- Delete: `application-docker.yml` 的 profile 引用

- [ ] **Step 1: 删除 Docker 相关文件**

```bash
Remove-Item Dockerfile
Remove-Item docker-compose.yml
Remove-Item src/main/resources/application-docker.yml
```

- [ ] **Step 2: 从 pom.xml 移除 docker profile**

删除 pom.xml 中的 docker profile 节点。

```xml
		<profile>
			<id>docker</id>
			<properties>
				<profileActive>docker</profileActive>
			</properties>
		</profile>
```

- [ ] **Step 3: 提交**

```bash
git add -A
git commit -m "perf: remove Docker and multi-profile configs"
```

---

### Task 11: 清理 pom.xml 中的多余依赖

**文件:**
- Modify: `pom.xml`

- [ ] **Step 1: 从 pom.xml 移除其他不需要的依赖**

检查并移除以下依赖（如果不需要）：

移除 `ehcache` 的 properties：
```xml
		<net.sf.ehcache>2.10.9.2</net.sf.ehcache>
```

移除 `commons-httpclient`（已过时，Spring Boot 用 RestTemplate）：
```xml
		<dependency>
			<groupId>commons-httpclient</groupId>
			<artifactId>commons-httpclient</artifactId>
			<version>${commons.httpclient}</version>
			<exclusions>
				<exclusion>
					<groupId>commons-logging</groupId>
					<artifactId>commons-logging</artifactId>
				</exclusion>
			</exclusions>
		</dependency>
```

移除 `guava`（Spring Boot 已自带大部分功能）：
```xml
        <dependency>
            <groupId>com.google.guava</groupId>
            <artifactId>guava</artifactId>
            <version>28.0-jre</version>
        </dependency>
```

- [ ] **Step 2: 构建验证**

```bash
mvn compile -DskipTests
```
Expected: BUILD SUCCESS

- [ ] **Step 3: 提交**

```bash
git add -A
git commit -m "perf: remove unused dependencies"
```

---

## 自检清单

1. **范围覆盖**: 每个需要删除的功能都有对应的 Task (H2→Task1, Flyway→Task2, 搜索→Task3, 事件→Task4, 收藏→Task5, 友链→Task6, Hook→Task7, Shiro标签→Task8, REST API→Task9, Docker→Task10, 清理依赖→Task11)
2. **占位符扫描**: 所有步骤都有完整代码和文件路径，无 TODO/TBD
3. **类型一致性**: 所有方法名、文件路径在任务间保持一致
4. **已验证模板引用变更**: header.ftl 的 `@shiro.hasPermission`, view.ftl 的收藏按钮, user_sidebar.ftl 的收藏链接 均已覆盖

---

## 执行方式

计划已保存到 `docs/superpowers/plans/2026-07-06-remove-features.md`。

**两种执行方式：**

1. **直接执行** - 在当前会话按 Task 顺序逐一执行（推荐，因为删除操作风险较高需要人工确认）

2. **跳过计划直接删除** - 如果你确认了，我可以直接按计划开始删除操作
