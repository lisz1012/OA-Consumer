package com.lisz.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lisz.controller.config.WeChatConfig;

import weixin.popular.api.MenuAPI;
import weixin.popular.bean.BaseResult;
import weixin.popular.bean.message.EventMessage;
import weixin.popular.bean.xmlmessage.XMLImageMessage;
import weixin.popular.bean.xmlmessage.XMLMessage;
import weixin.popular.bean.xmlmessage.XMLTextMessage;
import weixin.popular.support.ExpireKey;
import weixin.popular.support.TokenManager;
import weixin.popular.support.expirekey.DefaultExpireKey;
import weixin.popular.util.SignatureUtil;
import weixin.popular.util.XMLConverUtil;

@Controller
@RequestMapping("/WeChat")
public class WeChatController {
	@Autowired
	private WeChatConfig weChatConfig;
	
	private static final Log LOGGER = LogFactory.getLog(WeChatController.class);
	
	private static ExpireKey expireKey = new DefaultExpireKey();
	
	// 这个API是在微信服务器那边注册登记过的，用户已一进入公众号，微信服务器就调用这个API，并把outputStream的内容写回用户端
	@RequestMapping("signature")
	@ResponseBody
	public void signature(@RequestParam Map<String, String> params, HttpServletRequest request, HttpServletResponse response) throws Exception {
		LOGGER.info("Touched");
		// TODO加入验证
		ServletInputStream inputStream = request.getInputStream();
		ServletOutputStream outputStream = response.getOutputStream();

		// 算出来的签名
		String signature = params.get("signature");
		String echostr = params.get("echostr");
		String timestamp = params.get("timestamp");
		String nonce = params.get("nonce");
		
		// 对称加密 本地
		String token = weChatConfig.getTokenString();
		
		if (StringUtils.isEmpty(signature) || StringUtils.isEmpty(timestamp)) {
			OutputStreamWriter(outputStream, "fail request");
			return;
		}
		
		if (echostr != null) {
			OutputStreamWriter(outputStream, echostr);
			return;
		}
		
		// 验证请求签名
		if (!signature.equals(SignatureUtil.generateEventMessageSignature(token, timestamp, nonce))) {
			System.out.println("The request signature is invalid");
			return;
		}
		
		if (inputStream != null) {
			EventMessage eventMessage = XMLConverUtil.convertToObject(EventMessage.class, inputStream);
			System.out.println("ToStringBuilder: " + ToStringBuilder.reflectionToString(eventMessage, ToStringStyle.JSON_STYLE));
			String key = eventMessage.getFromUserName() + "__" + eventMessage.getToUserName() + "__" + eventMessage.getMsgId() + "__" + eventMessage.getCreateTime();
			
			// 微信不确定发的消息是否被接受，所以发好几次，这里我们用key避免重复处理
			if (expireKey.exists(key)) {
				LOGGER.warn("重复通知，不作处理。");
				return;
			} else {
				expireKey.add(key);
			}
			
			// 这里注意：回复消息的FromUserName和ToUserNmae正好跟发进来的eventMessage是相反的，所以先是eventMessage.getFromUserName(),再写eventMessage.getToUserName()
			// 其中fromUserName就是openID，拿着它可以得到用户的个人信息。access_token是我们的服务器和微信服务器交互时用的
			XMLMessage xmlMessage = new XMLTextMessage(eventMessage.getFromUserName(), eventMessage.getToUserName(), "请先<a href='http://yhcdtx.natappfree.cc/profile/my'>完善一下信息</a>"); //回复文本消息
									//new XMLImageMessage(eventMessage.getFromUserName(), eventMessage.getToUserName(), "0IVg8-ywh4qskC42q3TVeTb9zOrW15RypCXw4JrBp1YGy4XjEHv0yPvyZBFMCfvX"); // 回复图片消息
									//这个文件暂存到了微信服务器，将来要通过文件流的方式转存到FastDFS
			// 回复
			if (null != xmlMessage) {
				xmlMessage.outputStreamWrite(outputStream);
			}
		}
	}

