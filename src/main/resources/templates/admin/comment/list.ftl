<#-- 后台评论管理列表页：展示所有评论，支持批量删除 -->
<#-- 引入后台公共 UI 框架（含 layout 宏与 pager 分页宏） -->
<#include "/admin/utils/ui.ftl"/>
<@layout>

<#-- 页面头部 + 面包屑导航 -->
<section class="content-header">
    <h1>评论管理</h1>
    <ol class="breadcrumb">
        <li><a href="${base}/admin">首页</a></li>
        <li class="active">评论管理</li>
    </ol>
</section>
<section class="content container-fluid">
    <div class="row">
        <div class="col-md-12">
            <div class="box">
                <div class="box-header with-border">
                    <h3 class="box-title">评论列表</h3>
                    <div class="box-tools">
<#-- 批量删除按钮 -->
                        <a class="btn btn-default btn-sm" href="javascrit:;" data-action="batch_del">批量删除</a>
                    </div>
                </div>
                <div class="box-body">
<#-- 搜索表单（仅保留分页参数） -->
                    <form id="qForm" class="form-inline">
                        <input type="hidden" name="pageNo" value="${page.number + 1}"/>
                    </form>
                    <div class="table-responsive">
                        <table id="dataGrid" class="table table-striped table-bordered">
                            <thead>
                            <tr>
<#-- 全选复选框 -->
                                <th width="50"><input type="checkbox" class="checkall"></th>
                                <th width="80">#</th>
                                <th>内容</th>
<#-- toId：评论所属的文章或父评论ID -->
                                <th>目标Id</th>
                                <th>作者</th>
                                <th>发表日期</th>
                                <th>最后修改</th>
                                <th width="50">操作</th>
                            </tr>
                            </thead>
                            <tbody>
                                <#-- page.content 为分页数据列表 -->
                                <#list page.content as row>
                                <tr>
<#-- 行选择复选框 -->
                                    <td>
                                        <input type="checkbox" name="id" value="${row.id}">
                                    </td>
                                    <td>${row.id}</td>
                                    <td>${row.content}</td>
                                    <td>${row.toId}</td>
                                    <#-- author 为关联用户对象，访问其 username 字段 -->
                                    <td>${row.author.username}</td>
<#-- 创建日期 -->
                                    <td>${row.created?string('yyyy-MM-dd')}</td>
<#-- 更新日期，可能为空 -->
                                    <td><#if row.updated??>${row.updated?string('yyyy-MM-dd HH:mm')}<#else>-</#if></td>
                                    <td>
                                        <a href="javascript:void(0);" class="btn btn-xs btn-primary" data-id="${row.id}"
                                           data-action="delete">删除
                                        </a>
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
<script type="text/javascript">
    var J = jQuery;

<#-- AJAX 请求回调：成功则刷新列表，失败则提示错误 -->
    function ajaxReload(json) {
        if (json.code >= 0) {
            if (json.message != null && json.message != '') {
                layer.msg(json.message, {icon: 1});
            }
            <#-- 通过提交 qForm 触发列表刷新（保留当前页码） -->
            $('#qForm').submit();
        } else {
            layer.msg(json.message, {icon: 2});
        }
    }

<#-- 调用后端评论删除接口 -->
    function doDelete(ids) {
        J.getJSON('${base}/admin/comment/delete', J.param({'id': ids}, true), ajaxReload);
    }

    $(function () {
<#-- 单条删除 -->
        $('#dataGrid a[data-action="delete"]').bind('click', function () {
            var that = $(this);
            layer.confirm('确定删除此项吗?', {
                btn: ['确定', '取消'],
                shade: false
            }, function () {
                doDelete(that.attr('data-id'));
            }, function () {
            });
            return false;
        });

<#-- 批量删除：收集所有选中的评论 ID -->
        $('a[data-action="batch_del"]').click(function () {
            var check_length = $("input[type=checkbox][name=id]:checked").length;

            <#-- 未选中任何项时提示 -->
            if (check_length == 0) {
                layer.msg("请至少选择一项", {icon: 2});
                return false;
            }

            <#-- 遍历选中复选框收集 id 数组 -->
            var ids = [];
            $("input[type=checkbox][name=id]:checked").each(function () {
                ids.push($(this).val());
            });

            layer.confirm('确定删除此项吗?', {
                btn: ['确定', '取消'],
                shade: false
            }, function () {
                doDelete(ids);
            }, function () {
            });
        });
    })
</script>
</@layout>