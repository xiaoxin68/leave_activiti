package com.shop.service;

import java.util.List;

import com.shop.pojo.Employee;
import com.shop.pojo.Leavebill;

public interface EmployeeService {

	/**
	 * ��¼
	 * 
	 * @param username
	 * @return
	 */
	Employee isLogin(String username);
	
	/**
	 * ����id��ѯ��һ��
	 * 
	 * @param managerId
	 * @return
	 */
	Employee findEmployeeByManageId(Long managerId);
	
	/**
	 * ��ȡ��ǰ�û��������ϼ�
	 * 
	 * @param managerId
	 * @return
	 */
	List<Employee> findAllEmployeeByManageId(Long managerId);
	
	/**
	 * ��ȡ����
	 * 
	 * @return
	 */
	List<Employee> getEmployeeList();
}
