package com.shop.service;

import java.util.List;

import com.shop.pojo.Leavebill;

public interface LeaveBillService {

	/**
	 * �����������
	 * @param leavebill
	 */
	void saveLeaveBill(Leavebill leavebill);

	/**
	 * ����id��ѯ��ٵ���Ϣ
	 * 
	 * @param id
	 * @return
	 */
	List<Leavebill> querMyLeaveBill(Long id);
}
