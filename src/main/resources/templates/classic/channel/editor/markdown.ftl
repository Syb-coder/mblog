<#-- 前台 Markdown 编辑器组件：工具栏 + 编辑区 + 预览区 -->
<div class="md-editor">
<#-- 编辑器工具栏 -->
    <div class="editor-toolbar">
<#-- 撤销/重做 -->
        <button type="button" event="undo">
            <i class="icon fa fa-rotate-left"></i>
        </button>
        <button type="button" event="redo">
            <i class="icon fa fa-rotate-right"></i>
        </button>
        <i class="separator">|</i>
<#-- 格式化按钮：加粗、斜体、标题、引用、链接、图片、上传图片、行内代码 -->
        <button type="button" event="bold">
            <i class="icon fa fa-bold"></i>
        </button>
        <button type="button" event="italic">
            <i class="icon fa fa-italic"></i>
        </button>
        <button type="button" event="h2">
            <i class="icon fa fa-header"></i>
        </button>
        <button type="button" event="blockquote">
            <i class="icon fa fa-quote-left"></i>
        </button>
        <button type="button" event="link">
            <i class="icon fa fa-link"></i>
        </button>
        <button type="button" event="image">
            <i class="icon fa fa-image"></i>
        </button>
        <button type="button" event="uploadimage">
            <i class="icon fa fa-file-image-o"></i>
        </button>
        <button type="button" event="inlinecode">
            <i class="icon fa fa-code"></i>
        </button>
        <i class="separator">|</i>
<#-- 视图模式切换：纯编辑、分屏预览、纯预览 -->
        <button type="button" class="active" event="premode" data-value="editMode">
            <i class="icon fa fa-tablet"></i>
        </button>
        <button type="button" event="premode" data-value="liveMode">
            <i class="icon fa fa-columns"></i>
        </button>
        <button type="button" event="premode" data-value="previewMode">
            <i class="icon fa fa-desktop"></i>
        </button>
        <i class="separator">|</i>
<#-- 全屏编辑 -->
        <button type="button" event="fullscreen">
            <i class="icon fa fa-arrows-alt"></i>
        </button>
    </div>
<#-- 编辑器容器，默认 editMode 模式 -->
    <div class="editor-container editMode">
<#-- 编辑区：textarea 存放 Markdown 原文 -->
        <div class="editor-body">
            <textarea id="content" name="content" rows="5" class="form-control" required>${view.content?html}</textarea>
        </div>
<#-- 预览区：实时渲染 Markdown 为 HTML -->
        <div class="editor-preview markdown-body">
        </div>
    </div>
</div>
<#-- 加载 Markdown 编辑器前端逻辑 -->
<script type="text/javascript">
    seajs.use('markdown');
</script>