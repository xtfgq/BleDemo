package com.test.bluetooth.core;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Build;

/**
 * 蓝牙BLE扫描封装类(针对android 4.3到4.4版本的蓝牙)
 * @author 梁佳旺
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class JellyBeanBluetoothScanner extends BaseBluetoothScanner {
	private static final String TAG = JellyBeanBluetoothScanner.class.getSimpleName();
	
	private BluetoothAdapter mBluetoothAdapter;
	private IBleScanCallback mScanCallback;
	
	public JellyBeanBluetoothScanner(BluetoothAdapter bluetoothAdapter, IBleScanCallback scanCallback) {
		this.mBluetoothAdapter = bluetoothAdapter;
		this.mScanCallback = scanCallback;
	}
	
	/**
	 * 扫描后的回调类
	 */
	private BluetoothAdapter.LeScanCallback jellyBeanLeScanCallback = new BluetoothAdapter.LeScanCallback() {
		
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        	if (null != mScanCallback) {
        		mScanCallback.onBleScan(device, rssi, scanRecord);
			}
        }
    };
    
    /**
	 * 开始扫描蓝牙设备
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void onStartBleScan() {
		if (null != mBluetoothAdapter) {
			mBluetoothAdapter.startLeScan(jellyBeanLeScanCallback);
		}
	}

	/**
	 * 停止扫描蓝牙设备
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void onStopBleScan() {
		if (null != mBluetoothAdapter) {
			mBluetoothAdapter.stopLeScan(jellyBeanLeScanCallback);
		}
	}
}
