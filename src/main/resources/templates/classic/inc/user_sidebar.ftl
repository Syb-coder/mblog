
<ul class="list-group about-user">
    <li class="list-group-item user-card" >
        <div class="user-avatar">
            <@utils.showAva user "img-circle"/>
        </div>
        <div class="user-name">
            <span>${user.name}</span>
        </div>
    </li>
    <li class="list-group-item">
        <div class="user-datas">
            <ul>
                <li><strong>${user.posts}</strong><span>发布</span></li>
                <li class="noborder"><strong>${user.comments}</strong><span>评论</span></li>
            </ul>
        </div>
    </li>
    <#if owner>
        <li class="list-group-item">
            <a class="btn btn-primary btn-block btn-sm" href="${base}/settings/profile">
                <i class="icon icon-note"></i> 编辑个人资料
            </a>
        </li>
    </#if>
</ul>
<nav class="navbar navbar-default shadow-box background-white">
    <div class="container-fluid">
        <!-- Brand and toggle get grouped for better mobile display -->
        <div class="navbar-header visible-xs">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#home-navbar" aria-expanded="false">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <span class="navbar-brand">导航</span>
        </div>
    </div>
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
