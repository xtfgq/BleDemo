package com.test.bluetooth.entry;

import java.io.Serializable;

public class BluetoothEntry implements Serializable {
	private static final long serialVersionUID = -1662305867018421540L;
	/**
	 * 蓝牙设备名称
	 */
	private String name;
	/**
	 * 蓝牙设备MAC地址
	 */
	private String address;
	/**
	 * 蓝牙设备密码
	 */
	private String pwd;
	/**
	 * 是否开启着
	 */
	private boolean isOpen;
	/**
	 * 蓝牙连接状态(0准备开始连接；1正在连接中；2连接成功)
	 */
	private Integer connectState;
	/**
	 * 是否继续存在于列表中，true继续存在，false移除列表
	 */
	private boolean isExitInList;

	public BluetoothEntry() {

	}

	public BluetoothEntry(String name, String address, String pwd,
			boolean isOpen, Integer connectState, boolean isExitInList) {
		this.name = name;
		this.address = address;
		this.pwd = pwd;
		this.isOpen = isOpen;
		this.connectState = connectState;
		this.isExitInList = isExitInList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public boolean isOpen() {
		return isOpen;
	}

	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

	public Integer getConnectState() {
		return connectState;
	}

	public void setConnectState(Integer connectState) {
		this.connectState = connectState;
	}

	public boolean isExitInList() {
		return isExitInList;
	}

	public void setExitInList(boolean isExitInList) {
		this.isExitInList = isExitInList;
	}
	
	@Override
	public String toString() {
		return "BluetoothEntry [name=" + name + ", address=" + address
				+ ", pwd=" + pwd + ", isOpen=" + isOpen + ", connectState="
				+ connectState + ", isExitInList=" + isExitInList + "]";
	}

	public interface BluetoothConnectState {
		/** 初始状态 **/
		static final int UNKNOWN = 0;
		/** 开始连接 **/
		static final int CONNECTING = 1;
		/** 连接成功 **/
		static final int CONNECT_SUCCESS = 2;
		/** 断开连接 **/
		static final int DISCONNECT = 3;
	}
	
	public interface BluetoothSearchState {
		static final int STOPPED = 0;
		static final int SEARCHING = 1;
	}
}
