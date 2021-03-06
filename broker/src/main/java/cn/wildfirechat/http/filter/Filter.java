package cn.wildfirechat.http.filter;

import cn.wildfirechat.http.handler.Response;
import cn.wildfirechat.http.handler.Request;

/**
 * 过滤器接口<br>
 * @author Looly
 *
 */
public interface Filter {
	
	/**
	 * 执行过滤
	 * @param request 请求对象
	 * @param response 响应对象
	 * @return 如果返回true，则继续执行下一步内容，否则中断
	 */
	public boolean doFilter(Request request, Response response);
}
