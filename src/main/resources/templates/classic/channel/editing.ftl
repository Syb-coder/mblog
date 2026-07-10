<#-- 前台文章编辑页：用户在此页面编写和发布文章 -->
<#-- 继承主布局模板 -->
<@layout.extends name="/inc/layout.ftl">
    <@layout.put block="title">
        <title>编辑文章</title>
    </@layout.put>

    <@layout.put block="contents">
<#-- 文章提交表单，enctype 支持文件上传（缩略图） -->
        <#-- action 指向 post/submit 接口处理文章提交 -->
        <form id="submitForm" class="form" action="${base}/post/submit" method="post" enctype="multipart/form-data">
<#-- 隐藏字段：文章状态（0=已发布，1=草稿） -->
            <input type="hidden" name="status" value="${view.status!0}"/>
<#-- 隐藏字段：编辑器类型，固定为 markdown -->
            <input type="hidden" name="editor" value="markdown"/>
            <div class="row">
<#-- 左侧：标题输入 + 编辑器 -->
                <#-- 占 8/12 列 -->
                <div class="col-xs-12 col-md-8 side-left">
                    <div id="message"></div>
<#-- 如果是编辑已有文章，传递文章ID和作者ID -->
                    <#if view??>
                        <input type="hidden" name="id" value="${view.id}"/>
                        <input type="hidden" name="authorId" value="${view.authorId}"/>
                    </#if>
<#-- 缩略图路径隐藏字段 -->
                    <input type="hidden" id="thumbnail" name="thumbnail" value="${view.thumbnail}"/>

                    <div class="form-group">
                        <#-- maxlength="128" 限制标题最大长度 -->
                        <input type="text" class="form-control" name="title" maxlength="128" value="${view.title}" placeholder="请输入标题" required>
                    </div>
                    <div class="form-group">
<#-- 动态引入编辑器模板，根据 editor 变量决定加载哪种编辑器 -->
                        <@layout.extends name="/channel/editor/${editor}.ftl" />
                    </div>
                </div>
<#-- 右侧：缩略图上传 + 栏目选择 + 标签输入 + 发布按钮 -->
                <#-- 占 4/12 列 -->
                <div class="col-xs-12 col-md-4 side-right">
                    <#-- 缩略图上传面板 -->
                    <div class="panel panel-default">
                        <div class="thumbnail-box">
<#-- 缩略图预览区域，有缩略图时显示背景图 -->
                            <#-- resource 宏处理图片路径 -->
                            <div class="convent_choice" id="thumbnail_image"  <#if view.thumbnail?? && view.thumbnail?length gt 0> style="background: url(<@resource src=view.thumbnail/>);" </#if>>
                                <div class="upload-btn">
                                    <label>
                                        <span>点击选择一张图片</span>
                                        <input id="upload_btn" type="file" name="file" accept="image/*" title="点击添加图片">
                                    </label>
                                </div>
                            </div>
                        </div>
                    </div>
                    <#-- 栏目选择面板 -->
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <h3 class="panel-title">发布到</h3>
                        </div>
                        <div class="panel-body">
<#-- 栏目下拉选择，遍历所有栏目，当前文章所属栏目设为选中 -->
                            <select class="form-control" name="channelId" required>
                                <option value="">请选择栏目</option>
                                <#list channels as row>
                                    <option value="${row.id}" <#if (view.channelId == row.id)> selected </#if>>${row.name}</option>
                                </#list>
                            </select>
                        </div>
                    </div>
                    <#-- 标签输入面板 -->
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <h3 class="panel-title">标签(用逗号或空格分隔)</h3>
                        </div>
                        <div class="panel-body">
                            <input type="text" id="tags" name="tags" class="form-control" value="${view.tags}" placeholder="添加相关标签，逗号分隔 (最多4个)">
                        </div>
                    </div>
                    <div class="col-xs-12 col-md-12">
                        <div class="form-group">
                            <div class="text-center">
<#-- data-status="0" 表示发布，JS 会将此值赋给 status 隐藏字段 -->
                                <#-- event="post_submit" 供 JS 绑定提交事件 -->
                                <button type="button" data-status="0" class="btn btn-primary" event="post_submit" style="padding-left: 30px; padding-right: 30px;">发布</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </form>
        <!-- /form-actions -->
<#-- 加载文章编辑页的前端逻辑 -->
        <#-- seajs.use 异步加载 post 模块并初始化编辑页逻辑 -->
        <script type="text/javascript">
        seajs.use('post', function (post) {
            post.init();
        });
        </script>
    </@layout.put>
</@layout.extends>