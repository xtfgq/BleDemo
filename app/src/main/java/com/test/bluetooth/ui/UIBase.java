package com.test.bluetooth.ui;

import android.widget.Toast;

import com.test.bluetooth.MyApplication;

/**
 * 界面共用的一些方法
 * 
 * @author 梁佳旺
 */
public class UIBase {
	private static final String TAG = UIBase.class.getSimpleName();

	public static void showToastLong(String text) {
		Toast.makeText(MyApplication.getInstance(), text, Toast.LENGTH_LONG).show();
	}
	
	public static void showToastShort(String text) {
		Toast.makeText(MyApplication.getInstance(), text, Toast.LENGTH_SHORT).show();
	}
}