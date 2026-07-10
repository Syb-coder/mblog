<#-- 用户文章列表页：展示某用户发表的所有文章，支持编辑和删除（仅本人可见操作按钮） -->
<#-- 继承主布局模板 -->
<@layout.extends name="/inc/layout.ftl">
    <@layout.put block="title">
        <title>${user.name}的文章</title>
    </@layout.put>

    <@layout.put block="contents">
        <div class="row users-show">
<#-- 左侧：用户信息侧边栏 -->
            <#-- 占 3/12 列 -->
            <div class="col-xs-12 col-md-3 side-left">
                <@layout.extends name="/inc/user_sidebar.ftl" />
            </div>
            <#-- 右侧：文章列表，占 9/12 列 -->
            <div class="col-xs-12 col-md-9 side-right">
                <div class="panel panel-default">
                    <div class="panel-heading">发表的文章</div>
<#-- user_contents 宏：获取指定用户的文章列表 -->
                    <@user_contents userId=user.id pageNo=pageNo>
                        <div class="panel-body">
                            <ul class="list-group">
                                <#-- el 属性用于 JS 定位文章项 -->
                                <#list results.content as row>
                                    <li class="list-group-item" el="loop-${row.id}">
                                        <a href="${base}/post/${row.id}" class="remove-padding-left">${row.title}</a>
                                        <span class="meta">
                                            ${row.comments} 回复
                                            <span> ⋅ </span>
                                            <span class="timeago">${timeAgo(row.created)}</span>
                                        </span>

                                        <#-- pull-right 右浮动，hidden-xs 小屏隐藏操作按钮 -->
                                        <div class="pull-right hidden-xs">
<#-- owner 变量判断当前登录用户是否为该文章作者 -->
                                            <#if owner>
                                                <#-- data-evt="edit" 编辑按钮，data-toggle="tooltip" 悬浮提示 -->
                                                <a class="act_edit" href="javascript:void(0);" data-evt="edit" data-id="${row.id}" data-toggle="tooltip" title="编辑文章">
                                                    <i class="icon icon-note"></i>
                                                </a>
                                                <a class="act_delete" href="javascript:void(0);" data-evt="trash" data-id="${row.id}" data-toggle="tooltip" title="删除文章">
                                                    <i class="icon icon-close"></i>
                                                </a>
                                            </#if>
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
                    </@user_contents>
                </div>
            </div>
        </div>
        <!-- /end -->

        <#-- 脚本：处理文章编辑和删除交互 -->
        <script type="text/javascript">
        $(function() {
            // delete
            <#-- 删除按钮：弹出确认框，确认后调用删除接口并刷新页面 -->
            $('a[data-evt=trash]').click(function () {
                var id = $(this).attr('data-id');

                layer.confirm('确定删除此项吗?', {
                    btn: ['确定','取消'], //按钮
                    shade: false //不显示遮罩
                }, function(){
                    jQuery.getJSON('${base}/post/delete/' + id, function (ret) {
                        layer.msg(ret.message, {icon: 1});
                        <#-- 删除成功后刷新页面 -->
                        if (ret.code >=0) {
                            location.reload();
                        }
                    });

                }, function(){

                });
            });

            // edit
            <#-- 编辑按钮：跳转到文章编辑页 -->
            $('a[data-evt=edit]').click(function () {
                var id = $(this).attr('data-id');
                window.location.href='${base}/post/editing?id=' + id;
            });
        })
        </script>
    </@layout.put>
</@layout.extends>