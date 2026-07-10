<#-- 站点信息配置表单：修改站点名称、域名、SEO、版权等基础信息 -->
<#-- form-horizontal 水平布局，提交至 options 控制器的 update 接口 -->
<#-- options 为系统配置 Map，通过 options['key'] 读取已保存的配置值 -->
<form id="qForm" class="form-horizontal" method="post" action="update">
    <div class="form-group">
        <label class="col-sm-2 control-label">站点名称</label>
        <div class="col-sm-6">
<#-- ${options['site_name']} 从后台系统配置中读取当前值 -->
            <input type="text" name="site_name" class="form-control" value="${options['site_name']}">
        </div>
    </div>
    <div class="form-group">
        <label class="col-sm-2 control-label">域名</label>
        <div class="col-sm-6">
            <input type="text" name="site_domain" class="form-control" value="${options['site_domain']}" placeholder="示例: http://example.com">
        </div>
    </div>
    <div class="form-group">
        <label class="col-sm-2 control-label">站点关键字</label>
        <div class="col-sm-6">
<#-- SEO 关键词，用于搜索引擎优化 -->
            <input type="text" name="site_keywords" class="form-control" value="${options['site_keywords']}">
        </div>
    </div>
    <div class="form-group">
        <label class="col-sm-2 control-label">站点描述</label>
        <div class="col-sm-6">
<#-- SEO 描述，用于搜索引擎结果展示 -->
            <input type="text" class="form-control" name="site_description" value="${options['site_description']}" />
        </div>
    </div>
    <div class="form-group">
        <label class="col-sm-2 control-label">扩展METAS</label>
        <div class="col-sm-6">
<#-- 自定义 meta 标签内容，会原样输出到页面 head 中 -->
            <input type="text" class="form-control" name="site_metas" value="${options['site_metas']}" placeholder="请输入meta标签"/>
        </div>
    </div>
    <div class="form-group">
        <label class="col-sm-2 control-label">版权信息</label>
        <div class="col-sm-6">
<#-- 显示在页脚的版权文字 -->
            <input type="text" name="site_copyright" class="form-control" value="${options['site_copyright']}" placeholder="示例: 版权所有 © example.com">
        </div>
    </div>
    <div class="form-group">
        <label class="col-sm-2 control-label">备案号</label>
        <div class="col-sm-6">
<#-- ICP 备案号，显示在页脚 -->
            <input type="text" name="site_icp" class="form-control" value="${options['site_icp']}" placeholder="示例: 京ICP备12345678号">
        </div>
    </div>
    <div class="form-group">
        <label class="col-sm-2 control-label">站点Logo</label>
        <div class="col-sm-6">
<#-- Logo 图片地址，显示在导航栏和页脚 -->
            <input type="text" name="site_logo" class="form-control" value="${options['site_logo']}" placeholder="请输入Logo地址">
        </div>
    </div>
    <div class="form-group">
        <label class="col-sm-2 control-label">站点图标</label>
        <div class="col-sm-6">
<#-- Favicon 图标地址，显示在浏览器标签页 -->
            <input type="text" name="site_favicon" class="form-control" value="${options['site_favicon']}" placeholder="请输入Favicon地址">
        </div>
    </div>
    <div class="form-group">
        <label class="col-lg-2 control-label">文章编辑器</label>
        <div class="col-lg-2">
<#-- 编辑器类型，当前固定为 Markdown，readonly 禁止修改 -->
            <input type="text" class="form-control" value="Markdown" readonly>
<#-- 隐藏字段传递编辑器类型值 -->
            <input type="hidden" name="editor" value="markdown">
        </div>
    </div>
    <#-- col-sm-offset-2 使按钮与上方表单项左对齐 -->
    <div class="form-group">
        <div class="col-sm-offset-2 col-sm-10">
            <button type="submit" class="btn btn-primary">提交</button>
        </div>
    </div>
</form>