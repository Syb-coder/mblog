/**
 * @module post
 *
 * 职责：
 *   - 标签录入（tagsinput）绑定；
 *   - 文章封面缩略图异步上传与回填；
 *
 * 依赖：
 *   - require('tagsinput')：标签录入组件
 *   - require.async('validation' / 'validation-additional')：jQuery 表单校验
 *   - 可选的 tinyMCE：富文本编辑器，提交前需触发内容同步
 *
 * 暴露接口：
 *   - exports.init() 初始化文章发布视图
 */
define(function(require, exports, module) {
	J = jQuery;
	require('tagsinput');

	// 文章发布视图构造函数，方法挂在原型上以便复用
	var PostView = function () {};
	
	PostView.prototype = {
        name : 'PostView',
        /**
         * 初始化视图，绑定所有相关事件。
         */
        init : function () {
        	this.bindEvents();
        },
        /**
         * 默认配置项。
         *
         * 字段说明：
         *   - type            内容类型（默认 text）
         *   - defaultEditor   默认富文本编辑器（ueditor）
         *   - maxFiles        最大上传文件数
         */
        defaults: {
        	type : 'text',
        	defaultEditor: 'ueditor',
        	maxFiles : 6,
        },
        /**
         * 绑定文章发布相关的所有事件。
         *
         * 实现说明：
         *   - 绑定标签、校验、上传三个子能力；
         *   - 通过 button[event="post_submit"] 选择器捕获提交按钮点击，
         *     读取 data-status 写入隐藏域 status，再统一提交 #submitForm。
         */
        bindEvents : function () {
        	var that = this;

        	that.bindTagit();
        	that.bindValidate();
        	that.bindUpload();

            $('button[event="post_submit"]').click(function () {
                var status = $(this).data('status');
                // 把按钮声明的 status 写入隐藏域，由后端统一处理不同状态
                $("input[name='status']").val(status);
                $("#submitForm").submit();
            });
        },
        
        /**
         * 初始化标签输入组件。
         *
         * 实现说明：
         *   - maxTags:4 限制最多 4 个标签，避免滥用；
         *   - trimValue:true 自动去除标签首尾空白。
         */
        bindTagit : function () {
            $('#tags').tagsinput({
                maxTags: 4,
                trimValue: true
            });
        },
        
        /**
         * 绑定封面图上传事件。
         *
         * 实现说明：
         *   - 上传至 /post/upload，附带 crop=thumbnail_post_size 用于后端裁剪；
         *   - 成功后把返回的图片 path 设置为 #thumbnail_image 的背景，
         *     并同步写入隐藏域 #thumbnail，供表单提交时携带。
         */
        bindUpload : function () {
            $('#upload_btn').change(function(){
                $(this).upload(_MTONS.BASE_PATH + '/post/upload?crop=thumbnail_post_size', function(data){
                    if (data.status == 200) {
                        var path = data.path;
                        // 用背景图方式展示缩略图预览，避免额外 img 元素
                        $("#thumbnail_image").css("background", "url(" + path + ") no-repeat scroll center 0 rgba(0, 0, 0, 0)");
                        // 同步图片地址到隐藏域，供表单提交时携带
                        $("#thumbnail").val(path);
                    }
                });
            });
        },

        /* *
         * 实现说明：
         *   - 异步加载 validation 与 validation-additional，避免阻塞首屏；
         *   - 在 submit 钩子中调用 tinyMCE.triggerSave()，把富文本编辑器内容
         *     同步到对应 textarea，确保校验能拿到最新内容；
         *   - highlight/unhighlight 切换 has-error / has-success 样式。
         */
        bindValidate: function () {
            require.async(['validation'], function () {
                require.async(['validation-additional'], function () {
                    $("#submitForm").submit(function () {
                    	// 富文本编辑器内容需先同步到 textarea，校验才能取到最新值
                        if (typeof tinyMCE == "function") {
                            tinyMCE.triggerSave();
                        }
                    }).validate({
                        ignore: "",
                        rules: {
                            title: 'required',
                            channelId: 'required',
                            content: {
                                required: true,
                                check_editor: true
                            }
                        },
                        messages: {
                            title: '请输入标题',
                            channelId: '请选择栏目',
                            content: {
                                required: '内容不能为空',
                                check_editor: '内容不能为空'
                            }
                        },
                        errorElement: "p",
                        errorPlacement: function (error, element) {
                        	// 给错误节点添加 help-block 样式，复用 Bootstrap 表单提示样式
                            error.addClass("help-block");
                            if (element.prop("type") === "checkbox") {
                                error.insertAfter(element.parent("label"));
                            } else if (element.is("textarea")) {
                                error.insertAfter(element.closest(".form-group"));
                            } else {
                                error.insertAfter(element);
                            }
                        },
                        highlight: function (element, errorClass, validClass) {
                        	// 校验失败：在最近的 div 上切换为错误样式
                            $(element).closest("div").addClass("has-error").removeClass("has-success");
                        },
                        unhighlight: function (element, errorClass, validClass) {
                        	// 校验通过：切换为成功样式，便于用户感知
                            $(element).closest("div").addClass("has-success").removeClass("has-error");
                        }
                    });
                });
            });
        }
    };
	
	exports.init = function () {
	    new PostView().init();
	}
});
