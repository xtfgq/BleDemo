package com.test.bluetooth.core;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bluetooth.bluetoothdemo.BaseActivity;

import com.bluetooth.bluetoothdemo.R;
import com.bluetooth.bluetoothdemo.bean.RxBus;
import com.bluetooth.bluetoothdemo.type.EventType;
import com.test.bluetooth.MyApplication;

import com.test.bluetooth.SysConfig;
import com.test.bluetooth.ui.UIBase;
import com.test.bluetooth.entry.BgMeasureRecordEntry;
import com.test.bluetooth.entry.BgMeasureRecordEntry.BgType;
import com.test.bluetooth.entry.BluetoothEntry;
import com.test.bluetooth.entry.BluetoothEntry.BluetoothConnectState;
import com.test.bluetooth.entry.BluetoothEntry.BluetoothSearchState;
import com.test.bluetooth.utils.ActivityUtil;
import com.test.bluetooth.utils.CRC16Util;
import com.test.bluetooth.utils.DateTimeUtil;
import com.test.bluetooth.utils.MyLog;
import com.test.bluetooth.utils.TimeTypeMathUtil;
import com.test.bluetooth.utils.TypeConversionUtil;

import static android.os.Build.VERSION_CODES.M;

/**
 * 自定义蓝牙各种操作工具类
 * 
 * @author 梁佳旺
 */
public class MyBluetoothManager {
	private static final String TAG = MyBluetoothManager.class.getSimpleName();
	
	public static final int REQUEST_CODE_BLUETOOTH_ENABLE = 0x998;
	public static final int REQUEST_CODE_BLUETOOTH_DISCOVERABLE = 0x999;
	
	/**
	 * 测量设备各种反馈数据的类型
	 */
	private interface ResponeDataType {
		/** 上次测量结果的状态 **/
		static final String LAST_TIME_MEASURE_STATE_PRE = "7B012001101266000510";
		/** 插入试纸条 **/
		static final String INSERT_BLOOD_GLUCOSE_SENSOR_PRE = "7B012001101266000511";
		/** 等待加血 **/
		static final String WAITING_TO_ADD_BLOOD_PRE = "7B012001101266000522";
		/** 已加血 **/
		static final String COMPLETED_ADD_BLOOD_PRE = "7B012001101266000533";
		/** 测量结果(血糖值) **/
		static final String MEASURE_RESULT_VALUE_PRE = "7B012001101266000544";
		/** 测量结果(时间值) **/
		static final String MEASURE_RESULT_TIME_PRE = "7B0120011044AA0006";
		/** 测量异常报警 **/
		static final String ERROR_ALARM_PRE = "7B012001101266000555";
		/** 设备S/N串号 **/
		static final String SN_PRE = "7B0120011077AA";
		/** 设备的测量单位 **/
		static final String UNIT_PRE = "7B01200110AAAA0001";
		/** 历史数据 **/
		static final String HISTORY_PRE = "7B01200110DDAA0009";
		/** 历史数据结束 **/
		static final String HISTORY_END_PRE = "7B01200110D166";
		/** 设备关机 **/
		static final String POWER_OFF = "7B01200110D26600000B0907007D";
		/** 同步设置设备时间成功 **/
		static final String SET_TIME_SUCCESS = "7B012001104499000111000802077D";
		/** 字符串"7D" **/
		static final String _7D = "7D";
	}
	
	/** 由mg/dl转换成mmol/l需除以18 **/
	public static final String MMOL = "mmol/L";
	/** 由mmol/L转换成mg/dl需乘以18 **/
	public static final String MG = "mg/dL";
	/** 蓝牙设备测量单位，未知 **/
	public static final String UNIT_UNKNOW = "unknow";
	/** 蓝牙设备测量单位，默认是未知的 **/
	public static String unit = UNIT_UNKNOW;
	/** mmol/L与mg/dl互转因子 **/
//	private static final float MMOL_MG_PARSER_VALUE = 18f;
	/** mmol/L单位的设备的数值要除以10才跟设备的显示的结果一样 **/
	private static final float MMOL_PARSER_VALUE = 10f;
	/** 当前发命令类型，默认为未知命令 **/
	private int currCmdType = CmdType.CMD_DEFAULT;
	private String bluetoothData = "";

	public List<BgMeasureRecordEntry> getHistoryData() {
		return historyData;
	}

	public void setHistoryData(List<BgMeasureRecordEntry> historyData) {
		this.historyData = historyData;
	}

	private List<BgMeasureRecordEntry> historyData = new ArrayList<BgMeasureRecordEntry>();
	
	private static MyBluetoothManager instance;
	
	public MyBluetoothManager() {
		initConfig();
	}
	
	public static MyBluetoothManager getIntance() {
		if (null == instance) {
			instance = new MyBluetoothManager();
		}
		
		return instance;
	}
	
	private Intent serviceIntent;
	
	private BluetoothService mBluetoothService;
	
	private boolean isServiceRunning = false;
	
	private DecimalFormat df;

	private void initConfig() {
		serviceIntent = new Intent(MyApplication.getInstance(), BluetoothService.class);// 意图对象，用来绑定Service的
	}

	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// 使用一个IBinder对象作为目标，获得Service端的Messenger对象
			mBluetoothService = ((BluetoothService.LocalBinder) service).getService();
			((BluetoothService.LocalBinder) service).setServiceHandler(mHandler);
			isServiceRunning = true;

