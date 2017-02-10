package com.test.bluetooth;

import java.io.Serializable;

import android.app.Application;
import android.text.TextUtils;

import com.bluetooth.bluetoothdemo.ACache;
import com.test.bluetooth.entry.BluetoothEntry;
import com.test.bluetooth.utils.MyLog;

/**
 * 
 * @author 梁佳旺
 */
public class MyApplication extends Application {
	private static final String TAG = MyApplication.class.getSimpleName();
	
	private static MyApplication instance;
	
	public static MyApplication getInstance() {
		return instance;
	}
	
	/** 缓存文件的名称 **/
	private static final String CACHE_NAME = "example_ACache";
	
	/** 最大缓存大小100MB **/
	private static final int MAX_SIZE = 1000 * 1000 * 100;



	private BluetoothEntry cacheBluetoothEntry = null;
	
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		
		MyLog.enableLog(true);
	}

	public static String getLoginAccount() {
		return "123456";
	}
	
	public static BluetoothEntry getCacheBluetoothEntry() {
		if (null == instance.cacheBluetoothEntry) {
			String loginAccount = getLoginAccount();
			
			if (!TextUtils.isEmpty(loginAccount)) {
				instance.cacheBluetoothEntry = (BluetoothEntry) getObjectFromCache(loginAccount);
			}
		}
		
		return instance.cacheBluetoothEntry;
	}
	
	public static void setCacheBluetoothEntry(BluetoothEntry bluetoothEntry) {
		instance.cacheBluetoothEntry = bluetoothEntry;
		String loginAccount = getLoginAccount();
		if (!TextUtils.isEmpty(loginAccount)) {
			saveObjectToCache(loginAccount, bluetoothEntry);
		}
	}

	public static boolean isSelectedBLE() {
		return true;
	}

	public static String getTextString(int resId) {
		return instance.getString(resId);
	}
	
	/**
	 * 保存 Serializable数据 到 缓存中
	 * @param key 保存的key
	 * @param value 保存的value
	 */
	public static void saveObjectToCache(String key, Serializable value) {
		if (null != value) {
			//ACache.get(instance, CACHE_NAME, MAX_SIZE).put(key, value);
			ACache.get(instance,CACHE_NAME).put(key,value);
		}
	}

	/**
	 * 获取Serializable数据
	 * @param key
	 * @return Serializable 数据
	 */
	public static Object getObjectFromCache(String key) {
		if (TextUtils.isEmpty(key)) {
			return null;
		}
		return ACache.get(instance, CACHE_NAME).getAsObject(key);
	}
//
	/**
	 * 移除Serializable数据
	 * @param key
	 * @return Serializable 数据
	 */
	public static Object removeObjectFromCache(String key) {
		return ACache.get(instance, CACHE_NAME).remove(key);
	}
}
