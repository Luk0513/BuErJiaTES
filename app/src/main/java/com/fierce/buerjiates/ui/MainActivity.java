package com.fierce.buerjiates.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.fierce.buerjiates.R;
import com.fierce.buerjiates.base.BaseActivity;
import com.fierce.buerjiates.config.MyApp;
import com.fierce.buerjiates.https.HttpManage;
import com.fierce.buerjiates.presents.IActdevicePresent;
import com.fierce.buerjiates.services.DownAPKService;
import com.fierce.buerjiates.services.LoadDataSevice;
import com.fierce.buerjiates.utils.DownloadUtil;
import com.fierce.buerjiates.utils.NetWorkUtils;
import com.fierce.buerjiates.views.IActiveDeviceView;
import com.fierce.buerjiates.widget.CustomDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MainActivity extends BaseActivity implements SurfaceHolder.Callback, IActiveDeviceView, View.OnTouchListener {
    private final String TAG = "MainActivity";
    @BindView(R.id.suv_advideo)
    SurfaceView suvAdvideo;
    @BindView(R.id.v_hideView)
    View vHideView;
//    @BindView(R.id.imge_money)
//    ImageView imageMoney;

    private CustomDialog actDialog;
    private CustomDialog guideDialog;
    private int lastPosition;
    private MediaPlayer mediaPlayer;
    private String deviceId;
    private String deviceKey;
    private IActdevicePresent devicePresent;
    private String videoUrl = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
    private String vedioPath = "/storage/emulated/0/adVideo/big_buck_bunny.mp4";
    private MyHandler mHandler;
    private MyBroadcastReceiver broadReceiver;
    private NetWorkUtils netWorkUtils;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        mHandler = new MyHandler(this);
        suvAdvideo.getHolder().addCallback(this);
        initGuideDialog();
        netWorkUtils = new NetWorkUtils(this);
        //检查网络状态
        if (!MyApp.getInstance().isActivateDevice()) {
            netWorkUtils.checkNetworkState();
            devicePresent = new IActdevicePresent(this);
            startService(new Intent(this, LoadDataSevice.class));
            inputDid();
        }
        updateAPP();
        vHideView.setOnTouchListener(this);
//        if (!videoIsExsit()) {
//            Log.e("TAG", "onCreate: --开始下载视频--");
//            vidoDownlod();
//        }

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
//        mediaPlayer = new MediaPlayer();
        //Log.e("TAG", "surfaceCreated____777777_____ " + lastPosition);
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
                // Log.e(TAG, "surfaceDestroyed6666: " + lastPosition);
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
        if (actDialog.isShowing()) {
            actDialog.dismiss();
        }
        showGuideDialog();

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

    ///////////////////////////////////////////////////////////////////////////////////////////////
    private AnimationDrawable anim;

    /**
     * 显示引导页
     */
    private void initGuideDialog() {
        View layout = View.inflate(this, R.layout.dialog_layout, null);
        ImageView iv = (ImageView) layout.findViewById(R.id.iv_image);
        iv.setBackgroundResource(R.drawable.daxiang_gif);
        anim = (AnimationDrawable) iv.getBackground();
        anim.start();
        guideDialog = new CustomDialog.Builder(this)
                .setIsFloating(true)
                .setIsFullscreen(true)
                .setView(layout)
                .setViewOnClike(R.id.iv_image, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismissGuideDialog();
                    }
                })
                .build();
    }

    private void showGuideDialog() {
        guideDialog.show();
        anim.start();
    }

    private void dismissGuideDialog() {
        if (guideDialog.isShowing()) {
            guideDialog.dismiss();
            anim.stop();
        }
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

    private long currentTime;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        currentTime = System.currentTimeMillis();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //结束计时
//                Log.e(TAG, "onTouch: :::::::::::::D");
                currentTime = System.currentTimeMillis() - currentTime;
                if (currentTime < 1000 * 10) {
                    if (mHandler.hasMessages(1))
                        mHandler.removeMessages(1);
                }
            case MotionEvent.ACTION_UP:
//                Log.e(TAG, "onTouch: :::::::::::::U");
                //开始计时
                currentTime = System.currentTimeMillis();
                mHandler.sendEmptyMessageDelayed(1, 1000 * 15);
                break;
        }
        return false;
    }


    @OnClick(R.id.imge_money)
    public void onViewClicked() {
        startActivity(new Intent(this, Lottery_activity.class));
    }

    private static class MyHandler extends Handler {
        private WeakReference<MainActivity> mWeakReference;

        private MyHandler(MainActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mWeakReference.get() == null)
                return;
            switch (msg.what) {
                case 1:
                    if (!mWeakReference.get().guideDialog.isShowing()) {
                        mWeakReference.get().showGuideDialog();
                    }
                    break;
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            hideNavigationBar();
        }
        hideNavigationBar();
        if (hasFocus && guideDialog != null && !guideDialog.isShowing()) {
            mHandler.sendEmptyMessageDelayed(1, 15 * 1000);
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onBackPressed() {
    }

    private int index = 0;

    @Override
    protected void onResume() {
        super.onResume();
        registrBodcast();
        Log.e(TAG, "onResume: ");
        index = 1;
        if (MyApp.getInstance().getDevice_id() != null) {
            deviceId = MyApp.getInstance().getDevice_id();
            deviceKey = "123456";
            devicePresent = new IActdevicePresent(this);
            devicePresent.acvDevice();
        }

    }


    @Override
    protected void onStop() {
        super.onStop();
        mHandler.removeCallbacksAndMessages(null);
        guideDialog.dismiss();
        unregisterReceiver(broadReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: :::::::::");
        mHandler.removeCallbacksAndMessages(null);
        if (guideDialog != null && guideDialog.isShowing()) {
            guideDialog.dismiss();
        }
    }

    /**
     * 接受Fragment发来的广播
     */
    public class MyBroadcastReceiver extends BroadcastReceiver {
        private Boolean isPopuShowe;

        @Override
        public void onReceive(Context context, Intent intent) {
            isPopuShowe = intent.getBooleanExtra("isPopuShowe", false);
            boolean isDone = intent.getBooleanExtra("isDone", false);
            final String apkPath = intent.getStringExtra("apk");
            Log.e(TAG, "onReceive: >>>>>>>>>>>>>" + apkPath);
            if (isPopuShowe) {
                vHideView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendBroadcast(new Intent("dismmisPopu"));
                    }
                });
                vHideView.setBackgroundResource(R.color.color_2);
                if (mHandler.hasMessages(1)) {
                    mHandler.removeMessages(1);
                }
            } else {
                vHideView.setBackgroundResource(R.color.color_9);
                sendBroadcast(new Intent("dismmisPopu"));
                vHideView.setClickable(false);
                mHandler.sendEmptyMessageDelayed(1, 15 * 1000);
            }
            Boolean isShowDialog = intent.getBooleanExtra("isshowDialog", false);
            if (isShowDialog)
                showGuideDialog();

            if (isDone) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("更新提示：")
                        .setMessage("发现新版本安装包！" +
                                "\n请立即更新")
                        .setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                installApk(apkPath);
                            }
                        });
                builder.setCancelable(false);
                builder.show();
            } else {

            }
        }
    }

    //打开APK程序代码
    private void installApk(String apkPath) {
//        File downloadFile = new File(Environment.getExternalStorageDirectory(), "update");
//        File[] files = new File(downloadFile.getAbsolutePath()).listFiles();
        File apk = new File(apkPath);
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(apk),
                "application/vnd.android.package-archive");
        startActivity(intent);
    }


    private void registrBodcast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("PupoState");
        broadReceiver = new MyBroadcastReceiver();
        registerReceiver(broadReceiver, intentFilter);

    }

    public void updateAPP() {
        //先检查本地文件夹是否存在
        File downloadFile = new File(Environment.getExternalStorageDirectory(), "update");
        if (!downloadFile.mkdirs()) {
            try {
                downloadFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //下载apk
        getApkfromeNet();
    }

    private void getApkfromeNet() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://120.76.159.2:8090/admin/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        Call<String> call = retrofit.create(HttpManage.class).updateApp();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject object = new JSONObject(response.body());
                    JSONArray array = object.getJSONArray("list");
                    JSONObject object2 = array.getJSONObject(0);
                    String versionTexe = object2.optString("versionText");
                    //服务器Apk 版本号
                    double versionCode = Double.valueOf(versionTexe).doubleValue();
                    //本地应用版本号
                    double appCode = getAppVersion();
                    if (versionCode > appCode) {
                        //后台下载Apk
                        String apkUrl = object2.optString("filePath");
                        Intent intent = new Intent(getBaseContext(), DownAPKService.class);
                        intent.putExtra("url", apkUrl);
                        startService(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("TAG", "onFailure: ::::::::::::::" + t.toString());
            }
        });
    }

    /**
     * 获取单个App版本号
     **/
    public double getAppVersion() {
        PackageManager pManager = this.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = pManager.getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int appVersion = packageInfo.versionCode;
        double version = Double.valueOf(appVersion).doubleValue();
        Log.e("TAG", "getAppVersion: " + appVersion + "   " + version);
        return version;
    }
}
