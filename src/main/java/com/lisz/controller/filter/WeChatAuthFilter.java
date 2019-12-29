package com.lisz.controller.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.lisz.controller.config.WeChatConfig;

import weixin.popular.bean.user.User;

@WebFilter(filterName = "WeChatAuthFilter", urlPatterns = "/profile/*")
public class WeChatAuthFilter implements Filter {

	private static final Log LOGGER = LogFactory.getLog(WeChatAuthFilter.class);

	@Autowired
	private WeChatConfig weChatConfig;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		LOGGER.info("WeChatAuthFilter init ... ");
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest)req;
		HttpServletResponse response = (HttpServletResponse)resp;
		//框架封装的user对象
		User user = (User)request.getSession().getAttribute("user");
		if (null == user) {
			String uri = request.getRequestURI();
			String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + weChatConfig.getAppID() + "&redirect_uri=http://yhcdtx.natappfree.cc/auth?uri=" + uri + "&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect\n";
			response.sendRedirect(url);
		} else {
			LOGGER.info("user: " + ToStringBuilder.reflectionToString(user));
			chain.doFilter(request, response); //只有这一句的话直接通过，相当于没有filter
		}
	}

}
