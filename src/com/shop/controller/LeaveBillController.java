package com.shop.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.shop.pojo.Employee;
import com.shop.pojo.Leavebill;
import com.shop.service.LeaveBillService;
import com.shop.utils.Constants;

@Controller
public class LeaveBillController {
	
	@Autowired
	private LeaveBillService leaveBillService;
	
	/**
	 * 查询我的请假单
	 * 
	 * @param session
	 * @return
	 */
	@RequestMapping("/querMyLeaveBill")
	public ModelAndView querMyLeaveBill(HttpSession session) {
		Employee employee = (Employee) session.getAttribute(Constants.GLOBLE_USER_SESSION);
		List<Leavebill> leavebillList = leaveBillService.querMyLeaveBill(employee.getId());
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("leavebillList", leavebillList);
		modelAndView.setViewName("my_leavebill");// 跳转到workflow_task
		return modelAndView;
	}

}
