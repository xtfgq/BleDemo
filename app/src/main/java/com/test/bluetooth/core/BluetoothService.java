package com.test.bluetooth.core;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;

import com.bluetooth.bluetoothdemo.R;
import com.bluetooth.bluetoothdemo.bean.RxBus;
import com.bluetooth.bluetoothdemo.type.EventType;
import com.test.bluetooth.MyApplication;

import com.test.bluetooth.entry.BluetoothEntry;
import com.test.bluetooth.entry.BluetoothEntry.BluetoothConnectState;
import com.test.bluetooth.utils.MyLog;
import com.test.bluetooth.utils.TypeConversionUtil;
import com.test.bluetooth.utils.VersionCompatibilityUtil;

/**
 * 自定义蓝牙服务
 * @author 梁佳旺
 */
public class BluetoothService extends Service implements IBleScanCallback {
	private static final String TAG = BluetoothService.class.getSimpleName();
	
	/** 配置蓝牙设备过滤器，搜索蓝牙设备的只搜索所列名称的蓝牙设备，其他设备过滤掉 **/
	public static final List<String> BLUETOOTH_DEVICE_NAME_FILTER = Arrays.asList(new String[]{ "BLE-glucose", "BLE-Glucowell", "BLE-Vivachek" });
	// 经络仪血糖的服务和特征值
	private static final UUID UUID_BLE = UUID.fromString("0003cdd0-0000-1000-8000-00805f9b0131");
	private static final UUID UUID_READ_WRITE = UUID.fromString("0003cdd2-0000-1000-8000-00805f9b0131");
	private static final UUID UUID_NOTIFY = UUID.fromString("0003cdd1-0000-1000-8000-00805f9b0131");
	// the descriptor of battery characteristic(battery service)
	private static final UUID UUID_CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
	
	/** 蓝牙搜索按钮状态 **/
	public static final int MSG_BLUETOOTH_SEARCH_STATE = 0x01;
	/** 注册BleService **/
	public static final int MSG_REGISTER_SERVICE_SUCCESS = 0x02;
	/** BLE蓝牙设备被发现  **/
	public static final int MSG_FOUND_BLUETOOTH_DEVICE = 0x03;
	/** BLE蓝牙不存在 **/
	public static final int MSG_NO_BLUETOOTH = 0x04;
	/** 开启蓝牙可见性 **/
	public static final int MSG_OPEN_BLUETOOTH_DISCOVERABLE = 0x05;
	/** 未开启蓝牙设备 **/
	public static final int MSG_UNOPENED_BLUETOOTH = 0x06;
	/** 检查蓝牙是否支持BLE **/
	public static final int MSG_CHECK_BLUETOOTH_IS_SUPPORT_BLE = 0x07;
	/** 手动执行连接蓝牙操作 **/
	public static final int MSG_CONNECT_BLUETOOTH = 0x08;
	/** 连接蓝牙成功 **/
	public static final int MSG_CONNECT_BLUETOOTH_SUCCESS = 0x09;
	/** 重新连接蓝牙 **/
	public static final int MSG_RECONNECT_BLUETOOTH = 0x10;
	/** 断开蓝牙连接**/
	public static final int MSG_DISCONNECT_BLUETOOTH = 0x11;
	/** 断开蓝牙的连接成功**/
	public static final int MSG_DISCONNECT_BLUETOOTH_SUCCESS = 0x12;
	/** 查找发现蓝牙服务成功 **/
	public static final int MSG_SERVICE_DISCOVERED_SUCCESS = 0x13;
	/** 有可用的数据 **/
	public static final int MSG_DATA_AVAILABLE = 0x14;
	/** 执行关闭和释放蓝牙资源操作**/
	public static final int MSG_CLOSE_BLUETOOTH = 0x15;
	
