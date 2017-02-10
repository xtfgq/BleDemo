package com.test.bluetooth.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import android.text.TextUtils;
/**
 * @功能描述：日期时间处理工具类
 *
 * @author 梁佳旺
 */
public class DateTimeUtil
{
	/** 时间日期格式化到年月日时分秒. */
	public static final String dateFormatYMDHMS_S = "yyyy-MM-dd HH:mm:ss S";
	
	/** 时间日期格式化到年月日时分秒. */
	public static final String dateFormatYMDHMS = "yyyy-MM-dd HH:mm:ss";
	
	/** 时间日期格式化到年月日. */
	public static final String dateFormatYMD = "yyyy-MM-dd";
	
	/** 时间日期格式化到年月. */
	public static final String dateFormatYM = "yyyy-MM";
	
	/** 时间日期格式化到年月日时分. */
	public static final String dateFormatYMDHM = "yyyy-MM-dd HH:mm";
	
	/** 时间日期格式化到月日. */
	public static final String dateFormatMD = "MM/dd";
	
	/** 时间日期格式化到月日. */
	public static final String dateFormatM_D = "MM-dd";
	
	/** 时分秒. */
	public static final String dateFormatHMS = "HH:mm:ss";
	
	/** 时分. */
	public static final String dateFormatHM = "HH:mm";
	
	/** 时 */
	public static final String dateFormat_HOUR = "HH";
	
	/** 分 */
	public static final String dateFormat_MINUTE = "mm";
	
	/** 秒 */
	public static final String dateFormat_SECOND = "ss";
	
	/** 毫秒 */
	public static final String dateFormat_MILLISECOND = "S";
	
	/** 时间日期格式化到年. */
	public static final String dateFormatY = "yyyy";
	
	/** 时间日期格式化到月. */
	public static final String dateFormatM = "MM";
	
	/** 时间日期格式化到日. */
	public static final String dateFormatD = "dd";
	
	/** 上午. */
    public static final String AM = "AM";

    /** 下午. */
    public static final String PM = "PM";
    
    /** 时间日期格式化成"yyyy年MM月" */
	public static final String dateFormatYNMY = "yyyy年MM月";
	
	/** 时间日期格式化成"MM月dd日" */
	public static final String dateFormatMYDR = "MM月dd日";
	
	/** 时间日期格式化成"dd日 HH:mm" */
	public static final String dateFormatDRHM = "dd日\nHH:mm";
	
	/** 时间日期格式化成"20160720124530" */
	public static final String dateFormat_ymdhms = "yyyyMMddHHmmss";

