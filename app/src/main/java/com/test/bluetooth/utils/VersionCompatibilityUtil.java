package com.test.bluetooth.utils;

import com.test.bluetooth.MyApplication;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build;

public class VersionCompatibilityUtil {
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	public static BluetoothAdapter getBluetoothAdapter() {
		BluetoothAdapter bluetoothAdapter = null;
		
		if (Build.VERSION.SDK_INT >= 18) {
			BluetoothManager bluetoothManager = (BluetoothManager) MyApplication.getInstance().getSystemService(Context.BLUETOOTH_SERVICE);
			bluetoothAdapter = bluetoothManager.getAdapter();
		} else {
			bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		}
		
		return bluetoothAdapter;
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	public static void closeBluetoothGatt(BluetoothGatt gatt) {
		if (null != gatt) {
			gatt.close();
			gatt = null;
		}
	}
}
