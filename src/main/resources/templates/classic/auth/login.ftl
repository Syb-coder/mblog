<#-- 登录页面：继承主布局，居中显示登录表单 -->
<#-- layout.extends 继承 inc/layout.ftl 主布局模板 -->
<@layout.extends name="/inc/layout.ftl">
<#-- 覆盖标题块 -->
    <@layout.put block="title">
        <title>登录</title>
    </@layout.put>

    <#-- 覆盖内容块：填充登录表单 -->
    <@layout.put block="contents">
        <div class="row">
<#-- col-md-offset-4 使表单居中显示（4列偏移4列） -->
            <#-- floating-box 自定义浮动盒子样式 -->
            <div class="col-md-4 col-md-offset-4 floating-box">
                <#-- panel 为 Bootstrap 面板组件 -->
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <h3 class="panel-title">请登录</h3>
                    </div>
                    <div class="panel-body">
<#-- 消息提示区域，引入 action_message.ftl 显示登录错误等信息 -->
                        <div id="message">
                            <@layout.extends name="/inc/action_message.ftl" />
                        </div>
<#-- 登录表单，提交到 login 接口 -->
                        <#-- accept-charset="UTF-8" 确保表单以 UTF-8 编码提交 -->
                        <form method="POST" action="login" accept-charset="UTF-8">
                            <div class="form-group">
                                <label class="control-label" for="username">账号</label>
                                <input class="form-control" name="username" type="text" required>
                            </div>
                            <div class="form-group">
                                <label class="control-label" for="password">密码</label>
                                <#-- type="password" 密码输入框，内容以圆点显示 -->
                                <input class="form-control" name="password" type="password" required>
                            </div>
                            <div class="form-group">
<#-- 记住登录复选框，value=1 表示勾选 -->
                                <label>
                                    <input type="checkbox" name="rememberMe" value="1"> 记住登录
                                </label>
                            </div>
                            <div class="form-group">
                                <#-- btn-block 使按钮撑满容器宽度 -->
                                <button type="submit" class="btn btn-primary btn-block">
                                    登录
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>

    </@layout.put>
</@layout.extends>