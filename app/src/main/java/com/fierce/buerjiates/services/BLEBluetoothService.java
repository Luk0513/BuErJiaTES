package com.fierce.buerjiates.services;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.fierce.buerjiates.utils.mlog;

import java.util.List;

/**
 * @Author : Lukang
 * >>Live and learn<<
 * blog : http://blog.csdn.net/fight_0513
 * @PackegeName : com.fierce.buerjiates.services
 * @ProjectName : BuErJiaTES
 * @Date :  2017-09-08
 */

public class BLEBluetoothService extends Service {

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt gatt;
    private static final int REQUEST_PERMISSION = 112;
    public static final int BLE_CONNECTED = 113;
    public static final int BLE_DISCONNECTED = 114;

    //特征列表集
    private List<BluetoothGattCharacteristic> mCharacteristics;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initBleutooth();
        mlog.e("开始ble服务");
        return super.onStartCommand(intent, flags, startId);
    }


    private void initBleutooth() {
        //如果蓝牙没有打开 打开蓝牙
        if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }
        scanBle();//扫描
    }

    BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            mlog.e(device.getName());
            gatt = device.connectGatt(getApplicationContext(), true, gattCallback);
            //连接
            final boolean connect = gatt.connect();
            mlog.e(connect);
        }
    };
    BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            mlog.e(status);
            super.onConnectionStateChange(gatt, status, newState);
            switch (newState) {//newState顾名思义，表示当前最新状态。status可以获取之前的状态。
                case BluetoothProfile.STATE_CONNECTED:
                    //这里表示已经成功连接，如果成功连接，我们就会执行discoverServices()方法去发现设备所包含的服务
                    mlog.e("连接成功");
                    sendBrodcast(BLE_CONNECTED);
                    gatt.discoverServices();
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    sendBrodcast(BLE_DISCONNECTED);
                    //表示gatt连接已经断开。
                    mlog.e("connection broken.");
                    gatt.close();
                    break;
            }
        }

        //有设备服务时，也就是设备上的Service被发现时。
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            mlog.e(" ");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //gatt.getServices()可以获得外设的所有服务。
                for (BluetoothGattService service : gatt.getServices()) {//接下来遍历所有服务
                    //每发现一个服务，我们再次遍历服务当中所包含的特征，service.getCharacteristics()可以获得当前服务所包含的所有特征
                    for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                        mCharacteristics.add(characteristic);//通常可以把所发现的特征放进一个列表当中以便后续操作。
                        mlog.e(characteristic.getUuid().toString());//打印特征的UUID。
                    }
                }
            }
            //当方法执行完后，我们就获取了设备所有的特征了。
            //如果你想知道每个特征都包含哪些描述符，很简单，再用一个循环去遍历每一个特征的getDescriptor()方法。
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            //读取特征回调。
            mlog.e(" ");

            if (status == BluetoothGatt.GATT_SUCCESS) {
                //如果程序执行到这里，证明特征的读取已经完成，我们可以在回调当中取出特征的值。
                //特征所包含的值包含在一个byte数组内，我们可以定义一个临时变量来获取。

                byte[] characteristicValueBytes = characteristic.getValue();
                //如果这个特征返回的是一串字符串，那么可以直接获得其值
                String bytesToString = new String(characteristicValueBytes);

                //如果只需要取得其中的几个byte，可以直接指定获取特定的数组位置的byte值.
                //例如协议当中定义了这串数据当中前2个byte表示特定一个数值，那么获取这个值，可以直接写成
                byte[] aValueBytes = new byte[]{
                        characteristic.getValue()[0], characteristic.getValue()[1]
                };
//                Log.i("c-u", "" + Integer.parseInt(UUID.bytesToHexString(characteristic.getValue()), 16));
                //至于这个值时表示什么，十进制数值？或是一个字符串？还是翻开协议慢慢找吧。
                //到这里为止，我们已经成功采用读的方式，获得了存在于特征当中的值。
                //characteristic还能为我们提供什么东西呢？属性，权限等是比较常用的。
            }

        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            //写入特征。
            mlog.e(" ");
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            mlog.e(" ");
            super.onCharacteristicChanged(gatt, characteristic);
        }

    };

    private void sendBrodcast(int sdate) {
        Intent intent = new Intent("BLE");
        intent.putExtra("sdate", sdate);
        sendBroadcast(intent);
    }

    private void scanBle() {
        mBluetoothAdapter.startLeScan(leScanCallback);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }
}

