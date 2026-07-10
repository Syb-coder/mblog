<#-- 首页：继承主布局，显示置顶文章轮播 + 文章列表 + 右侧边栏 -->
<#-- 继承主布局模板 -->
<@layout.extends name="/inc/layout.ftl">

    <@layout.put block="contents">
<#-- 置顶栏目的 ID，默认为1 -->
        <#assign topId = 1 />

<#-- 顶部轮播区：获取置顶栏目的3篇文章 -->
        <!-- top -->
        <#-- contents 宏：按栏目ID获取文章，size=3 限制返回3篇 -->
        <@contents channelId=topId size=3>
            <#if  results.content?size gt 0>
                <div class="row banner">
                    <#list results.content as row>
                        <#-- col-sm-4 col-md-4 每行显示3个轮播卡片 -->
                        <div class="banner-item col-xs-12 col-sm-4 col-md-4">
<#-- 轮播卡片：背景图为文章缩略图，无缩略图时使用默认占位图 -->
                            <div class="index-banner-box"
                                <#if row.thumbnail?? && row.thumbnail?length gt 0>
                                 style="background-image:url(<@resource src=row.thumbnail/>)"
                                <#else>
                                 style="background-image:url(${base}/dist/images/spinner-overlay.png)"
                                </#if> >
<#-- 点击整个卡片跳转到文章详情 -->
                                <a class="top" href="${base}/post/${row.id}">
                                    <#-- overlay 遮罩层，line 分隔线 -->
                                    <div class="overlay"></div>
                                    <div class="line"></div>
                                    <div class="title">
                                        <h3>${row.title?html}</h3>
                                    </div>
                                </a>
                            </div>
                        </div>
                    </#list>
                </div>
            </#if>
        </@contents>

        <!-- top/end -->

        <div class="row">
<#-- 左侧：文章列表区，占9列 -->
            <div class="col-xs-12 col-md-9 side-left">
                <div class="posts">
<#-- contents 宏：获取文章列表数据，传入页码 -->
                    <@contents pageNo=pageNo>
                    <ul class="posts-list">
<#-- 引入 posts_item 宏定义 -->
                        <@layout.extends name="/inc/posts_item.ftl" />
<#-- 遍历文章列表，调用 posts_item 宏渲染每篇文章 -->
                        <#list results.content as row>
                            <@posts_item row/>
                        </#list>
<#-- 如果没有文章，显示空提示 -->
                        <#if  results.content?size == 0>
                            <li class="content">
                                <div class="content-box posts-aside">
                                    <div class="posts-item">该目录下还没有内容!</div>
                                </div>
                            </li>
                        </#if>
                    </ul>
                    </@contents>
                </div>
                <div class="text-center">
<#-- 分页组件，调用 utils.ftl 中的 pager 宏 -->
                    <!-- Pager -->
                    <@utils.pager request.requestURI!"", results, 5/>
                </div>
            </div>
<#-- 右侧边栏：热门文章、最新发布、最新评论 -->
            <div class="col-xs-12 col-md-3 side-right">
                <@layout.extends name="/inc/right.ftl" />
            </div>
        </div>
    </@layout.put>
</@layout.extends>