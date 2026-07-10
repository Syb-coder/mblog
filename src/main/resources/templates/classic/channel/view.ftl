<#-- 文章详情页：展示文章内容、标签、社交分享、评论区 -->
<#-- 继承主布局模板 -->
<@layout.extends name="/inc/layout.ftl">
    <@layout.put block="title">
        <title>${view.title} - ${options['site_name']}</title>
    </@layout.put>

<#-- SEO 关键字和描述 -->
    <@layout.put block="keywords">
        <meta name="keywords" content="view.keywords?default(options['site_keywords'])">
    </@layout.put>

    <@layout.put block="description">
        <meta name="description" content="view.description?default(options['site_description'])">
    </@layout.put>

    <@layout.put block="contents">
        <div class="row main">
            <#-- 左侧主区域：占 9/12 列，展示文章详情 -->
            <div class="col-xs-12 col-md-9 side-left topics-show">
                <!-- view show -->
                <div class="topic panel panel-default">
<#-- 文章标题和元信息（作者、发布时间、阅读量） -->
                    <div class="infos panel-heading">
                        <h1 class="panel-title topic-title">${view.title}</h1>
                        <div class="meta inline-block">
                            <a class="author" href="${base}/users/${view.author.id}">
                            ${view.author.name}
                            </a>
<#-- timeAgo 函数将日期转为"xx分钟前"等友好格式 -->
                            <abbr class="timeago">${timeAgo(view.created)}</abbr>
<#-- 如果更新时间与创建时间不同，显示更新时间 -->
                            <#-- hidden-xs 在小屏幕隐藏更新时间 -->
                            <#if view.updated?? && view.updated?string('yyyy-MM-dd HH:mm') != view.created?string('yyyy-MM-dd HH:mm')>
                            <abbr class="hidden-xs">⋅ 更新于 ${timeAgo(view.updated)}</abbr>
                            </#if>
                            <abbr>⋅ ${view.views} 阅读</abbr>
                        </div>
                        <#-- clearfix 清除浮动 -->
                        <div class="clearfix"></div>
                    </div>

<#-- 文章正文内容（已渲染的 HTML） -->
                    <div class="content-body entry-content panel-body ">
                        <div class="markdown-body">
                        ${view.content}
                        </div>
                    </div>
<#-- 文章标签列表 -->
                    <#-- 遍历文章标签数组，每个标签链接到对应标签页 -->
                    <div class="panel-footer operate">
                        <#list view.tagsArray as tag>
                            <span>
                                <a class="label label-default" href="${base}/tag/${tag}/">#${tag}</a>
                            </span>
                        </#list>
                    </div>
<#-- 社交分享按钮（QQ、微博、微信等） -->
                    <#-- hidden-xs 小屏幕隐藏分享按钮 -->
                    <div class="panel-footer">
                        <div class="hidden-xs">
                            <#-- social-share 分享插件，data-sites 指定支持的分享平台 -->
                            <div class="social-share" data-sites="qq, weibo, wechat, qzone, facebook, twitter, google"></div>
                        </div>
                        <div class="clearfix"></div>
                    </div>
<#-- 展开全文按钮（长文章截断时使用） -->
                    <div class="more-box">
                        <a class="btn btn-fulltext" data-toggle="fulltext">
                            <i class="icon icon-arrow-down" aria-hidden="true"></i> 阅读全部
                        </a>
                    </div>
                </div>

                <!-- Comments -->
<#-- controls 宏：检查评论功能是否开启 -->
                <@controls name="comment">
                <div id="chat" class="chats shadow-box">
                    <div class="chat_header">
                        <h4>全部评论: <span id="chat_count">0</span> 条</h4>
                    </div>
<#-- 评论列表容器，由 JS 动态填充 -->
                    <ul id="chat_container" class="its"></ul>
                    <div id="pager" class="text-center"></div>
<#-- 评论输入区 -->
                    <div class="chat_post">
                        <div class="cbox-title">我有话说: <span id="chat_reply" style="display:none;">@<i
                                id="chat_to"></i></span>
                        </div>
                        <div class="cbox-post">
                            <div class="cbox-input">
                                <textarea id="chat_text" rows="3" placeholder="请输入评论内容"></textarea>
<#-- 隐藏字段：父评论ID，用于回复功能 -->
                                <input type="hidden" value="0" name="chat_pid" id="chat_pid"/>
                            </div>
                            <div class="cbox-ats clearfix">
                                <div class="ats-func">
