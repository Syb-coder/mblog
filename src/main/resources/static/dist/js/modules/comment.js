/**
 * @module comment
 *
 * 职责：
 *   - 加载并渲染文章评论列表，支持分页（pager）；
 *   - 提供评论提交能力（含登录校验、内容长度校验）；
 *
 * 依赖：
 *   - require('plugins')：jQuery 工具扩展（page、proxy 等）
 *   - require('authc')：用户登录态校验
 *   - layer：消息提示弹层
 *
 * 暴露接口：
 */
define(function(require, exports, module) {
	J = jQuery;
	require('plugins');
    require('owo-css');
    require('owo');

	var Authc = require('authc');
	
	var Comment = {
        name : 'Comment',
        /* *
         * 实现说明：
         *   - 将传入 options 与 defaults 合并；
         *   - load 为 false 时不加载数据（用于纯展示场景）；
         *   - 否则绑定 DOM 事件并触发首次评论加载。
         *
         * @param {Object} options 配置项，详见 defaults
         * @returns {boolean|void} load 为 false 时返回 false，否则无返回值
         */
        init : function (options) {
        	this.options = $.extend({}, this.defaults, options);

        	if (!this.options.load) {
        		return false;
			}
        	this.bindEvents();
        },
        /**
         * 默认配置项。
         *
         * 字段说明：
         *   - load       是否启用评论加载（false 时跳过数据请求）
         *   - load_url   评论列表接口地址
         *   - post_url   评论提交接口地址
         *   - toId       被评论的目标对象 ID（如文章 ID）
         *   - pageSize   单页评论数量
         */
        defaults: {
        	load: true,
        	load_url : null,
        	post_url : null,
        	toId : 0,
			pageSize :6,
            // callback
            onLoad : function (i, data) {}
        },
        /* *
         * 实现说明：
         *   - 首次加载第一页评论；
         *   - 绑定 #btn-chat 提交按钮点击事件，读取评论框内容并提交；
         */
        bindEvents : function () {
        	var that = this;
        	// 进入页面后立即加载第一页评论
        	that.pageCallback(1);
        	// 绑定评论提交按钮：读取文本与父评论 pid 后发起提交
        	$('#btn-chat').click(function () {
        		var text = $('#chat_text').val();
        		var pid = $('#chat_pid').val();
        		that.post(that.options.toId, pid, text);
        	});

        	// 初始化 OwO 表情面板：#face-btn 为触发按钮，#chat_text 为表情插入目标，
        	// 通过 OwO.json 加载表情数据，点击表情后自动插入到评论输入框
            new OwO({
                logo: '<i class="fa fa-smile-o fa-2"></i>',
                container: document.getElementById('face-btn'),
                target: document.getElementById('chat_text'),
                api: _SUNBLOG.BASE_PATH + '/dist/vendors/owo/OwO.json',
                position: 'down',
                width: '600px',
                maxHeight: '250px'
            });
        },
        
        /**
         * 重新加载评论列表（外部调用入口）。
         *
         * 实现说明：
         *   简单代理到 pageCallback(1)，回到第一页并刷新列表。
         */
        onLoad : function () {
        	this.pageCallback(1);
        },
        
        /**
         * 分页加载评论列表。
         *
         * 实现说明：
         *   - 通过 jQuery.getJSON 请求 load_url，参数携带 pageSize 与 pageNo；
         *   - 后端返回 Spring Data 标准分页结构（content / totalElements / totalPages / size）；
         *   - 调用 opts.onLoad 回调生成每条评论的 HTML，并追加到 #chat_container；
         *   - 列表为空时显示"占沙发"占位文案；
         *   - 总页数大于 1 时渲染 #pager 分页条，pageCallback 作为翻页回调。
         *
         * @param {number} pn 页码，从 1 开始
         */
        pageCallback: function (pn) {
        	var opts = this.options;
        	var that = this;
        	
        	var $list = $("#chat_container");
        	var html = '';

        	J.getJSON(opts.load_url, {pageSize : opts.pageSize, pageNo: pn}, function (ret) {
        		
        		// 更新页面上的评论总数显示
        		$('#chat_count').html(ret.totalElements);
        		
          		jQuery.each(ret.content, function(i, n) {
    				var item = opts.onLoad.call(this, i, n);

    				html += item;
          		});
        	
	        	// 清空旧列表后追加新内容，避免重复渲染
	        	$list.empty().append(html);
	        	
	    		if (ret.size < 1) {
	    			// 当前页无数据时显示占位提示，引导用户发表首条评论
	    			$list.append('<li><p>还没有评论, 快来占沙发吧!</p></li>');
	    		}
	    		if (ret.totalPages > 1) {
	    			// 多页时渲染分页条，J.proxy 保证翻页回调内 this 指向 Comment 实例
	    			$("#pager").page(ret, J.proxy(that, 'pageCallback'));
	    		}
        	});
        },
        
        /**
         * 提交一条评论。
         *
         * 实现说明：
         *   - 未登录时弹出登录框并中止提交（评论必须由登录用户发表）；
         *   - 校验内容非空且长度不超过 255（前端兜底，后端会再次校验）；
         *   - 使用同步 AJAX（async:false）提交，确保用户看到提交结果后再继续操作；
         *   - 成功后清空输入框、隐藏回复框、重置 pid，并刷新第一页评论。
         *
         * @param {number} toId 被评论的目标对象 ID
         * @param {number} pid 父评论 ID（0 表示顶级评论）
         * @param {string} text 评论内容
         * @returns {boolean} 校验失败时返回 false
         */
        post: function (toId, pid, text) {
        	var opts = this.options;
        	var that = this;

			// 登录态校验：未登录用户不允许评论，弹出登录框并终止流程
			if (!Authc.isAuthced()) {
				Authc.showLogin();
				return false;
			}

			// 内容非空校验，空内容提示用户输入
        	if (text.length == 0) {
        		layer.msg('请输入内容再提交!', {icon: 2});
        		return false;
        	}
        	// 内容长度校验，超过 255 字符直接拦截（前端兜底，避免后端报错）
        	if (text.length > 255) {
        		layer.msg('内容过长，请输入140以内个字符', {icon: 2});
        		return false;
        	}
        	
        	jQuery.ajax({
        		url: opts.post_url, 
        		data: {'toId': toId,'pid': pid, 'text': text},
        		dataType: "json",
        		type :  "POST",
        		cache : false,
        		// 同步提交：保证用户在结果返回前不会重复点击或离开页面
        		async: false,
        		error : function(i, g, h) {
        			layer.msg('发送错误', {icon: 2});
        		},
        		success: function(ret){
        			if(ret){
        				if (ret.code >= 0) {
        					// 提交成功：清空输入框、隐藏回复框、重置 pid，再刷新评论列表
        					layer.msg(ret.message, {icon: 1});
        					$('#chat_text').val('');
        					$('#chat_reply').hide();
        					$('#chat_pid').val('0');
        					//window.location.reload();
        					that.pageCallback(1);
        				} else {
        					layer.msg(ret.message, {icon: 5});
        				}
        			}
              	}
        	});
        }
    };
	
	/* *
	 * @param {Object} opts 配置项，会与 Comment.defaults 合并
	 */
	exports.init = function (opts) {
		Comment.init(opts);
	}
	
});
