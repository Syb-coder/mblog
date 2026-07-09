/**
 */
package com.mtons.mblog.modules.template.directive;

import com.mtons.mblog.base.lang.Consts;
import com.mtons.mblog.base.utils.BeanMapUtils;
import com.mtons.mblog.modules.data.PostVO;
import com.mtons.mblog.modules.entity.Channel;
import com.mtons.mblog.modules.service.ChannelService;
import com.mtons.mblog.modules.service.PostService;
import com.mtons.mblog.modules.template.DirectiveHandler;
import com.mtons.mblog.modules.template.TemplateDirective;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 文章列表指令 —— 最核心的模板指令
 *
 * <h3>作用</h3>
 * 前台的所有文章列表页（首页、栏目页）都通过 &lt;@contents&gt; 指令渲染。
 * 不用每个 Controller 都去查文章列表，模板里直接用指令即可。
 *
 * <h3>参数说明</h3>
 * - channelId：指定栏目（=0 时显示所有栏目，排除已关闭的）
 * - order：排序方式（newest / hottest）
 * - pageNo / size：分页参数
 *
 * <h3>为什么未指定栏目时要排除已关闭的？</h3>
 * 后台管理员可以关闭栏目（status=1）。前台显示"全部文章"时，
 * 不应该包含已关闭栏目的文章，否则用户通过首页 URL 能访问到
 * 管理员想隐藏的内容。
 */
@Component
public class ContentsDirective extends TemplateDirective {
    private static final Logger log = LoggerFactory.getLogger(ContentsDirective.class);

    @Autowired
    private PostService postService;
    @Autowired
    private ChannelService channelService;

    /**
     * 获取指令名称
     *
     * @return 指令名称 "contents"
     */
    @Override
    public String getName() {
        return "contents";
    }

    /**
     * @param handler 指令处理器
     * @throws Exception 异常
     */
    @Override
    public void execute(DirectiveHandler handler) throws Exception {
        Integer channelId = handler.getInteger("channelId", 0);
        String order = handler.getString("order", Consts.order.NEWEST);

        // 调试日志：打印接收到的 channelId，用于排查栏目筛选问题
        log.info("ContentsDirective 收到参数 —— channelId = {}, order = {}", channelId, order);

        Set<Integer> excludeChannelIds = new HashSet<>();

        // 未指定栏目时，排除所有已关闭的栏目
        if (channelId <= 0) {
            log.info("ContentsDirective —— channelId <= 0，不进行栏目筛选，将查询全部文章");
            List<Channel> channels = channelService.findAll(Consts.STATUS_CLOSED);
            if (channels != null) {
                channels.forEach((c) -> excludeChannelIds.add(c.getId()));
            }
        }

        Pageable pageable = wrapPageable(handler, Sort.by(Sort.Direction.DESC, BeanMapUtils.postOrder(order)));
        Page<PostVO> result = postService.paging(pageable, channelId, excludeChannelIds);
        handler.put(RESULTS, result).render();
    }
}