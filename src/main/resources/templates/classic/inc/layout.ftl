<#-- 前台主布局文件：定义页面的整体 HTML 骨架，子页面通过 layout.block 填充各区域 -->
<#-- Layout -->
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<#-- sunblog ASCII Art 标识，${site.version} 显示项目版本号 -->
    <!--
    ------------------------------------------------------
      ___ _   _ ___ ___ ___ _ _____   _____ ___ _____ ____
     / __| | | / __/ __/ __| |/ / _ \ / _ \ __/ __// ___/
     \__ \ |_| \__ \__ \__ \   <| (_) | (_) | _\_ \ (
     |___/\___/|___/___/___/_|\_\\___/ \___/_| \___\)
    ------------------------------------------------------------
    version: ${site.version}
    ------------------------------------------------------------
    -->
    <meta charset="utf-8">
<#-- 响应式布局：宽度跟随设备，初始缩放1.0 -->
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
<#-- IE 兼容模式：优先使用最新渲染引擎 -->
    <!--[if IE]>
    <meta http-equiv='X-UA-Compatible' content='IE=edge,chrome=1'/>
    <![endif]-->

<#-- 页面标题块，子页面可覆盖 -->
    <@layout.block name="title">
        <title>${options['site_name']}</title>
    </@layout.block>

<#-- SEO 关键词块 -->
    <@layout.block name="keywords">
        <meta name="keywords" content="${options['site_keywords']}">
    </@layout.block>

<#-- SEO 描述块 -->
    <@layout.block name="keywords">
        <meta name="description" content="${options['site_description']}">
    </@layout.block>

<#-- 输出后台配置的额外 meta 标签 -->
    ${options['site_metas']}

<#-- 加载页面加载进度条样式 -->
    <link href="${base}/dist/vendors/pace/themes/pace-theme-minimal.css" rel="stylesheet"/>
<#-- 加载 Bootstrap 框架样式 -->
    <link href="${base}/dist/vendors/bootstrap/css/bootstrap.min.css" rel="stylesheet"/>

<#-- 加载编辑器、插件、主题样式 -->
    <link href="${base}/dist/css/editor.css" rel="stylesheet"/>
    <link href="${base}/dist/css/plugins.css" rel="stylesheet"/>
    <link href="${base}/theme/classic/dist/css/style.css" rel="stylesheet"/>

<#-- 加载图标库：Simple Line Icons 和 Font Awesome -->
    <link href="${base}/dist/vendors/simple-line-icons/css/simple-line-icons.css" rel="stylesheet"/>
    <link href="${base}/dist/vendors/font-awesome/css/font-awesome.min.css" rel="stylesheet"/>

<#-- 页面加载进度条脚本 -->
    <script src="${base}/dist/vendors/pace/pace.min.js"></script>

<#-- 加载 jQuery、弹窗层(layer)、Bootstrap JS -->
    <script src="${base}/dist/js/jquery.min.js"></script>
    <script src="${base}/dist/vendors/layer/layer.js"></script>
    <script src="${base}/dist/vendors/bootstrap/js/bootstrap.min.js"></script>

<#-- 全局 JavaScript 变量：项目根路径和当前登录用户ID -->
    <script type="text/javascript">
        var _SUNBLOG = _SUNBLOG || {};
        _SUNBLOG.BASE_PATH = '${base}';
        _SUNBLOG.LOGIN_TOKEN = '${profile.id}';
    </script>

<#-- 加载 SeaJS 模块加载器及其配置 -->
    <script src="${base}/dist/js/sea.js"></script>
    <script src="${base}/dist/js/sea.config.js"></script>

<#-- 网站图标（Favicon） -->
    <!-- Favicons -->
    <link href="<@resource src=options['site_favicon']/>" rel="apple-touch-icon-precomposed" />
    <link href="<@resource src=options['site_favicon']/>" rel="shortcut icon" />

<#-- head 块：子页面可在此追加额外的 CSS/JS -->
    <@layout.block name="head">
        <script src="${base}/dist/js/sea.js"></script>
        <script src="${base}/dist/js/sea.config.js"></script>
    </@layout.block>
</head>
<body>
<#-- 头部区域块，默认引入 header.ftl -->
    <!-- header -->
    <@layout.block name="header">
        <#include "/classic/inc/header.ftl"/>
    </@layout.block>
    <!-- /header -->

<#-- 主内容区域块 -->
    <!-- content -->
    <div class="wrap">
        <!-- Main -->
        <div class="container">
            <@layout.block name="contents">
                <h2>Contents will be here</h2>
            </@layout.block>
        </div>
    </div>
    <!-- /content -->

<#-- 底部区域块，默认引入 footer.ftl -->
    <!-- footer -->
    <@layout.block name="footer">
        <#include "/classic/inc/footer.ftl"/>
    </@layout.block>
</body>
</html>