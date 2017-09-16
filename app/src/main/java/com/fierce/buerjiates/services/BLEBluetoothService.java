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
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.fierce.buerjiates.utils.mlog;

import java.util.List;
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


    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice bluetoothDevice;
    BluetoothGatt bluetoothGatt;
    Handler mHandler;


    @Override
    public void onCreate() {
        super.onCreate();
        BluetoothManager manager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        bluetoothAdapter = manager.getAdapter();
        mHandler = new Handler(this.getMainLooper());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        registBrodcast();
        openBLE();
        return super.onStartCommand(intent, flags, startId);
    }


    /**
     * 打开蓝牙
     */
    private void openBLE() {

        //版本低于6。0
        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
            mlog.e("____?");
        }

        if (bluetoothAdapter.isEnabled()) {
            if (bluetoothAdapter.isDiscovering()) {
                bluetoothAdapter.stopLeScan(leScanCallback);
                mlog.e("::");
            }
            mlog.e("++++");
            scanBLe();
        }
    }

    private void scanBLe() {
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            mlog.e("___");
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                        bluetoothAdapter.startLeScan(leScanCallback);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            mlog.e(">>>>>>>发现设备" + device.getName() + " : " + device.getAddress());
            if (device.getName() != null && device.getName().startsWith("TGF")) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        linkBle(device);
                    }
                });
            }
        }
    };

    private void linkBle(BluetoothDevice device) {
        bluetoothDevice = device;
//        if (bluetoothDevice.getName() != null && bluetoothDevice.getName().startsWith("TGF")) {
        //连接
        mlog.e("开始连接");
        if (bluetoothGatt != null) {
            bluetoothGatt.close();
            bluetoothGatt = null;
            mlog.e(">>>>>>>");
        }
        bluetoothGatt = device.connectGatt(getApplicationContext(), false, gattCallback);
        if (bluetoothGatt.connect()) {
            //连接上了就停止扫描
            mlog.e("停止扫描");
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    bluetoothAdapter.stopLeScan(leScanCallback);
                }
            });
        }
//        }
    }

    private void sendBLEBrodcast() {
        Intent intent = new Intent("BLE");
        sendBroadcast(intent);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bluetoothGatt != null) {
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
            bluetoothGatt = null;
        }
        stopSelf();
        mlog.e("onDestroy");
        bluetoothAdapter.stopLeScan(leScanCallback);
        mHandler.removeCallbacksAndMessages(null);
        unregisterReceiver(bleBroadcastReceiver);
    }


    public void test(View v) {
    }


    BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            switch (newState) {//newState顾名思义，表示当前最新状态。status可以获取之前的状态。
                case BluetoothProfile.STATE_CONNECTED:
                    mlog.e("连接成功");
                    //开启发现服务
                    gatt.discoverServices();
                    sendBLEBrodcast();
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    //表示gatt连接已经断开。
                    mlog.e("断开连接");
                    gatt.disconnect();
                    gatt.close();
                    scanBLe();
                    break;
                case BluetoothProfile.STATE_CONNECTING:
                    mlog.e("连接中");
                    break;
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            mlog.e(" 发现服务");
            List<BluetoothGattService> services = gatt.getServices();
            for (BluetoothGattService service : services) {
                Log.e("TAG", "---------------" + service.getUuid());
                for (BluetoothGattCharacteristic bluetoothGattCharacteristic : service.getCharacteristics()) {
                    Log.e("TAG", "------------->>>" + bluetoothGattCharacteristic.getUuid());
                }
            }

            //打开读取开关
            BluetoothGattService readerService = gatt.getService(UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb"));
            BluetoothGattCharacteristic reader = readerService
                    .getCharacteristic(UUID.fromString("0000fff4-0000-1000-8000-00805f9b34fb"));
            gatt.readCharacteristic(reader);
            for (BluetoothGattDescriptor descriptor : reader.getDescriptors()) {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                gatt.writeDescriptor(descriptor);
            }
            gatt.setCharacteristicNotification(reader, true);

        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Log.e("TAG", "----------->onCharacteristicRead");
            for (int i = 0; i < characteristic.getValue().length; i++) {
                Log.e("TAG", "------------>>>>>获取数据  value[" + i + "]: " + characteristic.getValue()[i]);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            Log.e("TAG", "----------->onCharacteristicChanged");
            for (int i = 0; i < characteristic.getValue().length; i++) {
                Log.e("TAG", "------------获取数据  value[" + i + "]: " + characteristic.getValue()[i]);
            }

            String date = String.valueOf(characteristic.getValue()[7]);
            Intent intents = new Intent("BLE");
            intents.putExtra("data", date);
            sendBroadcast(intents);
        }
    };

    BleBroadcastReceiver bleBroadcastReceiver;

    private void registBrodcast() {
        bleBroadcastReceiver = new BleBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        registerReceiver(bleBroadcastReceiver, intentFilter);
    }

    private class BleBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
                mlog.e("ACTION_DISCOVERY_STARTED");
            }
            if (intent.getAction().equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                mlog.e("ACTION_DISCOVERY_FINISHED");
            }
            if (intent.getAction().equals(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)) {
                mlog.e("ACTION_CONNECTION_STATE_CHANGED");
            }
            if (intent.getAction().equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                mlog.e("ACTION_BOND_STATE_CHANGED");
            }
        }
    }
}

