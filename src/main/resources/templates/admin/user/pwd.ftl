<#-- 后台重置用户密码页：管理员为指定用户设置新密码 -->
<#-- 引入后台公共 UI 框架 -->
<#include "/admin/utils/ui.ftl"/>
<@layout>

<#-- 页面头部 + 面包屑导航 -->
<section class="content-header">
    <h1>重置密码</h1>
    <ol class="breadcrumb">
        <li><a href="${base}/admin">首页</a></li>
        <li><a href="${base}/admin/user/list">用户管理</a></li>
        <li class="active">重置密码</li>
    </ol>
</section>
<section class="content container-fluid">
    <div class="row">
        <div class="col-md-12">
            <#-- 密码重置表单，POST 提交 -->
            <form id="qForm" class="form-horizontal form-label-left" method="post">
                <div class="box">
                    <div class="box-header with-border">
                        <h3 class="box-title">重置密码</h3>
                    </div>
                    <div class="box-body">
                        <#-- 操作结果消息提示 -->
                        <#include "/admin/message.ftl">
                        <div class="form-group">
<#-- 显示当前用户名 -->
                            <label class="col-lg-3 control-label">${view.username} 的新密码：</label>
                            <div class="col-lg-4">
                                <#-- newPassword 为新密码字段，required 必填 -->
                                <input type="text" class="input-small form-control" required name="newPassword" placeholder="新密码">
                            </div>
                        </div>
                    </div>
                    <#-- 表单底部提交按钮 -->
                    <div class="box-footer">
                        <button type="submit" class="btn btn-primary">提交</button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</section>
<script type="text/javascript">
var J = jQuery;

$(function() {
})
</script>
</@layout>