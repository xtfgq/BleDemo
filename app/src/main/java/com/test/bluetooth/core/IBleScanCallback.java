package com.test.bluetooth.core;

import android.bluetooth.BluetoothDevice;

/**
 * 蓝牙BLE扫描回调接口
 * @author 梁佳旺
 */
public interface IBleScanCallback {
	
	/** 蓝牙BLE扫描回调方法**/
	void onBleScan(BluetoothDevice device, int rssi, byte[] scanRecord);
	
	/** 蓝牙BLE扫描失败的回调方法 **/
	void onBleScanFailure(int errorCode);
}
