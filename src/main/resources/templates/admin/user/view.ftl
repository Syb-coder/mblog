<#-- 后台修改用户角色页：为用户分配或移除角色 -->
<#-- 引入后台公共 UI 框架 -->
<#include "/admin/utils/ui.ftl"/>
<@layout>

<#-- 页面头部 + 面包屑导航 -->
<section class="content-header">
    <h1>修改角色</h1>
    <ol class="breadcrumb">
        <li><a href="${base}/admin">首页</a></li>
        <li><a href="${base}/admin/user/list">用户管理</a></li>
        <li class="active">修改角色</li>
    </ol>
</section>
<section class="content container-fluid">
    <div class="row">
        <div class="col-md-12">
            <#-- 角色修改表单，提交至 update_role 接口 -->
            <form id="qForm" class="form-horizontal form-label-left" method="post" action="update_role">
                <div class="box">
                    <div class="box-header with-border">
                        <h3 class="box-title">修改角色</h3>
                    </div>
                    <div class="box-body">
                        <#-- 操作结果消息提示 -->
                        <#include "/admin/message.ftl">
<#-- 隐藏字段：用户ID -->
                        <input type="hidden" name="id" value="${view.id}" />

                        <div class="form-group">
                            <label class="col-sm-2 control-label">用户名</label>
                            <div class="col-sm-8">
<#-- 用户名只读显示 -->
                                <#-- disabled 禁用编辑，仅作展示 -->
                                <input class="form-control" type="text" value="${view.username}" disabled style="width:200px;">
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-2 control-label">角色</label>
                            <div class="col-sm-8">
<#-- 遍历所有角色，对比用户当前角色，已拥有的角色设为选中 -->
                                <#-- checkbox-inline 使复选框横向排列 -->
                                <#list roles as role>
                                    <#-- 默认标记为未拥有该角色 -->
                                    <#assign hasRole ="false">
                                    <label class="checkbox-inline">
                                        <#-- 遍历用户当前角色，判断是否包含该角色 -->
                                        <#list view.roles as userRole>
                                            <#if userRole.id == role.id>
                                                <#assign hasRole ="true">
                                                <#-- break 跳出内层循环 -->
                                                <#break>
                                            </#if>
                                        </#list>
                                        <#-- 已拥有的角色默认勾选 -->
                                        <#if hasRole == "true">
                                            <input type="checkbox" name="roleIds" value="${role.id}" checked="checked"> ${role.name}
                                        <#else>
                                            <input type="checkbox" name="roleIds" value="${role.id}"> ${role.name}
                                        </#if>
                                    </label>
                                </#list>
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