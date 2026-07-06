<form id="qForm" class="form-horizontal" method="post" action="update">
    <div class="form-group">
        <label class="col-sm-2 control-label">站点名称</label>
        <div class="col-sm-6">
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
            <input type="text" name="site_keywords" class="form-control" value="${options['site_keywords']}">
        </div>
    </div>
    <div class="form-group">
        <label class="col-sm-2 control-label">站点描述</label>
        <div class="col-sm-6">
            <input type="text" class="form-control" name="site_description" value="${options['site_description']}" />
        </div>
    </div>
    <div class="form-group">
        <label class="col-sm-2 control-label">扩展METAS</label>
        <div class="col-sm-6">
            <input type="text" class="form-control" name="site_metas" value="${options['site_metas']}" placeholder="请输入meta标签"/>
        </div>
    </div>
    <div class="form-group">
        <label class="col-sm-2 control-label">版权信息</label>
        <div class="col-sm-6">
            <input type="text" name="site_copyright" class="form-control" value="${options['site_copyright']}" placeholder="示例: 版权所有 © example.com">
        </div>
    </div>
    <div class="form-group">
        <label class="col-sm-2 control-label">备案号</label>
        <div class="col-sm-6">
            <input type="text" name="site_icp" class="form-control" value="${options['site_icp']}" placeholder="示例: 京ICP备12345678号">
        </div>
    </div>
    <div class="form-group">
        <label class="col-sm-2 control-label">站点Logo</label>
        <div class="col-sm-6">
            <input type="text" name="site_logo" class="form-control" value="${options['site_logo']}" placeholder="请输入Logo地址">
        </div>
    </div>
    <div class="form-group">
        <label class="col-sm-2 control-label">站点图标</label>
        <div class="col-sm-6">
            <input type="text" name="site_favicon" class="form-control" value="${options['site_favicon']}" placeholder="请输入Favicon地址">
        </div>
    </div>
    <div class="form-group">
        <label class="col-lg-2 control-label">文章编辑器</label>
        <div class="col-lg-2">
            <input type="text" class="form-control" value="Markdown" readonly>
            <input type="hidden" name="editor" value="markdown">
        </div>
    </div>
    <div class="form-group">
        <div class="col-sm-offset-2 col-sm-10">
            <button type="submit" class="btn btn-primary">提交</button>
        </div>
    </div>
</form>
