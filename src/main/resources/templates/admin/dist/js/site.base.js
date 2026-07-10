/**
 * @module site.base
 *
 * 职责：
 *   - 后台基础脚本，在 DOM 就绪后初始化：
 *     1) 还原 select 下拉框选中值（data-select 属性记录的值）；
 *     2) 全选/反选 checkbox 联动（.checkall 控制同表内所有复选框）；
 *     3) 根据当前 URL 高亮侧边栏对应菜单项；
 *     4) 初始化 Bootstrap tooltip。
 *
 * 依赖：
 *   - jQuery：DOM 操作与事件绑定
 *   - Bootstrap tooltip：提示气泡
 *
 * 暴露接口：
 *   - 无（页面加载即自动执行，无需外部调用）
 */
// jQuery DOM 就绪回调：页面加载完成后自动执行后台基础初始化逻辑
$(function () {
    // 记录当前页面完整 URL，用于后续侧边栏菜单高亮匹配
    var page = String(window.location);

    /**
     * 还原下拉框选中值。
     *
     * 实现说明：
     *   遍历所有带 data-select 属性的 select 元素，把 data-select 记录的值
     *   重新设置为当前选中项，实现表单回显（如筛选条件保留）。
     */
    $('select[data-select]').each(function () {
        var id = $(this).attr('data-select');
        // 仅在 data-select 有值时回填，避免误置空
        if (typeof(id) != 'undefined' && id.length > 0) {
            $(this).val(id);
        }
    });

    /**
     * 全选复选框联动。
     *
     * 实现说明：
     *   .checkall 为表头全选框，点击时将其选中状态同步到所在 table 内所有 checkbox，
     *   实现一键全选/反选。
     */
    $('.checkall').on('click', function (event) {
        // 读取当前全选框的勾选状态
        var checked = $(this).prop('checked');
        // 选中最近的 table，将其内所有 checkbox 同步为相同状态
        $(this).closest('table').find('input[type=checkbox]').prop('checked', checked);
    });

    /**
     * 侧边栏菜单高亮。
     *
     * 实现说明：
     *   遍历 .sidebar-menu 下所有链接，若链接 href 与当前 URL 完全一致，
     *   或父路径一致（去掉最后一段后相同），则为对应 li 添加 active 高亮，
     *   使后台当前页对应的菜单项处于选中态。
     */
    $('.sidebar-menu a').each(function () {
        var $this = $(this);
        // 取链接的绝对 href 进行匹配
        var href = $this[0].href;
        if (href === page) {
            // URL 完全匹配：高亮当前菜单项
            $this.closest('li').addClass("active");
        } else if (href.substring(0, href.lastIndexOf('/')) === page.substring(0, page.lastIndexOf('/'))) {
            // 父路径匹配（如列表页与详情页同属一个模块）：也高亮，保持模块选中态
            $this.closest('li').addClass("active");
        }
    });

    // 初始化所有带 data-toggle="tooltip" 的元素为 Bootstrap tooltip 提示气泡
    $('[data-toggle="tooltip"]').tooltip();
});
