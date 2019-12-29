package com.lisz.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.lisz.controller.config.WeChatConfig;

import weixin.popular.api.SnsAPI;
import weixin.popular.bean.sns.SnsToken;
import weixin.popular.bean.user.User;

@Controller
@RequestMapping("/auth")
public class AuthController {
	
	@Autowired
	private WeChatConfig weChatConfig;
	
	@RequestMapping("")
	public String list(Model model, @RequestParam Map<String, String> param, HttpServletRequest request) {
		System.out.println("___111");
		if (null == param || null == param.get("code")) {
			return "error login";
		}
		System.out.println("1231231111111111");
		String code = param.get("code");
		
		// code在这里被用来换取token，token是专门被用来拉取用户信息的
		SnsToken token = SnsAPI.oauth2AccessToken(weChatConfig.getAppID(), weChatConfig.getAppsecret(), code);
		User user = SnsAPI.userinfo(token.getAccess_token(), weChatConfig.getAppID(), "zh_CN");
		
		System.out.println(ToStringBuilder.reflectionToString(user));
		request.getSession().setAttribute("user", user);
		
		System.err.println(ToStringBuilder.reflectionToString(user));
		
		String uri = param.get("uri");
		
		// 访问受限的那个uri
		return "redirect:" + uri;
	}
}
