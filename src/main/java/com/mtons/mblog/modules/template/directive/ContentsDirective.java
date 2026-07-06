/* *
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * FreeMarker 使用方式：
 * <pre>{@code
 * <@contents channelId=1 order="newest" pn=pn>
 *     <#list results.content as row>
 *         ${row.title}
 *     </#list>
 * </@contents>
 * }</pre>
 * <p>
 * 支持参数：
 * <ul>
 *     <li>order：排序方式，如 newest（最新）、hottest（最热）</li>
 *     <li>pageNo：页码</li>
 *     <li>size：每页数量</li>
 * </ul>
 * 输出：results - 分页文章列表
 */
@Component
public class ContentsDirective extends TemplateDirective {
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

    /* *
     * @param handler 指令处理器
     * @throws Exception 异常
     */
    @Override
    public void execute(DirectiveHandler handler) throws Exception {
        Integer channelId = handler.getInteger("channelId", 0);
        String order = handler.getString("order", Consts.order.NEWEST);

        Set<Integer> excludeChannelIds = new HashSet<>();

        // 未指定栏目时，排除所有已关闭的栏目
        if (channelId <= 0) {
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
