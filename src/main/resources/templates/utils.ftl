<#-- 用户头像显示 -->
<#-- 定义宏 showAva：接收 user（用户对象）和 clazz（CSS类名）两个参数 -->
<#macro showAva user clazz>
<#-- 链接指向用户个人主页，${base}是项目根路径，${user.id}是用户ID -->
<a href="${base}/users/${user.id}">
<#-- 显示头像图片，class用传入的clazz，src通过<@resource>宏处理（自动加CDN前缀等） -->
    <img class="${clazz}" src="<@resource src=user.avatar />"/>
</a>
<#-- 宏定义结束 -->
</#macro>

<#-- 栏目名称显示 -->
<#-- 定义宏 showChannel：接收 row（文章/帖子对象）作为参数 -->
<#macro showChannel row>
<#-- 显示栏目标签，hidden-xs在超小屏幕隐藏，label/label-default是Bootstrap标签样式 -->
<span class="hidden-xs label label-default">${row.channel.name}</span>
<#-- 宏定义结束 -->
</#macro>

<#-- 分页显示 -->
<#-- 定义宏 pager：接收 url（分页请求地址）、p（分页对象，Spring Data 的 Page）、spans（页码显示跨度） -->
<#macro pager url p spans>
<#-- 判断分页对象 p 是否存在（不为 null） -->
    <#if p??>
<#-- 计算中间页码的跨度，spans减3（首页、末页、省略号占位）再除以2，得到当前页两侧各显示几个页码 -->
        <#local span = (spans - 3)/2 />
<#-- 判断 URL 中是否已经包含查询参数（是否有 "?"） -->
        <#if (url?index_of("?") != -1)>
<#-- 如果 URL 已有参数，用 & 拼接 pageNo 参数 -->
            <#local cURL = (url + "&pageNo=") />
        <#else>
<#-- 如果 URL 没有参数，用 ? 开始 pageNo 参数 -->
            <#local cURL = (url + "?pageNo=") />
        </#if>

<#-- 分页列表容器，使用 Bootstrap 的 pagination 样式 -->
    <ul class="pagination">
<#-- 计算当前页码，p.number 是从0开始的页码索引，+1 转为从1开始 -->
        <#assign pageNo = p.number + 1/>
<#-- 获取总页数 -->
        <#assign pageCount = p.totalPages />
<#-- 如果当前页大于第1页，显示"上一页"按钮（可点击） -->
        <#if (pageNo > 1)>
<#-- 上一页链接，href 指向上一页URL，pageNo减1，使用左箭头图标 -->
            <li><a href="${cURL}${pageNo - 1}" pageNo="${pageNo - 1}" class="prev"><i class="fa fa-angle-left"></i></a></li>
        <#else>
<#-- 如果已经是第1页，上一页按钮置灰不可点击 -->
            <li class="disabled"><span><i class="fa fa-angle-left"></i></span></li>
        </#if>

<#-- 计算总显示页码数（中间区域 + 首尾页） -->
        <#local totalNo = span * 2 + 3 />
<#-- totalNo1 是省略末尾页时的显示数量（比 totalNo 少1，因为末尾页单独显示） -->
        <#local totalNo1 = totalNo - 1 />
<#-- 如果总页数大于可显示的页码数，需要做省略处理 -->
        <#if (pageCount > totalNo)>
<#-- 情况1：当前页靠近首页（当前页 <= span+2），显示前几个页码 + 省略号 + 末页 -->
            <#if (pageNo <= span + 2)>
<#-- 遍历显示第1页到 totalNo1 页 -->
                <#list 1..totalNo1 as i>
<#-- 调用 pagelink 宏渲染单个页码 -->
                    <@pagelink pageNo, i, cURL/>
                </#list>
<#-- 显示省略号（idx=0 时 pagelink 会渲染为 "..."） -->
                <@pagelink 0, 0, "#"/>
<#-- 显示最后一页 -->
                <@pagelink pageNo, pageCount, cURL />
<#-- 情况2：当前页靠近末页（当前页 > 总页数 - span - 2），显示首页 + 省略号 + 末尾几个页码 -->
            <#elseif (pageNo > (pageCount - (span + 2)))>
<#-- 显示第1页 -->
                <@pagelink pageNo, 1, cURL />
<#-- 显示省略号 -->
                <@pagelink 0, 0, "#"/>
<#-- 计算末尾区域的起始页码 -->
                <#local num = pageCount - totalNo + 2 />
<#-- 遍历显示从 num 到最后一页 -->
                <#list num..pageCount as i>
                    <@pagelink pageNo, i, cURL/>
                </#list>
<#-- 情况3：当前页在中间位置，显示首页 + 省略号 + 当前页附近页码 + 省略号 + 末页 -->
            <#else>
<#-- 显示第1页 -->
                <@pagelink pageNo, 1, cURL />
<#-- 显示省略号 -->
                <@pagelink 0 0 "#" />
<#-- 计算当前页左侧起始页码 -->
                <#local num = pageNo - span />
<#-- 计算当前页右侧结束页码 -->
                <#local num2 = pageNo + span />
<#-- 遍历显示当前页附近的页码 -->
                <#list num..num2 as i>
                    <@pagelink pageNo, i, cURL />
                </#list>
<#-- 显示省略号 -->
                <@pagelink 0, 0, "#"/>
<#-- 显示最后一页 -->
                <@pagelink pageNo, pageCount, cURL />
            </#if>
<#-- 如果总页数大于1但不超过 totalNo，直接显示所有页码（不需要省略号） -->
        <#elseif (pageCount > 1)>
            <#list 1..pageCount as i>
                <@pagelink pageNo, i, cURL />
            </#list>
        <#else>
<#-- 如果只有1页，显示第1页（实际上只有一页时通常不需要分页） -->
            <@pagelink 1, 1, cURL/>
        </#if>

<#-- 如果当前页小于总页数，显示"下一页"按钮（可点击） -->
        <#if (pageNo < pageCount)>
<#-- 下一页链接，href 指向下一页URL，pageNo加1，使用右箭头图标 -->
            <li><a href="${cURL}${pageNo + 1}" pageNo="${pageNo + 1}" class="next"><i class="fa fa-angle-right"></i></a></li>
        <#else>
<#-- 如果已经是最后一页，下一页按钮置灰不可点击 -->
            <li class="disabled"><span><i class="fa fa-angle-right"></i></span></li>
        </#if>
    </ul>
<#-- if 判断结束 -->
    </#if>
<#-- pager 宏定义结束 -->
</#macro>

<#-- pagelink 宏：渲染单个页码项，接收 pageNo（当前页码）、idx（要显示的页码）、url（链接地址） -->
<#macro pagelink pageNo idx url>
<#-- 如果 idx 为0，渲染省略号（表示中间有页码被省略） -->
    <#if (idx == 0)>
    <li><span>...</span></li>
<#-- 如果 idx 等于当前页码，渲染为激活状态（高亮显示，不可点击） -->
    <#elseif (pageNo == idx)>
    <li class="active"><span>${idx}</span></li>
<#-- 否则渲染为可点击的页码链接 -->
    <#else>
    <li><a href="${url}${idx}">${idx}</a></li>
<#-- if 判断结束 -->
    </#if>
<#-- pagelink 宏定义结束 -->
</#macro>