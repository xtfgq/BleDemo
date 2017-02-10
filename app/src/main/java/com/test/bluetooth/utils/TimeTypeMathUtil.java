package com.test.bluetooth.utils;

import java.util.Arrays;
import java.util.List;

import android.content.Context;

import com.bluetooth.bluetoothdemo.R;


/**
 * 时间类型(凌晨、空腹、早餐后、午餐前、午餐后、晚餐前、晚餐后、睡前)的数值转换工具
 * 
 * @author 梁佳旺
 */
public class TimeTypeMathUtil {
	
	public static final long millis = 999;
	public static final long second = 1000;
	public static final long minute = 60 * 1000;
	public static final long hour = 60 * 60 * 1000;
	// 凌晨
	public static final long weeHours_1 = 0;
	public static final long weeHours_2 = 2 * hour + 59 * minute + 59 * second + millis;
	// 空腹
	public static final long beforeBreakFast_1 = 3 * hour;
	public static final long beforeBreakFast_2 = 5 * hour + 59 * minute + 59 * second + millis;
	// 早餐后
	public static final long afterBreakFast_1 = 6 * hour;
	public static final long afterBreakFast_2 = 8 * hour + 59 * minute + 59 * second + millis;
	// 午餐前
	public static final long beforeLunch_1 = 9 * hour;
	public static final long beforeLunch_2 = 11 * hour + 59 * minute + 59 * second + millis;
	// 午餐后
	public static final long afterLunch_1 = 12 * hour;
	public static final long afterLunch_2 = 14 * hour + 59 * minute + 59 * second + millis;
	// 晚餐前
	public static final long beforeDinner_1 = 15 * hour;
	public static final long beforeDinner_2 = 17 * hour + 59 * minute + 59 * second + millis;
	// 晚餐后
	public static final long afterDinner_1 = 18 * hour;
	public static final long afterDinner_2 = 20 * hour + 59 * minute + 59 * second + millis;
	// 睡前
	public static final long beforeSleep_1 = 21 * hour;
	public static final long beforeSleep_2 = 23 * hour + 59 * minute + 59 * second + millis;

	/**
	 * 根据当前时间获取此时的时间类型(凌晨、空腹、早餐后、午餐前、午餐后、晚餐前、晚餐后、睡前)
	 * @param context
	 * @param hourMinute
	 * @return (凌晨、空腹、早餐后、午餐前、午餐后、晚餐前、晚餐后、睡前)
	 */
	public static String getTimeTypeByCurrentTime(Context context, String hourMinute) {
		String[] hourMinutes = hourMinute.split(":");
		String h = hourMinutes[0];
		String m = hourMinutes[1];
		
		long timeMillis = Integer.parseInt(h) * hour + Integer.parseInt(m) * minute;
		
		return getTimeTypeByCurrentTime(context, timeMillis);
	}
	
	/**
	 * 根据当前时间获取此时的时间类型(凌晨、空腹、早餐后、午餐前、午餐后、晚餐前、晚餐后、睡前)
	 * @param context
	 * @param timeMillis
	 * @return (凌晨、空腹、早餐后、午餐前、午餐后、晚餐前、晚餐后、睡前)
	 */
	public static String getTimeTypeByCurrentTime(Context context, long timeMillis) {
//		List<String> timeTypes = Arrays.asList(context.getResources().getStringArray(R.array.time_type_array));
//
//		if (timeMillis >= weeHours_1 && timeMillis <= weeHours_2) {// 凌晨
//			return timeTypes.get(0);
//		} else if (timeMillis >= beforeBreakFast_1 && timeMillis <= beforeBreakFast_2) {// 空腹
//			return timeTypes.get(1);
//		} else if (timeMillis >= afterBreakFast_1 && timeMillis <= afterBreakFast_2) {// 早餐后
//			return timeTypes.get(2);
//		} else if (timeMillis >= beforeLunch_1 && timeMillis <= beforeLunch_2) {// 午餐前
//			return timeTypes.get(3);
//		} else if (timeMillis >= afterLunch_1 && timeMillis <= afterLunch_2) {// 午餐后
//			return timeTypes.get(4);
//		} else if (timeMillis >= beforeDinner_1 && timeMillis <= beforeDinner_2) {// 晚餐前
//			return timeTypes.get(5);
//		} else if (timeMillis >= afterDinner_1 && timeMillis <= afterDinner_2) {// 晚餐后
//			return timeTypes.get(6);
//		} else if (timeMillis >= beforeSleep_1 && timeMillis <= beforeSleep_2) {// 睡前
//			return timeTypes.get(7);
//		} else { // 午餐前
//			return timeTypes.get(3);
//		}
		return "";
	}
}
