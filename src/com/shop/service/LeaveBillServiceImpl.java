package com.shop.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shop.mapper.LeavebillMapper;
import com.shop.pojo.Leavebill;
import com.shop.pojo.LeavebillExample;
import com.shop.pojo.LeavebillExample.Criteria;

@Service
public class LeaveBillServiceImpl implements LeaveBillService {
	
	@Autowired
	private LeavebillMapper leavebillMapper;

	@Override
	public void saveLeaveBill(Leavebill leavebill) {
		leavebillMapper.insert(leavebill);

	}

	@Override
	public List<Leavebill> querMyLeaveBill(Long id) {
		LeavebillExample example = new LeavebillExample();
		Criteria criteria = example.createCriteria();
		criteria.andUserIdEqualTo(id);
		List<Leavebill> list = leavebillMapper.selectByExample(example);
		return list;
	}

}
