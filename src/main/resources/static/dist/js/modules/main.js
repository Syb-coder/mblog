/**
 * @module main
 *
 * 职责：
 *   - 提供"返回顶部"按钮的滚动监听与点击动画；
 *   - 绑定全站通用按钮事件（如收藏 favor）；
 *   - 初始化 Bootstrap tooltip。
 *
 * 依赖：
 *   - require('authc')：收藏操作前的登录态校验
 *   - layer：失败提示弹层
 *
 * 暴露接口：
 *   - exports.init() 初始化全站通用交互
 */
define(function(require, exports, module) {
    var plugins = require('plugins');

	// wpex 主题本地化文案，预留供主题相关组件使用
    var wpexLocalize = {
    		"mobileMenuOpen" : "Click here to navigate",
    		"mobileMenuClosed" : "Close navigation",
    		"isOriginLeft" : "1"
    	};
    
    // 图片懒加载
    // var imagesLazyload = function () {
    // 	require.async('lazyload', function () {
    // 		$("img").lazyload({
	//    	   		 placeholder: _MTONS.BASE_PATH + '/dist/images/spinner.gif',
	//    	   		 effect: "fadeIn"
	//    	   	});
    //     });
    // }
    
    // 返回顶部
    /**
     * 初始化"返回顶部"按钮交互。
     *
     * 实现说明：
     *   - 监听 window 滚动事件，滚动超过 100px 时淡入返回顶部按钮；
     *   - 点击按钮后用 400ms 动画将 html,body 滚动到顶部。
     */
    var backToTop = function () {
    	var $window = $(window);
    	// 选择器 a.site-scroll-top 对应主题中的返回顶部按钮元素
    	var $scrollTopLink = $( 'a.site-scroll-top' );
		$window.scroll(function () {
			// 滚动距离 > 100 才显示按钮，避免在页面顶部出现冗余控件
			if ($(this).scrollTop() > 100) {
				$scrollTopLink.fadeIn();
			} else {
				$scrollTopLink.fadeOut();
			}
		});		
		$scrollTopLink.on('click', function() {
			$( 'html, body' ).animate({scrollTop:0}, 400);
			return false;
		} );
    }
    
	/* *
	 * 实现说明：
	 *   - 启用返回顶部按钮交互；
	 *   - 初始化所有带 data-toggle="tooltip" 的元素为 Bootstrap tooltip。
	 */
    exports.init = function () {
    	backToTop();
		// Bootstrap tooltip 初始化：依赖 data-toggle="tooltip" 属性
        $('[data-toggle="tooltip"]').tooltip();
    };
    
});
