<#-- 右侧边栏 - 热门文章面板 -->
<div class="panel panel-default widget">
	<div class="panel-heading">
		<h3 class="panel-title"><i class="fa fa-area-chart"></i> 热门文章</h3>
	</div>
	<div class="panel-body">
<#-- 调用 sidebar 宏获取热门文章列表 -->
		<@sidebar method="hottest_posts">
		<ul class="list">
<#-- 遍历热门文章列表，row_index 是 FreeMarker 内置的循环索引（从0开始） -->
			<#list results as row>
            <li>${row_index + 1}. <a href="${base}/post/${row.id}">${row.title}</a></li>
			</#list>
		</ul>
		</@sidebar>
	</div>
</div>

<#-- 右侧边栏 - 最新发布面板 -->
<div class="panel panel-default widget">
	<div class="panel-heading">
		<h3 class="panel-title"><i class="fa fa-bars"></i> 最新发布</h3>
	</div>
	<div class="panel-body">
<#-- 调用 sidebar 宏获取最新发布文章列表 -->
		<@sidebar method="latest_posts">
			<ul class="list">
				<#list results as row>
					<li>${row_index + 1}. <a href="${base}/post/${row.id}">${row.title}</a></li>
				</#list>
			</ul>
		</@sidebar>
	</div>
</div>
<#-- controls 宏：判断评论功能是否开启，只有开启时才显示最新评论面板 -->
<@controls name="comment">
<#-- 右侧边栏 - 最新评论面板 -->
<div class="panel panel-default widget">
    <div class="panel-heading">
        <h3 class="panel-title"><i class="fa fa-comment-o"></i> 最新评论</h3>
    </div>
    <div class="panel-body">
<#-- 调用 sidebar 宏获取最新评论列表 -->
		<@sidebar method="latest_comments">
			<ul class="list">
				<#list results as row>
<#-- 评论内容链接到对应文章页面 -->
					<li><a href="${base}/post/${row.postId}">${row.content}</a></li>
				</#list>
			</ul>
		</@sidebar>
    </div>
</div>
</@controls>