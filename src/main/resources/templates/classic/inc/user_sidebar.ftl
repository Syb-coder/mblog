
<#-- 用户个人页侧边栏 -->
<ul class="list-group about-user">
<#-- 用户卡片：头像 + 昵称 -->
    <li class="list-group-item user-card" >
        <div class="user-avatar">
<#-- 调用 utils.ftl 中的 showAva 宏显示用户头像，img-circle 为圆形样式 -->
            <@utils.showAva user "img-circle"/>
        </div>
        <div class="user-name">
            <span>${user.name}</span>
        </div>
    </li>
<#-- 用户统计数据：发布数和评论数 -->
    <li class="list-group-item">
        <div class="user-datas">
            <ul>
                <li><strong>${user.posts}</strong><span>发布</span></li>
                <li class="noborder"><strong>${user.comments}</strong><span>评论</span></li>
            </ul>
        </div>
    </li>
<#-- owner 为 true 表示当前登录用户就是该页面的主人，显示编辑资料按钮 -->
    <#if owner>
        <li class="list-group-item">
            <a class="btn btn-primary btn-block btn-sm" href="${base}/settings/profile">
                <i class="icon icon-note"></i> 编辑个人资料
            </a>
        </li>
    </#if>
</ul>
<#-- 用户导航栏 -->
<nav class="navbar navbar-default shadow-box background-white">
    <div class="container-fluid">
<#-- 移动端导航栏折叠按钮，visible-xs 只在超小屏幕显示 -->
        <div class="navbar-header visible-xs">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#home-navbar" aria-expanded="false">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span><span class="icon-bar"></span><span class="icon-bar"></span>
            </button>
            <span class="navbar-brand">导航</span>
        </div>
    </div>
<#-- 导航菜单：发表的文章、发表的评论 -->
    <div id="home-navbar" class="collapse navbar-collapse">
        <ul class="list-group user-nav first">
            <li class="list-group-item">
                <a href="${base}/users/${user.id}"><i class="icon icon-list"></i> 发表的文章</a>
            </li>
            <li class="list-group-item">
                <a href="${base}/users/${user.id}/comments"><i class="icon icon-speech"></i> 发表的评论</a>
            </li>

        </ul>
    </div>
</nav>