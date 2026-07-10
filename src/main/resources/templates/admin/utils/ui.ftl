<#-- 后台管理主布局宏：定义后台页面的整体 HTML 骨架（头部、侧边栏、内容区、底部） -->
<#-- layout -->
<#macro layout>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>后台管理 - ${options['site_name']}</title>

<#-- 网站图标 -->
    <!-- Favicons -->
    <link rel="apple-touch-icon-precomposed" href="${base}/dist/images/logo.png"/>
    <link rel="shortcut icon" href="${base}/dist/images/logo.png"/>

<#-- Bootstrap 框架样式 -->
    <!-- Bootstrap -->
    <link href="${base}/dist/vendors/bootstrap/css/bootstrap.min.css" rel="stylesheet">
<#-- Font Awesome 图标库 -->
    <!-- Font Awesome -->
    <link href="${base}/dist/vendors/font-awesome/css/font-awesome.min.css" rel="stylesheet">

<#-- 后台主题样式（AdminLTE 风格） -->
    <!-- Theme Style -->
    <link href="${base}/theme/admin/dist/css/site.css" rel="stylesheet">
    <link href="${base}/theme/admin/dist/css/site.addons.css" rel="stylesheet">
    <link href="${base}/theme/admin/dist/css/skins/skin-blue.css" rel="stylesheet">

<#-- 全局 JavaScript 变量：项目根路径和当前登录用户ID -->
    <script type="text/javascript">
        var _SUNBLOG = _SUNBLOG || {};
        _SUNBLOG.BASE_PATH = '${base}';
        _SUNBLOG.LOGIN_TOKEN = '${profile.id}';
    </script>

<#-- 加载 jQuery、Bootstrap、表单验证、弹窗层等 JS 库 -->
    <!-- jQuery -->
    <script src="${base}/dist/js/jquery.min.js"></script>
    <script src="${base}/dist/js/plugins.js"></script>
    <!-- Bootstrap -->
    <script src="${base}/dist/vendors/bootstrap/js/bootstrap.min.js"></script>
<#-- jQuery 表单验证插件及中文提示 -->
    <script src='${base}/dist/vendors/jquery-validation/jquery.validate.min.js'></script>
    <script src='${base}/dist/vendors/jquery-validation/additional-methods.js'></script>
    <script src='${base}/dist/vendors/jquery-validation/localization/messages_zh.min.js'></script>
<#-- Layer 弹窗库 -->
    <script src="${base}/dist/vendors/layer/layer.js"></script>
<#-- 后台主题 JS -->
    <script src="${base}/theme/admin/dist/js/site.js"></script>
    <script src="${base}/theme/admin/dist/js/site.base.js"></script>
</head>
<#-- AdminLTE 主题类：skin-blue 蓝色皮肤，sidebar-mini 迷你侧边栏 -->
<body class="hold-transition skin-blue sidebar-mini">
<div class="wrapper">
<#-- 顶部导航栏 -->
    <!-- Main Header -->
    <header class="main-header">
<#-- Logo 区域：mini 状态显示 SUN，展开状态显示 SUNBLOG -->
        <a href="${base}/index" class="logo">
            <span class="logo-mini">SUN</span>
            <span class="logo-lg"><b>SUN</b>BLOG</span>
        </a>
        <nav class="navbar navbar-static-top">
<#-- 侧边栏切换按钮 -->
            <a href="#" class="sidebar-toggle" data-toggle="push-menu" role="button">
                <span class="sr-only">切换导航</span>
            </a>
            <div class="navbar-custom-menu">
                <ul class="nav navbar-nav">
<#-- 跳转到前台的按钮 -->
                    <li><a href="/" title="跳转到前台" target="_blank"><i class="fa fa-television"></i></a></li>
<#-- 用户下拉菜单 -->
                    <li class="dropdown user user-menu">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown">
<#-- 用户头像 -->
                            <img src="<@resource src=profile.avatar/>" class="user-image" alt="User Image">
<#-- 用户名，hidden-xs 在小屏隐藏 -->
                            <span class="hidden-xs">${profile.username}</span>
                        </a>
                        <ul class="dropdown-menu">
<#-- 下拉菜单顶部：大头像 + 用户名 -->
                            <li class="user-header">
                                <img src="<@resource src=profile.avatar/>" class="img-circle" alt="User Image">
                                <p>${profile.username}</p>
                            </li>
<#-- 下拉菜单底部：个人资料和退出按钮 -->
                            <li class="user-footer">
                                <div class="pull-left">
                                    <a href="${base}/settings/profile" class="btn btn-default btn-flat">个人资料</a>
                                </div>
                                <div class="pull-right">
                                    <a href="${base}/logout" class="btn btn-default btn-flat">退出登录</a>
                                </div>
                            </li>
                        </ul>
                    </li>
                </ul>
            </div>
        </nav>
    </header>

<#-- 左侧边栏 -->
    <!-- Left side column -->
    <aside class="main-sidebar">
        <section class="sidebar">
