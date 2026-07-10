<#-- 后台 Markdown 编辑器：使用 CodeMirror 作为底层编辑器，比前台编辑器功能更强 -->
<#-- 引入 CodeMirror 样式和脚本 -->
<link href="${base}/dist/vendors/codemirror/lib/codemirror.css" rel="stylesheet">
<link href="${base}/dist/vendors/codemirror/theme/idea.css" rel="stylesheet">
<link href="${base}/dist/css/editor.css" rel="stylesheet">
<#-- CodeMirror 核心 + Markdown 语法高亮 + Sublime 快捷键 -->
<script type="text/javascript" charset="utf-8" src="${base}/dist/vendors/codemirror/lib/codemirror.js"></script>
<script type="text/javascript" charset="utf-8" src="${base}/dist/vendors/codemirror/mode/markdown/markdown.js"></script>
<script type="text/javascript" charset="utf-8" src="${base}/dist/vendors/codemirror/keymap/sublime.js"></script>
<#-- marked.js：Markdown 转 HTML 渲染库 -->
<script type="text/javascript" charset="utf-8" src="${base}/dist/vendors/marked/marked.min.js"></script>
<#-- 编辑器初始化脚本 -->
<script type="text/javascript" charset="utf-8" src="${base}/dist/js/app.markdown.js"></script>
<div class="md-editor">
<#-- 工具栏：与前台编辑器相同 -->
    <div class="editor-toolbar">
        <#-- 撤销/重做 -->
        <button type="button" event="undo">
            <i class="icon fa fa-rotate-left"></i>
        </button>
        <button type="button" event="redo">
            <i class="icon fa fa-rotate-right"></i>
        </button>
        <i class="separator">|</i>
        <#-- 文本格式化：加粗、斜体、二级标题、引用块 -->
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
        <#-- 插入元素：链接、网络图片、本地上传图片、行内代码 -->
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
        <#-- 视图模式切换：编辑模式（默认 active）、分屏实时预览、纯预览 -->
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
        <#-- 全屏切换 -->
        <button type="button" event="fullscreen">
            <i class="icon fa fa-arrows-alt"></i>
        </button>
    </div>
    <#-- 编辑器容器：editMode 表示默认编辑模式 -->
    <div class="editor-container editMode">
        <#-- 编辑区：textarea 被 CodeMirror 接管渲染，?html 转义内容防止 XSS -->
        <div class="editor-body">
            <textarea id="content" name="content" rows="5" class="form-control" required>${view.content?html}</textarea>
        </div>
        <#-- 预览区：实时渲染 Markdown 为 HTML -->
        <div class="editor-preview markdown-body">
        </div>
    </div>
</div>
<#-- 初始化 CodeMirror 编辑器实例 -->
<script type="text/javascript">
    $(function () {
        MdEditor.initEditor();
    });
</script>