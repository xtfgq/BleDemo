package com.test.bluetooth.entry;

import java.io.Serializable;

/**
 * 我的设备实体类
 * 
 * @author 梁佳旺
 */
public class MyDeviceEntry implements Serializable {
	/**
	 * 设备Id
	 */
	private Integer id;
	/**
	 * 用户Id
	 */
	private String userId;
	/**
	 * 设备名称
	 */
	private String name;
	/**
	 * 设备类型:0：不是低功耗蓝牙设备；1：低功耗蓝牙设备)
	 */
	private Integer type;
	/**
	 * 状态，用户是否已选择该设备(0未选择，1已选择)
	 */
	private Integer status;
	/**
	 * 设备使用量
	 */
	private Integer freCount;
	/**
	 * 设备图片Url
	 */
	private String logo;
	
	public MyDeviceEntry() {

	}

	public MyDeviceEntry(Integer id, String userId, String name, Integer type,
			Integer status, Integer freCount, String logo) {
		super();
		this.id = id;
		this.userId = userId;
		this.name = name;
		this.type = type;
		this.status = status;
		this.freCount = freCount;
		this.logo = logo;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getFreCount() {
		return freCount;
	}

	public void setFreCount(Integer freCount) {
		this.freCount = freCount;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	@Override
	public String toString() {
		return "MyDeviceEntry [id=" + id + ", userId=" + userId + ", name="
				+ name + ", type=" + type + ", status=" + status
				+ ", freCount=" + freCount + ", logo=" + logo + "]";
	}

	/**
	 * 设备类型:0：不是低功耗蓝牙设备；1：低功耗蓝牙设备)
	 */
	public interface DeviceType {
		/** 0：不是低功耗蓝牙设备 **/
		static final int DEFAULT = 0;
		/** 1：低功耗蓝牙设备 **/
		static final int BLE = 1;
	}
	
	/**
	 * 状态，用户是否已选择该设备(0未选择，1已选择)
	 */
	public interface DeviceStatus {
		/** 0未选择 **/
		static final int UNSELECT = 0;
		/** 1已选择 **/
		static final int SELECTED = 1;
	}
}