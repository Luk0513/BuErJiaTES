package com.fierce.buerjiates.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.fierce.buerjiates.R;


/**
 * Created by Administrator on 2016-11-23.
 */

public class CustomDialog extends Dialog {

    private static final String TAG = "CustomDialog";

    private CustomDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    private CustomDialog(Context context) {
        super(context);
        Dialog dialog;
    }

    private interface Callback {
        void onTaskFinish();
    }

    //异步任务
    public void task(final Runnable run, final Callback callback) {
        new Thread() {
            @Override
            public void run() {
                run.run();
                if (callback != null)
                    callback.onTaskFinish();
            }
        }.start();
    }

    /**
     * @param time 设置 显示时长 自动关闭
     */
    public void showDialog(final long time) {
        super.show();
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                dismiss();
            }
        };
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    sleep(time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(1);
            }
        }.start();

    }


    /**
     * 内部类 Builder
     * Dialog 相关样式设置
     */
    public static class Builder {
        private final Context mcontex;

        public Builder(Context context) {
            this.mcontex = context;
        }

        //参数
        private boolean isFloating = true;//是否为浮动
        private String title;   //设置标题
        private boolean isFullscreen = false;  //是否全屏

        private View view; //对话框布局

        public Builder setIsFloating(Boolean isFloating) {
            this.isFloating = isFloating;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setLayout(int res) {
            view = View.inflate(mcontex, res, null);
            return this;
        }

        public Builder setView(View view) {
            this.view = view;
            return this;
        }

        //设置全屏（默认不全屏）
        public Builder setIsFullscreen(Boolean isFull) {
            this.isFullscreen = isFull;
            return this;
        }

        public Builder setViewOnClike(int id, View.OnClickListener listener) {
            View view = this.view.findViewById(id);
            view.setOnClickListener(listener);
            return this;
        }

        public Builder setViewOnClike(int id, AdapterView.OnItemClickListener listener) {
            View view = this.view.findViewById(id);
            if (view instanceof AdapterView) {
                AdapterView v = (AdapterView) view;
                v.setOnItemClickListener(listener);
            }
            return this;
        }

        /**
         * @return 创建CustomeDialog实例
         */
        public CustomDialog build() {
            CustomDialog dialog;

            if (isFloating) {
                if (TextUtils.isEmpty(title)) {
                    dialog = new CustomDialog(mcontex, R.style.Dialog_NoTitle);
                    if (isFullscreen) {
                        dialog = new CustomDialog(mcontex, R.style.Dialog_FullScreen_NoTitle);
                    }
                } else {
                    dialog = new CustomDialog(mcontex);
                }

            } else {
                //全屏
                if (isFullscreen) {
                    if (TextUtils.isEmpty(title)) {
                        dialog = new CustomDialog(mcontex, R.style.Dialog_FullScreen_NoTitle2);
                    } else {
                        dialog = new CustomDialog(mcontex, R.style.Dialog_FullScreen);
                    }
                } else {
                    if (TextUtils.isEmpty(title)) {
                        //不浮动没有title 不全屏
                        dialog = new CustomDialog(mcontex, R.style.Dialog_NoTitle_NoFloating);
                    } else {
                        //不浮动
                        dialog = new CustomDialog(mcontex, R.style.Dialog_NoFloating);
                    }
                }
            }
            if (view == null) {
                throw new IllegalArgumentException("view 不能为空");
            }
            dialog.setContentView(view);
            return dialog;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }


    private void hideNavigationBar() {
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

    @Override
    protected void onStart() {
        super.onStart();
        hideNavigationBar();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideNavigationBar();
    }
}
