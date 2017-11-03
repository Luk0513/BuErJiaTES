package com.fierce.buerjiates.services;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.fierce.buerjiates.utils.mlog;

import java.util.UUID;

/**
 * @Author : Lukang
 * >>Live and learn<<
 * blog : http://blog.csdn.net/fight_0513
 * @PackegeName : com.fierce.buerjiates.services
 * @ProjectName : BuErJiaTES
 * @Date :  2017-09-08
 */

public class BLEBluetoothService extends Service {

    IBinder mIBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
//        registBrodcast();
        mlog.e("onBind");
        return mIBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        scanHandler = new Handler(getBaseContext().getMainLooper());
        mLeHandle = new BlEHandle();
        mlog.e("onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mlog.e(" onStartCommand ");
//        registBrodcast();
//        if (mLeHandle != null && !mLeHandle.hasMessages(SCANBLE))
//            mLeHandle.sendEmptyMessageDelayed(SCANBLE, 800);
        return super.onStartCommand(intent, flags, startId);
    }

    private boolean mScanning = false;
    private static final long SCAN_PERIOD = 10000;
    private static UUID WRITESERVICE = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    private static UUID READERID = UUID.fromString("0000fff4-0000-1000-8000-00805f9b34fb");
    private static UUID WRITEID = UUID.fromString("0000fff3-0000-1000-8000-00805f9b34fb");
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothManager bluetoothManager;
    private BluetoothDevice mBluetoothDevice;
    private BluetoothGattCharacteristic writer;
    private Handler scanHandler;
    private String mBluetoothDeviceAddress;
    private static final int STATE_CONNECTING = 1001;
    private static final int STATE_DISCONNECT = 1002;
    private int mConnectionState = -1;
    private int order;
    private BluetoothGattCharacteristic gattCharacteristic;
    private float weights;
    private int zukan;


    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            mlog.e(">>>>>>>发现设备  【" + device.getName() + "】 : " + device.getAddress());
            if (device.getName() != null && device.getName().startsWith("LWB")) {
                mLeHandle.sendEmptyMessage(UNSCAN);
                Message handelMes = Message.obtain();
                handelMes.what = LINKDEVICE;
                handelMes.obj = device.getAddress();
                mLeHandle.sendMessage(handelMes);
            }
        }
    };

    private void startScan() {
        if (mLeHandle != null && !mLeHandle.hasMessages(SCANBLE))
            mLeHandle.sendEmptyMessageDelayed(SCANBLE, 800);
    }

    //扫描
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            mlog.e("扫描");
            // Stops scanning after a pre-defined scan period.
            scanHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    mlog.e("停止扫描1");
                    sendBroadcast(new Intent("stopSCAN"));
                }
            }, SCAN_PERIOD); //10秒后停止搜索
            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback); //开始搜索
            sendBroadcast(new Intent("SCANING"));
        } else {
            mScanning = false;
            mlog.e("停止扫描2");
            sendBroadcast(new Intent("stopSCAN"));
            mBluetoothAdapter.stopLeScan(mLeScanCallback);//停止搜索
            if (mLeHandle.hasMessages(SCANBLE)) {
                mLeHandle.removeMessages(SCANBLE);
            }
        }
    }

    //连接
    private boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            mlog.e("BluetoothAdapter not initialized or unspecified address.");
            return false;
        }
        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            mlog.e("??????Device not found.  Unable to connect.");
            return false;
        }
        if (mBluetoothGatt != null) {
            mlog.e("???????");
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
        mBluetoothGatt = device.connectGatt(getApplication(), false, mGattCallback); //该函数才是真正的去进行连接
//        mBluetoothGatt.connect();
        mlog.e("Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            mlog.e("TAG", "status: " + status, "newState: " + newState);
            if (status != BluetoothGatt.GATT_SUCCESS) {
                String err = "Cannot connect device with error status: " + status;
                // 当尝试连接失败的时候调用 disconnect 方法是不会引起这个方法回调的，所以这里
                //   直接回调就可以了。
                mBluetoothGatt.close();
                if (!mLeHandle.hasMessages(SCANBLE)) {
                    mLeHandle.sendEmptyMessageDelayed(SCANBLE, 2000);
                }
                mlog.e(err);
                return;
            }

            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    mlog.e("已连接");
                    sendBLEBrodcast(true);
                    mConnectionState = STATE_CONNECTING;
                    mBluetoothGatt.discoverServices();
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    sendBLEBrodcast(false);
                    mlog.e("未连接");
                    mConnectionState = STATE_DISCONNECT;
                    mBluetoothGatt.close();
                    if (!mLeHandle.hasMessages(SCANBLE)) {
                        mLeHandle.sendEmptyMessageDelayed(SCANBLE, 2000);
                    }
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            mlog.e(" 发现服务  > " + status);
            mLeHandle.sendEmptyMessageDelayed(SETWRITER, 1000);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            mlog.e("onCharacteristicRead > " + status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            //数据写入结果回调   characteristic为你写入的指令 这可以判断数据是否完整写入
            byte[] value_1 = characteristic.getValue();
            if (value_1 == buf_Weight) {
                order = 1001;
            }
            if (value_1 == buf_ZK) {
                order = 1002;
            }
            if (value_1 == buf_queryStatus) {
                order = 1006;
                mlog.e("查询状态");
            }
            if (value_1 == buf_modelsleep) {
                mlog.e("休眠指令");
                order = 1008;
            }
            mlog.e("onCharacteristicWrite  > " + status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            mlog.e("TAG", "----------->onCharacteristicChanged");
            for (int i = 0; i < characteristic.getValue().length; i++) {
                Log.e("TAG", "------------获取数据  value[" + i + "]: " + characteristic.getValue()[i]);
            }
            gattCharacteristic = characteristic;
            mLeHandle.sendEmptyMessageDelayed(RECIVECHAR, 500);
        }
    };

    private void readerCharact(BluetoothGattCharacteristic characteristic) {
        switch (order) {
            case 1001:
                int stat = characteristic.getValue()[5];
                byte wh8 = characteristic.getValue()[6];
                byte wl8 = characteristic.getValue()[7];
                float w = (wh8 << 8) | (wl8 & 0xff);

                if (stat == 1) {
                    //稳定重量值
                    mLeHandle.sendEmptyMessageDelayed(GETZK, 1000);
                    weights = w / 10;
                } else {
                    mLeHandle.sendEmptyMessageDelayed(GETWEIGH, 1000);
                }
                mlog.e(weights);
                break;
            case 1002:
                Intent intent = new Intent("BleReciver");
                byte zk_h8 = characteristic.getValue()[5];
                byte zk_l8 = characteristic.getValue()[6];
                int zk = (zk_h8 << 8) | (zk_l8 & 0xff);

                intent.putExtra("zk", zk);
                intent.putExtra("weight", weights);
                mlog.e(zk);

                sendBroadcast(intent);
                break;
            case 1006:
                Intent intentQS = new Intent("QUERYSTATUS");
                int qres = characteristic.getValue()[5];
                intentQS.putExtra("qs", qres);
                sendBroadcast(intentQS);
                break;

        }
    }

    private void setWrite() {
        //打开读写服务
        BluetoothGattService readerService = mBluetoothGatt.getService(WRITESERVICE);
        //读服务特征值
        BluetoothGattCharacteristic reader = readerService.getCharacteristic(READERID);
        //写服务特征值
        writer = readerService.getCharacteristic(WRITEID);

        for (BluetoothGattDescriptor descriptor : reader.getDescriptors()) {
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);

            mBluetoothGatt.writeDescriptor(descriptor);
        }
        mBluetoothGatt.setCharacteristicNotification(reader, true);
        mLeHandle.sendEmptyMessageDelayed(QUIRESTATE, 800);
    }

    private void sendBLEBrodcast(boolean isconnect) {
        Intent intent = new Intent("BLEConnect");
        intent.putExtra("isconnected", isconnect);
        sendBroadcast(intent);
    }

    private byte[] buf_modelsleep;

    //休眠指令
    public void modelsleep() {
        buf_modelsleep = new byte[]{0x4C, 0x77, 0x07, 0x01, 0x63, 0x40, 0, 0x04, 0x1C};
        buf_modelsleep[6] = (byte) (buf_modelsleep[0] ^ buf_modelsleep[1] ^ buf_modelsleep[2]
                ^ buf_modelsleep[3] ^ buf_modelsleep[4] ^ buf_modelsleep[5]);
        writer.setValue(buf_modelsleep);
        mBluetoothGatt.writeCharacteristic(writer);
    }

    //重量
    private byte[] buf_Weight;

    private void weightOdert() {
        buf_Weight = new byte[]{0x4C, 0x77, 0x06, 0x01, 0x51, 0x6D, 0x04, 0x1C};
        writer.setValue(buf_Weight);
        mBluetoothGatt.writeCharacteristic(writer);
    }

    //阻抗
    private byte[] buf_ZK;

    private void bioOdert() {
        buf_ZK = new byte[]{0x4C, 0x77, 0x06, 0x01, 0x52, 0x6E, 0x04, 0x1C};
        buf_ZK[5] = (byte) (buf_ZK[0] ^ buf_ZK[1] ^ buf_ZK[2] ^ buf_ZK[3] ^ buf_ZK[4]);
        writer.setValue(buf_ZK);
        mBluetoothGatt.writeCharacteristic(writer);
        mlog.e("===");
    }

    private byte[] buf_queryStatus;

    //查询状态
    private void inquireState() {
        buf_queryStatus = new byte[]{0x4C, 0x77, 0x06, 0x01, 0x50, 0, 0x04, 0x1C};
        buf_queryStatus[5] = (byte) (buf_queryStatus[0] ^ buf_queryStatus[1]
                ^ buf_queryStatus[2] ^ buf_queryStatus[3] ^ buf_queryStatus[4]);
        writer.setValue(buf_queryStatus);
        mBluetoothGatt.writeCharacteristic(writer);
    }

    private BleReceiver bleReceiver;

    private void registBrodcast() {
        bleReceiver = new BleReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("reader");
        intentFilter.addAction("readerB");
        intentFilter.addAction("readerLook");
        intentFilter.addAction("sleep");
        intentFilter.addAction("SCANBLE");
        registerReceiver(bleReceiver, intentFilter);
    }

    private class BleReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("SCANBLE")) {
                startScan();
            }

            try {
                if (mConnectionState == STATE_CONNECTING) {
                    if (intent.getAction().equals("reader")) {
                        weightOdert();
                    }
                    if (intent.getAction().equals("sleep")) {
                        modelsleep();
                    }
                    if (intent.getAction().equals("readerB")) {
                        bioOdert();
                    }
                    if (intent.getAction().equals("readerLook")) {
                        inquireState();
                    }
                } else {
                    Toast.makeText(getBaseContext(), "蓝牙未连接", Toast.LENGTH_SHORT).show();
                }
            } catch (NullPointerException e) {
                Toast.makeText(getBaseContext(), "未建立连接", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static final int SCANBLE = 0x01;
    private static final int UNSCAN = 0x02;
    private static final int GETWEIGH = 0x03;
    private static final int GETZK = 0x04;
    private static final int QUIRESTATE = 0x05;
    private static final int LINKDEVICE = 0x06;
    private static final int SETWRITER = 0x07;
    private static final int RECIVECHAR = 0x08;

    private BlEHandle mLeHandle;

    class BlEHandle extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SCANBLE:
                    scanLeDevice(true);

                    if (!mLeHandle.hasMessages(SCANBLE))
                        mLeHandle.sendEmptyMessageDelayed(SCANBLE, (SCAN_PERIOD + 1000));
                    break;
                case UNSCAN:
                    scanLeDevice(false);
                    break;
                case GETWEIGH:
                    weightOdert();
                    break;
                case GETZK:
                    bioOdert();
                    break;
                case QUIRESTATE:
                    inquireState();
                    break;
                case LINKDEVICE:
                    String bleAddress = (String) msg.obj;
                    connect(bleAddress);
                    break;
                case SETWRITER:
                    setWrite();
                    break;
                case RECIVECHAR:
                    readerCharact(gattCharacteristic);
                    break;
            }
        }
    }

    public class LocalBinder extends Binder {
        public LocalBinder() {
        }

        public BLEBluetoothService getService() {
            return BLEBluetoothService.this;
        }

        public void starScanDevices() {
            startScan();
        }

        public void getWeight() {
            weightOdert();
        }

        public void getZuKan() {
            bioOdert();
        }

        public void initSleep() {
            modelsleep();
        }


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bleReceiver);
        mLeHandle.removeCallbacksAndMessages(null);
    }


}


