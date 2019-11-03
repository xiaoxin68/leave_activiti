package com.shop.utils;

import java.util.Map;

public class LeaveBillMap {
	public static Map<Integer,String> leaveMap;

	static {
		leaveMap.put(0, "已放弃");
		leaveMap.put(1, "处理中");
		leaveMap.put(2, "已批准");
		leaveMap.put(3, "已驳回");
	}
}
