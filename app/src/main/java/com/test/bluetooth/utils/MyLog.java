package com.test.bluetooth.utils;

import android.util.Log;

/**
 * log日志
 * 
 * @author 梁佳旺
 *
 */
public class MyLog {
	private static boolean mbLog = true;

	public static void enableLog(boolean enalbe) {
		mbLog = enalbe;
	}

	public static boolean isLogEnalbed() {
		return mbLog;
	}

	public static void i(String tag, String msg) {
		if (false == mbLog || tag == null || msg == null)
			return;

		Log.i(tag, msg);
	}

	public static void d(String tag, String msg) {
		if (false == mbLog || tag == null || msg == null)
			return;

		Log.d(tag, msg);
	}

	public static void v(String tag, String msg) {
		if (false == mbLog || tag == null || msg == null)
			return;

		Log.v(tag, msg);
	}

	public static void e(String tag, String msg) {
		if (false == mbLog || tag == null || msg == null)
			return;

		Log.e(tag, msg);
	}

	public static void w(String tag, String msg) {
		if (false == mbLog || tag == null || msg == null)
			return;
		Log.w(tag, msg);
	}
}
