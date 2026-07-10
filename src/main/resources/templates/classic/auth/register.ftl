<#-- 注册页面：继承主布局，居中显示注册表单 -->
<#-- 继承主布局模板 -->
<@layout.extends name="/inc/layout.ftl">
    <@layout.put block="title">
        <title>注册</title>
    </@layout.put>
    <@layout.put block="contents">
        <div class="row">
            <#-- col-md-offset-4 使表单居中显示 -->
            <div class="col-md-4 col-md-offset-4 floating-box">
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <h3 class="panel-title">注册</h3>
                    </div>
                    <div class="panel-body">
<#-- 消息提示区域 -->
                        <@layout.extends name="/inc/action_message.ftl" />
                        <div id="message">
                        </div>
<#-- 注册表单，提交到 register 接口 -->
                        <form id="submitForm" method="POST" action="register" accept-charset="UTF-8">
                            <div class="form-group ">
                                <label class="control-label" for="username">用户名</label>
<#-- 用户名要求：字母和数字组合，不少于5位 -->
                                <input class="form-control" id="username" name="username" type="text" placeholder="字母和数字的组合, 不少于5位" required>
                            </div>
                            <div class="form-group ">
                                <label class="control-label" for="username">密码</label>
                                <#-- maxlength="18" 限制密码最大长度为18位 -->
                                <input class="form-control" id="password" name="password" type="password" maxlength="18" placeholder="请输入密码" required>
                            </div>
                            <div class="form-group ">
                                <label class="control-label" for="username">确认密码</label>
<#-- 二次确认密码，前端 JS 会校验两次输入是否一致 -->
                                <input class="form-control" id="password2" name="password2" type="password" placeholder="请再一次输入密码" maxlength="18">
                            </div>
                            <#-- btn-block 使按钮撑满容器宽度 -->
                            <button type="submit" class="btn btn-primary btn-block">
                                提交
                            </button>
                        </form>
                    </div>
                </div>
            </div>
        </div>

<#-- 使用 SeaJS 加载表单验证模块，对注册表单进行前端校验 -->
        <#-- seajs.use 异步加载 validate 模块，加载完成后调用 register 方法绑定校验 -->
        <script type="text/javascript">
            seajs.use('validate', function (validate) {
                validate.register('#submitForm');
            });
        </script>
    </@layout.put>
</@layout.extends>