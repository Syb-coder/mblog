<#-- 用户设置-基本信息页：修改昵称和个性签名 -->
<#-- 继承主布局模板 -->
<@layout.extends name="/inc/layout.ftl">
	<@layout.put block="title">
    	<title>修改用户信息</title>
	</@layout.put>

	<@layout.put block="contents">
		<div class="panel panel-default stacked">
			<div class="panel-heading">
<#-- 账户设置导航标签页 -->
				<ul class="nav nav-pills account-tab">
					<li class="active"><a href="profile">基本信息</a></li>
					<li><a href="email">修改邮箱</a></li>
					<li><a href="avatar">修改头像</a></li>
					<li><a href="password">修改密码</a></li>
				</ul>
			</div>
			<div class="panel-body">
<#-- 消息提示区域 -->
				<div id="message">
					<@layout.extends name="/inc/action_message.ftl" />
				</div>
				<div class="tab-pane active" id="profile">
					<#-- 基本信息表单，POST 提交至 profile 接口 -->
					<form id="submitForm" action="profile" method="post" class="form-horizontal">
						<div class="form-group">
							<label class="control-label col-lg-3" for="nickname">昵称</label>
							<div class="col-lg-4">
								<#-- maxlength="7" 限制昵称最大7个字符 -->
								<input type="text" class="form-control" name="name" value="${view.name}" maxlength="7" required>
							</div>
						</div>
						<div class="form-group">
							<label class="control-label col-lg-3" for="nickname">个性签名</label>
							<div class="col-lg-6">
								<#-- textarea 多行文本输入，maxlength="128" 限制最大长度 -->
								<textarea name="signature" class="form-control" rows="3" maxlength="128">${view.signature}</textarea>
							</div>
						</div>
						<div class="form-group">
							<div class="text-center">
								<button type="submit" class="btn btn-primary">提交</button>
							</div>
						</div><!-- /form-actions -->
					</form>
				</div>
			</div><!-- /panel-content -->
		</div><!-- /panel -->

<#-- 加载表单验证模块 -->
		<#-- seajs.use 异步加载 validate 模块，调用 updateProfile 绑定校验 -->
		<script type="text/javascript">
			seajs.use('validate', function (validate) {
				validate.updateProfile('#submitForm');
			});
		</script>
	</@layout.put>
</@layout.extends>