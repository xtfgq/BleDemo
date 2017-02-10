package com.test.bluetooth.utils;

import java.util.Stack;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

/**
 * @功能描述：Activity管理工具类(建议在BaseActivity中初始化)
 * 
 * @author 梁佳旺
 * @version 1.0
 * @create time: 2014-11-27 下午1:07:40
 */
public class ActivityUtil
{
	// 有重复值的
	private static Stack<Activity> activityStack = new Stack<Activity>();

	/**
	 * 禁止实例化ActivityManagerUtil
	 */
	private ActivityUtil()
	{

	}

	/**
	 * 添加Activity到堆栈
	 */
	public static void addActivity(Activity activity)
	{
		if (null != activity) {
			activityStack.add(activity);
		}
	}

	/**
	 * 获取堆栈中所有的Activity
	 */
	public static Stack<Activity> getAllActivity()
	{
		if (activityStack.isEmpty()) {
			return null;
		}
		return activityStack;
	}
	
	/**
	 * 从堆栈中获取指定的activity
	 * @param clazz
	 * @return
	 */
	public static Activity getActivity(Class<?> clazz) {
		for (Activity activity : activityStack) {
			if (activity.getClass().equals(clazz)) {
				return activity;
			}
		}
		return null;
	}
	
	/**
	 * 获取栈顶的Activity（堆栈中最后一个压入的）
	 */
	public static Activity getStackTopActivity()
	{
		if (activityStack.isEmpty()) {
			return null;
		}
		return activityStack.lastElement();
	}

	/**
	 * 从堆栈中移出栈顶的Activity（堆栈中最后一个压入的）注意：没finish掉
	 */
	public static void removeStackTopActivity()
	{
		if (activityStack.isEmpty()) {
			return;
		}
		Activity activity = activityStack.lastElement();
		removeActivity(activity);
	}

	/**
	 * 从堆栈中移出指定的Activity，注意：没finish掉
	 */
	public static void removeActivity(Activity activity)
	{
		if (!activityStack.isEmpty() && null != activity) {
			activityStack.remove(activity);
		}
	}
	
	/**
	 * 结束栈顶的Activity（堆栈中最后一个压入的）
	 */
	public static void finishStackTopActivity()
	{
		if (activityStack.isEmpty()) {
			return;
		}
		Activity activity = activityStack.lastElement();
		finishActivity(activity);
	}

	/**
	 * 结束指定的Activity
	 */
	public static void finishActivity(Activity activity)
	{
		if (!activityStack.isEmpty() && null != activity) {
			activityStack.remove(activity);
			activity.finish();
			activity = null;
		}
	}

	/**
	 * 结束指定类名的Activity
	 */
	public static void finishActivity(Class<?> clazz)
	{
		for (Activity activity : activityStack) {
			if (activity.getClass().equals(clazz)) {
				finishActivity(activity);
			}
		}
	}

	/**
	 * 结束所有Activity
	 */
	public static void finishAllActivity()
	{
		for (Activity activity : activityStack) {
			if (null != activity && !activity.isFinishing()) {
				activity.finish();
			}
		}
		activityStack.clear();
	}

	/**
	 * 结束所有Activity并且退出应用程序
	 */
	public static void exitApp(Context context)
	{
		try {
			finishAllActivity();
			
			ActivityManager activityMgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			// 使用restartPackage这个方法需要加入权限：<uses-permissionandroid:name="android.permission.RESTART_PACKAGES"/>
			// 使用killBackgroundProcesses时必须在androidmanifest.xml文件中加入KILL_BACKGROUND_PROCESSES这个权限
			// restartPackage(packageName)方法只适用于2.2以前的，2.2(含)以后的可用killBackgroundProcesses(packageName)
//			activityMgr.restartPackage(context.getPackageName());
			activityMgr.killBackgroundProcesses(context.getPackageName());
			System.gc();
		} catch (Exception e) {
			e.printStackTrace();
//			CrashHandler.runtimeException(e);
		} finally {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
	}
}
