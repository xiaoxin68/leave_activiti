package com.shop.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.shop.pojo.Employee;
import com.shop.service.EmployeeService;
import com.shop.utils.Constants;

/**
 * 用户管理
 * 
 * @Description: 
 * @Source: JDK 1.8
 * @Author: ZhangXiaoxin
 * @Date: 2019年10月20日
 * @Since: 1.0
 */
@Controller
public class EmployeeController {
	
	@Autowired
	private EmployeeService employeeService; 

	/**
	 * 用户登录
	 * 
	 * @param username
	 * @param password
	 * @param session
	 * @param model
	 * @return
	 */
	@RequestMapping("/login")
	public String login(String username,String password,HttpSession session,Model model) {
		Employee employee = employeeService.isLogin(username);
		if (employee != null) {
			if (employee.getPassword().equals(password)) {
				//表示查询到某个用户：存储到session
				session.setAttribute(Constants.GLOBLE_USER_SESSION, employee);
				model.addAttribute("username",username);
				return "index";
			}else {
				model.addAttribute("errorMsg", "账号或密码错误");
				return "login";
			}
		}else {
			model.addAttribute("errorMsg", "账号或密码错误");
			return "login";
		}
	}
	
	/**
	 * 退出登录
	 * 
	 * @return
	 */
	@RequestMapping("/logout")
	public String logout(HttpSession session) {
		//清除session
		session.invalidate();
		//重定向到login.jsp
		return "redirect:/login.jsp";
	}
	
	@RequestMapping("/getEmployeeList")
	public ModelAndView getEmployeeList() {
		List<Employee> employeeList = employeeService.getEmployeeList();
		ModelAndView model = new ModelAndView();
		model.addObject("employeeList",employeeList);
		model.setViewName("userList");
		return model;
	}
}
