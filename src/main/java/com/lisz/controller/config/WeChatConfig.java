package com.lisz.controller.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class WeChatConfig {
	// 从application.properties里面读取,下面的setters其实不需要
	@Value("${WeChat.appID}")
	private String appID;
	@Value("${WeChat.appsecret}")
	private String appsecret;
	@Value("${WeChat.tokenString}")
	private String tokenString;
	@Value("${WeChat.baseDomain}")
	private String baseDomain;
	@Value("${WeChat.templateId}")
	private String templateId;
	@Value("${WeChat.username}")
	private String username;
	
	public String getAppID() {
		return appID;
	}
	public void setAppID(String appID) {
		this.appID = appID;
	}
	public String getAppsecret() {
		return appsecret;
	}
	public void setAppsecret(String appsecret) {
		this.appsecret = appsecret;
	}
	public String getTokenString() {
		return tokenString;
	}
	public void setTokenString(String tokenString) {
		this.tokenString = tokenString;
	}
	public String getBaseDomain() {
		return baseDomain;
	}
	public void setBaseDomain(String baseDomain) {
		this.baseDomain = baseDomain;
	}
	public String getTemplateId() {
		return templateId;
	}
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
}
