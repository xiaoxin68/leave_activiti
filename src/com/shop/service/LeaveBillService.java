package com.shop.service;

import java.util.List;

import com.shop.pojo.Leavebill;

public interface LeaveBillService {

	/**
	 * 保存请假申请
	 * @param leavebill
	 */
	void saveLeaveBill(Leavebill leavebill);

	/**
	 * 根据id查询请假单信息
	 * 
	 * @param id
	 * @return
	 */
	List<Leavebill> querMyLeaveBill(Long id);
}
