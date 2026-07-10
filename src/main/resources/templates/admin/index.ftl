<#-- 后台仪表盘首页：展示系统统计数据、系统信息、缓存管理、最新评论 -->
<#-- 引入后台公共 UI 框架 -->
<#include "/admin/utils/ui.ftl"/>
<@layout>

<#-- 页面头部 + 面包屑导航 -->
<section class="content-header">
    <h1>仪表盘</h1>
    <ol class="breadcrumb">
        <li><a href="${base}/admin"><i class="fa fa-dashboard"></i> 首页</a></li>
        <li class="active">仪表盘</li>
    </ol>
</section>
<section class="content container-fluid">
    <div class="row">
<#-- 四个统计卡片：栏目数、文章数、评论数、用户数 -->
        <#-- col-lg-3 大屏占1/4，col-xs-6 手机端占1/2；small-box 为 AdminLTE 统计卡片样式 -->
        <div class="col-lg-3 col-xs-6">
            <!-- small box -->
            <#-- bg-aqua 青色背景 -->
            <div class="small-box bg-aqua">
                <div class="inner">
                    <h3>${channelCount}</h3>
                    <p>栏目</p>
                </div>
                <div class="icon">
                    <i class="fa fa-bars"></i>
                </div>
                <a href="${base}/admin/channel/list" class="small-box-footer">更多 <i class="fa fa-arrow-circle-right"></i></a>
            </div>
        </div>
        <!-- ./col -->
        <div class="col-lg-3 col-xs-6">
            <!-- small box -->
            <#-- bg-green 绿色背景 -->
            <div class="small-box bg-green">
                <div class="inner">
                    <h3>${postCount}</h3>
                    <p>文章</p>
                </div>
                <div class="icon">
                    <i class="fa fa-clone"></i>
                </div>
                <a href="${base}/admin/post/list" class="small-box-footer">更多 <i class="fa fa-arrow-circle-right"></i></a>
            </div>
        </div>
        <!-- ./col -->
        <div class="col-lg-3 col-xs-6">
            <!-- small box -->
            <#-- bg-yellow 黄色背景 -->
            <div class="small-box bg-yellow">
                <div class="inner">
                    <h3>${commentCount}</h3>
                    <p>评论</p>
                </div>
                <div class="icon">
                    <i class="fa fa-comments-o"></i>
                </div>
                <a href="${base}/admin/comment/list" class="small-box-footer">更多 <i class="fa fa-arrow-circle-right"></i></a>
            </div>
        </div>
        <!-- ./col -->
        <div class="col-lg-3 col-xs-6">
            <!-- small box -->
            <#-- bg-red 红色背景 -->
            <div class="small-box bg-red">
                <div class="inner">
                    <h3>${userCount}</h3>
                    <p>用户</p>
                </div>
                <div class="icon">
                    <i class="fa fa-user"></i>
                </div>
                <a href="${base}/admin/user/list" class="small-box-footer">更多 <i class="fa fa-arrow-circle-right"></i></a>
            </div>
        </div>
        <!-- ./col -->
    </div>
    <div class="row">
        <div class="col-md-6">
<#-- 系统占用情况面板 -->
            <#-- box-primary 蓝色主题卡片 -->
            <div class="box box-primary">
                <div class="box-header with-border">
                    <h3 class="box-title">系统占用情况</h3>

                    <div class="box-tools pull-right">
                        <#-- data-widget="collapse" 折叠/展开面板 -->
                        <button type="button" class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i>
                        </button>
                    </div>
                </div>
                <div class="box-body">
                    <table class="table table-bordered">
                        <tr>
                            <td>内存消耗:</td>
                            <td>
<#-- 内存使用进度条 -->
                                <#-- progress-bar-striped 条纹进度条，width 按内存使用百分比设置 -->
                                <div class="progress">
                                    <div class="progress-bar progress-bar-info progress-bar-striped" role="progressbar" style="width: ${memPercent}%; min-width: 2em;">
                                        <span>${usedMemory}M / ${totalMemory}M</span>
                                    </div>
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td style="width:120px;">操作系统:</td>
                            <td>${os}</td>
                        </tr>
                        <tr>
                            <td style="width:120px;">JDK版本:</td>
                            <td>${javaVersion}</td>
                        </tr>
                    </table>
                </div>
            </div>
            <#-- box-success 绿色主题卡片 -->
            <div class="box box-success">
                <div class="box-header with-border">
                    <h3 class="box-title">缓存</h3>
                    <div class="box-tools pull-right">
                        <button type="button" class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i>
                        </button>
                    </div>
                </div>
                <div class="box-body">
<#-- 刷新系统变量缓存按钮 -->
                    <button type="button" class="btn btn-primary" data-action="reload_options">
                        刷新系统变量
                    </button>

                </div>
            </div>
        </div>
        <div class="col-md-6">
<#-- 最新评论面板 -->
            <#-- box-info 浅蓝色主题卡片 -->
            <div class="box box-info">
                <div class="box-header with-border">
                    <h3 class="box-title">最新评论</h3>
                    <div class="box-tools pull-right">
                        <button type="button" class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i>
                        </button>
                    </div>
                </div>
                <#-- chat 样式使列表呈现聊天气泡效果 -->
                <div class="box-body chat" id="chat-box">
                    <!-- chat item -->
                    <div class="item">
                        <p>没有最新内容</p>
                    </div>
                    <!-- /.item -->
                </div>
            </div>
        </div>
    </div>
</section>
<#-- 最新评论模板：{0}=头像, {1}=用户ID, {2}=时间, {3}=用户名, {4}=内容 -->
<#-- type="text/plain" 防止浏览器解析为脚本执行，仅供 JS 读取 -->
<script type="text/plain" id="chat">
    <div class="item">
        <img src="{0}" alt="user image" class="offline">

        <p class="message">
            <a href="${base}/users/{1}" class="name">
                <small class="text-muted pull-right"><i class="fa fa-clock-o"></i> {2}</small>
                {3}
            </a>
            {4}
        </p>
    </div>
</script>
<script>
    var J = jQuery;

    <#-- AJAX 回调：弹出操作结果提示 -->
    function ajaxReload(json){
        layer.alert(json.message);
    }
	$(function () {
<#-- 刷新系统变量缓存 -->
        <#-- 点击刷新按钮，确认后调用后端 reload_options 接口 -->
        $('button[data-action="reload_options"]').bind('click', function(){
            if(confirm('确定要刷新系统变量的缓存吗？')){
                J.getJSON('${base}/admin/options/reload_options', ajaxReload);
            }
            return false;
        });


<#-- 通过 API 加载最新评论并填充模板 -->
        <#-- 请求最新评论接口，使用模板字符串格式化后渲染到页面 -->
        J.getJSON('${base}/api/latest_comments', function (result) {
            if (result.length > 0) {
                var template = $('#chat')[0].text;
                var html = [];
                <#-- 遍历评论数据，用 format 方法填充模板占位符 -->
                J.each(result, function (i, n) {
                    var row = J.format(template, n.author.avatar, n.author.id, n.created, n.author.name, n.content);
                    html.push(row);
                })
                $('#chat-box').html(html);
            }
        })
    })
</script>
</@layout>