			mBluetoothService.sendMessage(BluetoothService.MSG_REGISTER_SERVICE_SUCCESS);

		}

		// 内存不足或发生意外时才会被调用
		@Override
		public void onServiceDisconnected(ComponentName name) {
			isServiceRunning = false;
			MyLog.i(TAG, "意外导致解除绑定服务");
		}
	};
	
	public void bindService() {
		if (null != serviceIntent && null != mConnection && !isServiceRunning && MyApplication.isSelectedBLE()) {
			MyApplication.getInstance().bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
		}
		
		if (null == df) {
			df = new DecimalFormat("#0.0");
		}
	}
	
	public void reBindService() {
		if (!isServiceRunning) {
			bindService();
		} else {
			if (null != mBluetoothService && MyApplication.isSelectedBLE() && !isExitConnected()) {
				mBluetoothService.sendMessage(BluetoothService.MSG_REGISTER_SERVICE_SUCCESS);
			}
		}

	}

	public void unbindService() {
		if (null != mBluetoothService) {
			disconnect(null, false);
			mBluetoothService.sendMessage(BluetoothService.MSG_CLOSE_BLUETOOTH);
		}

		
		if (null != mConnection && isServiceRunning) {
			MyApplication.getInstance().unbindService(mConnection);
		}
		
		isServiceRunning = false;
		mConnection = null;
		mBluetoothService = null;
	}
	
	/**
	 * 手动执行发起蓝牙连接操作
	 * @param deviceAddress
	 */
	public void connect(String deviceAddress) {
		if (null != mBluetoothService) {
			mBluetoothService.sendMessage(BluetoothService.MSG_CONNECT_BLUETOOTH, deviceAddress);
		}
	}
	
	/**
	 * 断开蓝牙连接
	 * @param deviceAddress
	 * @param isExitInList
	 */
	public void disconnect(String deviceAddress, boolean isExitInList) {
		if (null != mBluetoothService) {
			mBluetoothService.sendMessage(BluetoothService.MSG_DISCONNECT_BLUETOOTH, deviceAddress, isExitInList);
//			mBluetoothService.close();
		}
	}
	
	public void close() {
		if (null != mBluetoothService) {
			mBluetoothService.sendMessage(BluetoothService.MSG_CLOSE_BLUETOOTH);
		}
	}
	
	public void close(long delayMillis) {
		if (null != mBluetoothService) {
			mBluetoothService.sendMessageDelayed(BluetoothService.MSG_CLOSE_BLUETOOTH, delayMillis);
		}
	}
	
	/**
	 * 开始扫描搜索蓝牙设备
	 */
	public void startBleScan() {
		if (null != mBluetoothService) {
			mBluetoothService.startBleScan();
		}
	}

	private Handler mHandler = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
			BaseActivity activity = (BaseActivity) ActivityUtil.getStackTopActivity();
			Bundle bundle = msg.getData();
			int msgCode = msg.what;
			
			switch (msgCode) {
			case BluetoothService.MSG_BLUETOOTH_SEARCH_STATE:// 蓝牙搜索按钮状态
				if (null != mOnBluetoothListener) {
					int searchSate = getBluetoothDeviceSearchState();
					mOnBluetoothListener.bluetoothDeviceSearchState(searchSate);
				}
				break;
				
			case BluetoothService.MSG_REGISTER_SERVICE_SUCCESS:// 自定义的蓝牙服务注册开启成功
				if (null != mBluetoothService) {
					mBluetoothService.openBluetooth();// 检查并开启蓝牙
				}
				break;
				
			case BluetoothService.MSG_NO_BLUETOOTH:// 用户设备没有蓝牙硬件，不支持蓝牙功能
				UIBase.showToastLong(MyApplication.getInstance().getString(R.string.bluetooth_not_supported));
				break;
				
			case BluetoothService.MSG_UNOPENED_BLUETOOTH:// 未开启蓝牙设备
				// 方式一
				if (null != activity) {
					Intent settingIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
					settingIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
					activity.startActivityForResult(settingIntent, MyBluetoothManager.REQUEST_CODE_BLUETOOTH_ENABLE);
				}
				// 方式二
//				bluetoothAdapter.enable();
				break;
				
//			case BluetoothService.MSG_OPEN_BLUETOOTH_DISCOVERABLE:// 开启蓝牙可见性
//				if (null != mActivity) {
//					openBluetoothDiscoverable(mActivity);
//				}
//				break;
				
			case BluetoothService.MSG_CHECK_BLUETOOTH_IS_SUPPORT_BLE:// 检查你的蓝牙设备是否支持BLE通信功能
				boolean isSupportBLE = null == activity ? false : checkBluetoothIsSupportBLE();
				
				if (isSupportBLE) {
					int hasAccessCoarseLocationPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
					int hasAccessFineLocationPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
					
					if (hasAccessCoarseLocationPermission == PackageManager.PERMISSION_GRANTED && hasAccessFineLocationPermission == PackageManager.PERMISSION_GRANTED) {
						startBleScan();
					} else {
						ActivityCompat.requestPermissions(activity, new String[] { Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION }, SysConfig.REQUEST_CODE_PERMISSION_ACCESS_COARSE_OR_FINE_LOCATION);
					}
				} else {
					if (isServiceRunning) {
						unbindService();
						MyLog.i(TAG, "不支持BLE导致准备解除绑定服务");
					}
				}
				break;
				
			case BluetoothService.MSG_FOUND_BLUETOOTH_DEVICE:// 发现新的蓝牙设备
				BluetoothEntry bluetoothEntry = (BluetoothEntry) bundle.getSerializable(BluetoothService.BLUETOOTH_DEVICE_ENTRY);
				if (null != bluetoothEntry) {
					if (null != mOnBluetoothListener) {
						mOnBluetoothListener.foundBluetoothDevice();
					}
					
					String deviceAddress = bluetoothEntry.getAddress();
					MyLog.i(TAG, "发现新的蓝牙设备:"+bluetoothEntry.getName()+", "+deviceAddress + ", isOpen="+bluetoothEntry.isOpen());
					if (bluetoothEntry.isOpen()) {
						if (null != mBluetoothService) {
							int bluetoothConnectState = mBluetoothService.connect(deviceAddress);
							if (null != mOnBluetoothListener) {
								mOnBluetoothListener.bluetoothDeviceConnectState(deviceAddress, bluetoothConnectState);
							}
						}
					}
				}
				break;
				
			case BluetoothService.MSG_CONNECT_BLUETOOTH:// 手动执行连接蓝牙操作
				String deviceAddress0 = bundle.getString(BluetoothService.BLUETOOTH_ADDRESS);
				if (null != mBluetoothService) {
					Integer bluetoothConnectState = mBluetoothService.connect(deviceAddress0);
					
					if (null != mOnBluetoothListener) {
						mOnBluetoothListener.bluetoothDeviceConnectState(deviceAddress0, bluetoothConnectState);
					}
				}
				break;
				
			case BluetoothService.MSG_CONNECT_BLUETOOTH_SUCCESS:// 指定的蓝牙设备连接成功
				String deviceAddress1 = bundle.getString(BluetoothService.BLUETOOTH_ADDRESS);
				if (!MyApplication.isSelectedBLE()) {
					mBluetoothService.disconnect(deviceAddress1, false);// 立即断开客户端与设备的连接状态
					return;
				}
				
				if (null != mBluetoothService) {
					mBluetoothService.discoverServices(deviceAddress1);// 查找发现该蓝牙的服务
				}
				RxBus.getInstance().send(new EventType("found_bluetooth_device"));
				break;
				
			case BluetoothService.MSG_SERVICE_DISCOVERED_SUCCESS:// 查找发现蓝牙服务成功
				sendCommandsToBluetoothDevice(CmdType.CMD_GET_SN);
				break;
				
			case BluetoothService.MSG_DISCONNECT_BLUETOOTH:// 执行断开蓝牙连接操作
				String deviceAddress2 = bundle.getString(BluetoothService.BLUETOOTH_ADDRESS);
				boolean isExitInList = bundle.getBoolean(BluetoothService.IS_EXIT_IN_LIST, false);
				if (null != mBluetoothService) {
					mBluetoothService.disconnect(deviceAddress2, isExitInList);
				}
				break;
				
			case BluetoothService.MSG_DISCONNECT_BLUETOOTH_SUCCESS:// 断开蓝牙连接成功
				String deviceAddress3 = bundle.getString(BluetoothService.BLUETOOTH_ADDRESS);
				if (null != mBluetoothService && !mBluetoothService.getDeviceMap().containsKey(deviceAddress3)) {
					UIBase.showToastShort(MyApplication.getTextString(R.string.disconnect_bluetooth_service_success_text));
				}
				unit = UNIT_UNKNOW;// 修改设备单位为未知状态
				if (null != mOnBluetoothListener) {
					mOnBluetoothListener.bluetoothDeviceConnectState(deviceAddress3, BluetoothConnectState.DISCONNECT);
				}
				break;
				
			case BluetoothService.MSG_RECONNECT_BLUETOOTH:// 断开后重新连接蓝牙
				String deviceAddress4 = bundle.getString(BluetoothService.BLUETOOTH_ADDRESS);
				MyLog.i(TAG, "-----断开后重新连接蓝牙-----"+mBluetoothService.getDeviceMap().toString());
				if (null != mBluetoothService && !mBluetoothService.getDeviceMap().containsKey(deviceAddress4)) {
					mBluetoothService.connect(deviceAddress4);
				}
				break;
				
			case BluetoothService.MSG_DATA_AVAILABLE:// 蓝牙数据传输成功
				if (null != bundle) {
					String deviceAddress5 = bundle.getString(BluetoothService.BLUETOOTH_ADDRESS);
					String tempBluetoothData = bundle.getString(BluetoothService.BLUETOOTH_DATA);
					
					if (ResponeDataType.POWER_OFF.equals(tempBluetoothData)) {// 如果接收到该数据，说明蓝牙设备已关闭，由于收到设备自己断开的广播有延迟，无法及时断开设备的连接，所以使用了该数据作为断开的数据
						mBluetoothService.disconnect(deviceAddress5, false);// 立即断开客户端与设备的连接状态
						return;
					}
					
					parserData(deviceAddress5, tempBluetoothData);
				}
				break;
				
			case BluetoothService.MSG_CLOSE_BLUETOOTH:// 执行关闭和释放蓝牙资源操作
				if (null != mBluetoothService) {
					mBluetoothService.close();
				}
				break;
			}
		}
	};

	/**
	 * 根据不同的actionType需求，发送相应的指令到蓝牙设备上并时刻监听蓝牙设备回复的数据和信息
	 * @param cmdType 不同指令的类型
	 * @param deviceAddress 当前连接成功的设备MAC地址
	 * @return true 发送指令成功；false 发送指令失败
	 */
	public boolean sendCommandsToBluetoothDevice(int cmdType) {
		byte[] cmd = null;
		currCmdType = cmdType;
		bluetoothData = "";

		switch (cmdType) {
			case CmdType.CMD_GET_SN:// 获取设备S/N串号
				cmd = new byte[]{ 0x7B, 0x01, 0x10, 0x01, 0x20, 0x77, 0x55, 0x00, 0x00, 0x01, 0x0B, 0x0B, 0x04, 0x7D };// 读
				break;

			case CmdType.CMD_GET_UNIT:// 获取设备测量单位
				cmd = new byte[]{ 0x7B, 0x01, 0x10, 0x01, 0x20, (byte) 0xAA, 0x55, 0x00, 0x00, 0x02, 0x01, 0x0D, 0x08, 0x7D };// 读
				break;

			case CmdType.CMD_SET_TIME:// 设置蓝牙设备的时间(需传时间值)
				String currTime = DateTimeUtil.getStringByFormat(System.currentTimeMillis(), DateTimeUtil.dateFormat_ymdhms);
				currTime = currTime.substring(2);// 去掉前面两位数字即如20160720221455变成了160720221455
				int halfLength = currTime.length() / 2;//160711155042
				String hexSrc = "";//10070B0F322A
				for (int i = 0; i < halfLength; i++) {//3874-->07 04 03 08
					hexSrc += TypeConversionUtil.intToHexStringUpperCase(Integer.parseInt(currTime.substring(2 * i, 2 * i + 2)), 1);
				}
				byte[] times = TypeConversionUtil.hexString2Bytes(hexSrc);
//			byte[] cmdData = new byte[] { 0x01, 0x10, 0x01, 0x20, 0x44, 0x66, 0x00, 0x06, 0x10, 0x07, 0x0B, 0x0F, 0x32, 0x2A };
				byte[] cmdData = new byte[] { 0x01, 0x10, 0x01, 0x20, 0x44, 0x66, 0x00, 0x06, times[0], times[1], times[2], times[3], times[4], times[5] };
				String crcStr = String.format("%04X", CRC16Util.calcCrc16(cmdData));// 四位十六进制(大写)
				int crcLength = crcStr.length();
				byte[] crcData = new byte[crcLength];
				for (int i = 0; i < crcLength; i++) {
					crcData[i] = Integer.valueOf("0" + crcStr.charAt(i), 16).byteValue();
				}

//			cmd = new byte[]{ 0x7B, 0x01, 0x10, 0x01, 0x20, 0x44, 0x66, 0x00, 0x06, 0x10, 0x07, 0x0B, 0x0F, 0x32, 0x2A, 0x07, 0x04, 0x03, 0x08, 0x7D };
				cmd = new byte[]{ 0x7B, 0x01, 0x10, 0x01, 0x20, 0x44, 0x66, 0x00, 0x06, times[0], times[1], times[2], times[3], times[4], times[5], crcData[2], crcData[3], crcData[0], crcData[1], 0x7D };
				break;

			case CmdType.CMD_GET_MEASURE:// 仪器状态和测量结果，即实时获取蓝牙设备的测量数据
				cmd = new byte[]{ 0x7B, 0x01, 0x10, 0x01, 0x20, 0x12, 0x55, 0x00, 0x00, 0x00, 0x05, 0x07, 0x08, 0x7D };
//			cmd = new byte[]{};
				break;

			case CmdType.CMD_REPLY_MEASURE:// 回复仪器状态和测量结果，即实时获取蓝牙设备的测量数据已接收完毕
				cmd = new byte[]{ 0x7B, 0x01, 0x10, 0x01, 0x20, 0x12, (byte) 0x99, 0x00, 0x00, 0x0C, 0x05, 0x04, 0x07, 0x7D };
				break;

			case CmdType.CMD_DELETE_MEASURE:// 删除已接收的实时测量数据
				cmd = new byte[]{ 0x7B, 0x01, 0x10, 0x01, 0x20, 0x56, 0x66, 0x00, 0x01, 0x11, 0x08, 0x06, 0x0D, 0x04, 0x7D };
				break;

			case CmdType.CMD_GET_HISTORY:// 客户端读取历史
				cmd = new byte[]{ 0x7B, 0x01, 0x10, 0x01, 0x20, (byte) 0xDD, 0x55, 0x00, 0x00, 0x03, 0x0A, 0x06, 0x0C, 0x7D };
				break;

			case CmdType.CMD_DELETE_HISTORY:// 删除已接收的历史测量数据
				cmd = new byte[]{ 0x7B, 0x01, 0x10, 0x01, 0x20, 0x55, 0x66, 0x00, 0x01, 0x11, 0x0C, 0x02, 0x0D, 0x04, 0x7D };
				break;

			case 9:// 历史数据导出(该命令暂时不需要)
				cmd = new byte[]{ 0x7B, 0x01, 0x10, 0x01, 0x20, 0x22, 0x55, 0x00, 0x00, 0x00, 0x0A, 0x07, 0x08, 0x7D };
				break;
		}

		if (null == cmd || null == mBluetoothService) {
			return false;
		}

		return mBluetoothService.sendCommandsToBluetoothDevice(cmd, cmdType);
	}

	/**
	 * 设置当前使用的指令类型
	 * @param cmdType 不同指令的类型
	 */
	public void setCurrCmdType(int cmdType) {
		currCmdType = cmdType;
	}

	/**
	 * 解析蓝牙设备传输过来的数据
	 * @param deviceAddress
	 * @param data
	 */
	private void parserData(String deviceAddress, String data) {
		switch (currCmdType) {
		case CmdType.CMD_GET_SN:// 获取设备S/N串号
			parserSNData(deviceAddress, data);
			break;
			
		case CmdType.CMD_GET_UNIT:// 获取设备测量单位
			parserUnitData(data, CmdType.CMD_GET_UNIT);
			break;
			
		case CmdType.CMD_SET_TIME:// 设置蓝牙设备的时间(需传时间值)
			parserTimeData(data, CmdType.CMD_SET_TIME);
			break;
			
		case CmdType.CMD_DELETE_MEASURE:// 删除该条实时测量数据
			sendCommandsToBluetoothDevice(CmdType.CMD_REPLY_MEASURE);
			currCmdType = CmdType.CMD_GET_MEASURE;
			MyLog.i(TAG, "删除该条实时测量数据成功");
			break;
			
		case CmdType.CMD_REPLY_MEASURE:// 回复仪器状态和测量结果，即实时获取蓝牙设备的测量数据已接收完毕
			currCmdType = CmdType.CMD_GET_MEASURE;
			MyLog.i(TAG, "接收实时测量数据完毕");
			break;
			
		case CmdType.CMD_GET_HISTORY:// 客户端读取历史

			parserHistoryData(deviceAddress, data, CmdType.CMD_GET_HISTORY);
			break;
			
		case CmdType.CMD_DELETE_HISTORY:// 删除已接收的历史测量数据
			currCmdType = CmdType.CMD_GET_MEASURE;
			MyLog.i(TAG, "删除历史数据成功");
			break;
			
		default :// 仪器状态和测量结果，即实时获取蓝牙设备的测量数据
			parserMeasureData(deviceAddress, data, currCmdType);
			break;
		}
	}
	
	private String SN = "";
