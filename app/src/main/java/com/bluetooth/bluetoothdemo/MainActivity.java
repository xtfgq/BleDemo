package com.bluetooth.bluetoothdemo;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bluetooth.bluetoothdemo.bean.RxBus;
import com.bluetooth.bluetoothdemo.type.EventType;
import com.test.bluetooth.MyApplication;
import com.test.bluetooth.core.MyBluetoothManager;
import com.test.bluetooth.entry.BgMeasureRecordEntry;
import com.test.bluetooth.entry.BluetoothEntry;
import com.test.bluetooth.utils.ActivityUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;


public class MainActivity extends BaseActivity implements View.OnClickListener{
    private MyBluetoothManager myBluetoothManager;
    private Button btnScan;
    private CompositeSubscription compositeSubscription;
    private MyApplication app;
    private ListView mListView;
    private MyAdapter myAdapter;
    private List<BluetoothEntry> list;
    private TextView tvcontent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUtil.addActivity(this);
        app = MyApplication.getInstance();
        myBluetoothManager = MyBluetoothManager.getIntance();
//        myBluetoothManager.setOnBluetoothListener(this);
        setContentView(R.layout.activity_main);
        initView();


        initEnvent();
    }

    private void initView() {
        compositeSubscription = new CompositeSubscription();
        btnScan = (Button) findViewById(R.id.btn_scan);
        mListView = (ListView) findViewById(R.id.list_devices);
        tvcontent=(TextView)findViewById(R.id.tv_content);
        list = new ArrayList<>();
        myAdapter = new MyAdapter(list);
        mListView.setAdapter(myAdapter);
    }

    private void initEnvent() {
        btnScan.setOnClickListener(this);
        Subscription subscription = RxBus.getInstance().toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object event) {
                        if (event == null) {
                            return;
                        }

                        if (event instanceof EventType) {
                            EventType type = (EventType) event;
                            if (("found_bluetooth_device").equals(type.getType())) {
                                list.clear();
                                list.add(app.getCacheBluetoothEntry());
                                myAdapter.setList(list);
                                myAdapter.notifyDataSetChanged();

                            }
                            if (("history_data").equals(type.getType())) {
                                String content="";
                                for(BgMeasureRecordEntry bean:myBluetoothManager.getHistoryData()){
                                    content+= "单位"+bean.getMeasureUnit()+"时间"+bean.getMeasureTime()+"测量值"+bean.getValue()+":::";
                                }
                                tvcontent.setText(content);

                            }
                            if (("now_data").equals(type.getType())) {
                                int lastPostion=myBluetoothManager.getHistoryData().size()-1;
                                BgMeasureRecordEntry bean=myBluetoothManager.getHistoryData().get(lastPostion);
                                String content="";
                                content= "实时数据：单位"+bean.getMeasureUnit()+"时间"+bean.getMeasureTime()+"测量值"+bean.getValue()+":::";
                                tvcontent.setText(content);

                            }

                        }

                    }
                });

        compositeSubscription.add(subscription);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_scan:
                myBluetoothManager.bindService();

                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myBluetoothManager.unbindService();
    }


    class MyAdapter extends BaseAdapter {
        public void setList(List<BluetoothEntry> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        private List<BluetoothEntry> list;

        public MyAdapter(List<BluetoothEntry> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            final BluetoothEntry item = (BluetoothEntry) getItem(position);
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.bluetooth_item, null);
                holder.dv_name = (TextView) convertView.findViewById(R.id.dv_name);
                holder.dv_mac = (TextView) convertView.findViewById(R.id.dv_mac);
                holder.btn_link = (Button) convertView.findViewById(R.id.btn_link);
                holder.btn_history=(Button)convertView.findViewById(R.id.btn_history);
                holder.btn_now=(Button)convertView.findViewById(R.id.btn_now);
                holder.btn_dis=(Button)convertView.findViewById(R.id.btn_dis);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.dv_name.setText(item.getName());
            holder.dv_mac.setText(item.getAddress());
            holder.btn_link.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        myBluetoothManager.connect(item.getAddress());
                }
            });
            holder.btn_history.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myBluetoothManager.sendCommandsToBluetoothDevice(MyBluetoothManager.CmdType.CMD_GET_HISTORY);

                }
            });
            holder.btn_now.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myBluetoothManager.sendCommandsToBluetoothDevice(MyBluetoothManager.CmdType.CMD_GET_MEASURE);


                }
            });
            holder.btn_dis.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myBluetoothManager.disconnect(item.getAddress(), true);
                }
            });

            return convertView;
        }
    }

    public static class ViewHolder {
        public TextView dv_mac;
        public TextView dv_name;
        public Button btn_link;
        public Button btn_history;
        public Button btn_now;
        public Button btn_dis;

    }
}
