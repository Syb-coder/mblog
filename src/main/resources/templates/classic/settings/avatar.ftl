<#-- 用户设置-修改头像页：上传新头像图片 -->
<#-- 继承主布局模板 -->
<@layout.extends name="/inc/layout.ftl">
	<@layout.put block="title">
    	<title>修改用户信息</title>
	</@layout.put>

	<@layout.put block="contents">
		<div class="panel panel-default stacked">
			<div class="panel-heading">
<#-- 账户设置导航标签页，当前激活"修改头像" -->
				<#-- nav-pills 胶囊式导航，active 标记当前页 -->
				<ul class="nav nav-pills account-tab">
					<li><a href="profile">基本信息</a></li>
					<li><a href="email">修改邮箱</a></li>
					<li class="active"><a href="avatar">修改头像</a></li>
					<li><a href="password">修改密码</a></li>
				</ul>
			</div>
			<div class="panel-body">
				<#-- 操作结果消息提示 -->
				<div id="message">
					<@layout.extends name="/inc/action_message.ftl" />
				</div>
					<#-- 文件选择按钮，accept 限定为图片类型 -->
					<div class="upload-btn">
						<label>
							<span>点击选择一张图片</span>
							<input id="upload_btn" type="file" name="file" accept="image/*" title="点击添加图片">
						</label>
					</div>
<#-- 当前头像预览，使用 <@resource> 宏处理图片路径 -->
					<#-- id="target" 供裁剪插件识别操作目标 -->
					<div class="update_ava">
						<img src="<@resource src=profile.avatar/>" id="target" alt="[Example]" />
					</div>
			</div><!-- /panel-content -->
		</div><!-- /panel -->

<#-- 加载头像裁剪上传模块 -->
		<#-- seajs.use 异步加载 avatar 模块，实现图片裁剪与上传 -->
		<script type="text/javascript">
			seajs.use('avatar');
		</script>
	</@layout.put>
</@layout.extends>