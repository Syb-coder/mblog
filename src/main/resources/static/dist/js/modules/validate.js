/**
 * @module validate
 *
 * 职责：
 *     highlight / unhighlight 样式切换）；
 *   - 为不同业务表单（注册 register、修改密码 updatePassword、
 *
 * 依赖：
 *   - require.async('validation' / 'validation-additional')：jQuery 表单校验
 *
 * 暴露接口：
 */
define(function(require, exports, module) {
    var J = jQuery, _BATH = _MTONS.BASE_PATH;

    /**
     * 默认校验配置。
     *
     * 字段说明：
     *   - errorElement    错误信息使用的标签（p）
     *   - highlight       校验失败时为最近 div 添加 has-error 样式
     *   - unhighlight     校验通过时切换为 has-success 样式
     */
    var _configs = {
        errorElement: "p",
        errorPlacement: function (error, element) {
        	// 添加 Bootstrap help-block 样式，复用表单提示样式
            error.addClass("help-block");
            if ( element.prop( "name" ) === "email" ) {
                error.insertAfter(element.parent());
            } else {
                error.insertAfter(element);
            }
        },
        highlight: function (element, errorClass, validClass) {
            J(element).closest("div").addClass("has-error").removeClass("has-success");
        },
        unhighlight: function (element, errorClass, validClass) {
            J(element).closest("div").addClass("has-success").removeClass("has-error");
        }
    };

    /**
     * 为指定表单绑定校验。
     *
     * 实现说明：
     *   - 将默认 _configs 与传入 configs 合并，后者可覆盖默认项；
     *   - 异步加载 validation 与 validation-additional 后再调用 validate，
     *     避免依赖未就绪导致校验失效。
     *
     * @param {String} formId 表单选择器（如 '#registerForm'）
     * @param {Object} configs 该表单专属的 rules / messages 等配置
     */
    var _bind_validate = function (formId, configs) {
        var options = J.extend({}, _configs, configs);

        require.async(['validation', 'validation-additional'], function () {
            J(formId).validate(options);
        });
    };

    var Validate = {
        /* *
         * 实现说明：
         *     （只能是字母/字母+数字，不少于 5 位）；
         *   - 密码：必填；
         *   - 确认密码：必填，且与 #password 一致。
         *
         * @param {String} formId 表单选择器
         */
        register: function (formId) {
            _bind_validate(formId, {
                rules: {
                    username: {
                        required: true,
                        check_username: true
                    },
                    password: {
                        required: true
                    },
                    password2: {
                        required: true,
                        equalTo: "#password"
                    }
                },
                messages: {
                    username: {
                        required: '请输入用户名',
                        check_username: '只能是字母/字母+数字,不少于5位'
                    },
                    password: {
                        required: '请输入密码'
                    },
                    password2: {
                        required: '请输入确认密码',
                        equalTo: '两次输入的密码不一致'
                    }
                }
            });
        },
        /* *
         * 实现说明：
         *   - 当前密码：必填；
         *   - 新密码：必填；
         *   - 确认密码：必填，且与新密码 #password 一致。
         *
         * @param {String} formId 表单选择器
         */
        updatePassword: function (formId) {
            _bind_validate(formId, {
                rules: {
                    oldPassword: 'required',
                    password: 'required',
                    password2: {
                        required: true,
                        equalTo: "#password"
                    }
                },
                messages: {
                    oldPassword: '请输入当前密码',
                    password: '请输入新密码',
                    password2: {
                        required: '请输入确认密码',
                        equalTo: '两次输入的密码不一致'
                    }
                }
            });
        },

        /* *
         * 实现说明：
         *   - 昵称：必填。
         *
         * @param {String} formId 表单选择器
         */
        updateProfile: function (formId) {
            _bind_validate(formId, {
                rules: {
                    name: 'required'
                },
                messages: {
                    name: '请输入昵称'
                }
            });
        }
    };

    module.exports = Validate;
});
