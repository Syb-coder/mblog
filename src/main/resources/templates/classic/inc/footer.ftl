<#-- 页脚区域 -->
<footer class="footer">
    <div class="container">
        <div class="footer-row">
            <nav class="footer-nav">
<#-- 站点Logo，链接回首页 -->
                <a class="footer-nav-item footer-nav-logo" href="${base}/">
                    <img src="<@resource src=options['site_logo']/>" alt="sunblog"/>
                </a>
<#-- 显示版权信息和备案号，来自后台系统配置 -->
                <span class="footer-nav-item">${options['site_copyright']}</span>
                <span class="footer-nav-item">${options['site_icp']}</span>
            </nav>
            <div class="gh-foot-min-back hidden-xs hidden-sm">
                <!-- 请保留此处标识-->
<#-- sunblog 版权标识，hidden-xs hidden-sm 在小屏幕隐藏 -->
                <span class="footer-nav-item">由 sunblog 强力驱动</span>
            </div>
        </div>
    </div>
</footer>

<#-- 回到顶部按钮 -->
<a href="#" class="site-scroll-top">
    <i class="icon-arrow-up"></i>
</a>

<#-- 使用 SeaJS 模块加载器初始化前端主逻辑 -->
<script type="text/javascript">
    seajs.use('main', function (main) {
        main.init();
    });
</script>