	public static final String BLUETOOTH_DEVICE_ENTRY = "bluetoothDeviceEntry";
	/** 蓝牙数据 **/
	public static final String BLUETOOTH_DATA = "bluetoothData";
	/** 正在使用中的蓝牙设备的mac地址 **/
	public static final String BLUETOOTH_ADDRESS = "bluetoothAddress";
	/** 是否存在于搜索列表中，true表示存在，false从列表中剔除掉 **/
	public static final String IS_EXIT_IN_LIST = "isExitInList";
	
	/** 搜索扫描蓝牙设备最大时长 **/
	private static final long SCAN_PERIOD = 1 * 40000;
	/** 是否正在扫描蓝牙设备 **/
	private boolean isScanning = false;
	
	public boolean isScanning() {
		return isScanning;
	}

	private String currConnectedBluetoothDeviceAddress;
	
	private Handler serviceHandler;
	
	private BluetoothAdapter bluetoothAdapter;
	private BluetoothScanner mBluetoothScanner;
	private BluetoothGatt mGatt;
	
	private BluetoothGattCallback mGattCallback = null;
	
	private Map<String, BluetoothEntry> mDeviceMap = new ConcurrentHashMap<String, BluetoothEntry>();
	
	public Map<String, BluetoothEntry> getDeviceMap() {
		return mDeviceMap;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		bluetoothAdapter = VersionCompatibilityUtil.getBluetoothAdapter();
		
		/**PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(), 0);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
		Notification notification = builder.setTicker(getString(R.string.glucowell_bluetooth_service_text))
											.setContentTitle("")
											.setContentText(getString(R.string.glucowell_bluetooth_service_text))
											.setWhen(System.currentTimeMillis())
											.setContentIntent(pendingIntent)
											.setPriority(Notification.PRIORITY_HIGH)
											.setSmallIcon(R.drawable.ic_icon)
											.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_icon))
											.build();
		 */
		
//		Notification notification = new Notification(R.drawable.ic_icon, getString(R.string.glucowell_bluetooth_service_text), System.currentTimeMillis());
//		notification.setLatestEventInfo(this, getString(R.string.glucowell_bluetooth_service_text), "", null);//pendingIntent);
		
//		startForeground(11, notification);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	private LocalBinder mBinder = new LocalBinder();
	
	public class LocalBinder extends Binder {
		
		public BluetoothService getService() {
			return BluetoothService.this;
		}
		
		public void setServiceHandler(Handler handler) {
			if (null == handler) {
				serviceHandler = new Handler();
			} else {
				serviceHandler = handler;
			}
		}
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}
	
	/**
	 * 开启蓝牙功能
	 */
	public void openBluetooth() {
		// 用户设备没有蓝牙硬件，不支持蓝牙功能
		if (null == bluetoothAdapter) {
			sendMessage(MSG_NO_BLUETOOTH);
		} else {
			if (bluetoothAdapter.isEnabled()) {// 判断蓝牙是否开启，已开启
//				int scanMode = bluetoothAdapter.getScanMode();
//				if (scanMode != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {// 判断是否已开启蓝牙可见性，未开启则开启蓝牙可见性
//					sendBroadcast(ACTION_OPEN_BLUETOOTH_DISCOVERABLE);
//				} else {
					// 检查你的蓝牙设备是否支持BLE通信功能
				sendMessage(MSG_CHECK_BLUETOOTH_IS_SUPPORT_BLE);
//				}
			} else {
				// 蓝牙未开启
				sendMessage(MSG_UNOPENED_BLUETOOTH);
			}
		}
	}
	
	/**
	 * 开始扫描蓝牙
	 */
	public void startBleScan() {
		if (isScanning) {
			stopBleScan();
		} 
//		else {
			serviceHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (isScanning) {
						stopBleScan();
					}
				}
			}, SCAN_PERIOD);
			
			if (null == mBluetoothScanner) {
				mBluetoothScanner = new BluetoothScanner(bluetoothAdapter, this);
			}
			
			mBluetoothScanner.startBleScan();
			isScanning = true;
			
			sendMessage(MSG_BLUETOOTH_SEARCH_STATE);
