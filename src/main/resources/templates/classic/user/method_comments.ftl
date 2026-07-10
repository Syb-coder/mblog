<#-- 用户评论列表页：展示某用户发表的所有评论，支持删除（仅本人可见操作按钮） -->
<#-- 继承主布局模板 -->
<@layout.extends name="/inc/layout.ftl">
    <@layout.put block="title">
        <title>${user.name}的评论</title>
    </@layout.put>

    <@layout.put block="contents">
        <div class="row users-show">
<#-- 左侧：用户信息侧边栏 -->
            <#-- 占 3/12 列 -->
            <div class="col-xs-12 col-md-3 side-left">
                <@layout.extends name="/inc/user_sidebar.ftl" />
            </div>
            <#-- 右侧：评论列表，占 9/12 列 -->
            <div class="col-xs-12 col-md-9 side-right">
                <div class="panel panel-default">
                    <div class="panel-heading">发表的评论</div>
<#-- user_comments 宏：获取指定用户的评论列表 -->
                    <@user_comments userId=user.id pageNo=pageNo>
                        <div class="panel-body">
                            <ul class="list-group">
                                <#-- el 属性用于 JS 定位并删除评论项 -->
                                <#list results.content as row>
                                    <li class="list-group-item" el="loop-${row.id}">
<#-- 评论关联的文章，文章可能已被删除 -->
                                        <#if row.post??>
                                            <a href="${base}/post/${row.post.id}" class="remove-padding-left">${row.post.title}</a>
                                        <#else>
                                            <a href="javascript:;" class="remove-padding-left">文章已删除</a>
                                        </#if>
                                        <span class="meta">
                                            <span class="timeago">${timeAgo(row.created)}</span>
                                        </span>

                                        <#-- pull-right 右浮动，hidden-xs 小屏隐藏操作按钮 -->
                                        <div class="pull-right hidden-xs">
<#-- owner 变量判断当前登录用户是否为该评论作者 -->
                                            <#if owner>
                                                <#-- data-toggle="tooltip" 启用悬浮提示 -->
                                                <a class="act" href="javascript:void(0);" data-evt="trash" data-id="${row.id}" data-toggle="tooltip" title="删除评论">
                                                    <i class="icon icon-close"></i>
                                                </a>
                                            </#if>
                                        </div>

<#-- 评论内容 -->
                                        <div class="reply-body markdown-reply content-body">
                                            <p><i class="icon-bubble"></i> ${row.content}</p>
                                        </div>
                                    </li>
                                </#list>

                                <#if results.content?size == 0>
                                    <li class="list-group-item ">
                                        <div class="infos">
                                            <div class="media-heading">该目录下还没有内容!</div>
                                        </div>
                                    </li>
                                </#if>
                            </ul>
                        </div>
                        <div class="panel-footer">
                            <@utils.pager request.requestURI!'', results, 5/>
                        </div>
                    </@user_comments>
                </div>
            </div>
        </div>
        <!-- /end -->

        <#-- 脚本：处理评论删除交互 -->
        <script type="text/javascript">
        $(function() {
            <#-- 删除按钮：弹出确认框，确认后调用删除接口并移除 DOM 元素 -->
            $('a[data-evt=trash]').click(function () {
                var id = $(this).attr('data-id');

                layer.confirm('确定删除此项吗?', {
                    btn: ['确定','取消'], //按钮
                    shade: false //不显示遮罩
                }, function(){
                    jQuery.getJSON('${base}/comment/delete', {'id':id }, function (ret) {
                        layer.msg(ret.message, {icon: 1});
                        <#-- 删除成功后从页面移除该评论项 -->
                        if (ret.code >=0) {
                            var el = $('li[el=loop-' + id + ']');
                            el.remove();
                        }
                    });

                }, function(){

                });
            });
        })
        </script>
    </@layout.put>
</@layout.extends>