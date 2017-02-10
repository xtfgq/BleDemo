package com.test.bluetooth.entry;

import java.io.Serializable;

/**
 * 各种测量记录(血糖测量、食物测量、药物测量、运动测量、HBA1C测量、体重测量)的实体基类
 * 
 * @author 梁佳旺
 */
public class BaseMeasureRecordEntry implements Serializable {
	private static final long serialVersionUID = 2811896268183624457L;
	/**
	 * 用户id
	 */
	protected String userId;
	/**
	 * 测量时间
	 */
	protected Long measureTime;
	/**
	 * 测量时段(早餐前、早餐后、午餐前、午餐后、晚餐前、晚餐后)
	 */
	protected String timeType;

	public BaseMeasureRecordEntry() {
		
	}

	public BaseMeasureRecordEntry(String userId, Long measureTime, String timeType) {
		super();
		this.userId = userId;
		this.measureTime = measureTime;
		this.timeType = timeType;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Long getMeasureTime() {
		return measureTime;
	}

	public void setMeasureTime(Long measureTime) {
		this.measureTime = measureTime;
	}

	public String getTimeType() {
		return timeType;
	}

	public void setTimeType(String timeType) {
		this.timeType = timeType;
	}

	@Override
	public String toString() {
		return "BaseMeasureRecordEntry [userId=" + userId + ", measureTime="
				+ measureTime + ", timeType=" + timeType + "]";
	}
}