//		}
	}
	
	/**
	 * 蓝牙BLE扫描回调方法
	 */
	@Override
	public void onBleScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
		if (null == device) {
			return;
		}
		
		String deviceName = device.getName();
		boolean isExistValidDevice = false;
		//我暂时先把过滤去掉

		for (String filterName : BLUETOOTH_DEVICE_NAME_FILTER) {
			if (filterName.equalsIgnoreCase(deviceName)) {
				isExistValidDevice = true;
				break;
			}
		}
		if (!isExistValidDevice) {
			return;
		}

		String deviceAddress = device.getAddress();
		BluetoothEntry cacheBluetoothEntry = MyApplication.getCacheBluetoothEntry();
		if (mDeviceMap.containsKey(deviceAddress)) {
			
		} else {
			MyLog.i(TAG, "蓝牙设备名称：" + deviceName + "，MAC地址：" + deviceAddress);
			// 发现蓝牙设备
			BluetoothEntry bluetoothEntry = new BluetoothEntry();
			bluetoothEntry.setName(deviceName);
			bluetoothEntry.setAddress(deviceAddress);
			bluetoothEntry.setConnectState(BluetoothConnectState.UNKNOWN);
			bluetoothEntry.setExitInList(true);
			
			if (null != cacheBluetoothEntry && deviceAddress.equals(cacheBluetoothEntry.getAddress())) {
				bluetoothEntry.setPwd(cacheBluetoothEntry.getPwd());
				bluetoothEntry.setOpen(cacheBluetoothEntry.isOpen());
				bluetoothEntry.setConnectState(cacheBluetoothEntry.getConnectState());
			}
			
			mDeviceMap.put(deviceAddress, bluetoothEntry);
			
			sendMessage(MSG_FOUND_BLUETOOTH_DEVICE, BLUETOOTH_DEVICE_ENTRY, bluetoothEntry);

			MyApplication.setCacheBluetoothEntry(bluetoothEntry);
//			connect(deviceAddress);
			MyLog.i(TAG, "搜索到的蓝牙设备信息：" + mDeviceMap.toString());

		}
	}
	
	@Override
	public void onBleScanFailure(int errorCode) {
		stopBleScan();
	}
	
	/**
	 * 停止扫描蓝牙
	 */
	public void stopBleScan() {
		if (null == mBluetoothScanner) {
			mBluetoothScanner = new BluetoothScanner(bluetoothAdapter, this);
		}
		
		mBluetoothScanner.stopBleScan();
		isScanning = false;
		
		sendMessage(MSG_BLUETOOTH_SEARCH_STATE);
	}
	
	/**
	 * 连接蓝牙
	 * @param deviceAddress 蓝牙设备MAC地址
	 * @return true 连接成功；false 连接失败
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	public int connect(String deviceAddress) {
		if (null == bluetoothAdapter || TextUtils.isEmpty(deviceAddress)) {
			updateDataState(deviceAddress, BluetoothConnectState.UNKNOWN);
			return BluetoothConnectState.UNKNOWN;
		}

		// Previously connected device. Try to reconnect.
		if (deviceAddress.equals(currConnectedBluetoothDeviceAddress) && null != mGatt) {
			if (mGatt.connect()) {// Trying to use an existing mBluetoothGatt for connection.
				MyLog.i(TAG, "正在试图重连蓝牙设备");
				updateDataState(deviceAddress, BluetoothConnectState.CONNECTING);
				return BluetoothConnectState.CONNECTING;
			} else {
				MyLog.i(TAG, "--------------------试图重连蓝牙设备失败--------------------------");
				updateDataState(deviceAddress, BluetoothConnectState.UNKNOWN);
				return BluetoothConnectState.UNKNOWN;
			}
		}
		
		BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
		if (null == device) {
			MyLog.i(TAG, "无蓝牙设备，无法连接");
			updateDataState(deviceAddress, BluetoothConnectState.UNKNOWN);
			return BluetoothConnectState.UNKNOWN;
		}
		
		if (null == mGattCallback) {
			mGattCallback = getBluetoothGattCallback();
		}
		updateDataState(deviceAddress, BluetoothConnectState.CONNECTING);
		mGatt = device.connectGatt(this, false, mGattCallback);
//		refreshBluetoothDeviceCache(mGatt);
		currConnectedBluetoothDeviceAddress = deviceAddress;
		MyLog.i(TAG, "------------------------正在尝试连接蓝牙设备------------------------------------");
		
		return BluetoothConnectState.CONNECTING;
	}
	
	/**
	 * 查找发现蓝牙服务
	 * @return
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	public boolean discoverServices(String deviceAddress) {
		if (null == mGatt) {
			updateDataState(deviceAddress, BluetoothConnectState.UNKNOWN);
			return false;
		} else {
			return mGatt.discoverServices();
		}
	}
	
	/**
	 * 断开蓝牙连接
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	public void disconnect(String deviceAddress, boolean isExitInList) {
		if (null == bluetoothAdapter || null == mGatt) {
			return;
		}
		
		if (!TextUtils.isEmpty(deviceAddress)) {
			BluetoothEntry bluetoothEntry = mDeviceMap.get(deviceAddress);
			if (null != bluetoothEntry) {
				bluetoothEntry.setExitInList(isExitInList);
			}
		}
		
		mGatt.disconnect();
	}

	public void close() {
		if (null == mGatt) {
			return;
		}
		mGatt.close();
		mGatt = null;
		currConnectedBluetoothDeviceAddress = null;
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	private BluetoothGattCallback getBluetoothGattCallback() {
		return new BluetoothGattCallback() {
			@Override
			public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
				String deviceAddress = null;
				if (null != gatt && null != gatt.getDevice()) {
					deviceAddress = gatt.getDevice().getAddress();
				}
				
				String currState = newState == BluetoothProfile.STATE_CONNECTED ? "已连接成功" : (newState == BluetoothProfile.STATE_DISCONNECTED ? "已断开连接" : "");
				MyLog.i(TAG, "onConnectionStateChange--->当前连接状态："+currState+"，设备MAC地址："+deviceAddress);
				
				switch (newState) {
				case BluetoothProfile.STATE_CONNECTED:// 已连接成功
					String deviceName = gatt.getDevice().getName();

					boolean isExistValidDevice = false;
					for (String filterName : BLUETOOTH_DEVICE_NAME_FILTER) {
						if (filterName.equalsIgnoreCase(deviceName)) {
							isExistValidDevice = true;
							break;
						}
					}
					if (isExistValidDevice) {
						MyLog.i(TAG, "已连接成功---->蓝牙设备名称：" + deviceName + "，MAC地址：" + deviceAddress);

						BluetoothEntry cacheBluetoothEntry = MyApplication.getCacheBluetoothEntry();

						if (mDeviceMap.containsKey(deviceAddress)) {
							
						} else {
							// 发现蓝牙设备
							BluetoothEntry bluetoothEntry = new BluetoothEntry();
							bluetoothEntry.setName(deviceName);
							bluetoothEntry.setAddress(deviceAddress);
							bluetoothEntry.setConnectState(BluetoothConnectState.UNKNOWN);
							bluetoothEntry.setExitInList(true);
							
							if (null != cacheBluetoothEntry && deviceAddress.equals(cacheBluetoothEntry.getAddress())) {
								bluetoothEntry.setPwd(cacheBluetoothEntry.getPwd());
								bluetoothEntry.setOpen(cacheBluetoothEntry.isOpen());
								bluetoothEntry.setConnectState(cacheBluetoothEntry.getConnectState());
							}
							
							mDeviceMap.put(deviceAddress, bluetoothEntry);
							MyApplication.setCacheBluetoothEntry(bluetoothEntry);

						}
					}

					sendMessage(MSG_CONNECT_BLUETOOTH_SUCCESS, gatt);
					break;
					
				case BluetoothProfile.STATE_DISCONNECTED:// 已断开连接
					BluetoothEntry bluetoothEntry = mDeviceMap.get(deviceAddress);
					if (null == bluetoothEntry) {
						MyLog.i(TAG, "-----早就断开连接了，现在才提示-----");
						return;
					}
					
					final boolean isExitConnected = isExitConnected();
					
					updateDataState(deviceAddress, BluetoothConnectState.DISCONNECT);
					
					if (!bluetoothEntry.isExitInList() || isExitConnected) {
						MyLog.i(TAG, "-----发送断开连接的消息----并剔除掉-");
						mDeviceMap.remove(deviceAddress);
					}
					
					if (MyApplication.isSelectedBLE() && bluetoothEntry.isOpen()) {
						MyApplication.setCacheBluetoothEntry(bluetoothEntry);
						sendMessageDelayed(MSG_RECONNECT_BLUETOOTH, deviceAddress, 2500);
					}
					
					isWriting = false;
					writeQueue.clear();
					MyLog.i(TAG, "-----发送断开连接的消息-----");
					sendMessage(MSG_DISCONNECT_BLUETOOTH_SUCCESS, gatt);
					break;
				}
			}
			
			@Override
			public void onServicesDiscovered(BluetoothGatt gatt, int status) {
				String deviceAddress = gatt == null ? null : (gatt.getDevice() == null ? null : gatt.getDevice().getAddress());
				MyLog.i(TAG, "onServicesDiscovered--->"+((status == BluetoothGatt.GATT_SUCCESS) ? "查找发现蓝牙服务成功" : "查找发现蓝牙服务失败") + ", MAC地址=" + deviceAddress);
				if (status == BluetoothGatt.GATT_SUCCESS) {
					sendMessage(MSG_SERVICE_DISCOVERED_SUCCESS, gatt);
				} else {
					refreshBluetoothDeviceCache(mGatt);
				}
			}
			
			@Override
			public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
//				String cmd = TypeConversionUtil.bytes2HexString(characteristic.getValue());
//				String cmdText = getCmdTextString(cmd);
//				MyLog.e(TAG, cmdText + "：" + cmd);
				onNextWriteCharacteristicOrDescriptor();
			}

			@Override
			public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			}
			
			@Override
			public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
				List<BluetoothGattCharacteristic> gattCharacteristics = characteristic.getService().getCharacteristics();
//				for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
//					byte[] bytes = gattCharacteristic.getValue();
//					if (null != bytes) {
//						MyLog.i(TAG, "onCharacteristicChanged--->"+TypeConversionUtil.bytes2HexString(bytes));
//					}
//				}
				
				if (null != gattCharacteristics && !gattCharacteristics.isEmpty()) {
					BluetoothGattCharacteristic gattCharacteristicValue = gattCharacteristics.get(0);
					byte[] bytesValue = gattCharacteristicValue.getValue();
					String value = "";
					if (null != bytesValue) {
						value = TypeConversionUtil.bytes2HexString(bytesValue);
					}
					
//					String cmdText = "";
//					if (gattCharacteristics.size() > 1) {
//						BluetoothGattCharacteristic gattCharacteristicCmd = gattCharacteristics.get(1);
//						byte[] bytesCmd = gattCharacteristicCmd.getValue();
//						if (null != bytesCmd) {
//							String cmd = TypeConversionUtil.bytes2HexString(bytesCmd);
//							cmdText = getCmdTextString(cmd);
//						}
//					}
//					MyLog.i(TAG, cmdText + "后的反馈数据：" + value);
					MyLog.i(TAG, "蓝牙设备反馈数据：" + value);
				}
				
				sendMessage(MSG_DATA_AVAILABLE, characteristic, gatt);
				onNextWriteCharacteristicOrDescriptor();
			}
			
			@Override
			public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
				onNextWriteCharacteristicOrDescriptor();
			}
			
			@Override
			public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
			}
			
			private String getCmdTextString(String cmd) {
				String cmdTextString = "";
				if (cmd.equalsIgnoreCase("7B0110012077550000010B0B047D")) {
					cmdTextString = "发送获取该血糖仪设备的S/N串号命令";
					
				} else if (cmd.equalsIgnoreCase("7B01100120AA55000002010D087D")) {
					cmdTextString = "发送获取该血糖仪所使用的测量单位命令";
					
				} else if (cmd.startsWith("7B0110012044660006")) {
					cmdTextString = "发送时间命令到血糖仪上进行时间同步设置";
					
				} else if (cmd.equalsIgnoreCase("7B0110012012550000000507087D")) {
					cmdTextString = "发送测量血糖的命令即实时测量命令以开通血糖仪的实时测量功能";
					
				} else if (cmd.equalsIgnoreCase("7B01100120DD550000030A060C7D")) {
					cmdTextString = "发送获取设备上的历史测量数据的命令";
					
				}
				return cmdTextString;
			}

			@Override
			public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
				super.onReliableWriteCompleted(gatt, status);
				MyLog.i(TAG, "::::::::写入完成：：：：：：：：：：：：：：：：" + status);
			}
		};
	}

	public void sendMessage(int msgCode) {
		Message msg = serviceHandler.obtainMessage();
		msg.what = msgCode;
		
		serviceHandler.sendMessage(msg);
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	public void sendMessage(int msgCode, BluetoothGatt gatt) {
		Bundle bundle = new Bundle();
		if (null != gatt && null != gatt.getDevice()) {
			bundle.putString(BLUETOOTH_ADDRESS, gatt.getDevice().getAddress());
		}
		
		Message msg = serviceHandler.obtainMessage();
		msg.what = msgCode;
		msg.setData(bundle);
		
		serviceHandler.sendMessage(msg);
	}
	
	public void sendMessage(int msgCode, String deviceAddress) {
		Bundle bundle = new Bundle();
		if (!TextUtils.isEmpty(deviceAddress)) {
			bundle.putString(BLUETOOTH_ADDRESS, deviceAddress);
		}
		
		Message msg = serviceHandler.obtainMessage();
		msg.what = msgCode;
		msg.setData(bundle);
		
		serviceHandler.sendMessage(msg);
	}
	
	public void sendMessage(int msgCode, String deviceAddress, boolean isExitInList) {
		Bundle bundle = new Bundle();
		if (!TextUtils.isEmpty(deviceAddress)) {
			bundle.putString(BLUETOOTH_ADDRESS, deviceAddress);
		}
		bundle.putBoolean(IS_EXIT_IN_LIST, isExitInList);
		
		Message msg = serviceHandler.obtainMessage();
		msg.what = msgCode;
		msg.setData(bundle);
		
		serviceHandler.sendMessage(msg);
	}
	
	public void sendMessage(int msgCode, String key, BluetoothEntry value) {
		Bundle bundle = new Bundle();
		bundle.putSerializable(key, value);
		
		Message msg = serviceHandler.obtainMessage();
		msg.what = msgCode;
		msg.setData(bundle);
		
		serviceHandler.sendMessage(msg);
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	public void sendMessage(int msgCode, BluetoothGattCharacteristic characteristic, BluetoothGatt gatt) {
		if (null == characteristic || null == gatt || null == gatt.getDevice()) {
			return;
		}
		
		byte[] data = characteristic.getValue();
		if (null == data || data.length == 0) {
			return;
		}
		
		StringBuilder stringBuilder = new StringBuilder(data.length);// StringBuilder非线程安全，执行速度最快
		for (byte byteChar : data) {
			// 以十六进制输出,2为指定的输出字段的宽度.如果位数小于2,则左端补0
			stringBuilder.append(String.format("%02X", byteChar));// "%02X"2位十六进制(大写),以FFFF形式解析数据(注意有无空格)
		}
		
		Bundle bundle = new Bundle();
		bundle.putString(BLUETOOTH_DATA, stringBuilder.toString());
		bundle.putString(BLUETOOTH_ADDRESS, gatt.getDevice().getAddress());
		
		Message msg = serviceHandler.obtainMessage();
		msg.what = msgCode;
		msg.setData(bundle);
		
		serviceHandler.sendMessage(msg);
	}

	public void sendMessageDelayed(int msgCode, long delayMillis) {
		Message msg = serviceHandler.obtainMessage();
		msg.what = msgCode;
		
		serviceHandler.sendMessageDelayed(msg, delayMillis);
	}
	
	public void sendMessageDelayed(int msgCode, String deviceAddress, long delayMillis) {
		Bundle bundle = new Bundle();
		bundle.putString(BLUETOOTH_ADDRESS, deviceAddress);
		
		Message msg = serviceHandler.obtainMessage();
		msg.what = msgCode;
		msg.setData(bundle);
		
		serviceHandler.sendMessageDelayed(msg, delayMillis);
	}
	
	/**
	 * 发送指令到蓝牙设备上并时刻监听蓝牙设备回复的数据和信息
	 * @param value 指令字节数组
	 * @return true 发送指令成功；false 发送指令失败
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	public synchronized boolean sendCommandsToBluetoothDevice(byte[] value, int cmdType) {
		BluetoothGattService gattService = getSupportedGattServices(UUID_BLE);
		if (null == gattService) {
			return false;
		}
		
		BluetoothGattCharacteristic mCharacteristic = gattService.getCharacteristic(UUID_READ_WRITE);
		BluetoothGattCharacteristic mCharacteristicNotify = gattService.getCharacteristic(UUID_NOTIFY);
		if (null == mCharacteristic || null == mCharacteristicNotify) {
			return false;
		}
		
		boolean isSuccess = writeCharacteristic(mCharacteristic, value);
		MyLog.i(TAG, "------正在发送的蓝牙(" + cmdType + ")指令是否成功：" + isSuccess);
//		if (isSuccess) {// 发送指令到蓝牙设备上
			setCharacteristicNotification(mCharacteristicNotify, true);// 用来接收的特征值，具有通知特性，可监听特征值的变化。一有改变，立刻通知
			return isWriting;
//			return true;
//		} else {
//			return false;
//		}
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	public synchronized boolean writeCharacteristic(BluetoothGattCharacteristic characteristic, byte[] value) {
		if (null == mGatt || null == characteristic) {
			return false;
		}
		
		if (null != value){// && value.length != 0) {
			characteristic.setValue(value);
		}
		
		writeCharacteristicOrDescriptor(characteristic);
		
		return isWriting;//mGatt.writeCharacteristic(characteristic);
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	public synchronized void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
		if (null == bluetoothAdapter || null == mGatt || null == characteristic) {
			return;
		}
		
		mGatt.setCharacteristicNotification(characteristic, enabled);
		// 设置characteristic的描述值。 所有的服务、特征值、描述值都用UUID来标识，先根据characteristic的UUID找到characteristic，
		// 再根据BluetoothGattDescriptor的UUID找到BluetoothGattDescriptor，然后设定其值。 关于descriptor，可以通过getDescriptor()方法的返回值来理解
		BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID_CLIENT_CHARACTERISTIC_CONFIG);
		
		if (descriptor != null) {
			if (enabled) {
				descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);// ENABLE_NOTIFICATION_VALUE
			} else {
				descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
			}
			
			writeCharacteristicOrDescriptor(descriptor);//mGatt.writeDescriptor(descriptor);
		}
	}
	
	private static boolean isWriting = false;
	protected static final Queue<Object> writeQueue = new ConcurrentLinkedQueue<Object>();
	
	protected synchronized void writeCharacteristicOrDescriptor(Object object) {
		if (writeQueue.isEmpty() && !isWriting) {
			doWriteCharacteristicOrDescriptor(object);
		} else {
			writeQueue.add(object);
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	private synchronized void doWriteCharacteristicOrDescriptor(Object object) {
		if (object instanceof BluetoothGattCharacteristic) {
			isWriting = mGatt.writeCharacteristic((BluetoothGattCharacteristic) object);
		} else if (object instanceof BluetoothGattDescriptor) {
			isWriting = mGatt.writeDescriptor((BluetoothGattDescriptor) object);
		} else {
			nextWriteCharacteristicOrDescriptor();
		}
	}

	private synchronized void onNextWriteCharacteristicOrDescriptor() {
		isWriting = false;
		nextWriteCharacteristicOrDescriptor();
	}

	private synchronized void nextWriteCharacteristicOrDescriptor() {
		// empty enable write
		if(isWriting) {
			isWriting = !writeQueue.isEmpty();
		}
		
		if (!writeQueue.isEmpty() && !isWriting) {
			doWriteCharacteristicOrDescriptor(writeQueue.poll());
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	public BluetoothGattService getSupportedGattServices(UUID uuid) {
		if (null == mGatt || null == uuid) {
			return null;
		}
		
		return mGatt.getService(uuid);
	}
	
	/**
	 * 清除手机端中缓存的蓝牙设备信息
	 * @param gatt
	 * @return
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	public static boolean refreshBluetoothDeviceCache(BluetoothGatt gatt) {
		if (null == gatt) {
			return false;
		}
		
		/*
		 * There is a refresh() method in BluetoothGatt class but for now it's hidden. We will call it using reflections.
		 */
		try {
			final Method refresh = BluetoothGatt.class.getMethod("refresh");
			if (refresh != null) {
				final boolean success = ((Boolean) refresh.invoke(gatt)).booleanValue();
				return success;
			}
		} catch (Exception e) {
			return false;
		}
		
		return false;
	}
	
	public List<BluetoothEntry> getBluetoothDeviceList() {
		return new ArrayList<BluetoothEntry>(mDeviceMap.values());
	}
	
	public boolean isExitConnected() {
		boolean isConnectState = false;
		
		if (mDeviceMap.isEmpty()) {
			isConnectState = false;
		} else {
			for (BluetoothEntry bluetoothEntry : mDeviceMap.values()) {
				if (bluetoothEntry.isOpen() && null != bluetoothEntry.getConnectState() && BluetoothConnectState.CONNECT_SUCCESS == bluetoothEntry.getConnectState()) {
					isConnectState = true;
					break;
				} else {
					isConnectState = false;
				}
			}
		}
		
		return isConnectState;
	}
	
	public void updateDataState(String deviceAddress, Integer connectState) {
		if (TextUtils.isEmpty(deviceAddress) || mDeviceMap.isEmpty() || !mDeviceMap.containsKey(deviceAddress)) {
			return;
		}
		
		BluetoothEntry bluetoothEntry = mDeviceMap.get(deviceAddress);
		bluetoothEntry.setConnectState(connectState);
	}
	
	@Override
	public void onDestroy() {
		MyLog.i(TAG, "服务被销毁了");
		if (isScanning) {
			stopBleScan();
		}
		
		refreshBluetoothDeviceCache(mGatt);
		close();
		
		isWriting = false;
		writeQueue.clear();
		
		super.onDestroy();
	}
	
	private static byte[] reverseBytes(byte[] a)
    {
        int len = a.length;
        byte[] b = new byte[len];
        for (int k = 0; k < len; k++) {
            b[k] = a[a.length - 1 - k];
        }
        return b;
    }
//    // byte转十六进制字符串
//    public static String bytes2HexString(byte[] bytes) {
//        String ret = "";
//        for (int i = 0; i < bytes.length; i++) {
//            String hex = Integer.toHexString(bytes[i] & 0xFF);
//            if (hex.length() == 1) {
//                hex = '0' + hex;
//            }
//            ret += hex.toUpperCase();
//        }
//        return ret;
//    }

	public static String bytes2HexString(byte[] bytes) {
		final String HEX = "0123456789abcdef";
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		for (byte b : bytes) {
			// 取出这个字节的高4位，然后与0x0f与运算，得到一个0-15之间的数据，通过HEX.charAt(0-15)即为16进制数
			sb.append(HEX.charAt((b >> 4) & 0x0f));
			// 取出这个字节的低位，与0x0f与运算，得到一个0-15之间的数据，通过HEX.charAt(0-15)即为16进制数
			sb.append(HEX.charAt(b & 0x0f));
		}

		return sb.toString();
	}
}