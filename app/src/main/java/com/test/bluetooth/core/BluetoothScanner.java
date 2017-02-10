package com.test.bluetooth.core;

import android.bluetooth.BluetoothAdapter;
import android.os.Build;

/**
 * 蓝牙BLE扫描版本兼容封装类
 * @author 梁佳旺
 */
public class BluetoothScanner {
	
	private BaseBluetoothScanner mBaseBluetoothScanner;
	
	public BluetoothScanner(BluetoothAdapter bluetoothAdapter, IBleScanCallback scanCallback) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			mBaseBluetoothScanner = new LollipopBluetoothScanner(bluetoothAdapter, scanCallback);
		} else {
			mBaseBluetoothScanner = new JellyBeanBluetoothScanner(bluetoothAdapter, scanCallback);
		}
	}
	
	/**
	 * 开始扫描蓝牙设备
	 */
	public void startBleScan(){
		if (null != mBaseBluetoothScanner) {
			mBaseBluetoothScanner.onStartBleScan();
		}
	}
	
	/**
	 * 停止扫描蓝牙设备
	 */
	public void stopBleScan(){
		if (null != mBaseBluetoothScanner) {
			mBaseBluetoothScanner.onStopBleScan();
		}
	}
}
