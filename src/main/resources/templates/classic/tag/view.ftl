<#-- 标签搜索结果页：显示某个标签下的所有文章 -->
<#-- 继承主布局模板 -->
<@layout.extends name="/inc/layout.ftl">
    <@layout.put block="title">
        <title>标签: ${kw}</title>
    </@layout.put>

    <@layout.put block="contents">
        <div class="row streams">
            <#-- 左侧主区域：占 9/12 列，展示搜索结果 -->
            <div class="col-xs-12 col-md-9 side-left">
                <div class="posts ">
                    <ul class="posts-list">
<#-- 显示标签名和搜索结果总数 -->
                        <#-- totalElements 为分页结果的总记录数 -->
                        <li class="content">
                            <div class="content-box posts-aside">
                                <div class="posts-item">标签: ${name} 共 ${results.totalElements} 个结果.</div>
                            </div>
                        </li>
                        <#-- 引入文章列表项模板，定义 posts_item 宏 -->
                        <@layout.extends name="/inc/posts_item.ftl" />
<#-- 遍历搜索结果，row.post 是文章对象（因为结果包含标签关联信息） -->
                        <#list results.content as row>
                            <@posts_item row.post/>
                        </#list>
                        <#-- 结果为空时显示提示 -->
                        <#if !results?? || results.content?size == 0>
                            <li class="content">
                                <div class="content-box posts-aside">
                                    <div class="posts-item">该目录下还没有内容!</div>
                                </div>
                            </li>
                        </#if>
                    </ul>
                </div>
                <#-- 分页组件 -->
                <div class="text-center">
                    <@utils.pager request.requestURI, results, 5/>
                </div>
            </div>
            <#-- 右侧侧栏：占 3/12 列 -->
            <div class="col-xs-12 col-md-3 side-right">
                <@layout.extends name="/inc/right.ftl" />
            </div>
        </div>
    </@layout.put>
</@layout.extends>