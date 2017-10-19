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
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

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


    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private BluetoothGatt bluetoothGatt;
    //    private Handler mHandler;
    private boolean isConnect = false;
    private BluetoothGattCharacteristic writer;
    private UUID readerID = UUID.fromString("0000fff4-0000-1000-8000-00805f9b34fb");
    private UUID writeID = UUID.fromString("0000fff3-0000-1000-8000-00805f9b34fb");

    @Override
    public void onCreate() {
        super.onCreate();
        BluetoothManager manager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        bluetoothAdapter = manager.getAdapter();
//        mHandler = new Handler(this.getMainLooper());
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

//        if (!bluetoothAdapter.isEnabled()) {
//            bluetoothAdapter.enable();
//            mlog.e("____?");
//        }

        if (bluetoothAdapter.isEnabled() && !isConnect) {
            mlog.e("++++");
            scanBLe();//扫描
        }
    }

    private void scanBLe() {
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            mlog.e("___");
//            mHandler.post(new Runnable() {
//                @Override
//                public void run() {
            try {
                bluetoothAdapter.stopLeScan(leScanCallback);//防止程序意外退出 没有结束上一次的扫描
                Thread.sleep(2000);
                bluetoothAdapter.startLeScan(leScanCallback);
                Intent intent = new Intent("scan");
                intent.putExtra("scan", true);
                sendBroadcast(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
//                }
//            });
        }
    }

    BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            mlog.e(">>>>>>>发现设备" + device.getName() + " : " + device.getAddress());
            if (device.getName() != null && device.getName().startsWith("LWB")) {
                bluetoothAdapter.stopLeScan(leScanCallback);
                mlog.e("停止扫描");
                linkBle(device);
                Intent intent = new Intent("scan");
                intent.putExtra("scan", false);
                sendBroadcast(intent);
            }
        }
    };

    private void linkBle(final BluetoothDevice device) {
//        mHandler.post(new Runnable() {
//            @Override
//            public void run() {
        bluetoothDevice = device;
        //连接
        mlog.e("开始连接");
        if (bluetoothGatt != null) {
            bluetoothGatt.close();
            bluetoothGatt = null;
            mlog.e(">>>>>>>");
        }
        bluetoothGatt = device.connectGatt(getApplicationContext(), false, gattCallback);
//            }
//        });
    }

    //标定点
    public void stateOdert() {
        byte[] buf = new byte[17];
//        {
//            0x4C, 0x77, 0x0f, 0x01, 0x61, 0x04, ,0x04, 0x1C
//        } ;
        buf[0] = 0x4C;
        buf[1] = 0x77;
        buf[2] = 0x0f;
        buf[3] = 0x01;
        buf[4] = 0x61;
        buf[5] = 0x04;
        //5
        buf[6] = 0x00;
        buf[7] = 0x05;
        //40
        buf[8] = 0x00;
        buf[9] = 0x28;
        //80
        buf[10] = 0x0;
        buf[11] = 0x50;
        //120
        buf[12] = 0x00;
        buf[13] = (byte) 0x78;
        //verify
        buf[14] = 0x55;

        mlog.e("TAG", 120 >> 8 & 0xff, 120 & 0xff);

        buf[15] = 0x04;
        buf[16] = 0x1c;

        mlog.e(buf[0] ^ buf[1] ^ buf[2] ^ buf[3] ^ buf[4] ^ buf[5] ^ buf[6] ^ buf[7] ^ buf[8] ^ buf[9] ^ buf[10] ^ buf[11] ^ buf[12] ^ buf[13]);
        writer.setValue(buf);
        bluetoothGatt.writeCharacteristic(writer);
    }

    //重量
    public void weightOdert() {
        byte[] buf = {0x4C, 0x77, 0x06, 0x01, 0x51, 0x6D, 0x04, 0x1C};
        writer.setValue(buf);

        boolean b = bluetoothGatt.writeCharacteristic(writer);
        mlog.e(b);
    }

    //阻抗
    public void bioOdert() {
        byte[] buf = {0x4C, 0x77, 0x06, 0x01, 0x52, 0x6E, 0x04, 0x1C};
        writer.setValue(buf);
        bluetoothGatt.writeCharacteristic(writer);
        mlog.e("===");
    }

    //置零
    public void zeroOdert() {
        byte[] buf = new byte[]{0x4C, 0x77, 0x06, 0x01, 0x72, 0x4E, 0x04, 0x1C};
        writer.setValue(buf);
        bluetoothGatt.writeCharacteristic(writer);
    }


    private void sendBLEBrodcast() {
        Intent intent = new Intent("BLEConnect");
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
//        mHandler.removeCallbacksAndMessages(null);
        unregisterReceiver(bleBroadcastReceiver);
    }

    BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            switch (newState) {//newState顾名思义，表示当前最新状态。status可以获取之前的状态。
                case BluetoothProfile.STATE_CONNECTED:
                    mlog.e("连接成功");
                    //开启发现服务
                    isConnect = true;
                    gatt.discoverServices();
                    //发送通知连接成功，跳出交互页
                    sendBLEBrodcast();
                    Intent intent = new Intent("connect");
                    intent.putExtra("connect", true);
                    sendBroadcast(intent);
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    //表示gatt连接已经断开。
                    mlog.e("断开连接");
                    isConnect = false;
                    gatt.close();
                    scanBLe();
                    Intent intent2 = new Intent("connect");
                    intent2.putExtra("connect", false);
                    sendBroadcast(intent2);
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
            BluetoothGattService readerService =
                    gatt.getService(UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb"));

            //读服务特征值
            BluetoothGattCharacteristic reader = readerService
                    .getCharacteristic(readerID);

            //写服务特征值
            writer = readerService.getCharacteristic(writeID);

            for (BluetoothGattDescriptor descriptor : reader.getDescriptors()) {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                gatt.writeDescriptor(descriptor);
//                gatt.readDescriptor(descriptor);
            }
            gatt.readCharacteristic(reader);//读
            gatt.setCharacteristicNotification(reader, true);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            //设备发来数据
            if (status == BluetoothGatt.GATT_SUCCESS)
                mlog.e("TAG", "----------->onCharacteristicRead");
            for (int i = 0; i < characteristic.getValue().length; i++) {
                mlog.e("TAG", "------------>>>>>获取数据  value[" + i + "]: " + characteristic.getValue()[i]);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            //数据写入结果回调   characteristic为你写入的指令 这可以判断数据是否完整写入
            mlog.e("onCharacteristicWrite");
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            mlog.e("TAG", "----------->onCharacteristicChanged");
            for (int i = 0; i < characteristic.getValue().length; i++) {
                Log.e("TAG", "------------获取数据  value[" + i + "]: " + characteristic.getValue()[i]);
            }
            byte h8 = characteristic.getValue()[6];
            byte l8 = characteristic.getValue()[7];
            byte H8 = characteristic.getValue()[5];
            byte L8 = characteristic.getValue()[6];


            int w = (h8 << 8) | (l8 & 0xff);
            int Z = (H8 << 8) | (L8 & 0xff);
            mlog.e("ZK" + Z);
            mlog.e("TAG", w, (h8), l8);
            String date = String.valueOf(characteristic.getValue()[7]);
        }

    };

    BleBroadcastReceiver bleBroadcastReceiver;

    private void registBrodcast() {
        bleBroadcastReceiver = new BleBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("reader");
        intentFilter.addAction("readerB");
        intentFilter.addAction("readerBD");
        intentFilter.addAction("readerZ");
        registerReceiver(bleBroadcastReceiver, intentFilter);
    }

    private class BleBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals("reader")) {
                mlog.e("  ");
                weightOdert();
            }
            if (intent.getAction().equals("readerB")) {
                mlog.e("  ");
                bioOdert();
            }
            if (intent.getAction().equals("readerBD")) {
                mlog.e("  ");
                stateOdert();
            }
            if (intent.getAction().equals("readerZ")) {
                mlog.e("  ");
                zeroOdert();
            }
        }
    }
}

