package com.shop.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.shop.pojo.Employee;
import com.shop.service.EmployeeService;

public class CustomerTaskAssignee implements TaskListener {
	
	/* 注意：此类没有交给spring容器进行管理，不能直接注入-->下面的写法是错误的 */
	/*
	 * @Autowired private EmployeeService employeeService;
	 */
	
	@Override
	public void notify(DelegateTask delegateTask) {
		//调用EmployeeService查询出当前待办人的上级
		// 获取到spring容器
		WebApplicationContext applicationContext = ContextLoader.getCurrentWebApplicationContext();
		//注意：要给service设置一个名字
		EmployeeService  employeeService = (EmployeeService) applicationContext.getBean("employeeService");
		// 获取request对象
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		// 员工
		//Employee employee = (Employee) request.getSession().getAttribute(Constants.GLOBLE_USER_SESSION);
		// 根据managerId拿到的上一级
		//Employee manager = employeeService.findEmployeeByManageId(employee.getManagerId());
		String assignee = (String)request.getSession().getAttribute(Constants.MANAGER_ASSIGN);
		// 使用监听器分配待办人
		//delegateTask.setAssignee(manager.getName());
		delegateTask.setAssignee(assignee);
		System.out.println("待办人");
	}
}
