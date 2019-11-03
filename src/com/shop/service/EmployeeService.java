package com.shop.service;

import java.util.List;

import com.shop.pojo.Employee;
import com.shop.pojo.Leavebill;

public interface EmployeeService {

	/**
	 * 登录
	 * 
	 * @param username
	 * @return
	 */
	Employee isLogin(String username);
	
	/**
	 * 根据id查询上一级
	 * 
	 * @param managerId
	 * @return
	 */
	Employee findEmployeeByManageId(Long managerId);
	
	/**
	 * 获取当前用户的所有上级
	 * 
	 * @param managerId
	 * @return
	 */
	List<Employee> findAllEmployeeByManageId(Long managerId);
	
	/**
	 * 获取所有
	 * 
	 * @return
	 */
	List<Employee> getEmployeeList();
}
