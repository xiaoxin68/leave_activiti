package com.shop.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shop.mapper.EmployeeMapper;
import com.shop.pojo.Employee;
import com.shop.pojo.EmployeeExample;
import com.shop.pojo.EmployeeExample.Criteria;
import com.shop.pojo.Leavebill;

@Service("employeeService")
public class EmployeeServiceImpl implements EmployeeService{
	
	@Autowired
	private EmployeeMapper employeeMapper;

	@Override
	public Employee isLogin(String username) {
		EmployeeExample example = new EmployeeExample();
		Criteria criteria = example.createCriteria();
		criteria.andNameEqualTo(username);
		List<Employee> list = employeeMapper.selectByExample(example);
		if (list != null && list.size()>0) {
			return list.get(0);
		}
		return null;
	}

	@Override
	public Employee findEmployeeByManageId(Long managerId) {
		Employee employee = employeeMapper.selectByPrimaryKey(managerId);
		return employee;
	}

	@Override
	public List<Employee> findAllEmployeeByManageId(Long managerId) {
		EmployeeExample example = new EmployeeExample();
		Criteria criteria = example.createCriteria();
		criteria.andManagerIdEqualTo(managerId-1);
		List<Employee> list = employeeMapper.selectByExample(example);
		return list;
	}

	@Override
	public List<Employee> getEmployeeList() {
		EmployeeExample example = new EmployeeExample();
		Criteria criteria = example.createCriteria();
		List<Employee> list = employeeMapper.selectByExample(example);
		return list;
	}

}
