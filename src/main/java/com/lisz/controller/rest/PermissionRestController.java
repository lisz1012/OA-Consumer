package com.lisz.controller.rest;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lisz.entity.Permission;
import com.lisz.entity.ResponseStatus;
import com.lisz.service.PermissionService;

import io.swagger.annotations.Api;

/**
 * Restful风格的URI的Controller，只跟用户交换JSON数据
 * @author shuzheng
 *
 */
@RestController //Restful Controller, 返回对象的时候，方法的脑袋顶上不用写@ResponseBody注解
//下面括号里如果还有method = RequestMethod.GET则会在swagger UI中出现6个API，每个方法都可以用GET方式访问
@RequestMapping(value = "/api/v1/manager/permission") //v1是为了后面出新版本的时候用v2，当前版本不用改.Restful是一种规范：/api/版本号/系统名称/实体/方法/被操作ID。。不一定完全遵循
@Api(tags = {"权限管理"}, description = "对于权限对象的增删改")
public class PermissionRestController {
	@Reference(version = "1.0.0")
	private PermissionService permissionService;
	
	@PostMapping("add")
	public ResponseStatus add(@RequestBody Permission permission) {
		System.out.println(ToStringBuilder.reflectionToString(permission, ToStringStyle.MULTI_LINE_STYLE));
		return permissionService.add(permission);
	}
	
	@PostMapping("update")
	public ResponseStatus update(@RequestBody Permission permission) {
		System.out.println(ToStringBuilder.reflectionToString(permission, ToStringStyle.MULTI_LINE_STYLE));
		return permissionService.update(permission);
	}
	
	@DeleteMapping("delete")
	public ResponseStatus deleteById(@RequestParam int id) {
		System.out.println("Deleting permission id = " + id);
		return permissionService.deleteById(id);
	}
}
