<#-- 错误提示页面：继承主布局，显示错误信息 -->
<@layout.extends name="/inc/layout.ftl">
<#-- 替换标题块 -->
	<@layout.put block="title" type="replace">
    	<title>消息提示</title>
	</@layout.put>

<#-- 替换内容块，显示错误信息面板 -->
	<@layout.put block="contents" type="replace">
		<div class="panel panel-default" style="min-height: 300px; max-width: 460px; margin: 30px auto;">
			<div class="panel-heading">提示</div>
			<div class="panel-body">
				<fieldset>
<#-- 如果后端传来了 error 变量，显示错误信息 -->
					<#if error??>
						${error}
					</#if>
				</fieldset>
			</div><!-- /panel-content -->
		</div><!-- /panel -->
	</@layout.put>
</@layout.extends>