    /**
	 * 描述：String类型的日期时间转化为Date类型.
	 *
	 * @param strDate String形式的日期时间
	 * @param format 格式化字符串，如："yyyy-MM-dd HH:mm:ss"
	 * @return Date Date类型日期时间
	 */
	public static Date getDateByFormat(String strDate, String format) {
		SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
		Date date = null;
		try {
			date = mSimpleDateFormat.parse(strDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	/**
	 * 描述：获取偏移之后的Date.
	 * @param date 日期时间
	 * @param calendarField Calendar属性，对应offset的值， 如(Calendar.DATE,表示+offset天,Calendar.HOUR_OF_DAY,表示＋offset小时)
	 * @param offset 偏移(值大于0,表示+,值小于0,表示－)
	 * @return Date 偏移之后的日期时间
	 */
	public Date getDateByOffset(Date date,int calendarField,int offset) {
		Calendar c = new GregorianCalendar();
		try {
			c.setTime(date);
			c.add(calendarField, offset);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return c.getTime();
	}
	
	/**
	 * 描述：获取指定日期时间的字符串(可偏移).
	 *
	 * @param strDate String形式的日期时间
	 * @param format 格式化字符串，如："yyyy-MM-dd HH:mm:ss"
	 * @param calendarField Calendar属性，对应offset的值， 如(Calendar.DATE,表示+offset天,Calendar.HOUR_OF_DAY,表示＋offset小时)
	 * @param offset 偏移(值大于0,表示+,值小于0,表示－)
	 * @return String String类型的日期时间
	 */
	public static String getStringByOffset(String strDate, String format, int calendarField, int offset) {
		String mDateTime = null;
		try {
			Calendar c = new GregorianCalendar();
			SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
			c.setTime(mSimpleDateFormat.parse(strDate));
			c.add(calendarField, offset);
			mDateTime = mSimpleDateFormat.format(c.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return mDateTime;
	}
	
	/**
	 * 描述：获取指定日期时间的字符串(可偏移).
	 *
	 * @param strDate String形式的日期时间
	 * @param format 格式化字符串，如："yyyy-MM-dd HH:mm:ss"
	 * @param calendarField Calendar属性，对应offset的值， 如(Calendar.DATE,表示+offset天,Calendar.HOUR_OF_DAY,表示＋offset小时)
	 * @param offset 偏移(值大于0,表示+,值小于0,表示－)
	 * @return String String类型的日期时间
	 */
	public static String getStringByOffset(long milliseconds, String format, int calendarField, int offset) {
		String mDateTime = null;
		
		Calendar c = new GregorianCalendar();
		SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
		c.setTimeInMillis(milliseconds);
		c.add(calendarField, offset);
		mDateTime = mSimpleDateFormat.format(c.getTime());
			
		return mDateTime;
	}
	
	/**
	 * 描述：Date类型转化为String类型(可偏移).
	 *
	 * @param date the date
	 * @param format the format
	 * @param calendarField the calendar field
	 * @param offset the offset
	 * @return String String类型日期时间
	 */
	public static String getStringByOffset(Date date, String format,int calendarField,int offset) {
		String strDate = null;
		try {
			Calendar c = new GregorianCalendar();
			SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
			c.setTime(date);
			c.add(calendarField, offset);
			strDate = mSimpleDateFormat.format(c.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strDate;
	}
	

	/**
	 * 描述：Date类型转化为String类型.
	 *
	 * @param date the date
	 * @param format the format
	 * @return String String类型日期时间
	 */
	public static String getStringByFormat(Date date, String format) {
		SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
		String strDate = null;
		try {
			strDate = mSimpleDateFormat.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strDate;
	}
	
	/**
	 * 描述：获取指定日期时间的字符串,用于导出想要的格式.
	 *
	 * @param strDate String形式的日期时间，必须为yyyy-MM-dd HH:mm:ss格式
	 * @param format 输出格式化字符串，如："yyyy-MM-dd HH:mm:ss"
	 * @return String 转换后的String类型的日期时间
	 */
	public static String getStringByFormat(String strDate, String format) {
		String mDateTime = null;
		try {
			Calendar c = new GregorianCalendar();
			SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(dateFormatYMDHMS);
			c.setTime(mSimpleDateFormat.parse(strDate));
			SimpleDateFormat mSimpleDateFormat2 = new SimpleDateFormat(format);
			mDateTime = mSimpleDateFormat2.format(c.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mDateTime;
	}
	
	/**
	 * 将日期时间字符串如“2015-05-27”转换成这样的字符串“2015-05”
	 * @param strDate 日期时间字符串，格式如“2015-05-27”
	 * @param initFormat 格式化字符串，必须与strDate格式对应，如：yyyy-MM-dd
	 * @param requestFormat 新转成字符串的格式化方式，如：yyyy-MM
	 * @return string
	 */
	public static String getStringByFormat(String strDate, String initFormat, String requestFormat) {
		long milliseconds = getMillisecondsByFormat(strDate, initFormat);
		String resultDate = getStringByFormat(milliseconds, requestFormat);
		return resultDate;
	}
	
	/**
	 * 将日期时间字符串如“2015-05-27”转换成毫秒
	 * @param strDate 日期时间字符串，格式如“2015-05-27”
	 * @param format 格式化字符串，如："yyyy-MM-dd HH:mm:ss"
	 * @return long 
	 */
	public static long getMillisecondsByFormat(String strDate, String format) {
		long milliseconds = 0;
		try {
			SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
			Date date = mSimpleDateFormat.parse(strDate);
			milliseconds = date.getTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return milliseconds;
	}
	
	
	/**
	 * 描述：获取milliseconds表示的日期时间的字符串.
	 *
	 * @param milliseconds the milliseconds
	 * @param format  格式化字符串，如："yyyy-MM-dd HH:mm:ss"
	 * @return String 日期时间字符串
	 */
	public static String getStringByFormat(long milliseconds, String format) {
		String thisDateTime = null;
		try {
			SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
//			Calendar calendar = Calendar.getInstance();
//			calendar.setTimeInMillis(milliseconds);
//			thisDateTime = mSimpleDateFormat.format(calendar.getTime());
			thisDateTime = mSimpleDateFormat.format(new Date(milliseconds));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return thisDateTime;
	}
	
	/**
	 * 比较两个日期时间字符串的大小(必须在同一格式化下)如：“2015-05-27”
	 * 
	 * @param currStrDate 当前的日期时间字符串，如“2015-05-27”
	 * @param lastStrDate 上次的日期时间字符串，如“2015-05-25”
	 * @param format 格式化字符串，如："yyyy-MM-dd HH:mm:ss"
	 * @return 返回0表示相等，返回1表示currStrDate > lastStrDate，返回-1表示currStrDate < lastStrDate，返回-2表示无效
	 */
	public static int compareDateSize(String currStrDate, String lastStrDate, String format) {
		int isEquals = -2;
		long currMilliseconds = getMillisecondsByFormat(currStrDate, format);
		long lastMilliseconds = getMillisecondsByFormat(lastStrDate, format);
		if (currMilliseconds == lastMilliseconds) {
			isEquals = 0;
		} else if (currMilliseconds > lastMilliseconds) {
			isEquals = 1;
		} else if (currMilliseconds < lastMilliseconds) {
			isEquals = -1;
		}
		return isEquals;
	}
	/**
	 * 描述：获取表示当前日期时间的字符串.
	 *
	 * @param format  格式化字符串，如："yyyy-MM-dd HH:mm:ss"
	 * @return String String类型的当前日期时间
	 */
	public static String getCurrentDate(String format) {
		String curDateTime = null;
		try {
			SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
			Calendar c = new GregorianCalendar();
			curDateTime = mSimpleDateFormat.format(c.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return curDateTime;
	}

	/**
	 * 描述：获取表示当前日期时间的字符串(可偏移).
	 *
	 * @param format 格式化字符串，如："yyyy-MM-dd HH:mm:ss"
	 * @param calendarField Calendar属性，对应offset的值， 如(Calendar.DATE,表示+offset天,Calendar.HOUR_OF_DAY,表示＋offset小时)
	 * @param offset 偏移(值大于0,表示+,值小于0,表示－)
	 * @return String String类型的日期时间
	 */
	public static String getCurrentDateByOffset(String format,int calendarField,int offset) {
		String mDateTime = null;
		try {
			SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
			Calendar c = new GregorianCalendar();
			c.add(calendarField, offset);
			mDateTime = mSimpleDateFormat.format(c.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mDateTime;
	}

	/**
	 * 描述：计算两个日期所差的天数.
	 *
	 * @param milliseconds1 the milliseconds1
	 * @param milliseconds2 the milliseconds2
	 * @return int 所差的天数，值大于0时，milliseconds1 > milliseconds2；值小于0时，milliseconds1 < milliseconds2；
	 */
	public static int getOffectDay(long milliseconds1, long milliseconds2) {
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTimeInMillis(milliseconds1);
		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTimeInMillis(milliseconds2);
		//先判断是否同年
		int y1 = calendar1.get(Calendar.YEAR);
		int y2 = calendar2.get(Calendar.YEAR);
		int d1 = calendar1.get(Calendar.DAY_OF_YEAR);
		int d2 = calendar2.get(Calendar.DAY_OF_YEAR);
		int maxDays = 0;
		int day = 0;
		if (y1 - y2 > 0) {
			maxDays = calendar2.getActualMaximum(Calendar.DAY_OF_YEAR);
			day = d1 - d2 + maxDays;
		} else if (y1 - y2 < 0) {
			maxDays = calendar1.getActualMaximum(Calendar.DAY_OF_YEAR);
			day = d1 - d2 - maxDays;
		} else {
			day = d1 - d2;
		}
		return day;
	}
	
	/**
	 * 描述：计算两个日期所差的天数.
	 *
	 * @param strDate1 日期时间1
	 * @param format1
	 * @param strDate2 日期时间2
	 * @param format2
	 * @return int 所差的天数，值大于0时，strDate1 > strDate2；值小于0时，strDate1 < strDate2；
	 */
	public static int getOffectDay(String strDate1, String format1, String strDate2, String format2) {
		SimpleDateFormat sdf1 = new SimpleDateFormat(format1);
    	SimpleDateFormat sdf2 = new SimpleDateFormat(format2);
    	
    	Calendar calendar1 = Calendar.getInstance();
    	Calendar calendar2 = Calendar.getInstance();
    	try {
    		calendar1.setTime(sdf1.parse(strDate1));
    		calendar2.setTime(sdf2.parse(strDate2));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		//先判断是否同年
		int y1 = calendar1.get(Calendar.YEAR);
		int y2 = calendar2.get(Calendar.YEAR);
		int d1 = calendar1.get(Calendar.DAY_OF_YEAR);
		int d2 = calendar2.get(Calendar.DAY_OF_YEAR);
		int maxDays = 0;
		int day = 0;
		if (y1 - y2 > 0) {
			maxDays = calendar2.getActualMaximum(Calendar.DAY_OF_YEAR);
			day = d1 - d2 + maxDays;
		} else if (y1 - y2 < 0) {
			maxDays = calendar1.getActualMaximum(Calendar.DAY_OF_YEAR);
			day = d1 - d2 - maxDays;
		} else {
			day = d1 - d2;
		}
		return day;
	}
	
	/**
	 * 描述：计算两个日期所差的小时数.
	 *
	 * @param date1 第一个时间的毫秒表示
	 * @param date2 第二个时间的毫秒表示
	 * @return int 所差的小时数
	 */
	public static int getOffectHour(long date1, long date2) {
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTimeInMillis(date1);
		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTimeInMillis(date2);
		int h1 = calendar1.get(Calendar.HOUR_OF_DAY);
		int h2 = calendar2.get(Calendar.HOUR_OF_DAY);
		int h = 0;
		int day = getOffectDay(date1, date2);
		h = h1-h2+day*24;
		return h;
	}
	
	/**
	 * 描述：计算两个日期所差的分钟数.
	 *
	 * @param date1 第一个时间的毫秒表示
	 * @param date2 第二个时间的毫秒表示
	 * @return int 所差的分钟数
	 */
	public static int getOffectMinutes(long date1, long date2) {
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTimeInMillis(date1);
		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTimeInMillis(date2);
		int m1 = calendar1.get(Calendar.MINUTE);
		int m2 = calendar2.get(Calendar.MINUTE);
		int h = getOffectHour(date1, date2);
		int m = 0;
		m = m1-m2+h*60;
		return m;
	}

	/**
	 * 描述：获取本周一.
	 *
	 * @param format the format
	 * @return String String类型日期时间
	 */
	public static String getFirstDayOfWeek(String format) {
		return getDayOfWeek(format,Calendar.MONDAY);
	}

	/**
	 * 描述：获取本周日.
	 *
	 * @param format the format
	 * @return String String类型日期时间
	 */
	public static String getLastDayOfWeek(String format) {
		return getDayOfWeek(format,Calendar.SUNDAY);
	}

	/**
	 * 描述：获取本周的某一天.
	 *
	 * @param format the format
	 * @param calendarField the calendar field
	 * @return String String类型日期时间
	 */
	private static String getDayOfWeek(String format,int calendarField) {
		String strDate = null;
		try {
			Calendar c = new GregorianCalendar();
			SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
			int week = c.get(Calendar.DAY_OF_WEEK);
			if (week == calendarField){
				strDate = mSimpleDateFormat.format(c.getTime());
			}else{
				int offectDay = calendarField - week;
				if (calendarField == Calendar.SUNDAY) {
					offectDay = 7-Math.abs(offectDay);
				} 
				c.add(Calendar.DATE, offectDay);
				strDate = mSimpleDateFormat.format(c.getTime());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strDate;
	}
	
	/**
	 * 描述：获取本月第一天.
	 *
	 * @param format the format
	 * @return String String类型日期时间
	 */
	public static String getFirstDayOfMonth(String format) {
		String strDate = null;
		try {
			Calendar c = new GregorianCalendar();
			SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
			//当前月的第一天
			c.set(GregorianCalendar.DAY_OF_MONTH, 1);
			strDate = mSimpleDateFormat.format(c.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strDate;
	}
	
	/**
	 * 获取某个日期下(2015-02-23 15:56)的月内第一天
	 *
	 * @param milliseconds 日期时间毫秒
	 * @param format the format
	 * @return String String类型日期时间
	 */
	public static String getFirstDayOfMonth(long milliseconds, String format) {
		String strDate = null;
		try {
			Calendar c = new GregorianCalendar();
			SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
			// 某个日期下(2015-02-23 15:56)的月内第一天
			c.setTimeInMillis(milliseconds);
			c.set(GregorianCalendar.DAY_OF_MONTH, 1);
			strDate = mSimpleDateFormat.format(c.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strDate;
	}

	/**
	 * 描述：获取本月最后一天.
	 *
	 * @param format the format
	 * @return String String类型日期时间
	 */
	public static String getLastDayOfMonth(String format) {
		String strDate = null;
		try {
			Calendar c = new GregorianCalendar();
			SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
			// 当前月的最后一天
			c.set(Calendar.DATE, 1);
			c.roll(Calendar.DATE, -1);
			strDate = mSimpleDateFormat.format(c.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strDate;
	}
	
	/**
	 * 获取某个日期下(2015-02-23 15:56)的月内最后一天
	 *
	 * @param milliseconds 日期时间毫秒
	 * @param format the format
	 * @return String String类型日期时间
	 */
	public static String getLastDayOfMonth(long milliseconds, String format) {
		String strDate = null;
		try {
			Calendar c = new GregorianCalendar();
			SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
			// 某个日期下(2015-02-23 15:56)的月内最后一天
			c.setTimeInMillis(milliseconds);
			c.set(Calendar.DATE, 1);
			c.roll(Calendar.DATE, -1);
			strDate = mSimpleDateFormat.format(c.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strDate;
	}

	

	/**
	 * 描述：获取表示当前日期的0点时间毫秒数.
	 *
	 * @return the first time of day
	 */
	public static long getFirstTimeOfDay() {
		Date date = null;
		try {
			String currentDate = getCurrentDate(dateFormatYMD);
			date = getDateByFormat(currentDate+" 00:00:00",dateFormatYMDHMS);
			return date.getTime();
		} catch (Exception e) {
		}
		return -1;
	}
	
	/**
	 * 描述：获取表示当前日期24点时间毫秒数.
	 *
	 * @return the last time of day
	 */
	public static long getLastTimeOfDay() {
		Date date = null;
		try {
			String currentDate = getCurrentDate(dateFormatYMD);
			date = getDateByFormat(currentDate+" 24:00:00",dateFormatYMDHMS);
			return date.getTime();
		} catch (Exception e) {
		}
		return -1;
	}
	
	/**
	 * 描述：判断是否是闰年()
	 * <p>(year能被4整除 并且 不能被100整除) 或者 year能被400整除,则该年为闰年.
	 *
	 * @param year 年代（如2012）
	 * @return boolean 是否为闰年
	 */
	public static boolean isLeapYear(int year) {
		if ((year % 4 == 0 && year % 400 != 0) || year % 400 == 0) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 描述：根据时间返回格式化后的时间的描述.
	 * 小于1小时显示多少分钟前  大于1小时显示今天＋实际日期，大于今天全部显示实际时间
	 *
	 * @param strDate the str date
	 * @param outFormat the out format
	 * @return the string
	 */
	public static String formatDateStr2Desc(String strDate,String outFormat) 
	{
		DateFormat df = new SimpleDateFormat(dateFormatYMDHMS);
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		try {
			c2.setTime(df.parse(strDate));
			c1.setTime(new Date());
			int d = getOffectDay(c1.getTimeInMillis(), c2.getTimeInMillis());
			if(d==0){
				int h = getOffectHour(c1.getTimeInMillis(), c2.getTimeInMillis());
				if(h>0){
					return "今天"+getStringByFormat(strDate,dateFormatHM);
					//return h + "小时前";
				}else if(h<0){
					//return Math.abs(h) + "小时后";
				}else if(h==0){
					int m = getOffectMinutes(c1.getTimeInMillis(), c2.getTimeInMillis());
					if(m>0){
						return m + "分钟前";
					}else if(m<0){
						//return Math.abs(m) + "分钟后";
					}else{
						return "刚刚";
					}
				}
				
			}else if(d>0){
				if(d == 1){
					//return "昨天"+getStringByFormat(strDate,outFormat);
				}else if(d==2){
					//return "前天"+getStringByFormat(strDate,outFormat);
				}
			}else if(d<0){
				if(d == -1){
					//return "明天"+getStringByFormat(strDate,outFormat);
				}else if(d== -2){
					//return "后天"+getStringByFormat(strDate,outFormat);
				}else{
				    //return Math.abs(d) + "天后"+getStringByFormat(strDate,outFormat);
				}
			}
			
			String out = getStringByFormat(strDate,outFormat);
			if(!TextUtils.isEmpty(out)){
				return out;
			}
		} catch (Exception e) {
		}
		
		return strDate;
	}
	
	
	/**
	 * 取指定日期为星期几.
	 *
	 * @param strDate 指定日期
	 * @param inFormat 指定日期格式
	 * @return String   星期几
	 */
    public static String getWeekNumber(String strDate,String inFormat) {
      String week = "星期日";
      Calendar calendar = new GregorianCalendar();
      DateFormat df = new SimpleDateFormat(inFormat);
      try {
		   calendar.setTime(df.parse(strDate));
	  } catch (Exception e) {
		  return "错误";
	  }
      int intTemp = calendar.get(Calendar.DAY_OF_WEEK) - 1;
      switch (intTemp){
        case 0:
          week = "星期日";
          break;
        case 1:
          week = "星期一";
          break;
        case 2:
          week = "星期二";
          break;
        case 3:
          week = "星期三";
          break;
        case 4:
          week = "星期四";
          break;
        case 5:
          week = "星期五";
          break;
        case 6:
          week = "星期六";
          break;
      }
      return week;
    }
    
    /**
     * 根据给定的日期判断是否为上下午.
     *
     * @param strDate the str date
     * @param format the format
     * @return the time quantum
     */
    public static String getTimeQuantum(String strDate, String format) {
        Date mDate = getDateByFormat(strDate, format);
        int hour  = mDate.getHours();
        if(hour >=12)
           return "PM";
        else
           return "AM";
    }
    
    /**
     * 根据给定的毫秒数算得时间的描述.
     *
     * @param milliseconds the milliseconds
     * @return the time description
     */
    public static String getTimeDescription(long milliseconds) 
    {
        if(milliseconds > 1000){
            //大于一分
            if(milliseconds/1000/60>1){
                long minute = milliseconds/1000/60;
                long second = milliseconds/1000%60;
                return minute+"分"+second+"秒";
            }else{
                //显示秒
                return milliseconds/1000+"秒";
            }
        }else{
            return milliseconds+"毫秒";
        }
    }
	
    /**
     * 根据给定的秒数返回时间字符串(格式如：01:21:11)
     * 
     * @param seconds 秒数
     * @return 时间如：01:21:11
     */
    public static String getTime(long seconds)
    {
    	long hour = seconds / 60 / 60;// 小时
    	long minute = seconds / 60;// 分
    	if (minute >= 60) {
    		minute = minute % 60;// 分
    	}
    	long second = seconds % 60;// 秒
    	
    	String hourStr = hour + "";
    	String minuteStr = minute + "";
    	String secondStr = second + "";
    	
    	hourStr = hourStr.length() == 1 ? "0" + hourStr : hourStr;
    	minuteStr = minuteStr.length() == 1 ? "0" + minuteStr : minuteStr;
    	secondStr = secondStr.length() == 1 ? "0" + secondStr : secondStr;
    	
    	return hourStr + ":" + minuteStr + ":" + secondStr;
    }
    
    /**
     * 根据给定的秒数返回中文式的时间字符串(格式如：1小时21分3秒)
     * 
     * @param seconds 秒数
     * @return 时间如：1小时21分3秒
     */
    public static String getTimeByChinese(long seconds)
    {
    	long hour = seconds / 60 / 60;// 小时
    	long minute = seconds / 60;// 分
    	if (minute >= 60) {
    		minute = minute % 60;// 分
    	}
    	long second = seconds % 60;// 秒
    	
    	String hourStr = hour + "小时";
    	String minuteStr = minute + "分";
    	String secondStr = second + "秒";
    	
    	hourStr = hour == 0 ? "" : hourStr;
    	minuteStr = minute == 0 ? "" : minuteStr;
    	secondStr = second == 0 ? "" : secondStr;
    	
    	return hourStr + minuteStr + secondStr;
    }
    
    /**
     * 获取从1970-01到2100-12这段时间的年月列表
     * @return 返回格式如：1985-04的列表list
     */
    public static List<String> getYearMonthList() {
    	List<String> yearMonthList = new ArrayList<String>();
    	for (int year = 1970; year <= 2100; year++) {
			for (int month = 1; month <= 12; month++) {
				String yearMonth = year + "-" + String.format("%02d", month);
				yearMonthList.add(yearMonth);
			}
		}

    	return yearMonthList;
    }
    
    /**
     * 获取从指定的时间开始，倒退偏移offset月数后的这段时间内的年月列表
     * @param startMilliseconds 开始时间
     * @param offSet 偏移月数，单位为月如1、2、3、-1、-2、-3......
     * @param isDesc 是否倒序排列
     * @return
     */
    public static List<String> getYearMonthList(long startMilliseconds, int offSet, boolean isDesc) {
    	List<String> yearMonthList = new ArrayList<String>();
    	Calendar[] calendars = new Calendar[offSet];
    	for (int i = 0; i < offSet; i++) {
    		calendars[i] = Calendar.getInstance();
    		calendars[i].setTimeInMillis(startMilliseconds);
    		calendars[i].add(Calendar.MONTH, -i);
			String yearMonth = getStringByFormat(calendars[i].getTimeInMillis(), dateFormatYM);
			if (isDesc) {
				yearMonthList.add(0, yearMonth);// 倒序添加
			} else {
				yearMonthList.add(yearMonth);
			}
		}
    	calendars = null;
    	
    	return yearMonthList;
    }
    
    /**
     * 获取两个日期之间相差的月数，如2015-09-21到1988-11-01
     * @param startMilliseconds 开始日期的毫秒数
     * @param endMilliseconds 结束日期的毫秒数
     * @return 返回相差的月数
     */
    public static int getMonths(long startMilliseconds, long endMilliseconds) {
    	Calendar startCalendar = Calendar.getInstance();
    	Calendar endCalendar = Calendar.getInstance();
    	
    	startCalendar.setTimeInMillis(startMilliseconds);
    	endCalendar.setTimeInMillis(endMilliseconds);
    	
    	int deltaMonths = (endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR)) * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
    	return deltaMonths;
    }
    
    /**
     * 获取两个日期之间相差的月数，如2015-09-21 HH:mm到1988-11-01
     * @param startDate 开始日期，如1988-11-01
     * @param endDate 结束日期，如2015-09-21 HH:mm
     * @param startFormat 开始日期的格式化格式，如yyyy-MM-dd HH:mm
     * @param endFormat 结束日期的格式化格式，如yyyy-MM-dd
     * @return 返回相差的月数
     */
    public static int getMonths(String startDate, String endDate, String startFormat, String endFormat) {
    	SimpleDateFormat startSdf = new SimpleDateFormat(startFormat);
    	SimpleDateFormat endSdf = new SimpleDateFormat(endFormat);
    	
    	Calendar startCalendar = Calendar.getInstance();
    	Calendar endCalendar = Calendar.getInstance();
    	
    	try {
			startCalendar.setTime(startSdf.parse(startDate));
			endCalendar.setTime(endSdf.parse(endDate));
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	int deltaMonths = (endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR)) * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
    	return deltaMonths;
    }
    
    /**
     * 获取从指定的时间开始，倒退偏移offset月数后的这段时间内的年月日列表
     * @param startMilliseconds 开始时间
     * @param offSet 偏移天数，单位为月如1、2、3、-1、-2、-3......
     * @param isDesc 是否倒序排列
     * @return
     */
    public static List<String> getYearMonthDayList(long startMilliseconds, int offSet, boolean isDesc) {
    	List<String> yearMonthDayList = new ArrayList<String>();
    	Calendar[] calendars = new Calendar[offSet];
    	for (int i = 0; i < offSet; i++) {
    		calendars[i] = Calendar.getInstance();
    		calendars[i].setTimeInMillis(startMilliseconds);
    		calendars[i].add(Calendar.DAY_OF_MONTH, -i);
			String yearMonth = getStringByFormat(calendars[i].getTimeInMillis(), dateFormatYMD);
			if (isDesc) {
				yearMonthDayList.add(0, yearMonth);// 倒序添加
			} else {
				yearMonthDayList.add(yearMonth);
			}
		}
    	calendars = null;
    	
    	return yearMonthDayList;
    }
    
    /**
     * 获取从指定的时间开始，倒退偏移offset月数后的这段时间内的年月日列表
     * @param startMilliseconds 开始时间
     * @param offSet 偏移天数，单位为月如1、2、3、-1、-2、-3......
     * @param isDesc 是否倒序排列
     * @return
     */
    public static List<String> getYearMonthDayList(String strDate1, String format1, String strDate2, String format2, String resultFormat, boolean isDesc) {
    	int offSet = getOffectDay(strDate1, format1, strDate2, format2);
    	
    	List<String> yearMonthDayList = new ArrayList<String>();
    	
    	SimpleDateFormat sdf = new SimpleDateFormat(format1);
    	Calendar[] calendars = new Calendar[offSet + 1];
    	
    	for (int i = 0; i <= offSet; i++) {
    		try {
    			calendars[i] = Calendar.getInstance();
    			calendars[i].setTime(sdf.parse(strDate1));
    			calendars[i].add(Calendar.DAY_OF_MONTH, -i);
    		} catch (ParseException e) {
    			e.printStackTrace();
    		}
    		String yearMonthDay = getStringByFormat(calendars[i].getTimeInMillis(), resultFormat);
    		if (isDesc) {
    			yearMonthDayList.add(0, yearMonthDay);// 倒序添加
    		} else {
    			yearMonthDayList.add(yearMonthDay);
    		}
    	}
    	calendars = null;
    	
    	return yearMonthDayList;
    }
    
    /**
     * 根据指定日期如2015-08-05 09:07获取该日期下月内的总天数
     * (注意：指定的日期与日期格式化format必须一致)
     * @param date 指定的日期
     * @param format 格式化字符串
     * @return 返回该日期下月内的总天数
     */
    public static int getDaysByDate(String date, String format) {
    	SimpleDateFormat sdf = new SimpleDateFormat(format);
    	Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(sdf.parse(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		int days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    	return days;
    }
    
    /**
     * 根据(以毫秒来表示的)指定日期获取该日期下月内的总天数
     * @param milliseconds  (以毫秒来表示的)指定日期
     * @return 返回该日期下月内的总天数
     */
    public static int getDaysByDate(long milliseconds) {
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTimeInMillis(milliseconds);
    	int days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    	return days;
    }

    /**
     * 根据出生日期计算出年龄
     * @param birthday
     * @return
     */
	public static int getAgeByBirthday(String birthday) {
		if (birthday == null) {
			return 0;
		}

		int age = 0;
		Date now = new Date();

		SimpleDateFormat ymdFormat = new SimpleDateFormat(dateFormatYMD);
		SimpleDateFormat yFormat = new SimpleDateFormat(dateFormatY);
		SimpleDateFormat mFormat = new SimpleDateFormat(dateFormatM);

		try {
			String yearBirthday = yFormat.format(ymdFormat.parse(birthday));
			String yearNow = yFormat.format(now);
			
			String monthBirthday = mFormat.format(ymdFormat.parse(birthday));
			String monthNow = mFormat.format(now);
			
			// 初步，估算
			age = Integer.parseInt(yearNow) - Integer.parseInt(yearBirthday);
			// 如果未到出生月份，则age - 1
			if (monthNow.compareTo(monthBirthday) < 0) {
				age -= 1;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return age < 0 ? 0 : age;
	}
	
	/**
	 * 计算某个日期与至今相差几年几月几日
	 * @param birthMilliseconds
	 * @return 数值形式：[0]是年，[1]是月，[2]是日
	 */
	public static int[] getNeturalAge(Long birthMilliseconds) {
		if (null == birthMilliseconds || birthMilliseconds == 0) {
			return null;
		}
		
		Calendar calendarBirth = Calendar.getInstance();
		calendarBirth.setTimeInMillis(birthMilliseconds);
		
		Calendar calendarNow = Calendar.getInstance();
		calendarNow.setTimeInMillis(System.currentTimeMillis());
		
		return getNeturalAge(calendarBirth, calendarNow);
	}
	
	/**
	 * 计算两个日期相差几年几月几日
	 * @param birthMilliseconds
	 * @param nowMilliseconds
	 * @return 数值形式：[0]是年，[1]是月，[2]是日
	 */
	public static int[] getNeturalAge(long birthMilliseconds, long nowMilliseconds) {
		if (birthMilliseconds == 0 || nowMilliseconds == 0) {
			return null;
		}
		
		Calendar calendarBirth = Calendar.getInstance();
		calendarBirth.setTimeInMillis(birthMilliseconds);
		
		Calendar calendarNow = Calendar.getInstance();
		calendarNow.setTimeInMillis(nowMilliseconds);
		
		return getNeturalAge(calendarBirth, calendarNow);
	}
	
	/**
	 * 计算两个日期相差几年几月几日
	 * @param calendarBirth
	 * @param calendarNow
	 * @return 数值形式：[0]是年，[1]是月，[2]是日
	 */
	public static int[] getNeturalAge(Calendar calendarBirth, Calendar calendarNow) {
		int diffYears = 0, diffMonths, diffDays;
		int dayOfBirth = calendarBirth.get(Calendar.DAY_OF_MONTH);
		int dayOfNow = calendarNow.get(Calendar.DAY_OF_MONTH);
		if (dayOfBirth <= dayOfNow) {
			diffMonths = getMonthsOfAge(calendarBirth, calendarNow);
			diffDays = dayOfNow - dayOfBirth;
			if (diffMonths == 0)
				diffDays++;
		} else {
			if (isEndOfMonth(calendarBirth)) {
				if (isEndOfMonth(calendarNow)) {
					diffMonths = getMonthsOfAge(calendarBirth, calendarNow);
					diffDays = 0;
				} else {
					calendarNow.add(Calendar.MONTH, -1);
					diffMonths = getMonthsOfAge(calendarBirth, calendarNow);
					diffDays = dayOfNow + 1;
				}
			} else {
				if (isEndOfMonth(calendarNow)) {
					diffMonths = getMonthsOfAge(calendarBirth, calendarNow);
					diffDays = 0;
				} else {
					calendarNow.add(Calendar.MONTH, -1);// 上个月
					diffMonths = getMonthsOfAge(calendarBirth, calendarNow);
					// 获取上个月最大的一天
					int maxDayOfLastMonth = calendarNow
							.getActualMaximum(Calendar.DAY_OF_MONTH);
					if (maxDayOfLastMonth > dayOfBirth) {
						diffDays = maxDayOfLastMonth - dayOfBirth + dayOfNow;
					} else {
						diffDays = dayOfNow;
					}
				}
			}
		}
		// 计算月份时，没有考虑年
		diffYears = diffMonths / 12;
		diffMonths = diffMonths % 12;
		return new int[] { diffYears, diffMonths, diffDays };
	}

	/**
	 * 获取两个日历的月份之差
	 * 
	 * @param calendarBirth
	 * @param calendarNow
	 * @return
	 */
	public static int getMonthsOfAge(Calendar calendarBirth, Calendar calendarNow) {
		return (calendarNow.get(Calendar.YEAR) - calendarBirth.get(Calendar.YEAR)) * 12 + calendarNow.get(Calendar.MONTH) - calendarBirth.get(Calendar.MONTH);
	}

	/**
	 * 判断这一天是否是月底
	 * 
	 * @param calendar
	 * @return
	 */
	public static boolean isEndOfMonth(Calendar calendar) {
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		if (dayOfMonth == calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
			return true;
		}
		return false;
	}
	
	/**
	 * 将当前的日期时间转成给定的时区和要输出的日期时间格式
	 * @param localTimeInMillis 当前的日期时间
	 * @param newFormat 要输出的日期时间格式 "HH:mm"
	 * @param newTimeZone 给定的时区，如"GMT+8:00"
	 * @return
	 */
	public static String parserDateTimeByNewTimeZone(long localTimeInMillis, String newFormat, String newTimeZone) {
		Calendar localCalendar = Calendar.getInstance(TimeZone.getDefault());// 本地的时区
		localCalendar.setTimeInMillis(localTimeInMillis);
		
		SimpleDateFormat sdf = new SimpleDateFormat(newFormat);// 要输出的日期时间格式
		sdf.setTimeZone(TimeZone.getTimeZone(newTimeZone));// 指定的时区
		
		return sdf.format(localCalendar.getTime());
	}
	
	/**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {//2015-10-15T06:31:43.251Z
//		System.out.println(formatDateStr2Desc("2012-3-2 12:2:20","MM月dd日  HH:mm"));
//		System.out.println(getStringByFormat("2015-10-15T06:31:43.251Z", "yyyy-MM-ddTHH:mm:ss SSSZ"));
	}

}

