package com.test.bluetooth.entry;

import java.io.Serializable;

/**
 * 血糖测量实体类
 * 
 * @author 梁佳旺
 */
public class BgMeasureRecordEntry extends BaseMeasureRecordEntry implements Serializable {
	private static final long serialVersionUID = -7834783399935761588L;
	/**
	 * 血糖id
	 */
	private Integer bgId;
	/**
	 * 血糖值
	 */
	private Float value;
	/**
	 * 血糖仪测量设备的编号(如果是通过客户端手动输入的血糖值，则该字段数值为空；如果是使用血糖仪进行测量的，则为该血糖仪的固有编号)
	 */
	private String deviceNo;
	
	/**
	 * 测量单位(mmol/L和mg/dL)
	 */
	private String measureUnit;
	
	/**
	 * 血糖值类型("BLOOD"：血液测量的血糖值；"CTRL"：质控液测量的血糖值；"DEFAULT"：其他方式测量的血糖值，默认值是这个)
	 */
	private String bgType;
	
	public BgMeasureRecordEntry() {
		
	}
	
	public BgMeasureRecordEntry(Integer bgId, Float value, String deviceNo,
			String measureUnit, String bgType) {
		super();
		this.bgId = bgId;
		this.value = value;
		this.deviceNo = deviceNo;
		this.measureUnit = measureUnit;
		this.bgType = bgType;
	}

	public Integer getBgId() {
		return bgId;
	}

	public void setBgId(Integer bgId) {
		this.bgId = bgId;
	}

	public Float getValue() {
		return value;
	}

	public void setValue(Float value) {
		this.value = value;
	}

	public String getDeviceNo() {
		return deviceNo;
	}

	public void setDeviceNo(String deviceNo) {
		this.deviceNo = deviceNo;
	}

	public String getMeasureUnit() {
		return measureUnit;
	}

	public void setMeasureUnit(String measureUnit) {
		this.measureUnit = measureUnit;
	}

	public String getBgType() {
		return bgType;
	}

	public void setBgType(String bgType) {
		this.bgType = bgType;
	}

	@Override
	public String toString() {
		return "BgMeasureRecordEntry [bgId=" + bgId + ", value=" + value
				+ ", deviceNo=" + deviceNo + ", measureUnit=" + measureUnit
				+ ", bgType=" + bgType + "]";
	}

	/**
	 * 血糖值类型("BLOOD"：血液测量的血糖值；"CTRL"：质控液测量的血糖值；"DEFAULT"：其他方式测量的血糖值，默认值是这个)
	 */
	public interface BgType {
		/** "DEFAULT"：其他方式测量的血糖值，默认值是这个 **/
		static final String DEFAULT = "DEFAULT";
		/** "BLOOD"：血液测量的血糖值 **/
		static final String BLOOD = "BLOOD";
		/** "CTRL"：质控液测量的血糖值 **/
		static final String CTRL = "CTRL";
	}
}