//	private TextDialogFragment authDialog = null;
	
	/**
	 * 过滤掉夹杂数据
	 * @param dataSrc
	 * @param startStr
	 * @param endStr
	 * @return
	 */
	private String filterData(String dataSrc, String startStr, String endStr) {
		if (dataSrc.contains(startStr)) {
			int start = dataSrc.indexOf(startStr);
			int end = dataSrc.indexOf(endStr, start) + 2;
			
			dataSrc = dataSrc.replace(dataSrc.substring(start, end), "");
		}
		return dataSrc;
	}
	
	/**
	 * 从字符串中提取想要的数据
	 * @param dataSrc
	 * @param startStr
	 * @param endStr
	 * @return
	 */
	private String subStringData(String dataSrc, String startStr, String endStr) {
		if (dataSrc.contains(startStr)) {
			int start = dataSrc.indexOf(startStr);
			int end = dataSrc.indexOf(endStr, start) + 2;
			
			dataSrc = dataSrc.substring(start, end);
		}
		return dataSrc;
	}

	/**
	 * 过滤掉可能夹杂有的其他数据
	 */
	private void filterAllData() {
		// 过滤掉上次测量结果的数据
		bluetoothData = filterData(bluetoothData, ResponeDataType.LAST_TIME_MEASURE_STATE_PRE, ResponeDataType._7D);
		// 过滤掉请插入试纸的信息
		bluetoothData = filterData(bluetoothData, ResponeDataType.INSERT_BLOOD_GLUCOSE_SENSOR_PRE, ResponeDataType._7D);
		// 过滤掉等待加血的信息
		bluetoothData = filterData(bluetoothData, ResponeDataType.WAITING_TO_ADD_BLOOD_PRE, ResponeDataType._7D);
		// 过滤掉完成加血的信息
		bluetoothData = filterData(bluetoothData, ResponeDataType.COMPLETED_ADD_BLOOD_PRE, ResponeDataType._7D);
		// 过滤掉测量异常报警的信息
		bluetoothData = filterData(bluetoothData, ResponeDataType.ERROR_ALARM_PRE, ResponeDataType._7D);
	}
	
	/**
	 * 解析S/N号
	 * @param deviceAddress
	 * @param data
	 */
	private void parserSNData(final String deviceAddress, String data) {
		bluetoothData += data;
		
		if (bluetoothData.contains(ResponeDataType.SN_PRE) && data.endsWith(ResponeDataType._7D) && bluetoothData.length() > 42) {
			// 过滤掉可能夹杂有的其他数据
			filterAllData();
			// 提取出S/N串号的信息
			bluetoothData = subStringData(bluetoothData, ResponeDataType.SN_PRE, ResponeDataType._7D);
			
			int length = bluetoothData.length();
			String result = bluetoothData.substring(length - 42, length - 10);
			result = result.substring(result.length() - 26);// 后13位有效即倒退26个字符
			int halfLength = result.length() / 2;
			SN = "";
			char hexChar;
			for (int i = 0; i < halfLength; i++) {
				hexChar = (char) intToHex(result.substring(2 * i, 2 * i + 2));
				SN += hexChar;
			}
			
			MyLog.i(TAG, "设备S/N串号：" +SN);
			bluetoothData = "";
			
			BluetoothEntry bluetoothEntry = mBluetoothService.getDeviceMap().get(deviceAddress);
			if (null == bluetoothEntry) {
				return;
			}
			
			if (bluetoothEntry.isOpen() && null != bluetoothEntry.getPwd() && bluetoothEntry.getPwd().equals(SN)) {
				bluetoothEntry.setConnectState(BluetoothConnectState.CONNECT_SUCCESS);
				bluetoothEntry.setExitInList(true);
				
				MyApplication.setCacheBluetoothEntry(bluetoothEntry);
				
				if (null != mOnBluetoothListener) {
					mOnBluetoothListener.bluetoothDeviceConnectState(deviceAddress, BluetoothConnectState.CONNECT_SUCCESS);
				}
				
				if (UNIT_UNKNOW.equals(unit)) {// 如果没有获取测量单位，则先获取设备上的测量单位
					sendCommandsToBluetoothDevice(CmdType.CMD_GET_UNIT);
				} else {// 已获取设备上的测量单位，则同步设置设备的时间
					sendCommandsToBluetoothDevice(CmdType.CMD_SET_TIME);
				}
				
			}

				 else {
				BaseActivity activity = (BaseActivity) ActivityUtil.getStackTopActivity();
				LayoutInflater inflater = LayoutInflater.from(activity);
				View layout = inflater.inflate(R.layout.dialog_default_ensure_click, null);
				AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				builder.setView(layout);
				builder.setCancelable(false);
				final AlertDialog dialog = builder.create();
				dialog.show();
				TextView tvok = (TextView) layout.findViewById(R.id.dialog_default_click_ensure);
				TextView tvCancle = (TextView) layout.findViewById(R.id.dialog_default_click_cancel);
				final EditText edcontent = (EditText) layout.findViewById(R.id.dialog_default_click_text_msg);

				tvok.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						String content = edcontent.getText().toString();
						if (TextUtils.isEmpty(content) || !content.equals(SN.substring(SN.length() - 5))) {
							UIBase.showToastShort(MyApplication.getInstance().getString(R.string.your_sn_invalid_text));
							disconnect(deviceAddress, true);
						} else {
							BluetoothEntry bluetoothEntry = mBluetoothService.getDeviceMap().get(deviceAddress);
							bluetoothEntry.setPwd(SN);
							bluetoothEntry.setOpen(true);
							bluetoothEntry.setExitInList(true);
							bluetoothEntry.setConnectState(BluetoothConnectState.CONNECT_SUCCESS);

							MyApplication.setCacheBluetoothEntry(bluetoothEntry);

							if (null != mOnBluetoothListener) {
								mOnBluetoothListener.bluetoothDeviceConnectState(deviceAddress, BluetoothConnectState.CONNECT_SUCCESS);
							}

							if (UNIT_UNKNOW.equals(unit)) {// 如果没有获取测量单位，则先获取设备上的测量单位
								sendCommandsToBluetoothDevice(CmdType.CMD_GET_UNIT);
							} else {// 已获取设备上的测量单位，则同步设置设备的时间
								sendCommandsToBluetoothDevice(CmdType.CMD_SET_TIME);
							}
							dialog.dismiss();
						}


					}
				});
				tvCancle.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						dialog.dismiss();
						disconnect(deviceAddress, true);
					}
				});
