package com.test.bluetooth.core;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanFilter.Builder;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Build;

/**
 * 蓝牙BLE扫描封装类(针对android 5.0及以上版本的蓝牙)
 * @author 梁佳旺
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class LollipopBluetoothScanner extends BaseBluetoothScanner {
	private static final String TAG = LollipopBluetoothScanner.class.getSimpleName();
	
	private IBleScanCallback mScanCallback;
	private BluetoothLeScanner mBluetoothScanner;
	
	public LollipopBluetoothScanner(BluetoothAdapter bluetoothAdapter, IBleScanCallback scanCallback) {
		this.mScanCallback = scanCallback;
		
		if (null != bluetoothAdapter) {
			mBluetoothScanner = bluetoothAdapter.getBluetoothLeScanner();
		}
	}
	
	/**
	 * 扫描后的回调类
	 */
	private ScanCallback lollipopScanCallback = new ScanCallback() {

		@Override
		public void onScanResult(int callbackType, ScanResult result) {
			super.onScanResult(callbackType, result);
			if (null == result) {
				return;
			}
			
			ScanRecord scanRecord = result.getScanRecord();
			
			if (null != mScanCallback && null != scanRecord) {
				mScanCallback.onBleScan(result.getDevice(), result.getRssi(), scanRecord.getBytes());
			}
		}

		@Override
		public void onBatchScanResults(List<ScanResult> results) {
			super.onBatchScanResults(results);
		}

		@Override
		public void onScanFailed(int errorCode) {
			super.onScanFailed(errorCode);
			if (null != mScanCallback) {
				mScanCallback.onBleScanFailure(errorCode);
			}
		}
	};
    
	/**
	 * 开始扫描蓝牙设备
	 */
	@Override
	public void onStartBleScan() {
		if (null != mBluetoothScanner) {
//			List<ScanFilter> scanFilters = new ArrayList<ScanFilter>();
//			
//			for (String deviceName : BluetoothService.BLUETOOTH_DEVICE_NAME_FILTER) {
//				ScanFilter scanFilter = new ScanFilter.Builder().setDeviceName(deviceName).build();
//				scanFilters.add(scanFilter);
//			}
//			// 在低功耗模式下执行蓝牙LE扫描。这是默认扫描模式,因为它消耗最小功率
//			ScanSettings settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_POWER).build();
//			ScanSettings settings = new ScanSettings.Builder().build();
//			mBluetoothScanner.startScan(scanFilters, settings, lollipopScanCallback);
			mBluetoothScanner.startScan(lollipopScanCallback);
		}
	}

	/**
	 * 停止扫描蓝牙设备
	 */
	@Override
	public void onStopBleScan() {
		if (null != mBluetoothScanner) {
			mBluetoothScanner.stopScan(lollipopScanCallback);
		}
	}
}
