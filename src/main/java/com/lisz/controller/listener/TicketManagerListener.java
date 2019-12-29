package com.lisz.controller.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.lisz.controller.config.WeChatConfig;

import weixin.popular.support.TicketManager;
/*
 * Ticket是网页接口用的， Token是Java API去调接口用的
 */
@WebListener  //springboot下要加这个注解
public class TicketManagerListener implements ServletContextListener {
	
	private static final Log LOGGER = LogFactory.getLog(TicketManagerListener.class);
	
	@Autowired
	private WeChatConfig weChatConfig;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		LOGGER.info("----- TicketManagerListener contextInitialized -----");
		TicketManager.init(weChatConfig.getAppID(), 15, 60 * 119);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		LOGGER.info("----- TicketManagerListener contextDestroyed -----");
		TicketManager.destroyed();
	}
	
}
