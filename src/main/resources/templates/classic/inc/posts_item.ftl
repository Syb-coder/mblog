<#-- 文章列表项宏：渲染单篇文章卡片，接收 row（文章对象）和 escape（是否转义HTML，默认true） -->
<#macro posts_item row escape=true>
<li class="content">
<#-- 如果文章有缩略图，显示带图片的卡片布局 -->
    <#if row.thumbnail?? && row.thumbnail?length gt 0>
        <div class="content-box">
<#-- 缩略图区域，点击跳转文章详情 -->
            <div class="posts-item-img">
                <a href="${base}/post/${row.id}" title="">
                    <div class="overlay"></div>
<#-- lazy 类用于图片懒加载，<@resource> 宏处理图片路径 -->
                    <img class="lazy thumbnail" src="<@resource src=row.thumbnail/>" style="display: inline-block;">
                </a>
            </div>
            <div class="posts-item posts-item-gallery">
<#-- 文章标题，?html 对内容进行 HTML 转义防止 XSS -->
                <h2><a href="${base}/post/${row.id}"><#if escape>${row.title?html}<#else>${row.title}</#if></a></h2>
<#-- 文章摘要 -->
                <div class="item-text">${row.summary}</div>
                <div class="item-info">
                    <ul>
<#-- 作者信息：头像 + 昵称，hidden-xs 在小屏隐藏 -->
                        <li class="post-author hidden-xs">
                            <div class="avatar">
<#-- 头像URL加 ?t=时间戳 防止浏览器缓存 -->
                                <img src="<@resource src=row.author.avatar + '?t=' + .now?time/>" class="lazy avatar avatar-50 photo" height="50" width="50">
                            </div>
                            <a href="${base}/users/${row.author.id}" target="_blank">${row.author.name}</a>
                        </li>
<#-- 调用 utils.ftl 的 showChannel 宏显示栏目标签 -->
                        <li class="ico-cat"><@utils.showChannel row/></li>
<#-- 发布时间，timeAgo 是自定义函数，显示"xx分钟前"格式 -->
                        <li class="ico-time"><i class="icon-clock"></i>${timeAgo(row.created)}</li>
<#-- 如果更新日期和创建日期不同，显示更新时间 -->
                        <#if row.updated?? && row.updated?string('yyyy-MM-dd') != row.created?string('yyyy-MM-dd')>
                        <li class="ico-time hidden-xs"><i class="icon-note"></i>更新于 ${timeAgo(row.updated)}</li>
                        </#if>
<#-- 阅读数和评论数 -->
                        <li class="ico-eye hidden-xs"><i class="icon-book-open"></i>${row.views}</li>
                        <li class="ico-like hidden-xs"><i class="icon-bubble"></i>${row.comments}</li>
                    </ul>
                </div>
            </div>
        </div>
<#-- 如果没有缩略图，显示纯文字卡片布局 -->
    <#else>
        <div class="content-box posts-aside">
            <div class="posts-item">
                <div class="item-title">
                    <h2><a href="${base}/post/${row.id}"><#if escape>${row.title?html}<#else>${row.title}</#if></a></h2>
                </div>
                <div class="item-text">${row.summary}</div>
                <div class="item-info">
                    <ul>
                        <li class="post-author hidden-xs">
                            <div class="avatar">
                                <img src="<@resource src=row.author.avatar + '?t=' + .now?time/>" class="lazy avatar avatar-50 photo" height="50" width="50">
                            </div>
                            <a href="${base}/users/${row.author.id}" target="_blank">${row.author.name}</a>
                        </li>
                        <li class="ico-cat"><@utils.showChannel row/></li>
                        <li class="ico-time"><i class="icon-clock"></i>${timeAgo(row.created)}</li>
                        <#if row.updated?? && row.updated?string('yyyy-MM-dd') != row.created?string('yyyy-MM-dd')>
                        <li class="ico-time hidden-xs"><i class="icon-note"></i>更新于 ${timeAgo(row.updated)}</li>
                        </#if>
                    </ul>
                </div>
            </div>
        </div>
    </#if>
</li>
</#macro>