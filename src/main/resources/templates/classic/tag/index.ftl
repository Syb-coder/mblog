<#-- 标签列表页：展示所有标签及每个标签下的最新文章 -->
<#-- 继承主布局模板 -->
<@layout.extends name="/inc/layout.ftl">
    <@layout.put block="title">
        <title>标签列表</title>
    </@layout.put>

    <@layout.put block="contents">
        <div class="row">
            <#-- 左侧主区域：占 9/12 列，展示标签列表 -->
            <div class="col-xs-12 col-md-9 side-left">
                <div class="panel panel-default">
                    <div class="panel-body streams-tags">
<#-- 遍历所有标签 -->
                        <#-- results.content 为标签分页数据 -->
                        <#list results.content as row>
<#-- 获取该标签下最新的一篇文章 -->
                            <#assign post = row.post />
                            <#-- col-sm-6 每行显示2个标签卡片 -->
                            <div class="col-sm-6 row-item">
                                <h2 class="title">
<#-- 标签名链接到标签搜索结果页，label 显示文章数 -->
                                    <a href="${base}/tag/${row.name}/"><i class="fa fa-quote-left"></i> ${row.name}</a>
                                    <span class="label label-default">${row.posts}</span>
                                </h2>
<#-- 如果该标签下有文章，显示最新文章的作者和标题 -->
                                <#-- ?if_exists 安全访问，post 为空时不报错 -->
                                <#if post?if_exists>
                                    <#-- media 为 Bootstrap 媒体对象（左图右文布局） -->
                                    <div class="media">
                                        <div class="media-left">
<#-- 调用 utils.ftl 的 showAva 宏显示作者头像 -->
                                            <@utils.showAva post.author "media-object"/>
                                        </div>
                                        <div class="media-body">
                                            <h4 class="media-heading">
                                                <#-- ?html 对标题进行 HTML 转义 -->
                                                <a href="${base}/post/${post.id}">${post.title?html}</a>
                                            </h4>
                                        </div>
                                    </div>
                                </#if>
                            </div>
                        </#list>
                        <#-- 列表为空时显示提示 -->
                        <#if results.content?size == 0>
                            <div class="infos">
                                <div class="media-heading">该目录下还没有内容!</div>
                            </div>
                        </#if>
                    </div>
                </div>

                <!-- Pager -->
                <#-- 分页组件 -->
                <div class="text-center">
                    <@utils.pager request.requestURI!"", results, 5/>
                </div>
            </div>

            <#-- 右侧侧栏：占 3/12 列 -->
            <div class="col-xs-12 col-md-3 side-right">
                <@layout.extends name="/inc/right.ftl" />
            </div>

        </div>
    </@layout.put>
</@layout.extends>