<#-- 表情按钮 -->
                                    <div class="OwO" id="face-btn"></div>
                                </div>
                                <div class="ats-issue">
                                    <button id="btn-chat" class="btn btn-success btn-sm bt">发送</button>
                                </div>
                            </div>
                        </div>
<#-- 表情面板，默认隐藏 -->
                        <div class="phiz-box" id="c-phiz" style="display:none">
                            <div class="phiz-list" view="c-phizs"></div>
                        </div>
                    </div>
                </div>
                </@controls>
                <!-- /view show -->
            </div>
<#-- 右侧边栏：作者信息 + 侧边栏组件 -->
            <#-- hidden-xs hidden-sm 在小屏和中屏隐藏侧边栏 -->
            <div class="col-xs-12 col-md-3 side-right hidden-xs hidden-sm">
                <ul class="list-group about-user">
                    <li class="list-group-item user-card" >
                        <div class="user-avatar">
                            <#-- showAva 宏：渲染用户头像 -->
                            <@utils.showAva view.author "img-circle"/>
                        </div>
                        <div class="user-name">
                            <span>${view.author.name}</span>
                        </div>
                    </li>

                    <li class="list-group-item">
<#-- 作者统计信息：发布数和评论数 -->
                        <div class="user-datas">
                            <ul>
                                <li><strong>${view.author.posts}</strong><span>发布</span></li>
                                <li class="noborder"><strong>${view.author.comments}</strong><span>评论</span></li>
                            </ul>
                        </div>
                    </li>

                </ul>
                <@layout.extends name="/inc/right.ftl" />
            </div>
        </div>

<#-- 评论模板：使用 {0}~{6} 占位符，由 JS 动态填充 -->
        <#-- type="text/plain" 防止浏览器执行，仅供 JS 读取 -->
        <script type="text/plain" id="chat_template">
            <li id="chat{5}">
                <a class="avt fl" target="_blank" href="${base}/users/{0}">
                    <img src="{1}">
                </a>
                <div class="chat_body">
                    <h5>
                        <div class="fl"><a class="chat_name" href="${base}/users/{0}">{2}</a><span>{3}</span></div>
                        <div class="fr reply_this"><a href="javascript:void(0);" onclick="goto('{5}', '{2}')"><i class="icon icon-action-redo"></i></a></div>
                        <div class="clear"></div>
                    </h5>
                    <div class="chat_p">
                        <div class="chat_pct">{4}</div> {6}
                    </div>
                </div>
                <div class="clear"></div>
                <div class="chat_reply"></div>
            </li>
        </script>

        <script type="text/javascript">
<#-- 回复评论：设置父评论ID和被回复用户名 -->
            <#-- 滚动到评论框并聚焦，设置回复目标 -->
            function goto(pid, user) {
                document.getElementById('chat_text').scrollIntoView();
                $('#chat_text').focus();
                $('#chat_text').val('');
                $('#chat_to').text(user);
                $('#chat_pid').val(pid);

                $('#chat_reply').show();
            }
            var container = $("#chat_container");
            var template = $('#chat_template')[0].text;

<#-- 初始化评论模块：加载评论列表、提交评论 -->
            <#-- seajs.use 异步加载 comment 和 view 模块 -->
            seajs.use(['comment', 'view'], function (comment) {
                comment.init({
                    load: '${site.controls.comment}',
                    load_url: '${base}/comment/list/${view.id}',
                    post_url: '${base}/comment/submit',
                    toId: '${view.id}',
<#-- 评论加载回调：格式化评论数据并填充模板 -->
                    onLoad: function (i, data) {
                        var content = data.content;
                        var quoto = '';
<#-- 如果是回复评论，显示被引用的父评论内容 -->
                        if (data.pid > 0 && !(data.parent === null)) {
                            var pat = data.parent;
                            var pcontent = pat.content;
                            quoto = '<div class="quote"><a href="${base}/users/' + pat.author.id + '">@' + pat.author.name + '</a>: ' + pcontent + '</div>';
                        }
<#-- {0}=作者ID, {1}=头像, {2}=作者名, {3}=时间, {4}=内容, {5}=评论ID, {6}=引用 -->
                        <#-- 使用 format 方法将数据填充到模板占位符 -->
                        var item = jQuery.format(template,
                                data.author.id,
                                data.author.avatar,
                                data.author.name,
                                data.created,
                                content,
                                data.id, quoto);
                        return item;
                    }
                });
            });

        </script>
    </@layout.put>
</@layout.extends>