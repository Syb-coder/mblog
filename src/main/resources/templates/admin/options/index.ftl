<#-- 后台系统配置页：包含站点信息和图片存储两个 Tab -->
<#-- 引入后台公共 UI 框架 -->
<#include "/admin/utils/ui.ftl"/>
<@layout>

<#-- 页面头部 + 面包屑导航 -->
<section class="content-header">
    <h1>系统配置</h1>
    <ol class="breadcrumb">
        <li><a href="${base}/admin">首页</a></li>
        <li class="active">系统配置</li>
    </ol>
</section>
<section class="content container-fluid">
    <div class="row">
        <div class="col-md-12">
<#-- 消息提示组件：展示操作成功/失败的提示信息 -->
            <#include "/admin/message.ftl">
            <#-- nav-tabs-custom：AdminLTE 定制版 Tab 选项卡容器 -->
            <div class="nav-tabs-custom">
<#-- Tab 导航：站点信息 和 图片存储 -->
                <ul class="nav nav-tabs">
                    <#-- data-toggle="tab" 启用 Bootstrap Tab 切换，active 为默认选中项 -->
                    <li class="active"><a href="#sites" data-toggle="tab" aria-expanded="true">站点信息</a></li>
                    <li class=""><a href="#storage" data-toggle="tab" aria-expanded="false">图片存储</a></li>
                    <#-- pull-right 右浮动的图标装饰 -->
                    <li class="pull-right header"><i class="fa fa-cogs"></i></li>
                </ul>
                <div class="tab-content">
<#-- Tab 1：站点信息配置，引入 sites.ftl -->
                    <#-- tab-pane active 表示默认显示的面板 -->
                    <div class="tab-pane active" id="sites">
                        <#include "/admin/options/sites.ftl">
                    </div>
<#-- Tab 2：图片存储配置，引入 storages.ftl -->
                    <div class="tab-pane" id="storage">
                        <#include "/admin/options/storages.ftl">
                    </div>
                </div>
                <!-- /.tab-content -->
            </div>
        </div>
    </div>
</section>
<script type="text/javascript">
$(function() {
})
</script>
</@layout>