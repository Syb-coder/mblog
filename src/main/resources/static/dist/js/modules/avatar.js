/**
 * @module avatar
 *
 * 职责：
 *   - 监听头像文件选择事件，异步上传至 /settings/avatar 接口并刷新页面。
 *
 * 依赖：
 *   - require('plugins')：jQuery 工具扩展，提供 upload 上传方法
 *   - layer：上传失败时的消息提示弹层
 *   - 全局 _SUNBLOG.BASE_PATH：站点根路径
 *
 * 暴露接口：
 *   - 无（模块加载即自动绑定事件，无需外部调用）
 */
// SeaJS 模块定义：模块加载即自动绑定头像上传逻辑
define(function(require, exports, module) {
	// 别名 J = jQuery，便于模块内简写引用
	J = jQuery;
	// 加载 plugins 插件扩展，提供 $.fn.upload 文件上传方法
	require('plugins');

	// 头像上传接口地址：/settings/avatar
	var upload_url = _SUNBLOG.BASE_PATH + '/settings/avatar';

	// 监听 #upload_btn（头像选择文件框）change 事件：用户选择文件后触发异步上传
	$('#upload_btn').change(function(){
		// 调用 plugins 扩展的 upload 方法，将所选文件异步上传至 upload_url
		$(this).upload(upload_url, function(data){
            if (data.status == 200) {
				// 上传成功：刷新页面以加载新头像
				window.location.reload();
			} else {
				// 上传失败：使用 layer 弹层提示后端返回的错误信息（icon:5 为哭泣图标）
                layer.msg(data.message, {icon: 5});
			}
		});
	});

});
