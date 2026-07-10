<#-- 栏目文章列表页：按栏目筛选文章，支持分页和排序 -->
<#-- 继承主布局模板 -->
<@layout.extends name="/inc/layout.ftl">
    <@layout.put block="title">
        <#-- 标题格式：栏目名 - 站点名 -->
        <title>${channel.name!''} - ${options['site_name']!''}</title>
    </@layout.put>

    <@layout.put block="contents">
        <div class="row">
            <#-- 左侧主区域：占 9/12 列，显示文章列表 -->
            <div class="col-xs-12 col-md-9 side-left">
<#-- 调试信息：显示栏目ID、页码、排序方式 -->
                <!-- 调试：channel.id = ${channel.id!0}, pageNo = ${pageNo!1}, order = ${order!'newest'} -->
<#-- contents 宏：按栏目ID获取文章列表，支持分页和排序 -->
                <#-- results 变量在宏内部被赋值为分页数据 -->
                <@contents channelId=channel.id pageNo=pageNo order=order>
                    <div class="posts">
                        <ul class="posts-list">
                            <#-- 引入文章列表项模板，定义 posts_item 宏 -->
                            <@layout.extends name="/inc/posts_item.ftl" />
                            <#-- 遍历文章分页数据，调用 posts_item 宏渲染每篇文章 -->
                            <#list results.content as row>
                                <@posts_item row/>
                            </#list>
                            <#-- 列表为空时显示提示信息 -->
                            <#if  results.content?size == 0>
                            <li class="content">
                                <div class="content-box posts-aside">
                                    <div class="posts-item">该目录下还没有内容!</div>
                                </div>
                            </li>
                            </#if>
                        </ul>
                    </div>

                    <!-- Pager -->
                    <#-- 分页组件：text-center 使分页居中 -->
                    <div class="text-center">
                        <@utils.pager request.requestURI!"", results, 5/>
                    </div>
                </@contents>

            </div>

            <#-- 右侧侧栏：占 3/12 列，引入公共右侧栏 -->
            <div class="col-xs-12 col-md-3 side-right">
                <@layout.extends name="/inc/right.ftl" />
            </div>
        </div>
    </@layout.put>
</@layout.extends>