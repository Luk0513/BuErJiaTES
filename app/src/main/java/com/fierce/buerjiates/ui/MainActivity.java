package com.fierce.buerjiates.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.transition.Transition;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.fierce.buerjiates.R;
import com.fierce.buerjiates.base.BaseActivity;
import com.fierce.buerjiates.config.MyApp;
import com.fierce.buerjiates.listener.OnTransitionListener;
import com.fierce.buerjiates.presents.IActdevicePresent;
import com.fierce.buerjiates.presents.IGteViedoURL_Present;
import com.fierce.buerjiates.services.BLEBluetoothService;
import com.fierce.buerjiates.services.LoadDataSevice;
import com.fierce.buerjiates.utils.FileUtils;
import com.fierce.buerjiates.utils.NetWorkUtils;
import com.fierce.buerjiates.utils.mlog;
import com.fierce.buerjiates.video.EmptyControlVideo;
import com.fierce.buerjiates.views.IActiveDeviceView;
import com.fierce.buerjiates.views.IgetVideoURL_View;
import com.fierce.buerjiates.widget.CustomDialog;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements SurfaceHolder.Callback, IActiveDeviceView, IgetVideoURL_View {
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
    private long lastPosition;
    private MediaPlayer mediaPlayer;
    private String deviceId;
    private String deviceKey;
    private IActdevicePresent devicePresent;
    private String vedioPath = "/storage/emulated/0/adVideo/big_buck_bunny.mp4";
    private MyBroadcastReceiver broadReceiver;
    private NetWorkUtils netWorkUtils;
    private IGteViedoURL_Present getViedoURL_present;

    //mp4相关
    public final static String IMG_TRANSITION = "IMG_TRANSITION";
    public final static String TRANSITION = "TRANSITION";

    @BindView(R.id.video_player)
    EmptyControlVideo videoPlayer;

    private OrientationUtils orientationUtils;
    private boolean isTransition;
    private Transition transition;
    private File filepath;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
//        视频框架DEbug模式
//        Debuger.enable();
        isTransition = getIntent().getBooleanExtra(TRANSITION, false);
        netWorkUtils = new NetWorkUtils(this);
        getViedoURL_present = new IGteViedoURL_Present(this);
        //检查网络状态
        if (!MyApp.getInstance().isActivateDevice()) {
            devicePresent = new IActdevicePresent(this);
            inputDid();
            netWorkUtils.checkNetworkState();
        }
        requsetPermisson();
        if (MyApp.getInstance().getDevice_id() != null) {
            getViedoURL_present.getVideoURL(MyApp.getInstance().getDevice_id());
        }
        Intent mIntent = new Intent(getApplicationContext(), BLEBluetoothService.class);
        bindService(mIntent, serviceCon, Context.BIND_AUTO_CREATE);
        mlog.e(MyApp.getInstance().getDevice_id());
    }

    BLEBluetoothService.LocalBinder localBinder;
    ServiceConnection serviceCon = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            localBinder = (BLEBluetoothService.LocalBinder) service;
            mlog.e("getSevice");

            if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE);
            } else {
                localBinder.starScanDevices();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    /**
     * android6.0以上需要动态获取定位权限
     */
    private void requsetPermisson() {
        if (Build.VERSION.SDK_INT >= 23) {
            int check = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            if (check != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
            } else {

            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            mlog.e("——————————————————————————");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE) {
            if (resultCode == RESULT_OK) {
                mlog.e("蓝牙打开 开始扫描________");
//                MyApp.getInstance().getBleService().startScan();
                localBinder.starScanDevices();
            } else {
                finish();
                mlog.e("打开蓝牙失败");
            }
        }
    }

    SurfaceHolder surfaceHolder;

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        surfaceHolder = holder;
        videoPlay();
        mlog.e("4545");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mlog.e("");
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

    private void videoPlay() {
        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.buerjia_video);
            mediaPlayer.setLooping(true);
            mediaPlayer.setDisplay(surfaceHolder);
            mediaPlayer.start();
            lastPosition = MyApp.getInstance().getVideoPosition();
            if (lastPosition > 0) {
                mediaPlayer.seekTo((int) lastPosition);
                lastPosition = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
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


    //激活成功
    @Override
    public void onSucceed() {
        startService(new Intent(this, LoadDataSevice.class));
        if (actDialog.isShowing()) {
            actDialog.dismiss();
        }
        for (int i = 1; i <= 6; i++) {
            MyApp.getInstance().getGoodsListBean(i + "", MyApp.getInstance().getDevice_id());
        }
        requsetPermisson();
        getViedoURL_present.getVideoURL(MyApp.getInstance().getDevice_id());
    }


    //设备被禁用
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


    @Override
    public void getURLSucceed(final String videoUrl, int urlid) {
        //播放视频
        URLVideoPlay(videoUrl, urlid);

    }

    @Override
    public void getTGPriceFailure(String msg) {
        if (netWorkUtils.checkNetworkState()) {
            //没有视频
        } else {
            //没有联网
        }
        //没有上传网络视频
        videoPlayer.setVisibility(View.GONE);
        suvAdvideo.setVisibility(View.VISIBLE);
        suvAdvideo.getHolder().addCallback(this);
        mlog.e("DssS..............");
    }

    private void URLVideoPlay(String videoUrl, int urlid) {
        //视频连接获取成功
        mlog.e(videoUrl);
        videoPlayer.setVisibility(View.VISIBLE);
        suvAdvideo.setVisibility(View.GONE);
        //如果网络视频更新清除当前缓存
        if (urlid != MyApp.getInstance().getVideoID()) {
            try {
                mlog.e("  清除缓存");
                FileUtils.cleanAllVidepoCache();
                MyApp.getInstance().saveVideo(urlid);
                MyApp.getInstance().saveVideoPosition(0);
            } catch (Exception e) {
                mlog.e(e);
            }
        }

        File pathFile = new File(FileUtils.getPath());
        videoPlayer.setUp(videoUrl, true, pathFile, "");
        videoPlayer.setLooping(true);
        mlog.e("DS");
        initTransition();

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
        mlog.e("onResume");
        index = 1;
        if (MyApp.getInstance().getDevice_id() != null) {
            deviceId = MyApp.getInstance().getDevice_id();
            deviceKey = "fierce321";
            devicePresent = new IActdevicePresent(this);
            devicePresent.acvDevice();
        }
        MyApp.getInstance().updateAPP();
        videoPlayer.onVideoResume();
        long currentPositionWhenPlaying = MyApp.getInstance().getVideoPosition();
        if (currentPositionWhenPlaying > 0) {
            videoPlayer.setSeekOnStart(currentPositionWhenPlaying);
            videoPlayer.setLooping(true);
            initTransition();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        mlog.e("onStop");
        unregisterReceiver(broadReceiver);
        releasVideo();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApp.getInstance().saveVideoPosition(videoPlayer.getCurrentPositionWhenPlaying());
        videoPlayer.onVideoPause();
        mlog.e("onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mlog.e("onDestroy");
        unbindService(serviceCon);
        videoPlayer.release();
        if (orientationUtils != null)
            orientationUtils.releaseListener();
        //释放所有
        videoPlayer.setStandardVideoAllCallBack(null);
        GSYVideoPlayer.releaseAllVideos();
    }


    private void releasVideo() {
        videoPlayer.release();
        videoPlayer.onVideoReset();
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
//            if (intent.getAction().equals("BindBleService")) {
//                if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
//                    startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE);
//                } else {
//                    MyApp.getInstance().getBleService().startScan();
//                }
////                startActivity(new Intent(getBaseContext(),ElectronicScale_Activity.class));
//            }
        }
    }

    private void registrBodcast() {
        IntentFilter intentFilter = new IntentFilter("PupoState");
        intentFilter.addAction("BindBleService");
        broadReceiver = new MyBroadcastReceiver();
        registerReceiver(broadReceiver, intentFilter);

    }


    private void initTransition() {
        if (isTransition && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition();
            ViewCompat.setTransitionName(videoPlayer, IMG_TRANSITION);
            addTransitionListener();
            startPostponedEnterTransition();
        } else {
            videoPlayer.startPlayLogic();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private boolean addTransitionListener() {
        transition = getWindow().getSharedElementEnterTransition();
        if (transition != null) {
            transition.addListener(new OnTransitionListener() {
                @Override
                public void onTransitionEnd(Transition transition) {
                    super.onTransitionEnd(transition);
                    videoPlayer.startPlayLogic();
                    transition.removeListener(this);
                }
            });
            return true;
        }
        return false;
    }

}
