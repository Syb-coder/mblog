<#-- Ajax 登录弹窗：用户未登录时点击需要登录的操作，弹出此模态框进行快速登录 -->
<!-- Login dialog BEGIN -->
<div id="login_alert" class="modal fade" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document" style="width: 400px;">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">请登录</h4>
            </div>
            <div class="modal-body">
<#-- Ajax 登录表单，提交到 /login 接口 -->
                <form method="POST" action="${base}/login" accept-charset="UTF-8">
                    <div class="form-group">
                        <label class="control-label" for="username">账号</label>
                        <input class="form-control" id="ajax_login_username" name="username" type="text" required>
                    </div>
                    <div class="form-group">
                        <label class="control-label" for="password">密码</label>
                        <input class="form-control" id="ajax_login_password" name="password" type="password" required>
                    </div>
                    <div class="form-group">
<#-- type="button" 而非 "submit"，表示通过 JS 异步提交而非表单同步提交 -->
                        <button id="ajax_login_submit" class="btn btn-primary btn-block btn-sm" type="button">
                            登录
                        </button>
                    </div>
                    <div class="form-group">
<#-- 登录错误信息显示区域 -->
                        <div id="ajax_login_message" class="text-danger"></div>
                    </div>
                </form>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->
<!-- Login dialog END -->

<#-- IE9 以下版本浏览器升级提示 -->
<!--[if lt IE 9]>
<div class="alert alert-danger alert-dismissible fade in" role="alert" style="margin-bottom:0">
	<button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">×</span><span class="sr-only">Close</span></button>
	<strong>您正在使用低版本浏览器，</strong> 在本页面的显示效果可能有差异。
	建议您升级到
	<a href="http://www.google.cn/intl/zh-CN/chrome/" target="_blank">Chrome</a>
	或以下浏览器：
	<a href="www.mozilla.org/en-US/firefox/‎" target="_blank">Firefox</a> /
	<a href="http://www.apple.com.cn/safari/" target="_blank">Safari</a> /
	<a href="http://www.opera.com/" target="_blank">Opera</a> /
	<a href="http://windows.microsoft.com/en-us/internet-explorer/download-ie" target="_blank">Internet Explorer 9+</a>
</div>
<![endif]-->

<#-- 顶部导航栏 -->
<!-- Fixed navbar -->
<header class="site-header headroom">
    <div class="container">
        <nav class="navbar" role="navigation">
            <div class="navbar-header">
<#-- 移动端汉堡菜单按钮 -->
                <button class="navbar-toggle" type="button" data-toggle="collapse" data-target=".navbar-collapse">
                    <span class="icon-bar"></span><span class="icon-bar"></span><span class="icon-bar"></span>
                </button>
<#-- 站点Logo，链接到首页 -->
                <a class="navbar-brand" href="${base}/">
                    <img src="<@resource src=options['site_logo']/>"/>
                </a>
            </div>
            <div class="collapse navbar-collapse">
<#-- 左侧导航链接 -->
                <ul class="nav navbar-nav">
<#-- 如果用户已登录（profile 存在），显示"我的主页"链接 -->
					<#if profile??>
						<li data="user">
							<a href="${base}/users/${profile.id}" nav="user">我的主页</a>
						</li>
					</#if>
<#-- 遍历所有栏目，生成导航链接 -->
					<#list channels as row>
						<li>
							<a href="${base}/channel/${row.id}" nav="${row.name}">${row.name}</a>
						</li>
					</#list>
<#-- 标签页链接 -->
                        <li>
                            <a href="${base}/tags" nav="tags">标签</a>
                        </li>
                </ul>
<#-- 右侧功能区：搜索、写文章、用户菜单 -->
                <ul class="navbar-button list-inline" id="header_user">
<#-- 搜索框，hidden-xs hidden-sm 在中小屏隐藏 -->
                    <li view="search" class="hidden-xs hidden-sm">
                        <form method="GET" action="${base}/search" accept-charset="UTF-8" class="navbar-form navbar-left">
                            <div class="form-group">
                                <input class="form-control search-input mac-style" placeholder="搜索" name="kw" type="text" value="${kw}">
                                <button class="search-btn" type="submit"><i class="fa fa-search"></i></button>
                            </div>
                        </form>
                    </li>

<#-- 已登录用户的功能区 -->
				<#if profile??>
<#-- controls 宏：判断发文功能是否开启，开启才显示"写文章"按钮 -->
                    <@controls name="post">
                        <li>
                            <a href="${base}/post/editing" class="plus"><i class="icon icon-note"></i> 写文章</a>
                        </li>
                    </@controls>
<#-- 用户下拉菜单：头像、昵称、我的主页、编辑资料、后台管理、退出 -->
                    <li class="dropdown">
                        <a href="#" class="user dropdown-toggle" data-toggle="dropdown">
<#-- 头像加时间戳参数防止缓存 -->
                            <img class="img-circle" src="<@resource src=profile.avatar + '?t=' + .now?time />">
                            <span>${profile.name}</span>
                        </a>
                        <ul class="dropdown-menu" role="menu">
                            <li>
                                <a href="${base}/users/${profile.id}">我的主页</a>
                            </li>
                            <li>
                                <a href="${base}/settings/profile">编辑资料</a>
                            </li>
                            <li><a href="${base}/admin">后台管理</a></li>
                            <li><a href="${base}/logout">退出</a></li>
                        </ul>
                    </li>
<#-- 未登录用户的功能区：登录和注册按钮 -->
				<#else>
                    <li><a href="${base}/login" class="btn btn-default btn-sm signup">登录</a></li>
<#-- controls 宏：判断注册功能是否开启 -->
                    <@controls name="register">
                        <li><a href="${base}/register" class="btn btn-primary btn-sm signup">注册</a></li>
                    </@controls>
				</#if>

                </ul>
            </div>
        </nav>
    </div>
</header>

<#-- 导航栏高亮：当前页面的导航项添加 active 类 -->
<script type="text/javascript">
$(function () {
	$('a[nav]').each(function(){
        $this = $(this);
        if($this[0].href == String(window.location)){
            $this.closest('li').addClass("active");
        }
    });
});
</script>
<!-- Header END -->