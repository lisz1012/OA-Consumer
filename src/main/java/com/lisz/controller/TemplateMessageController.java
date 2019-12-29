package com.lisz.controller;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lisz.controller.config.WeChatConfig;

import weixin.popular.api.MessageAPI;
import weixin.popular.bean.message.templatemessage.TemplateMessage;
import weixin.popular.bean.message.templatemessage.TemplateMessageItem;
import weixin.popular.bean.message.templatemessage.TemplateMessageResult;
import weixin.popular.support.TokenManager;

@RestController
@RequestMapping("/message")
public class TemplateMessageController {
	
	@Autowired
	private WeChatConfig weChatConfig;
	
	@RequestMapping("")
	public TemplateMessageResult list(Model model, @RequestParam Map<String, String> param, HttpServletRequest request) throws Exception {
		/*InputStream inputStream = request.getInputStream();
		EventMessage eventMessage = XMLConverUtil.convertToObject(EventMessage.class, inputStream);*/
		TemplateMessage templateMessage = new TemplateMessage();
		templateMessage.setUrl(weChatConfig.getBaseDomain() + "/profile/my");//点击（详情）后用户微信跳转到哪个页面
		templateMessage.setTemplate_id(weChatConfig.getTemplateId());//微信通知的那个小方块
		templateMessage.setTouser(weChatConfig.getUsername());//发给哪个用户
		
		TemplateMessageItem item = new TemplateMessageItem("李老师的系统架构设计课要开始啦！", "#173177");
		LinkedHashMap<String, TemplateMessageItem> map = new LinkedHashMap<>();
		map.put("course", item); //微信UI里面用{{course.DATA}}定义的模版的course占位符的内容及其字体颜色
		templateMessage.setData(map);
		TemplateMessageResult result = MessageAPI.messageTemplateSend(TokenManager.getDefaultToken(), templateMessage);
		
		return result;
	}
}
