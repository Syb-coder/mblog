/**
 */
package com.sunblog.modules.template.directive;

import com.sunblog.modules.data.PostVO;
import com.sunblog.modules.service.PostService;
import com.sunblog.modules.template.DirectiveHandler;
import com.sunblog.modules.template.TemplateDirective;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * 根据作者取文章列表指令
 * <p>
 * FreeMarker 使用方式：
 * <pre>{@code
 * <@user_contents userId=1>
 *     <#list results.content as row>
 *         ${row.title}
 *     </#list>
 * </@user_contents>
 * }</pre>
 * <p>
 * 支持参数：
 * <ul>
 *     <li>userId：作者 ID（默认 0）</li>
 *     <li>pageNo：页码</li>
 *     <li>size：每页数量</li>
 * </ul>
 * 输出：results - 分页文章列表
 */
@Component
public class UserContentsDirective extends TemplateDirective {
    @Autowired
	private PostService postService;

	/**
	 * 获取指令名称
	 *
	 * @return 指令名称 "user_contents"
	 */
	@Override
	public String getName() {
		return "user_contents";
	}

    /**
     * 执行作者文章分页查询
     *
     * @param handler 指令处理器
     * @throws Exception 异常
     */
    @Override
    public void execute(DirectiveHandler handler) throws Exception {
        long userId = handler.getInteger("userId", 0);
        Pageable pageable = wrapPageable(handler);

        Page<PostVO> result = postService.pagingByAuthorId(pageable, userId);
        handler.put(RESULTS, result).render();
    }

}