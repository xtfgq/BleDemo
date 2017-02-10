package com.test.bluetooth.core;

/**
 * 蓝牙BLE扫描回调接口
 * @author 梁佳旺
 */
public abstract class BaseBluetoothScanner {
	
	/** 开始扫描蓝牙设备 **/
	public abstract void onStartBleScan();

	/** 停止扫描蓝牙设备 **/
    public abstract void onStopBleScan();
}
