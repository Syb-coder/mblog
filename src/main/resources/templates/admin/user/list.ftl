<#-- 后台用户管理列表页：展示所有用户，支持搜索、启用/禁用、修改密码、修改角色 -->
<#-- 引入后台公共 UI 框架 -->
<#include "/admin/utils/ui.ftl"/>
<@layout>

<#-- 页面头部 + 面包屑导航 -->
<section class="content-header">
    <h1>用户管理</h1>
    <ol class="breadcrumb">
        <li><a href="${base}/admin">首页</a></li>
        <li class="active">用户管理</li>
    </ol>
</section>
<section class="content container-fluid">
    <div class="row">
        <div class="col-md-12">
            <div class="box">
                <div class="box-header with-border">
                    <h3 class="box-title">用户列表</h3>
                </div>
                <div class="box-body">
<#-- 搜索表单：按用户名关键字搜索 -->
                    <form id="qForm" class="form-inline search-row">
                        <input type="hidden" name="pageNo" value="${page.number + 1}"/>
                        <div class="form-group">
                            <input type="text" name="name" class="form-control" value="${name}" placeholder="请输入关键字">
                        </div>
                        <button type="submit" class="btn btn-default">查询</button>
                    </form>
                    <div class="table-responsive">
                        <table id="dataGrid" class="table table-striped table-bordered">
                            <thead>
                            <tr>
                                <th width="80">#</th>
                                <th>用户名</th>
                                <th>昵称</th>
                                <th>邮箱</th>
                                <th>角色</th>
<#-- 用户状态：0=启用，其他=禁用 -->
                                <th>状态</th>
                                <th>最后修改</th>
                                <th width="300"></th>
                            </tr>
                            </thead>
                            <tbody>
                                <#-- 遍历分页用户数据 -->
                                <#list page.content as row>
                                <tr>
                                    <#-- text-center 单元格内容居中 -->
                                    <td class="text-center">${row.id}</td>
                                    <td>${row.username}</td>
                                    <td>${row.name}</td>
                                    <td>${row.email}</td>
<#-- 显示用户所有角色名称 -->
                                    <#-- 遍历用户拥有的所有角色并显示名称 -->
                                    <td>
                                        <#list row.roles as role>
                                ${role.name}
                                </#list>
                                    </td>
                                    <td>
                                        <#-- 根据状态值显示"启用/禁用"标签 -->
                                        <#if (row.status == 0)>
                                            <#-- label-success 绿色标签 -->
                                            <span class="label label-success">启用</span>
                                        <#else>
                                            <#-- label-default 灰色标签 -->
                                            <span class="label label-default">禁用</span>
                                        </#if>
                                    </td>
                                    <td><#if row.updated??>${row.updated?string('yyyy-MM-dd HH:mm')}<#else>-</#if></td>
                                    <td class="text-center">
<#-- ID为1的是管理员账号，不可编辑 -->
                                        <#if row.id != 1>
<#-- 启用/禁用切换按钮 -->
                                            <#-- 已启用用户显示"关闭"按钮 -->
                                            <#if row.status == 0>
                                                <a href="javascript:void(0);" class="btn btn-xs btn-default" data-id="${row.id}" data-action="close">关闭</a>
                                            <#else>
                                                <#-- 已禁用用户显示"激活"按钮 -->
                                                <a href="javascript:void(0);" class="btn btn-xs btn-success" data-id="${row.id}" data-action="open">激活</a>
                                            </#if>
                                            <a href="${base}/admin/user/pwd?id=${row.id}" class="btn btn-xs btn-success">修改密码</a>

                                            <a href="${base}/admin/user/view?id=${row.id}" class="btn btn-xs btn-primary">修改角色</a>
                                        <#else>
                                            <#-- disabled 类使按钮不可点击 -->
                                            <a href="javascript:void(0);" class="btn btn-xs disabled"><i class="fa fa-check-square-o"></i> 不可编辑</a>
                                        </#if>
                                    </td>
                                </tr>
                                </#list>
                            </tbody>
                        </table>
                    </div>
                </div>
                <div class="box-footer">
                    <#-- 分页组件 -->
                    <@pager "list" page 5 />
                </div>
            </div>
        </div>
    </div>
</section>
<#-- 脚本：处理停用/激活用户交互 -->
<script type="text/javascript">
var J = jQuery;

<#-- AJAX 通用回调：成功刷新列表，失败提示错误 -->
function ajaxReload(json){
    if(json.code >= 0){
        if(json.message != null && json.message != ''){
			layer.msg(json.message, {icon: 1});
        }
        $('#qForm').submit();
    }else{
		layer.msg(json.message, {icon: 2});
    }
}

$(function() {
	// 停用
    <#-- 停用按钮：弹出确认框，确认后调用 close 接口 -->
    $('#dataGrid a[data-action="close"]').bind('click', function(){
		var that = $(this);
		layer.confirm('该账号停用后，将不能登录系统，确定要停用?', {
            btn: ['确定','取消'], //按钮
            shade: false //不显示遮罩
        }, function(){
			 J.getJSON('${base}/admin/user/close', {id: that.attr('data-id'), active: false}, ajaxReload);
        }, function(){
        });
        return false;
    });

    // 激活
    <#-- 激活按钮：弹出确认框，确认后调用 open 接口 -->
    $('#dataGrid a[data-action="open"]').bind('click', function(){
		var that = $(this);
		layer.confirm('该账号激活后，将可访问系统中的已授权功能，确定要激活?', {
            btn: ['确定','取消'], //按钮
            shade: false //不显示遮罩
        }, function(){
			 J.getJSON('${base}/admin/user/open', {id: that.attr('data-id'), active: true}, ajaxReload);
        }, function(){
        });
        return false;
    });
})
</script>
</@layout>