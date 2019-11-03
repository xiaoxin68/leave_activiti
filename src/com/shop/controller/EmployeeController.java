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
 * �û�����
 * 
 * @Description: 
 * @Source: JDK 1.8
 * @Author: ZhangXiaoxin
 * @Date: 2019��10��20��
 * @Since: 1.0
 */
@Controller
public class EmployeeController {
	
	@Autowired
	private EmployeeService employeeService; 

	/**
	 * �û���¼
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
				//��ʾ��ѯ��ĳ���û����洢��session
				session.setAttribute(Constants.GLOBLE_USER_SESSION, employee);
				model.addAttribute("username",username);
				return "index";
			}else {
				model.addAttribute("errorMsg", "�˺Ż��������");
				return "login";
			}
		}else {
			model.addAttribute("errorMsg", "�˺Ż��������");
			return "login";
		}
	}
	
	/**
	 * �˳���¼
	 * 
	 * @return
	 */
	@RequestMapping("/logout")
	public String logout(HttpSession session) {
		//���session
		session.invalidate();
		//�ض���login.jsp
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
