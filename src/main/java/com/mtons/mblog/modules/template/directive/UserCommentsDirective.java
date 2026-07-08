/**
 */
package com.mtons.mblog.modules.template.directive;

import com.mtons.mblog.modules.data.CommentVO;
import com.mtons.mblog.modules.service.CommentService;
import com.mtons.mblog.modules.template.DirectiveHandler;
import com.mtons.mblog.modules.template.TemplateDirective;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * 根据作者取评论列表指令
 * <p>
 * FreeMarker 使用方式：
 * <pre>{@code
 * <@user_comments userId=1>
 *     <#list results.content as row>
 *         ${row.content}
 *     </#list>
 * </@user_comments>
 * }</pre>
 * <p>
 * 支持参数：
 * <ul>
 *     <li>userId：作者 ID（默认 0）</li>
 *     <li>pageNo：页码</li>
 *     <li>size：每页数量</li>
 * </ul>
 * 输出：results - 分页评论列表
 *
 * @since 3.0
 */
@Component
public class UserCommentsDirective extends TemplateDirective {
    @Autowired
	private CommentService commentService;

	/**
	 * 获取指令名称
	 *
	 * @return 指令名称 "user_comments"
	 */
	@Override
	public String getName() {
		return "user_comments";
	}

    /**
     * 执行作者评论分页查询
     *
     * @param handler 指令处理器
     * @throws Exception 异常
     */
    @Override
    public void execute(DirectiveHandler handler) throws Exception {
        long userId = handler.getInteger("userId", 0);
        Pageable pageable = wrapPageable(handler);

        Page<CommentVO> result = commentService.pagingByAuthorId(pageable, userId);
        handler.put(RESULTS, result).render();
    }

}