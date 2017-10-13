package com.fierce.buerjiates.ui;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.fierce.buerjiates.R;
import com.fierce.buerjiates.base.BaseActivity;
import com.fierce.buerjiates.config.MyApp;
import com.fierce.buerjiates.presents.IActdevicePresent;
import com.fierce.buerjiates.services.BLEBluetoothService;
import com.fierce.buerjiates.services.LoadDataSevice;
import com.fierce.buerjiates.utils.DownloadUtil;
import com.fierce.buerjiates.utils.NetWorkUtils;
import com.fierce.buerjiates.utils.mlog;
import com.fierce.buerjiates.views.IActiveDeviceView;
import com.fierce.buerjiates.widget.CustomDialog;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements SurfaceHolder.Callback, IActiveDeviceView {
    @BindView(R.id.suv_advideo)
    SurfaceView suvAdvideo;
    @BindView(R.id.v_hideView)
    View vHideView;
    @BindView(R.id.tv_startscan)
    TextView tvStartscan;
    @BindView(R.id.tv_connect)
    TextView tvConnect;
    @BindView(R.id.tv_w)
    TextView tvW;

    private static final int REQUEST_ENABLE = 121;
    private static final int REQUEST_LOCATION = 131;
    private CustomDialog actDialog;
    private int lastPosition;
    private MediaPlayer mediaPlayer;
    private String deviceId;
    private String deviceKey;
    private IActdevicePresent devicePresent;
    private String videoUrl = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
    private String vedioPath = "/storage/emulated/0/adVideo/big_buck_bunny.mp4";
    private MyBroadcastReceiver broadReceiver;
    private NetWorkUtils netWorkUtils;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        suvAdvideo.getHolder().addCallback(this);
        netWorkUtils = new NetWorkUtils(this);
        //检查网络状态
//        if (!MyApp.getInstance().isActivateDevice()) {
//            devicePresent = new IActdevicePresent(this);
//            inputDid();
//            netWorkUtils.checkNetworkState();
//        }
//        if (!videoIsExsit()) {
//            vidoDownlod();
//        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                requsetPermisson();
//                startActivity(new Intent(MainActivity.this, ElectronicScale_Activity.class));
            }
        }).start();
        mlog.e(MyApp.getInstance().getDevice_id());
    }

    /**
     * android6.0以上需要动态获取定位权限
     */
    public void requsetPermisson() {
        if (Build.VERSION.SDK_INT >= 23) {
            int check = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            if (check != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
            }
        }

        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE);
        } else {
            startService(new Intent(MainActivity.this, BLEBluetoothService.class));
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mlog.e("——————————————————————————");
            startService(new Intent(MainActivity.this, BLEBluetoothService.class));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE) {
            if (resultCode == RESULT_OK) {
                mlog.e("))))))________");
                startService(new Intent(MainActivity.this, BLEBluetoothService.class));
            } else {
                mlog.e("打开蓝牙失败");
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
//        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.buerjia_video);
//            mediaPlayer.setDataSource(vedioPath);
//            mediaPlayer.prepare();
            mediaPlayer.setLooping(true);
            mediaPlayer.setDisplay(holder);
            mediaPlayer.start();
            lastPosition = MyApp.getInstance().getVideoPosition();
            if (lastPosition > 0) {
                mediaPlayer.seekTo(lastPosition);
                lastPosition = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                mediaPlayer.stop();
                lastPosition = mediaPlayer.getCurrentPosition();
                MyApp.getInstance().saveVideoPosition(lastPosition);
            }
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    @Override
    public void showToasAct(String msg) {
        if (index == 0)
            showToast(msg);
    }

    @Override
    public String getDeviceId() {
        return deviceId;
    }

    @Override
    public String getDeviceKey() {
        return "fierce321";
    }

    @Override
    public void onSucceed() {
        startService(new Intent(this, LoadDataSevice.class));
        if (actDialog.isShowing()) {
            actDialog.dismiss();
        }
        for (int i = 1; i <= 6; i++) {
            MyApp.getInstance().getGoodsListBean(i + "", MyApp.getInstance().getDevice_id());
        }
    }


    @Override
    public void onOFF() {
        if (actDialog != null && actDialog.isShowing())
            actDialog.dismiss();
        //设备被禁用
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示：")
                .setMessage("该设备账号已被禁用！" +
                        "\n如需继续使用请联系服务商")
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        inputDid();
                    }
                });
        builder.setCancelable(false);
        builder.show();

    }

    private boolean videoIsExsit() {
        File file = new File("/storage/emulated/0/adVideo",
                videoUrl.substring(videoUrl.lastIndexOf("/") + 1));
        return file.exists();
    }

    private void vidoDownlod() {
        DownloadUtil.get().download(videoUrl, "adVideo", new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess(Object o) {
//                Log.e("TAG", "onDownloadSuccess: ______下载完成______");
            }

            @Override
            public void onDownloading(int progress) {

            }

            @Override
            public void onDownloadFailed() {
//                Log.e("TAG", "onDownloadFailed: ______下载失败______");
            }
        });
    }

    // 激活设备 inputDid()
    private void inputDid() {
        index = 0;
        final View layout = View.inflate(this, R.layout.input_d_id, null);
        actDialog = new CustomDialog.Builder(this)
                .setView(layout)
                .setIsFullscreen(true)
                .setIsFloating(false)
                .setViewOnClike(R.id.tv_ensuer, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getEdtexString(layout);
                        if (deviceId.length() <= 0 || deviceKey.length() <= 0) {
                            showToast("设备ID或者密钥不能为空");
                        } else {
                            if (deviceKey.equals("380528777adc570d")) {
                                deviceKey = "fierce321";
                                index = 0;
                                devicePresent.acvDevice();
                            } else {
                                showToast("密码错误");
                            }
                        }
                    }
                })
                .build();
        actDialog.show();
    }

    private void getEdtexString(View v) {
        EditText ed_DeviceId = (EditText) v.findViewById(R.id.et_did);
        EditText ed_Key = (EditText) v.findViewById(R.id.et_paw);
        deviceId = ed_DeviceId.getText().toString();
        deviceKey = ed_Key.getText().toString();
    }

    @OnClick(R.id.imge_money)
    public void onViewClicked() {
        startActivity(new Intent(this, Lottery_activity.class));
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            hideNavigationBar();
        }
        hideNavigationBar();
    }

    @Override
    public void onBackPressed() {
    }

    private int index = 0;

    @Override
    protected void onResume() {
        super.onResume();
        registrBodcast();
        index = 1;
        if (MyApp.getInstance().getDevice_id() != null) {
            deviceId = MyApp.getInstance().getDevice_id();
            deviceKey = "fierce321";
            devicePresent = new IActdevicePresent(this);
            devicePresent.acvDevice();
        }
        MyApp.getInstance().updateAPP();
    }


    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadReceiver);
    }

    /**
     * 接受Fragment发来的广播 根据发来广播获取 popuWindo的状态 设置VhideView
     */
    public class MyBroadcastReceiver extends BroadcastReceiver {
        private Boolean isPopuShowe;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("PupoState")) {
                isPopuShowe = intent.getBooleanExtra("isPopuShowe", false);
                if (isPopuShowe) {
                    vHideView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sendBroadcast(new Intent("dismmisPopu"));//发送广播关闭popuwindow
                        }
                    });
                    vHideView.setBackgroundResource(R.color.color_2);
                } else {
                    vHideView.setBackgroundResource(R.color.color_9);
                    sendBroadcast(new Intent("dismmisPopu"));
                    vHideView.setClickable(false);
                }
            }

            if (intent.getAction().equals("scan")) {

                boolean isC = intent.getBooleanExtra("scan", false);
                if (isC) {
                    tvStartscan.setText("扫描设备");
                } else
                    tvStartscan.setText("停止扫描");
            }
            if (intent.getAction().equals("connect")) {
                boolean isC = intent.getBooleanExtra("connect", false);
                if (isC) {
                    tvConnect.setText("连接设备中");
                } else
                    tvConnect.setText("断开连接");
            }

            if (intent.getAction().equals("w")) {

                String s = intent.getStringExtra("data");
                tvW.setText(s);
            }

        }
    }

    private void registrBodcast() {
        IntentFilter intentFilter = new IntentFilter("PupoState");
        intentFilter.addAction("scan");
        intentFilter.addAction("connect");
        intentFilter.addAction("w");
        broadReceiver = new MyBroadcastReceiver();
        registerReceiver(broadReceiver, intentFilter);

    }
}
