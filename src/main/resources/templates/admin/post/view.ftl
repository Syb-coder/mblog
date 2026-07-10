<#-- 后台文章编辑页：新建或修改文章，包含标题、编辑器、缩略图、栏目、标签 -->
<#-- 引入后台公共 UI 框架 -->
<#include "/admin/utils/ui.ftl"/>
<@layout>
<#-- 引入标签输入插件样式和脚本（bootstrap-tagsinput） -->
<link rel='stylesheet' media='all' href='${base}/dist/css/plugins.css'/>
<script type="text/javascript" src="${base}/dist/vendors/bootstrap-tagsinput/bootstrap-tagsinput.js"></script>

<#-- 页面头部 + 面包屑导航 -->
<section class="content-header">
    <h1>文章编辑</h1>
    <ol class="breadcrumb">
        <li><a href="${base}/admin">首页</a></li>
        <li><a href="${base}/admin/post/list">文章管理</a></li>
        <li class="active">文章编辑</li>
    </ol>
</section>
<section class="content container-fluid">
    <div class="row">
        <#-- 文章提交表单，POST 至 update 接口 -->
        <form id="qForm" method="post" action="${base}/admin/post/update">
<#-- 编辑已有文章时传递文章ID -->
            <#if view??>
                <input type="hidden" name="id" value="${view.id}"/>
            </#if>
<#-- 隐藏字段：文章状态（0=已发布，1=草稿） -->
            <input type="hidden" name="status" value="${view.status!0}"/>
<#-- 隐藏字段：编辑器类型 -->
            <input type="hidden" name="editor" value="${editor!'markdown'}"/>
<#-- 隐藏字段：缩略图路径 -->
            <input type="hidden" id="thumbnail" name="thumbnail" value="${view.thumbnail}">
            <#-- 左侧主区域：占 9/12 列，包含标题与编辑器 -->
            <div class="col-md-9 side-left">
                <div class="box">
                    <div class="box-header with-border">
                        <h3 class="box-title">文章编辑</h3>
                    </div>
                    <div class="box-body">
                        <#-- 文章标题输入框，maxlength 限制最大 64 字符 -->
                        <div class="form-group">
                            <input type="text" class="form-control" name="title" value="${view.title}" maxlength="64" placeholder="文章标题" required >
                        </div>
                        <div class="form-group">
<#-- 动态引入编辑器模板 -->
                            <#-- 根据编辑器类型（markdown/tinymce）引入对应模板 -->
                            <#include "/admin/editor/${editor}.ftl"/>
                        </div>
                    </div>
                </div>
            </div>
            <#-- 右侧侧栏：占 3/12 列，包含缩略图、栏目、标签、提交按钮 -->
            <div class="col-md-3 side-right">
<#-- 缩略图上传区域 -->
                <div class="box">
                    <div class="box-header with-border">
                        <h3 class="box-title">预览图</h3>
                    </div>
                    <div class="box-body">
                        <div class="thumbnail-box">
                            <#-- 若已有缩略图则作为背景显示 -->
                            <div class="convent_choice" id="thumbnail_image" <#if view.thumbnail?? && view.thumbnail?length gt 0> style="background: url(${base + view.thumbnail});" </#if>>
                                <div class="upload-btn">
                                    <label>
                                        <span>点击选择一张图片</span>
                                        <input id="upload_btn" type="file" name="file" accept="image/*" title="点击添加图片">
                                    </label>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="box">
                    <div class="box-body">
<#-- 栏目选择下拉框 -->
                        <#-- 遍历所有栏目，若当前文章已属于该栏目则 selected -->
                        <div class="form-group">
                            <label>栏目</label>
                            <select class="form-control" name="channelId">
                                <#list channels as row>
                                    <option value="${row.id}" <#if (view.channelId == row.id)> selected </#if>>${row.name}</option>
                                </#list>
                            </select>
                        </div>
<#-- 标签输入，使用 bootstrap-tagsinput 插件 -->
                        <#-- data-role="tagsinput" 启用标签输入插件 -->
                        <div class="form-group">
                            <label>标签</label>
                            <input type="text" name="tags" data-role="tagsinput" class="form-control" value="${view.tags}" placeholder="添加相关标签，逗号分隔 (最多4个)">
                        </div>
                    </div>
                    <div class="box-footer">
<#-- data-status="1" 保存为草稿，data-status="0" 直接发布 -->
                        <#-- pull-right 使发布按钮右对齐 -->
                        <button type="button" data-status="1" class="btn btn-default btn-sm" event="post_submit">草稿</button>
                        <button type="button" data-status="0" class="btn btn-primary btn-sm pull-right" event="post_submit">发布</button>
                    </div>
                </div>
            </div>
        </form>
    </div>
</section>
<#-- 脚本：图片上传、提交按钮、表单校验 -->
<script type="text/javascript">
$(function() {
    <#-- 缩略图上传：选择文件后异步上传，成功后更新预览与隐藏字段 -->
    $('#upload_btn').change(function(){
        $(this).upload('${base}/post/upload?crop=thumbnail_post_size', function(data){
            if (data.status == 200) {
                var path = data.path;
                $("#thumbnail_image").css("background", "url(" + path + ") no-repeat scroll center 0 rgba(0, 0, 0, 0)");
                $("#thumbnail").val(path);
            }
        });
    });

    <#-- 提交按钮：根据 data-status 设置状态值后提交表单 -->
    $('button[event="post_submit"]').click(function () {
        var status = $(this).data('status');
        $("input[name='status']").val(status);
        $("form").submit();
    });

    <#-- 表单提交前同步 tinyMCE 内容（若使用了富文本编辑器） -->
    $("form").submit(function () {
        if (typeof tinyMCE == "function") {
            tinyMCE.triggerSave();
        }
    }).validate({
        <#-- jQuery validate 插件配置 -->
        ignore: "",
        rules: {
            <#-- 标题必填，内容必填且需通过 check_editor 自定义校验 -->
            title: "required",
            content: {
                required: true,
                check_editor: true
            }
        },
        errorElement: "em",
        <#-- 错误信息插入位置：根据元素类型分别处理 -->
        errorPlacement: function (error, element) {
            error.addClass("help-block");

            if (element.prop("type") === "checkbox") {
                error.insertAfter(element.parent("label"));
            } else if (element.is("textarea")) {
                error.insertAfter(element.next());
            } else {
                error.insertAfter(element);
            }
        },
        <#-- 校验失败时添加 has-error 样式（红色边框） -->
        highlight: function (element, errorClass, validClass) {
            $(element).closest("div").addClass("has-error").removeClass("has-success");
        },
        <#-- 校验通过时添加 has-success 样式（绿色边框） -->
        unhighlight: function (element, errorClass, validClass) {
            $(element).closest("div").addClass("has-success").removeClass("has-error");
        }
    });

});
</script>
</@layout>