//				Bundle bundle = new Bundle();
//				bundle.putString(TextDialogFragment.ARG_TITLE, MyApplication.getInstance().getString(R.string.bluetooth_device_authentication_text));
//				bundle.putString(TextDialogFragment.ARG_EDIT_HINT, MyApplication.getInstance().getString(R.string.please_input_bluetooth_device_sn_text));
//				bundle.putString(TextDialogFragment.ARG_EDIT_CONTENT, "");
//				if (null != authDialog && null != authDialog.getDialog() && authDialog.getDialog().isShowing()) {
//					return;
//				}
				
//				authDialog = TextDialogFragment.newInstance(bundle);
//				authDialog.setOnTextDialogListener(new TextDialogFragment.OnTextDialogListener() {
//					@Override
//					public void onTextDialogResult(int requestCode, Bundle bundle) {
//						String content = bundle.getString(TextDialogFragment.ARG_EDIT_CONTENT);
//						if (TextUtils.isEmpty(content) || !content.equals(SN.substring(SN.length() - 5))) {
//							UIBase.showToastShort(MyApplication.getInstance().getString(R.string.your_sn_invalid_text));
//							disconnect(deviceAddress, true);
//						} else {
//							BluetoothEntry bluetoothEntry = mBluetoothService.getDeviceMap().get(deviceAddress);
//							bluetoothEntry.setPwd(SN);
//							bluetoothEntry.setOpen(true);
//							bluetoothEntry.setExitInList(true);
//							bluetoothEntry.setConnectState(BluetoothConnectState.CONNECT_SUCCESS);
//
//							MyApplication.setCacheBluetoothEntry(bluetoothEntry);
//
//							if (null != mOnBluetoothListener) {
//								mOnBluetoothListener.bluetoothDeviceConnectState(deviceAddress, BluetoothConnectState.CONNECT_SUCCESS);
//							}
//
//							if (UNIT_UNKNOW.equals(unit)) {// 如果没有获取测量单位，则先获取设备上的测量单位
//								sendCommandsToBluetoothDevice(CmdType.CMD_GET_UNIT);
//							} else {// 已获取设备上的测量单位，则同步设置设备的时间
//								sendCommandsToBluetoothDevice(CmdType.CMD_SET_TIME);
//							}
//						}
////					}
//
//					@Override
//					public void onTextDialogCancel(int requestCode) {
//						disconnect(deviceAddress, true);
//					}
//				});
				