<#-- 侧边栏用户信息面板 -->
            <div class="user-panel">
                <div class="pull-left image">
                    <img src="<@resource src=profile.avatar/>" class="img-circle" alt="User Image">
                </div>
                <div class="pull-left info">
                    <p>${profile.username}</p>
                    <a href="#"><i class="fa fa-circle text-success"></i> 在线</a>
                </div>
            </div>

<#-- 侧边栏菜单 -->
            <!-- Sidebar Menu -->
            <ul class="sidebar-menu" data-widget="tree">
                <li class="header">菜单</li>
<#-- 仪表盘菜单项，默认高亮 -->
                <li>
                    <a href="${base}/admin" class="active"><i class="fa fa-dashboard"></i><span>仪表盘</span></a>
                </li>
<#-- menus 宏：动态加载后台菜单列表 -->
                <@menus>
                    <#list results as menu>
                        <li><a href="${base}/${menu.url}"><i class="${menu.icon}"></i><span>${menu.name}</span></a></li>
                    </#list>
                </@menus>
            </ul>
        </section>
    </aside>

<#-- 主内容区域，<#nested/> 将子页面内容插入此处 -->
    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <#nested/>
    </div>

<#-- 底部版权信息 -->
    <!-- Main Footer -->
    <footer class="main-footer">
        <!-- To the right -->
        <div class="pull-right hidden-xs">${site.version}</div>
        <!-- Default to the left -->
        <strong>版权所有 &copy; 2026</strong> 保留所有权利。
    </footer>
</div>
</body>
</html>
</#macro>

<#-- 后台分页宏：与前台 utils.ftl 中的 pager 宏逻辑相同，但样式适配后台 AdminLTE 主题 -->
<#macro pager url p spans>
<#-- 计算中间页码的跨度 -->
    <#local span = (spans - 3)/2 />
<#-- 当前页码（从1开始） -->
    <#local pageNo = p.number + 1 />
<#-- 拼接分页 URL 参数 -->
    <#if (url?index_of("?") != -1)>
        <#local cURL = (url + "&pageNo=") />
    <#else>
        <#local cURL = (url + "?pageNo=") />
    </#if>

<ul class="pagination no-margin pull-right">
<#-- 上一页按钮 -->
    <#if (pageNo > 1)>
        <#local prev = pageNo - 1 />
        <li><a class="prev" href="${cURL}${prev}" pageNo="1">&nbsp;<i class="fa fa-angle-left"></i>&nbsp;</a></li>
    </#if>

<#-- 计算总显示页码数 -->
    <#local totalNo = span * 2 + 3 />
    <#local totalNo1 = totalNo - 1 />
<#-- 页码省略逻辑：与前台分页相同，分三种情况处理 -->
    <#if (p.totalPages > totalNo)>
<#-- 情况1：当前页靠近首页 -->
        <#if (pageNo <= span + 2)>
            <#list 1..totalNo1 as i>
                <@pagelink pageNo, i, cURL/>
            </#list>
            <@pagelink 0, 0, "#"/>
            <@pagelink pageNo, p.totalPages, cURL />
<#-- 情况2：当前页靠近末页 -->
        <#elseif (pageNo > (p.totalPages - (span + 2)))>
            <@pagelink pageNo, 1, cURL />
            <@pagelink 0, 0, "#"/>
            <#local num = p.totalPages - totalNo + 2 />
            <#list num..p.totalPages as i>
                <@pagelink pageNo, i, cURL/>
            </#list>
<#-- 情况3：当前页在中间 -->
        <#else>
            <@pagelink pageNo, 1, cURL />
            <@pagelink 0 0 "#" />
            <#local num = pageNo - span />
            <#local num2 = pageNo + span />
            <#list num..num2 as i>
                <@pagelink pageNo, i, cURL />
            </#list>
            <@pagelink 0, 0, "#"/>
            <@pagelink pageNo, p.totalPages, cURL />
        </#if>
<#-- 总页数不多时直接显示所有页码 -->
    <#elseif (p.totalPages > 1)>
        <#list 1..p.totalPages as i>
            <@pagelink pageNo, i, cURL />
        </#list>
    <#else>
        <@pagelink 1, 1, cURL/>
    </#if>

<#-- 下一页按钮 -->
    <#if (pageNo lt p.totalPages)>
        <#local next = pageNo + 1/>
        <li><a href="${cURL}${next}" pageNo="${next}">&nbsp;<i class="fa fa-angle-right"></i>&nbsp;</a></li>
    </#if>
</ul>
</#macro>

<#-- 后台页码链接宏：渲染单个页码项 -->
<#macro pagelink pageNo idx url>
<#-- idx 为0时渲染省略号 -->
    <#if (idx == 0)>
    <li><span>...</span></li>
<#-- 当前页高亮显示 -->
    <#elseif (pageNo == idx)>
    <li class="active"><a href="javascript:void(0);"><span>${idx}</span></a></li>
<#-- 其他页码可点击跳转 -->
    <#else>
    <li><a href="${url}${idx}">${idx}</a></li>
    </#if>
</#macro>