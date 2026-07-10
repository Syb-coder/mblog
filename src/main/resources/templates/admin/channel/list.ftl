<#-- 后台栏目管理列表页：展示所有栏目，支持添加、置顶、修改、删除 -->
<#-- 引入后台公共 UI 框架（包含布局宏 layout、分页宏 pager 等） -->
<#include "/admin/utils/ui.ftl"/>
<#-- 调用 layout 宏渲染后台整体页面骨架（侧边栏+顶部导航+内容区） -->
<@layout>

<#-- 页面头部：标题 + 面包屑导航（AdminLTE 风格的 content-header） -->
<section class="content-header">
    <h1>栏目管理</h1>
    <#-- breadcrumb 面包屑：active 类标记当前所在层级 -->
    <ol class="breadcrumb">
        <li><a href="${base}/admin">首页</a></li>
        <li class="active">栏目管理</li>
    </ol>
</section>
<#-- 主内容区：container-fluid 使内容撑满宽度 -->
<section class="content container-fluid">
    <div class="row">
        <div class="col-md-12">
            <#-- box 是 AdminLTE 的卡片容器 -->
            <div class="box">
                <div class="box-header with-border">
                    <h3 class="box-title">栏目列表</h3>
                    <div class="box-tools">
<#-- 添加栏目按钮：跳转到栏目编辑页（无 id 参数表示新建） -->
                        <a class="btn btn-default btn-sm" href="${base}/admin/channel/view">添加栏目</a>
                    </div>
                </div>
                <div class="box-body">
                    <#-- table-responsive：小屏幕下表格可横向滚动 -->
                    <div class="table-responsive">
                        <#-- table-striped 隔行变色，table-bordered 显示边框 -->
                        <table id="dataGrid" class="table table-striped table-bordered">
                            <thead>
                            <tr>
                                <th width="80">#</th>
                                <th>名称</th>
                                <th>Key</th>
<#-- 栏目状态：0=显示，其他=隐藏 -->
                                <th>状态</th>
                                <th>最后修改</th>
                                <th width="140">操作</th>
                            </tr>
                            </thead>
                            <tbody>
                                <#-- 遍历栏目列表数据（list 由控制器传入） -->
                                <#list list as row>
                                <tr>
                                    <td>${row.id}</td>
                                    <td>${row.name}</td>
                                    <td>${row.key}</td>
                                    <td>
                                        <#-- 根据状态值显示"显示/隐藏" -->
                                        <#if (row.status == 0)>
                                            显示
                                        <#else>
                                            隐藏
                                        </#if>
                                    </td>
                                    <#-- updated 可能不存在，使用 ?? 判空，空值显示"-" -->
                                    <td><#if row.updated??>${row.updated?string('yyyy-MM-dd HH:mm')}<#else>-</#if></td>
                                    <td>
<#-- 置顶按钮：将栏目排序权重设为1（排到第一位） -->
                                        <a href="javascript:void(0);" class="btn btn-xs btn-default" data-id="${row.id}" data-action="weight">置顶</a>
                                        <a href="view?id=${row.id}" class="btn btn-xs btn-success">修改</a>
                                        <a href="javascript:void(0);" class="btn btn-xs btn-primary" data-id="${row.id}"
                                           data-action="delete">删除</a>
                                    </td>
                                </tr>
                                </#list>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>
<#-- 页面脚本：处理置顶、删除的交互逻辑，使用 layer 弹层组件确认 -->
<script type="text/javascript">
    var J = jQuery;

    <#-- AJAX 通用回调：code>=0 表示成功，刷新页面；否则弹出错误提示 -->
    function ajaxReload(json) {
        if (json.code >= 0) {
            if (json.message != null && json.message != '') {
                layer.msg(json.message, {icon: 1});
            }
            window.location.reload();
        } else {
            layer.msg(json.message, {icon: 2});
        }
    }

    <#-- 调用后端置顶接口：更新指定栏目的排序权重 -->
    function doUpdateWeight(id, weight) {
        J.getJSON('${base}/admin/channel/weight', J.param({'id': id, 'weight': weight}, true), ajaxReload);
    }

    <#-- DOM 就绪后绑定按钮事件 -->
    $(function () {
        <#-- 置顶按钮：弹出确认框，确认后调用 doUpdateWeight -->
        $('#dataGrid a[data-action="weight"]').bind('click', function(){
            var that = $(this);
            layer.confirm('确定将该项排序在第一位吗?', {
                btn: ['确定','取消'], //按钮
                shade: false //不显示遮罩
            }, function(){
                doUpdateWeight(that.attr('data-id'), 1);
            }, function(){
            });
            return false;
        });

        // 删除
        <#-- 删除按钮：弹出确认框，确认后调用后端删除接口 -->
        $('#dataGrid a[data-action="delete"]').bind('click', function () {
            var that = $(this);

            layer.confirm('确定删除此项吗?', {
                btn: ['确定', '取消'], //按钮
                shade: false //不显示遮罩
            }, function () {
                J.getJSON('${base}/admin/channel/delete', {id: that.attr('data-id')}, ajaxReload);
            }, function () {
            });
            return false;
        });

    })
</script>
</@layout>