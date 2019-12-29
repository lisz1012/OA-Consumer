package com.lisz.controller;

import java.awt.image.BufferedImage;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lisz.controller.config.WeChatConfig;

import weixin.popular.api.QrcodeAPI;
import weixin.popular.bean.qrcode.QrcodeTicket;
import weixin.popular.support.TokenManager;

@RestController
@RequestMapping("/QRCode")
public class QRCodeController {
	
	@Autowired
	private WeChatConfig weChatConfig;
	
	@RequestMapping("/create")
	public void list(Model model, @RequestParam Map<String, String> param, HttpServletRequest request, HttpServletResponse response) throws Exception {
		//第三个参数就是二维码可以附带的那个参数，如果传入的是userID，则可以获取是由谁的ID生成的二维码被别人扫了，这样可以做类似“助力”的奖励。IO耗资源，应考虑把它单搞成一个微服务
		QrcodeTicket ticket = QrcodeAPI.qrcodeCreateTemp(TokenManager.getDefaultToken(), 3600, weChatConfig.getUsername()); 
		System.out.println(ToStringBuilder.reflectionToString(ticket, ToStringStyle.JSON_STYLE));
		BufferedImage bufferedImage = QrcodeAPI.showqrcode(ticket.getTicket());
		ImageIO.write(bufferedImage, "jpg", response.getOutputStream());
	}
}
