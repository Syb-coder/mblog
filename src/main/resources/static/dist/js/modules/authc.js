/**
 * @module authc
 *
 * 职责：
 *   - 判断当前用户登录态（isAuthced）；
 *   - 弹出 AJAX 登录弹层并处理登录提交（showLogin / doPostLogin）。
 *
 * 依赖：
 *   - jQuery：DOM 操作与 AJAX
 *   - 全局 _SUNBLOG：提供 BASE_PATH、LOGIN_TOKEN 等站点配置
 *   - Bootstrap modal：登录弹层
 *
 * 暴露接口：
 *   - module.exports = Authc
 *     - isAuthced() 判断是否已登录
 *     - showLogin() 弹出登录弹层
 *     - doPostLogin() 执行登录 AJAX 提交
 */
// SeaJS 模块定义：通过 define 声明当前模块，require/exports/module 由 SeaJS 注入
define(function(require, exports, module) {
    // 将 jQuery 别名为 J，便于模块内简写引用
    J = jQuery;

    var Authc = {
        /**
         * 判断当前用户是否已登录。
         *
         * 实现说明：
         *   通过检测全局变量 _SUNBLOG.LOGIN_TOKEN 是否存在且非空来判断登录态。
         *
         * @returns {boolean} 已登录返回 true，否则 false
         */
        isAuthced: function () {
            return (typeof(_SUNBLOG.LOGIN_TOKEN) !== 'undefined' && _SUNBLOG.LOGIN_TOKEN.length > 0);
        },
        /**
         * 弹出登录弹层并绑定登录按钮点击事件。
         *
         * 实现说明：
         *   - #login_alert 为 Bootstrap modal 登录弹层；
         *   - #ajax_login_submit 为弹层内的登录按钮，unbind() 先解绑历史事件避免重复绑定，
         *     再绑定 click 触发 doPostLogin。
         */
        showLogin : function () {
            var that = this;
            // 选中 #login_alert 登录弹层并调用 modal() 弹出（Bootstrap modal）
            $('#login_alert').modal();
            // 选中 #ajax_login_submit 登录按钮，先 unbind() 解绑历史事件再绑定 click，
            // 防止多次调用 showLogin 造成事件重复触发
            $('#ajax_login_submit').unbind().click(function () {
                that.doPostLogin();
            });
        },
        /**
         * 执行 AJAX 登录提交。
         *
         * 实现说明：
         *   - 读取 #ajax_login_username 与 #ajax_login_password 输入值；
         *   - POST 至 /api/login 接口，code==0 表示登录成功，刷新页面以加载登录态；
         *   - 失败时在 #ajax_login_message 显示错误信息。
         *
         * @returns {void}
         */
        doPostLogin: function () {
            // 读取登录弹层中的用户名与密码输入值
            var un = $('#ajax_login_username').val();
            var pw = $('#ajax_login_password').val();
            // POST 登录接口 /api/login，提交用户名密码进行认证
            jQuery.post(_SUNBLOG.BASE_PATH + '/api/login', {'username': un, 'password': pw}, function (ret) {
                if (ret && ret.code == 0) {
                    // 登录成功：刷新页面，使服务端重新渲染登录态
                    window.location.reload();
                } else {
                    // 登录失败：在 #ajax_login_message 处展示后端返回的错误信息并显示
                    $('#ajax_login_message').text(ret.message).show();
                }
            });
        }
    };

    // 通过 module.exports 导出 Authc，供其他 SeaJS 模块 require 使用
    module.exports = Authc;
});
