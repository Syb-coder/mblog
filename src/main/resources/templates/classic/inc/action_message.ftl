<#-- 如果后端返回了 message 变量（通常为错误信息），显示红色警告框 -->
<#if message??>
	<div class="alert alert-danger">
		<#--<button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">&times;</span></button>-->
		${message}
	</div>
</#if>
<#-- 如果后端返回了 data 对象（包含 code 和 message），根据 code 判断显示成功或失败提示 -->
<#if data??>
	<#-- code >= 0 表示操作成功，显示绿色成功提示 -->
	<#if (data.code >= 0)>
		<div class="alert alert-success">
			<#--<button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">&times;</span></button>-->
			${data.message}
		</div>
	<#-- code < 0 表示操作失败，显示红色错误提示 -->
	<#else>
		<div class="alert alert-danger">
			<#--<button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">&times;</span></button>-->
			${data.message}
		</div>
	</#if>
</#if>