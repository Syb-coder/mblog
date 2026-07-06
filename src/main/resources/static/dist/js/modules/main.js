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
	var Authc = require('authc');

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
    
	// 绑定按钮事件
	/**
	 * 绑定全站通用按钮事件。
	 *
	 * 实现说明：
	 *   - 收藏（a[rel=favor]）：未登录先弹登录框；登录后调用 /user/favor 接口，
	 *     成功时将收藏数 +1，失败时弹出后端错误消息。
	 *
	 * 注：下方 pjax 相关代码已被注释关闭，保留以便后续按需启用。
	 */
	var bindClickEvent = function () {
		// Favor
		// 收藏按钮：通过 rel=favor 标识，data-id 携带目标文章 ID
		$('a[rel=favor]').click(function () {
			var id = $(this).attr('data-id');

			// 收藏属用户行为，未登录时弹出登录框并中止
			if (!Authc.isAuthced()) {
				Authc.showLogin();
				return false;
			}

			if (parseInt(id) > 0) {
				// 调用后端收藏接口，BASE_PATH 用于适配反向代理/部署上下文
				jQuery.getJSON(_MTONS.BASE_PATH +'/user/favor', {'id': id}, function (ret) {
					if (ret.code >=0) {
						// 成功：读取当前收藏数并 +1，避免重新拉取列表
						var favors = $('#favors').text();
						$('#favors').text(parseInt(favors) + 1);
					} else {
						// 业务失败：弹出后端返回的错误消息（icon:5 表示失败）
						layer.msg(ret.message, {icon: 5});
					}
				});
			}
		});

		//$(document).pjax('a[rel=pjax]', '#wrap', {
		//	fragment: '#wrap',
		//	timeout: 10000,
		//	maxCacheLength: 0
		//});
	}

	/* *
	 * 实现说明：
	 *   - 启用返回顶部按钮交互；
	 *   - 绑定全站通用按钮事件；
	 *   - 初始化所有带 data-toggle="tooltip" 的元素为 Bootstrap tooltip。
	 */
    exports.init = function () {
    	// imagesLazyload();
    	backToTop();
		bindClickEvent();
		// Bootstrap tooltip 初始化：依赖 data-toggle="tooltip" 属性
        $('[data-toggle="tooltip"]').tooltip();
    };
    
});
