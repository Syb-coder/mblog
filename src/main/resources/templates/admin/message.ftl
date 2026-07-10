<#-- 后台消息提示组件：与前台 action_message.ftl 类似，但增加了自动关闭功能 -->
<#-- 如果后端返回了 message 变量（错误信息），显示红色警告框 -->
<#if message??>
<div class="alert alert-danger">
    <button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">&times;</span></button>
    ${message}
</div>
</#if>

<#-- 如果后端返回了 data 对象，根据 code 判断显示成功或失败提示 -->
<#if data??>
<#-- code >= 0 表示操作成功 -->
	<#if (data.code >= 0)>
    <div class="alert alert-success">
        <button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">&times;</span></button>
	${data.message}
    </div>
<#-- code < 0 表示操作失败 -->
	<#else>
    <div class="alert alert-danger">
        <button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">&times;</span></button>
	${data.message}
    </div>
	</#if>
</#if>
<#-- 3秒后自动关闭提示框 -->
<script>
    window.setTimeout(function(){
        $('[data-dismiss="alert"]').alert('close');
    },3000);
</script>