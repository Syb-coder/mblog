<#-- 后台栏目编辑页：新建或修改栏目，包含名称、唯一标识、状态、缩略图 -->
<#-- 引入后台公共 UI 框架 -->
<#include "/admin/utils/ui.ftl"/>
<#-- 渲染后台整体布局 -->
<@layout>
<#-- 页面头部 + 面包屑导航 -->
<section class="content-header">
    <h1>修改栏目</h1>
    <ol class="breadcrumb">
        <li><a href="${base}/admin">首页</a></li>
        <li><a href="${base}/admin/channel/list">栏目管理</a></li>
        <li class="active">修改栏目</li>
    </ol>
</section>
<section class="content container-fluid">
    <div class="row">
        <div class="col-md-12">
            <#-- form-horizontal 水平排列表单，提交至 update 接口 -->
            <form id="qForm" class="form-horizontal form-label-left" method="post" action="update">
<#-- 编辑已有栏目时传递栏目ID -->
                <#if view??>
                    <input type="hidden" name="id" value="${view.id}" />
                </#if>
<#-- 隐藏字段：栏目排序权重 -->
                <input type="hidden" name="weight" value="${view.weight!0}">
<#-- 隐藏字段：缩略图路径 -->
                <input type="hidden" id="thumbnail" name="thumbnail" value="${view.thumbnail}">
                <div class="box">
                    <div class="box-header with-border">
                        <h3 class="box-title">修改栏目</h3>
                    </div>
                    <div class="box-body">
                        <#-- 栏目名称输入框（required 必填） -->
                        <div class="form-group">
                            <label class="col-lg-2 control-label">名称</label>
                            <div class="col-lg-3">
                                <input type="text" name="name" class="form-control" value="${view.name}" required>
                            </div>
                        </div>
                        <div class="form-group">
<#-- 唯一标识（Key），用于 URL 路由和程序中引用 -->
                            <label class="col-lg-2 control-label">唯一标识</label>
                            <div class="col-lg-3">
                                <input type="text" name="key" class="form-control" value="${view.key}" required>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-lg-2 control-label">导航栏状态</label>
                            <div class="col-lg-3">
<#-- data-select 用于 JS 回显当前选中状态 -->
                                <select name="status" class="form-control" data-select="${view.status}">
                                    <option value="0">显示</option>
                                    <option value="1">隐藏</option>
                                </select>
                            </div>
                        </div>
                        <#-- 缩略图上传区域：点击选择图片后异步上传 -->
                        <div class="form-group">
                            <label class="col-lg-2 control-label">缩略图</label>
                            <div class="col-lg-3">
                                <div class="thumbnail-box">
                                    <#-- 若已有缩略图则作为背景图显示 -->
                                    <div class="convent_choice" id="thumbnail_image" <#if view.thumbnail?? && view.thumbnail?length gt 0> style="background: url(${base + view.thumbnail}) no-repeat scroll top;" </#if>>
                                        <div class="upload-btn">
                                            <label>
                                                <span>点击选择一张图片</span>
                                                <#-- accept="image/*" 限定只能选择图片文件 -->
                                                <input id="upload_btn" type="file" name="file" accept="image/*" title="点击添加图片">
                                            </label>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <#-- box-footer：表单底部提交按钮区 -->
                    <div class="box-footer">
                        <button type="submit" class="btn btn-primary">提交</button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</section>
<#-- 脚本：监听文件选择事件，调用 upload 插件上传图片并回填缩略图路径 -->
<script type="text/javascript">
var J = jQuery;

$(function() {
    <#-- 文件选择变化时触发上传，crop 参数指定服务端裁剪尺寸 -->
    $('#upload_btn').change(function(){
        $(this).upload('${base}/post/upload?crop=thumbnail_channel_size', function(data){
            if (data.status == 200) {
                var path = data.path;
                <#-- 更新预览背景图 + 回填隐藏字段 -->
                $("#thumbnail_image").css("background", "url(" + path + ") no-repeat scroll center 0 rgba(0, 0, 0, 0)");
                $("#thumbnail").val(path);
            }
        });
    });
})
</script>
</@layout>