//				authDialog.setCanceledOnTouchOutside(false);
//				BaseActivity activity = (BaseActivity) ActivityUtil.getStackTopActivity();
//				authDialog.show(activity.getSupportFragmentManager(), "authDialog");
			}
		}
	}
	
	/**
	 * 解析蓝牙设备测量单位
	 * @param data
	 * @param cmdType
	 */
	private void parserUnitData(String data, int cmdType) {
		bluetoothData += data;
		
		if (bluetoothData.contains(ResponeDataType.UNIT_PRE) && data.endsWith(ResponeDataType._7D) && bluetoothData.length() >= 30) {
			// 过滤掉可能夹杂有的其他数据
			filterAllData();
			// 提取设备测量单位的信息
			bluetoothData = subStringData(bluetoothData, ResponeDataType.UNIT_PRE, ResponeDataType._7D);
			
			int length = bluetoothData.length();
			String temp = bluetoothData.substring(length - 12, length - 10);
			if (TextUtils.isEmpty(temp)) {
				unit = UNIT_UNKNOW;
				return;
			}
			
			if (temp.equals("11") || temp.equals("22")) {// 7B 01 20 01 10  12 66 00 05 11033311 00 0D0705037D 7B01200110AAAA000111 0E0F07047D
				if (temp.equals("11")) {
					unit = MG;
				} else if (temp.equals("22")) {
					unit = MMOL;
				}
				
				sendCommandsToBluetoothDevice(CmdType.CMD_SET_TIME);// 发送同步设置设备的时间命令
				
			} else {
				unit = UNIT_UNKNOW;
			}
			
			bluetoothData = "";
		}
	}
	
	/**
	 * 解析设置时间
	 * @param data
	 * @param cmdType
	 */
	private void parserTimeData(String data, int cmdType) {
		if (TextUtils.isEmpty(data)) {
			return;
		}
		
		bluetoothData += data;
		
		if (bluetoothData.contains(ResponeDataType.SET_TIME_SUCCESS) && data.endsWith(ResponeDataType._7D) && bluetoothData.length() >= 30) {
			// 过滤掉可能夹杂有的其他数据
			filterAllData();
			// 提取设备测量单位的信息
			bluetoothData = subStringData(bluetoothData, ResponeDataType.SET_TIME_SUCCESS, ResponeDataType._7D);
			
			int length = bluetoothData.length();
			String temp = bluetoothData.substring(length - 12, length - 10);
			if (TextUtils.isEmpty(temp)) {
				return;
			}
			
			if (temp.equals("11")) {// 设置时间成功
				UIBase.showToastShort(MyApplication.getTextString(R.string.find_bluetooth_service_success_text));
				sendCommandsToBluetoothDevice(CmdType.CMD_GET_MEASURE);// 发送实时测量命令
			}
			
			bluetoothData = "";
		}
	}
	
	/**
	 * 解析获取的历史数据
	 * @param deviceAddress
	 * @param data
	 * @param cmdType
	 */
	private void parserHistoryData(String deviceAddress, String data, int cmdType) {
		bluetoothData += data;
		
		if (bluetoothData.contains(ResponeDataType.HISTORY_END_PRE) && bluetoothData.contains(ResponeDataType.HISTORY_PRE) && data.endsWith(ResponeDataType._7D)) {
			// 过滤掉可能夹杂有的其他数据
			filterAllData();
			
			String[] temps = bluetoothData.split(ResponeDataType._7D);
			MyLog.i(TAG, "历史数据："+bluetoothData);
			if (null == temps || 0 == temps.length) {
				return;
			}
			
			historyData.clear();
			BluetoothEntry bluetoothEntry = mBluetoothService.getDeviceMap().get(deviceAddress);
			String deviceNo = "";
			if (null != bluetoothEntry) {
				deviceNo = bluetoothEntry.getPwd();
			}
			
			int length = temps.length;
			for (int i = 0; i < length; i++) {
				MyLog.i(TAG, "历史数据分组："+temps[i]);
				int tempLength = temps[i].length();
				if (tempLength > 40) {
					String bgType = temps[i].substring(tempLength - 10, tempLength - 8);// 11为血液  22为质控液
					if ("22".equals(bgType)) {// 是否过滤掉质控液测量的数据值
						continue;// 过滤掉
					}
					
					if ("11".equals(bgType)) {// 11为血液
						bgType = BgType.BLOOD;
					} else if ("22".equals(bgType)) {// 22为质控液
						bgType = BgType.CTRL;
					} else {
						bgType = BgType.DEFAULT;
					}
					
					String temp = temps[i].substring(tempLength - 26, tempLength - 12);
					BgMeasureRecordEntry bgMeasureRecordEntry = getBgMeasureRecordEntry(deviceNo, temp, bgType);
					if (null != bgMeasureRecordEntry) {
						historyData.add(bgMeasureRecordEntry);
					}
				}
			}
			RxBus.getInstance().send(new EventType("history_data"));
			bluetoothData = "";
			
			if (null != mOnBluetoothListener) {
				mOnBluetoothListener.bluetoothDeviceDataCallBack(historyData, cmdType);
			}
		} else if (bluetoothData.contains(ResponeDataType.HISTORY_END_PRE) && data.endsWith(ResponeDataType._7D)) {// 暂无历史数据
			bluetoothData = "";
			
			if (null != mOnBluetoothListener) {
				mOnBluetoothListener.bluetoothDeviceDataCallBack(null, cmdType);
			}
		}
	}
	
	/**
	 * 解析实时测量数据
	 * @param data
	 * @param cmdType
	 */
	private void parserMeasureData(String deviceAddress, String data, int cmdType) {
		System.err.println("实时测量原始数据："+data);
		bluetoothData += data;
		
		// 上次测量结果状态提示和过滤
		getMeasureStateAndFilter(ResponeDataType.LAST_TIME_MEASURE_STATE_PRE, ResponeDataType._7D, "unToast_10");
		// 已插入试纸提示和过滤
		getMeasureStateAndFilter(ResponeDataType.INSERT_BLOOD_GLUCOSE_SENSOR_PRE, ResponeDataType._7D, "11");
		// 等待加血提示和过滤
		getMeasureStateAndFilter(ResponeDataType.WAITING_TO_ADD_BLOOD_PRE, ResponeDataType._7D, "22");
		// 完成加血提示和过滤
		getMeasureStateAndFilter(ResponeDataType.COMPLETED_ADD_BLOOD_PRE, ResponeDataType._7D, "33");
		// 测量异常报警提示和过滤
		getMeasureStateAndFilter(ResponeDataType.ERROR_ALARM_PRE, ResponeDataType._7D, "55");
		
		// 解析血糖值和血糖测量时间
		if (bluetoothData.contains(ResponeDataType.MEASURE_RESULT_VALUE_PRE) && bluetoothData.contains(ResponeDataType.MEASURE_RESULT_TIME_PRE) && bluetoothData.endsWith(ResponeDataType._7D)) {
			System.err.println("实时测量提取后的数据："+bluetoothData);
			int startValue = bluetoothData.indexOf(ResponeDataType.MEASURE_RESULT_VALUE_PRE);
			int endValue = bluetoothData.indexOf(ResponeDataType._7D, startValue) + 2;
			
			int startTime = bluetoothData.indexOf(ResponeDataType.MEASURE_RESULT_TIME_PRE);
			int endTime = bluetoothData.indexOf(ResponeDataType._7D, startTime) + 2;
			
			String dataSrcValue = bluetoothData.substring(startValue, endValue);
			String dataSrcTime = bluetoothData.substring(startTime, endTime);
			
			int lengthValue = dataSrcValue.length();
			int lengthTime = dataSrcTime.length();
			
			if (lengthValue > 20 && lengthTime > 22) {
				String tempValue = dataSrcValue.substring(lengthValue - 20, lengthValue - 10);
				String tempTime = dataSrcTime.substring(lengthTime - 22, lengthTime - 10);
				
				if (!TextUtils.isEmpty(tempValue) && !TextUtils.isEmpty(tempTime)) {
					String stateCode = tempValue.substring(0, 2);
					
					if (stateCode.equals("44")) {
						String bgType = tempValue.substring(6, 8);// 11为血液  22为质控液
						if ("22".equals(bgType)) {// 是否过滤掉质控液测量的数据值
							return;// 过滤掉
						}

						if ("11".equals(bgType)) {// 11为血液
							bgType = BgType.BLOOD;
						} else if ("22".equals(bgType)) {// 22为质控液
							bgType = BgType.CTRL;
						} else {
							bgType = BgType.DEFAULT;
						}
						
						String value = getBgValue(tempValue.substring(2, 4), tempValue.substring(4, 6));
						if (TextUtils.isEmpty(value)) {
							return;
						}
						
						float bgValue = Float.parseFloat(value);
//						if (MG.equals(unit)) {
//							bgValue = Float.parseFloat(df.format(bgValue / MMOL_MG_PARSER_VALUE));
//						} else 
						if (MMOL.equals(unit)) {
							bgValue = Float.parseFloat(df.format(bgValue / MMOL_PARSER_VALUE));
						}
						
						BluetoothEntry bluetoothEntry = mBluetoothService.getDeviceMap().get(deviceAddress);
						String deviceNo = "";
						if (null != bluetoothEntry) {
							deviceNo = bluetoothEntry.getPwd();
						}
						
						long measureTime = parserMeasureTime(tempTime);
						String timeType = TimeTypeMathUtil.getTimeTypeByCurrentTime(MyApplication.getInstance(), DateTimeUtil.getStringByFormat(measureTime, DateTimeUtil.dateFormatHM));
						
						BgMeasureRecordEntry bgMeasureRecordEntry = new BgMeasureRecordEntry();
						bgMeasureRecordEntry.setValue(bgValue);
						bgMeasureRecordEntry.setTimeType(timeType);
						bgMeasureRecordEntry.setMeasureTime(measureTime);
						bgMeasureRecordEntry.setMeasureUnit(unit);
						if (!TextUtils.isEmpty(deviceNo)) {
							bgMeasureRecordEntry.setDeviceNo(deviceNo);
						}
						bgMeasureRecordEntry.setBgType(bgType);
						
						bluetoothData = "";
						
						if (null != mOnBluetoothListener) {
							mOnBluetoothListener.bluetoothDeviceDataCallBack(Arrays.asList(bgMeasureRecordEntry), cmdType);
						}
					}
				}
			}
			RxBus.getInstance().send(new EventType("now_data"));
		}
	}

	private void getMeasureStateAndFilter(String startStr, String endStr, String code) {
		if (bluetoothData.contains(startStr) && bluetoothData.contains(endStr)) {
			int start = bluetoothData.indexOf(startStr);
			int end = bluetoothData.indexOf(endStr, start) + 2;
			
			String dataSrc = bluetoothData.substring(start, end);
			int length = dataSrc.length();
			if (length > 20) {
				String temp = dataSrc.substring(length - 20, length - 10);
				if (!TextUtils.isEmpty(temp)) {
					String stateCode = temp.substring(0, 2);
					
					if (stateCode.equals(code)) {
						//显示不同信息
//						UIBase.showToastShort(toastText);
					}
				}
			}
			
			bluetoothData = bluetoothData.replace(dataSrc, "");
		}
	}
	
	/**
	 * 16进制位转10进制
	 * @param highByte 高位
	 * @param lowByte 低位
	 * @return
	 */
	private String getBgValue(String highByte, String lowByte) {
		if (TextUtils.isEmpty(highByte) || TextUtils.isEmpty(lowByte)) {
			return "";
		}
		// 血糖值计算(高位乘以100后，加上低位的值即为血糖值)
		int high = Integer.valueOf(highByte, 16);
		int down = Integer.valueOf(lowByte, 16);
		int result = (high * 100) + down;
		
		return String.valueOf(result);
	}
	
	/**
	 * 10进制转16进制
	 * @param intStr
	 * @return
	 */
	private int intToHex(String intStr) {
		return Integer.valueOf(intStr, 16);
	}

	/**
	 * 获取血糖对象
	 * @param deviceNo
	 * @param data
	 * @param bgType
	 * @return
	 */
	private BgMeasureRecordEntry getBgMeasureRecordEntry(String deviceNo, String data, String bgType) {
		BgMeasureRecordEntry bgMeasureRecordEntry = null;
		if (data.length() == 14) {
			long measureTime = parserMeasureTime(data);

			String value = getBgValue(data.substring(10, 12), data.substring(12, 14));
			float bgValue = Float.parseFloat(value);
//			if (MG.equals(unit)) {
//				bgValue = Float.parseFloat(df.format(bgValue / MMOL_MG_PARSER_VALUE));
//			} else 
			if (MMOL.equals(unit)) {
				bgValue = Float.parseFloat(df.format(bgValue / MMOL_PARSER_VALUE));
			}
			
			String timeType = TimeTypeMathUtil.getTimeTypeByCurrentTime(MyApplication.getInstance(), DateTimeUtil.getStringByFormat(measureTime, DateTimeUtil.dateFormatHM));
			bgMeasureRecordEntry = new BgMeasureRecordEntry();
			bgMeasureRecordEntry.setValue(bgValue);
			bgMeasureRecordEntry.setTimeType(timeType);
			bgMeasureRecordEntry.setMeasureTime(measureTime);
			bgMeasureRecordEntry.setMeasureUnit(unit);
			if (!TextUtils.isEmpty(deviceNo)) {
				bgMeasureRecordEntry.setDeviceNo(deviceNo);
			}
			bgMeasureRecordEntry.setBgType(bgType);
		}
		return bgMeasureRecordEntry;
	}
	
	private long parserMeasureTime(String data) {
		if (TextUtils.isEmpty(data)) {
			return System.currentTimeMillis();
		}
		
		int length = data.length();
		
		int year = Integer.parseInt(data.substring(0, 2), 16) + 2000;// 年份
		int month = Integer.parseInt(data.substring(2, 4), 16);// 月
		int day = Integer.parseInt(data.substring(4, 6), 16);// 日
		int hour = Integer.parseInt(data.substring(6, 8), 16);// 时
		int minute = Integer.parseInt(data.substring(8, 10), 16);// 分
		boolean flag = length >= 12;
		int second = flag ? Integer.parseInt(data.substring(10, 12), 16) : 0;// 秒
		String time = year + "-" + String.format("%02d", month) + "-" + String.format("%02d", day) + " " + String.format("%02d", hour) + ":" + String.format("%02d", minute) + (flag ? (":" + String.format("%02d", second)) : "");
		
		return DateTimeUtil.getMillisecondsByFormat(time, flag ? DateTimeUtil.dateFormatYMDHMS : DateTimeUtil.dateFormatYMDHM);
	}
	
	public void sendMessage(int msgCode) {
		if (null == mBluetoothService) {
			return;
		}
		
		if (isServiceRunning) {
			mBluetoothService.sendMessage(msgCode);
		}
	}

	public List<BluetoothEntry> getBluetoothDeviceList() {
		if (!isServiceRunning || null == mBluetoothService) {
			return null;
		}
		
		return mBluetoothService.getBluetoothDeviceList();
	}
	
	public Map<String, BluetoothEntry> getDeviceMap() {
		if (!isServiceRunning || null == mBluetoothService) {
			return null;
		}
		
		return mBluetoothService.getDeviceMap();
	}

	public boolean isExitConnected() {
		if (!isServiceRunning || null == mBluetoothService) {
			return false;
		}
		
		return mBluetoothService.isExitConnected();
	}
	
	public int getBluetoothDeviceSearchState() {
		if (!isServiceRunning || null == mBluetoothService) {
			return BluetoothSearchState.STOPPED;
		}
		
		return mBluetoothService.isScanning() ? BluetoothSearchState.SEARCHING : BluetoothSearchState.STOPPED;
	}
	
	/**
	 * 设置自己的蓝牙可见性即自己的蓝牙可被其他设备监测到
	 * @param mActivity
	 */
	public void openBluetoothDiscoverable(BaseActivity mActivity) {
		Intent discoverIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
		mActivity.startActivityForResult(discoverIntent, REQUEST_CODE_BLUETOOTH_DISCOVERABLE);
	}
	
	/**
	 * 检查你的蓝牙设备是否支持BLE通信功能
	 */
	public boolean checkBluetoothIsSupportBLE() {
		boolean isSupportedBLE = false;
		
		isSupportedBLE = MyApplication.getInstance().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
		if (!isSupportedBLE) {
			UIBase.showToastLong(MyApplication.getInstance().getString(R.string.bluetooth_not_support_ble));
		}
		
		return isSupportedBLE;
	}
	
	public static void pairDevice(BluetoothDevice device) {
		Method method;
		try {
			method = device.getClass().getMethod("createBond");
			method.invoke(device);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void unPairDevice(BluetoothDevice device) {
		Method method;
		try {
			method = device.getClass().getMethod("removeBond");
			method.invoke(device);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 利用反射机制修改蓝牙可见性时间不超过300秒的限制问题
	 * @param timeout
	 */
	public static void setDiscoverableTimeout(int timeout) {
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		try {
			Method setDiscoverableTimeout = BluetoothAdapter.class.getMethod("setDiscoverableTimeout", int.class);
			Method setScanMode = BluetoothAdapter.class.getMethod("setScanMode", int.class, int.class);
			
			setDiscoverableTimeout.setAccessible(true);
			setScanMode.setAccessible(true);
			
			setDiscoverableTimeout.invoke(adapter, timeout);
			setScanMode.invoke(adapter, BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE, timeout);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 利用反射机制修改蓝牙可见性的关闭功能即1秒钟后关闭蓝牙可见性
	 */
	public static void closeDiscoverableTimeout() {
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		try {
			Method setDiscoverableTimeout = BluetoothAdapter.class.getMethod("setDiscoverableTimeout", int.class);
			Method setScanMode = BluetoothAdapter.class.getMethod("setScanMode", int.class, int.class);
			
			setDiscoverableTimeout.setAccessible(true);
			setScanMode.setAccessible(true);
			
			setDiscoverableTimeout.invoke(adapter, 1);
			setScanMode.invoke(adapter, BluetoothAdapter.SCAN_MODE_CONNECTABLE, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private OnBluetoothListener mOnBluetoothListener;
	
	public void setOnBluetoothListener(OnBluetoothListener listener) {
		mOnBluetoothListener = listener;
	}
	
	public interface OnBluetoothListener {
		
		void foundBluetoothDevice();
		
		void bluetoothDeviceConnectState(String deviceAddress, Integer state);
		
		void bluetoothDeviceSearchState(int state);
		
		void bluetoothDeviceDataCallBack(List<BgMeasureRecordEntry> bgMeasureRecordEntries, int cmdType);
	}
	
	/**
	 * 蓝牙设备发命令类型
	 */
	public interface CmdType {
		/** 默认为未知命令 **/
		static final int CMD_DEFAULT = 0;
		/** 获取蓝牙设备S/N串号 **/
		static final int CMD_GET_SN = 1;
		/** 获取蓝牙设备测量单位 **/
		static final int CMD_GET_UNIT = 2;
		/** 设置蓝牙设备时间 **/
		static final int CMD_SET_TIME = 3;
		/** 获取蓝牙设备实时测量数据 **/
		static final int CMD_GET_MEASURE = 4;
		/** 回复蓝牙设备实时测量数据接收完毕 **/
		static final int CMD_REPLY_MEASURE = 5;
		/** 删除已接收的实时测量数据 **/
		static final int CMD_DELETE_MEASURE = 6;
		/** 获取蓝牙设备历史数据 **/
		static final int CMD_GET_HISTORY = 7;
		/** 删除已接收的蓝牙设备历史数据 **/
		static final int CMD_DELETE_HISTORY = 8;
	}
}