	// 必须在浏览器里刷一下 http://spza8h.natappfree.cc/WeChat/createMenu 才能让这里的新改动生效，这个菜单和createMenu2，哪个被在浏览器注册了，哪个就起作用，后面执行的覆盖前面的
	@RequestMapping("createMenu")
	@ResponseBody
	public BaseResult createMenu() {
		LOGGER.info("createMenu is called!");
		String menuString = "{\n" + 
				"     \"button\":[\n" + 
				"     {	\n" + 
				"          \"type\":\"click\",\n" + 
				"          \"name\":\"今日歌曲\",\n" + 
				"          \"key\":\"V1001_TODAY_MUSIC\"\n" + 
				"      },\n" + 
				"      {\n" + 
				"           \"name\":\"菜单\",\n" + 
				"           \"sub_button\":[\n" + 
				"           {	\n" + 
				"               \"type\":\"view\",\n" + 
				"               \"name\":\"搜索\",\n" + 
				"               \"url\":\"http://www.soso.com/\"\n" + 
				"            },\n" + 
				"            {\n" + 
				"               \"type\":\"click\",\n" + 
				"               \"name\":\"赞一下我们吧\",\n" + 
				"               \"key\":\"V1001_GOOD\"\n" + 
				"            }]\n" + 
				"       }]\n" + 
				" }";
		//通知微信服务器：创建公众号菜单，以后关注的人登陆进来之后就能看见了
		BaseResult result = MenuAPI.menuCreate(TokenManager.getDefaultToken(), menuString);
		return result;
	}
	
	// 必须在浏览器里刷一下 http://spza8h.natappfree.cc/WeChat/createMenu2 才能让这里的新改动生效
	@RequestMapping("createMenu2")
	@ResponseBody
	public BaseResult createMenu2() {
		LOGGER.info("createMenu2 is called!");
		String menuString = "{\n" + 
				"    \"button\": [\n" + 
				"        {\n" + 
				"            \"name\": \"扫码\", \n" + 
				"            \"sub_button\": [\n" + 
				"                {\n" + 
				"                    \"type\": \"scancode_waitmsg\", \n" + 
				"                    \"name\": \"扫码带提示\", \n" + 
				"                    \"key\": \"rselfmenu_0_0\", \n" + 
				"                    \"sub_button\": [ ]\n" + 
				"                }, \n" + 
				"                {\n" + 
				"                    \"type\": \"scancode_push\", \n" + 
				"                    \"name\": \"扫码推事件\", \n" + 
				"                    \"key\": \"rselfmenu_0_1\", \n" + 
				"                    \"sub_button\": [ ]\n" + 
				"                }\n" + 
				"            ]\n" + 
				"        }, \n" + 
				"        {\n" + 
				"            \"name\": \"发图\", \n" + 
				"            \"sub_button\": [\n" + 
				"                {\n" + 
				"                    \"type\": \"pic_sysphoto\", \n" + 
				"                    \"name\": \"系统拍照发图\", \n" + 
				"                    \"key\": \"rselfmenu_1_0\", \n" + 
				"                   \"sub_button\": [ ]\n" + 
				"                 }, \n" + 
				"                {\n" + 
				"                    \"type\": \"pic_photo_or_album\", \n" + 
				"                    \"name\": \"拍照或者相册发图\", \n" + 
				"                    \"key\": \"rselfmenu_1_1\", \n" + 
				"                    \"sub_button\": [ ]\n" + 
				"                }, \n" + 
				"                {\n" + 
				"                    \"type\": \"pic_weixin\", \n" + 
				"                    \"name\": \"微信相册发图\", \n" + 
				"                    \"key\": \"rselfmenu_1_2\", \n" + 
				"                    \"sub_button\": [ ]\n" + 
				"                }\n" + 
				"            ]\n" + 
				"        }, \n" + 
				"        {\n" + 
				"            \"name\": \"发送位置\", \n" + 
				"            \"type\": \"location_select\", \n" + 
				"            \"key\": \"rselfmenu_2_0\"\n" + 
				"        },\n" + 
				"    ]\n" + 
				"}";
		//通知微信服务器：创建公众号菜单，以后关注的人登陆进来之后就能看见了
		BaseResult result = MenuAPI.menuCreate(TokenManager.getDefaultToken(), menuString);
		return result;
	}
	
	// 直接outputStream把字节流写出去，这里HttpServletResponse的outputStream不要关闭Servlet会自动将其关闭
	private boolean OutputStreamWriter(OutputStream outputStream, String text) {
		try {
			outputStream.write(text.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
