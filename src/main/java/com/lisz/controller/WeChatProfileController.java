package com.lisz.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 展示修改个人信息，在微信端
 * @author shuzheng
 */
@Controller
@RequestMapping("/profile")
public class WeChatProfileController {
	
	@RequestMapping("/my")
	public String my(Model model) {
		
		return "/profile/my";
	}
}
