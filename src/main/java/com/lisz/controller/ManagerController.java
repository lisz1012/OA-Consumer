package com.lisz.controller;


import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.pagehelper.PageInfo;
import com.lisz.entity.Account;
import com.lisz.entity.Permission;
import com.lisz.entity.Role;
import com.lisz.service.AccountService;
import com.lisz.service.PermissionService;
import com.lisz.service.RoleService;

/**
 * 用户账号相关
 * @author shuzheng
 *
 */
@Controller // 写@RestController里面就没法跳转页面了
@RequestMapping("/manager")
public class ManagerController {
	
	@Reference(version = "1.0.0")
	private AccountService accountService; //service属于model，和后端做计算存储和整理数据的
	
	@Reference(version = "1.0.0")
	private PermissionService permissionService; //service属于model，和后端做计算存储和整理数据的
	
	@Reference(version = "1.0.0")
	private RoleService roleService; //service属于model，和后端做计算存储和整理数据的
	
	@GetMapping("accountList")
	public String accountList(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "5") int pageSize, Model model) {
		PageInfo<Account> page = accountService.findByPage(pageNum, pageSize);
		model.addAttribute("page", page);
		return "manager/accountList";
	}
	
	@GetMapping("roleList")
	public String roleList(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "5") int pageSize, Model model) {
		PageInfo<Role> page = roleService.findByPage(pageNum, pageSize);
		model.addAttribute("page", page);
		return "manager/roleList";
	}
	
	@GetMapping("permissionList")
	public String permissionList(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "5") int pageSize, Model model) {
		PageInfo<Permission> page = permissionService.findByPage(pageNum, pageSize);
		model.addAttribute("page", page);
		return "manager/permissionList";
	}
	
	@GetMapping("permissionModify")
	public String modify(@RequestParam int id, Model model) {
		Permission permission = permissionService.findById(id);
		model.addAttribute("permission", permission);
		return "manager/permissionModify";
	}
	
	@GetMapping("permissionAdd")
	public String permissionAdd() {
		return "manager/permissionModify"; //复用permissionModify.html
	}
	
	@GetMapping("rolePermissions/{id}")
	public String getPermissionsForRoleId(@PathVariable int id, Model model) {
		//PageInfo<Permission> page = permissionService.getPermissionsForRoleId(id);
		List<Permission> permissions = permissionService.findAll();
		Role role = roleService.findById(id);
		List<Permission> nonRolePermissions = permissions.stream().filter(p->!role.getPermissions().contains(p)).collect(Collectors.toList());
		System.out.println("Role: " + ToStringBuilder.reflectionToString(role, ToStringStyle.MULTI_LINE_STYLE));
		model.addAttribute("nonRolePermissions", nonRolePermissions);
		model.addAttribute("role", role);
		return "manager/rolePermissions";
	}
	
	@GetMapping("roleAdd")
	public String roleAdd() {
		return "manager/roleAdd";
	}
	
	@GetMapping("accountRoles/{id}")
	public String getRolesForAccountId(@PathVariable int id, Model model) {
		//PageInfo<Permission> page = permissionService.getPermissionsForRoleId(id);
		List<Role> roles = roleService.findAll();
		Account account = accountService.findById(id);
		//TODO NPE if account has no role, need to fix
		List<Role> nonAccountRoles = roles.stream().filter(r->!account.getRoles().contains(r)).collect(Collectors.toList());
		System.out.println("Account: " + ToStringBuilder.reflectionToString(account, ToStringStyle.MULTI_LINE_STYLE));
		model.addAttribute("nonAccountRoles", nonAccountRoles);
		model.addAttribute("account", account);
		return "manager/accountRoles";
	}
}
