<#-- 后台文章管理列表页：展示所有文章，支持搜索、分页、新建、删除 -->
<#-- 引入后台公共 UI 框架 -->
<#include "/admin/utils/ui.ftl"/>
<@layout>

<#-- 页面头部 + 面包屑导航 -->
<section class="content-header">
    <h1>文章管理</h1>
    <ol class="breadcrumb">
        <li><a href="${base}/admin">首页</a></li>
        <li class="active">文章管理</li>
    </ol>
</section>
<section class="content container-fluid">
    <div class="row">
        <div class="col-md-12">
            <div class="box">
                <div class="box-header with-border">
                    <h3 class="box-title">文章列表</h3>
                    <div class="box-tools">
<#-- 新建文章按钮 -->
                        <a class="btn btn-default btn-sm" href="${base}/admin/post/view">新建</a>
<#-- 批量删除按钮 -->
                        <a class="btn btn-default btn-sm" href="javascrit:;" data-action="batch_del">批量删除</a>
                    </div>
                </div>
                <div class="box-body">
<#-- 搜索表单：按栏目和标题关键字筛选 -->
                    <#-- form-inline 内联表单，search-row 自定义搜索行样式 -->
                    <form id="qForm" class="form-inline search-row">
<#-- 隐藏字段：当前页码 -->
                        <input type="hidden" name="pageNo" value="${page.number + 1}"/>
                        <div class="form-group">
<#-- 栏目下拉选择，data-select 用于 JS 回显选中项 -->
                            <select class="form-control" name="channelId" data-select="${channelId}">
                                <option value="0">查询所有栏目</option>
                                <#list channels as row>
                                    <option value="${row.id}">${row.name}</option>
                                </#list>
                            </select>
                        </div>
                        <div class="form-group">
                            <input type="text" name="title" class="form-control" value="${title}" placeholder="请输入标题关键字">
                        </div>
                        <button type="submit" class="btn btn-default">查询</button>
                    </form>
                    <div class="table-responsive">
                        <table id="dataGrid" class="table table-striped table-bordered">
                            <thead>
                            <tr>
<#-- 全选复选框 -->
                                <th width="30"><input type="checkbox" class="checkall"></th>
                                <th width="80">#</th>
                                <th>文章标题</th>
                                <th width="120">作者</th>
                                <th width="100">发表日期</th>
                                <th width="100">最后修改</th>
                                <th width="60">访问数</th>
                                <th width="80">发布</th>
                                <th width="120">操作</th>
                            </tr>
                            </thead>
                            <tbody>
                                <#-- page.content 为分页数据 -->
                                <#list page.content as row>
                                <tr>
<#-- 行选择复选框 -->
                                    <td>
                                        <input type="checkbox" name="id" value="${row.id}">
                                    </td>
<#-- 缩略图列 -->
                                    <#-- resource 宏：对资源路径做统一处理（拼接 CDN/域名前缀等） -->
                                    <td>
                                        <img src="<@resource src=row.thumbnail/>" style="width: 80px;">
                                    </td>
<#-- 文章标题，链接到前台文章详情页（新窗口打开） -->
                                    <td>
                                        <a href="${base}/post/${row.id}" target="_blank">${row.title}</a>
                                    </td>
                                    <td>${row.author.username}</td>
<#-- 创建日期，格式化为 yyyy-MM-dd -->
                                    <td>${row.created?string('yyyy-MM-dd')}</td>
<#-- 更新日期，可能为空 -->
                                    <td><#if row.updated??>${row.updated?string('yyyy-MM-dd HH:mm')}<#else>-</#if></td>
                                    <#-- label 标签样式展示访问数 -->
                                    <td><span class="label label-default">${row.views}</span></td>
<#-- 文章状态：0=已发布，1=草稿 -->
                                    <td>
                                        <#if (row.status = 0)>
                                            <#-- label-default 灰色标签 -->
                                            <span class="label label-default">已发布</span>
                                        </#if>
                                        <#if (row.status = 1)>
                                            <#-- label-warning 橙色警告标签 -->
                                            <span class="label label-warning">草稿</span>
                                        </#if>
                                    </td>
<#-- 操作按钮：修改和删除 -->
                                    <td>
                                        <a href="${base}/admin/post/view?id=${row.id}" class="btn btn-xs btn-info">修改</a>
                                        <a href="javascript:void(0);" class="btn btn-xs btn-primary" data-id="${row.id}" rel="delete">删除</a>
                                    </td>
                                </tr>
                                </#list>
                            </tbody>
                        </table>
                    </div>
                </div>
                <div class="box-footer">
<#-- 分页组件：pager 宏参数依次为 列表动作名、分页对象、每侧显示页码数 -->
                    <@pager "list" page 5 />
                </div>
            </div>
        </div>
    </div>
</section>
<#-- 脚本：处理单条/批量删除交互 -->
<script type="text/javascript">
var J = jQuery;

<#-- AJAX 请求回调：成功则刷新列表，失败则提示错误 -->
function ajaxReload(json){
    if(json.code >= 0){
        if(json.message != null && json.message != ''){
			layer.msg(json.message, {icon: 1});
        }
        <#-- 提交搜索表单以保留筛选条件并刷新 -->
        $('#qForm').submit();
    }else{
		layer.msg(json.message, {icon: 2});
    }
}

<#-- 调用后端删除接口 -->
function doDelete(ids) {
	J.getJSON('${base}/admin/post/delete', J.param({'id': ids}, true), ajaxReload);
}

$(function() {
<#-- 单条删除：弹出确认框 -->
    $('#dataGrid a[rel="delete"]').bind('click', function(){
        var that = $(this);
		layer.confirm('确定删除此项吗?', {
            btn: ['确定','取消'],
            shade: false
        }, function(){
			doDelete(that.attr('data-id'));
        }, function(){
        });
        return false;
    });

<#-- 批量删除：收集所有选中的 ID -->
    $('a[data-action="batch_del"]').click(function () {
		var check_length=$("input[type=checkbox][name=id]:checked").length;

		<#-- 未选中任何项时提示 -->
		if (check_length == 0) {
			layer.msg("请至少选择一项", {icon: 2});
			return false;
		}

		<#-- 收集选中的 id 数组 -->
		var ids = [];
		$("input[type=checkbox][name=id]:checked").each(function(){
			ids.push($(this).val());
		});

		layer.confirm('确定删除此项吗?', {
            btn: ['确定','取消'],
            shade: false
        }, function(){
			doDelete(ids);
        }, function(){
        });
    });
})
</script>
</@layout>