/**
 * @module view
 *
 * 职责：
 *   - 绑定"展开全文"按钮事件，超出高度的文章可通过点击展开；
 *   - 当文章实际高度不超过 700px 时，自动展开全文，无需用户操作。
 *
 * 依赖：
 *   - require('share-css') / require('share')：分享组件及其样式
 *   - require.async('highlight-css' / 'highlight')：highlight.js 语法高亮
 *   - 全局 hljs：highlight.js 暴露的对象
 *
 * 暴露接口：
 */
define(function(require, exports, module) {
    require('share-css');
    require('share');

    require.async(['highlight-css', 'highlight'], function () {
        // 启用页面加载时的高亮监听（与下方手动高亮配合使用）
        hljs.initHighlightingOnLoad();
        $('pre').each(function(i, block) {
            hljs.highlightBlock(block);
        });
    });

    // 点击"展开全文"按钮：为 .topic 添加 fulltext 类，CSS 控制展开样式
    $('a[data-toggle="fulltext"]').click(function () {
        $('.topic').addClass('fulltext');
    });

    // 文章较短时自动展开，避免出现"展开全文"按钮却无内容可展开的尴尬交互
    if ($('.topic').outerHeight(true) <= 700) {
        $('.topic').addClass('fulltext');
    }
});
