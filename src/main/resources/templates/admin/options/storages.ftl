<#-- 图片存储配置表单：配置文章图片、缩略图等静态资源的存储方式 -->
<form id="qForm" class="form-horizontal" method="post" action="update">
<#-- 隐藏字段：存储方案，native 表示本地存储 -->
    <input type="hidden" name="storage_scheme" value="native">
    <div class="form-group">
        <label class="col-sm-2 control-label">存储方式</label>
        <div class="col-sm-3">
<#-- 当前只支持本地存储，readonly 禁止修改 -->
            <input type="text" class="form-control" value="本地存储" readonly>
        </div>
    </div>
    <#-- col-sm-offset-2 使按钮与上方表单项左对齐 -->
    <div class="form-group">
        <div class="col-sm-offset-2 col-sm-10">
            <button type="submit" class="btn btn-primary">提交</button>
        </div>
    </div>
</form>