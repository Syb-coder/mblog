/**
 * @module markdown
 *
 * 职责：
 *   - 异步加载 Markdown 模式、快捷键以及应用层封装 app.markdown，
 *     加载完成后调用 MdEditor.initEditor() 初始化编辑器实例。
 *
 * 依赖：
 *   - codemirror-css / codemirror-theme：CodeMirror 主题与基础样式
 *   - codemirror：CodeMirror 编辑器核心
 *   - marked：Markdown 解析库
 *   - codemirror-markdown：CodeMirror 的 Markdown 语法 mode
 *   - codemirror-keymap：CodeMirror 快捷键绑定
 *   - app.markdown：业务层 Markdown 编辑器封装，提供 MdEditor.initEditor
 *
 * 暴露接口：
 */
define('markdown', [
    'codemirror-css',
    'codemirror-theme',
    'codemirror',
    'marked'
    ], function(require, exports, module) {

    // 异步加载编辑器扩展资源，避免阻塞主流程；加载完毕后初始化编辑器实例
    require.async(['codemirror-markdown', 'codemirror-keymap', 'app.markdown'], function () {
        MdEditor.initEditor();
    });
});
