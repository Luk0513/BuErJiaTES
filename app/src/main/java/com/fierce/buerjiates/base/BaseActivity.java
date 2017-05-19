package com.fierce.buerjiates.base;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.fierce.buerjiates.https.HttpManage;
import com.fierce.buerjiates.services.DownAPKService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by Administrator on 2017/3/11.
 */

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideNavigationBar();
        setContentView(getLayoutRes());
        ButterKnife.bind(this);
        initView();
        updateAPP();
    }

    protected abstract int getLayoutRes();

    protected abstract void initView();

    public void showToast(final String msg) {
        //直接将弹出 Toast 方法方在子线程 提高方法的复用性
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, -250);
                toast.show();
            }
        });
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
        String savePath = downloadFile.getAbsolutePath();
        //先检查本地文件夹有没有apk文件
        File[] mfile = new File(savePath).listFiles();
        //查看apk版本号
        final File apk = mfile[0];
        String apkPath = apk.getAbsolutePath();
        PackageManager pm = this.getPackageManager();
        //获取本地apk文件的包名 版本号
        PackageInfo packageInfo = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
        int code = packageInfo.versionCode;
        String packageName = packageInfo.packageName;
//        Log.e("TAG", "updateAPP:<>><><><><><><><>< >>" + code + "    " + packageName);
        if (code > getAppVersion() && packageName.equals(this.getPackageName())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("更新提示：")
                    .setMessage("发现新版本安装包！" +
                            "\n请立即更新")
                    .setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            installApk(apk);
                        }
                    });
            builder.setCancelable(false);
            builder.show();

        } else {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://120.76.159.2:8090/admin/")
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
            Call<String> call = retrofit.create(HttpManage.class).updateApp();
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Log.e("TAG", "onResponse: __________" + response.body());
                    try {
                        JSONObject object = new JSONObject(response.body());
                        JSONArray array = object.getJSONArray("list");
                        JSONObject object2 = array.getJSONObject(0);
                        String versionTexe = object2.optString("versionText");
                        //服务器Apk 版本号
                        double versionCode = Double.valueOf(versionTexe).doubleValue();
//                    Log.e("TAG", "onResponse: " + versionCode);
                        //本地应用版本号
                        if (versionCode > getAppVersion()) {
                            //后台下载Apk
                            String apkUrl = object2.optString("filePath");
                            Intent intent = new Intent(getBaseContext(), DownAPKService.class);
                            intent.putExtra("url", apkUrl);
                            startService(intent);
//                        Log.e("TAG", "onResponse: versionCode > getAppVersion())");
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

    public Activity getActivity() {
        return this;
    }

    /**
     * Android 4.4以上才有效果
     * 沉浸式状态栏 导航栏
     *
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            hideNavigationBar();
        }
        hideNavigationBar();
    }

    //todo 隐藏导航栏
    public void hideNavigationBar() {
        int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN; // hide status bar

        if (Build.VERSION.SDK_INT >= 19) {
            //SYSTEM_UI_FLAG_IMMERSIVE_STICKY: hide navigation bars - compatibility:
            // building API level is lower thatn 19, use magic number directly for
            // higher API target level
            uiFlags |= 0x00001000;
        } else {
            uiFlags |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
        }

        getWindow().getDecorView().setSystemUiVisibility(uiFlags);
    }

    //打开APK程序代码
    private void installApk(File file) {
        // TODO Auto-generated method stub
        Log.e("OpenFile", file.getName());
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        startActivity(intent);
    }
}
