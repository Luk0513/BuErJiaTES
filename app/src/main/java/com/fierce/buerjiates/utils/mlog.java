package com.fierce.buerjiates.utils;


import android.util.Log;

import com.fierce.buerjiates.BuildConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Formatter;

/**
 * @Author : Lukang
 * >>Live and learn<<
 * blog : http://blog.csdn.net/fight_0513
 * @Date :  2017-08-04
 */
public  class mlog {

    private mlog() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    private static final int V = 0x01;
    private static final int D = 0x02;
    private static final int I = 0x04;
    private static final int W = 0x08;
    private static final int E = 0x10;
    private static final int A = 0x20;
    private static final int JSON = 0xF2;
     // BuildConfig 导包时一定导入当前module的包
    private static boolean isLog = BuildConfig.DEBUG; // log总开关  发布release版本时，BuildConfig.DEBUG 为false;
    private static String sGlobalTag = null; // log标签
    private static boolean sTagIsSpace = true; // log标签是否为空白
    private static boolean isLogBorder = true; // log边框开关

    private static final String TOP_BORDER = "┱┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄";
    private static final String LEFT_BORDER = "┃ ";
    private static final String BOTTOM_BORDER = "┹┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄";
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");//等同"\n"，没有 Windows和Linux的区别
    private static final int MAX_LEN = 1024; //单条log打印长度限制为 4*1024个字符长度 网上多把这个值设置为4000，经测试发现还是会丢失内容
    private static final String NULL_TIPS = "Log with null object.";
    private static final String NULL = "null";
    private static final String ARGS = "args";

    /**
     * @param contents 不需要输入Tag  以类名为Tag
     */
    public static void v(Object contents) {
        log(V, sGlobalTag, contents);
    }

    /**
     * @param tag      输入Tag
     * @param contents 打印内容
     */
    public static void v(String tag, Object... contents) {
        log(V, tag, contents);
    }

    public static void d(Object contents) {
        log(D, sGlobalTag, contents);
    }

    public static void d(String tag, Object... contents) {
        log(D, tag, contents);
    }

    public static void i(Object contents) {
        log(I, sGlobalTag, contents);
    }

    public static void i(String tag, Object... contents) {
        log(I, tag, contents);
    }

    public static void w(Object contents) {
        log(W, sGlobalTag, contents);
    }

    public static void w(String tag, Object... contents) {
        log(W, tag, contents);
    }

    public static void e(Object contents) {
        log(E, sGlobalTag, contents);
    }

    public static void e(String tag, Object... contents) {
        log(E, tag, contents);
    }

    public static void a(Object contents) {
        log(A, sGlobalTag, contents);
    }

    public static void a(String tag, Object... contents) {
        log(A, tag, contents);
    }

    public static void json(String contents) {
        log(JSON, sGlobalTag, contents);
    }

    public static void json(String tag, String contents) {
        log(JSON, tag, contents);
    }


    private static void log(int type, String tag, Object... contents) {
        if (!isLog) return;
        final String[] processContents = processContents(type, tag, contents);
        tag = processContents[0];
        String msg = processContents[1];
        switch (type) {
            case V:
            case D:
            case I:
            case W:
            case E:
            case A:
                printLog(type, tag, msg);
                break;
            case JSON:
                printLog(D, tag, msg);
                break;
        }

    }


    /**
     * @param type
     * @param tag
     * @param contents
     * @return
     */
    private static String[] processContents(int type, String tag, Object... contents) {

        //获取堆栈信息
        // 这里的数组的index=5是根据你工具类的层级做不同的定义，遍历之后就知道啦
        StackTraceElement[] targetElement = Thread.currentThread().getStackTrace();
        String fileName = targetElement[5].getFileName();
        String mTag = fileName.substring(0, fileName.indexOf("."));
        if (!sTagIsSpace) {// 如果全局tag不为空，那就用全局tag
            tag = sGlobalTag;
        } else {// 全局tag为空时，如果传入的tag为空那就显示类名，否则显示tag
            tag = isSpace(tag) ? mTag : tag;
        }

        String headMsg = new Formatter()//严格按（FileName:LineNuber）的格式来写 才可以定位
                .format("Thread: %s, %s`(%s:%d)" + LINE_SEPARATOR,
                        Thread.currentThread().getName(),
                        targetElement[5].getMethodName(),
                        fileName,
                        targetElement[5].getLineNumber())
                .toString();

        //打印内容部分
        String msg = NULL_TIPS;
        if (contents != null) {
            if (contents.length == 1) {//单个msg
                Object object = contents[0];
                msg = object == null ? NULL : object.toString();
                if (type == JSON) {
                    msg = formatJson(msg);
                }
            } else {//传入多个 “msg”
                StringBuilder sb = new StringBuilder();
                for (int i = 0, len = contents.length; i < len; ++i) {
                    Object content = contents[i];
                    sb.append(ARGS)
                            .append("[")
                            .append(i)
                            .append("]")
                            .append(" = ")
                            .append(content == null ? NULL : content.toString())
                            .append(LINE_SEPARATOR);
                }
                msg = sb.toString();
            }
        }
        //拼接左边边宽
        if (isLogBorder) {
            StringBuilder sb = new StringBuilder();
            String[] lines = msg.split(LINE_SEPARATOR);
            for (String line : lines) {

                sb.append(LEFT_BORDER).append(line).append(LINE_SEPARATOR);
            }
            msg = sb.toString();

        }
        return new String[]{tag, headMsg + msg};
    }


    /**
     * @param json 格式化json字符串
     * @return
     */
    private static String formatJson(String json) {
        try {
            if (json.startsWith("{")) {
                json = new JSONObject(json).toString(4);
            } else if (json.startsWith("[")) {
                json = new JSONArray(json).toString(4);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * 打印完整日志
     *
     * @param type
     * @param tag
     * @param msg
     */
    private static void printLog(int type, String tag, String msg) {

        if (isLogBorder)//打印上边框
            printBorder(type, tag, true);

        //防止 日志长度超限 导致日志信息打印不全
        int len = msg.length();
        int countOfSub = len / MAX_LEN;
        int index = 0;
        if (countOfSub > 0) {
            String sub;
            for (int i = 0; i < countOfSub; i++) {
                sub = msg.substring(index, index + MAX_LEN);
                printSubLog(type, tag, sub);
                index += MAX_LEN;
            }
            printSubLog(type, tag, msg.substring(index, len));
        } else {
            printSubLog(type, tag, msg);
        }
        if (isLogBorder)  //打印下边框
            printBorder(type, tag, false);
    }

    /**
     * 打印日志内容
     *
     * @param type
     * @param tag
     * @param msg
     */
    private static void printSubLog(final int type, final String tag, String msg) {
        if (isLogBorder)
            msg = LEFT_BORDER + msg;
        switch (type) {
            case V:
                Log.v(tag, msg);
                break;
            case D:
                Log.d(tag, msg);
                break;
            case I:
                Log.i(tag, msg);
                break;
            case W:
                Log.w(tag, msg);
                break;
            case E:
                Log.e(tag, msg);
                break;
            case A:
                Log.wtf(tag, msg);
                break;
        }
    }

    /**
     * 打印日志边框
     *
     * @param type
     * @param tag
     * @param isTop
     */
    private static void printBorder(int type, String tag, boolean isTop) {
        String border = isTop ? TOP_BORDER : BOTTOM_BORDER;
        switch (type) {
            case V:
                Log.v(tag, border);
                break;
            case D:
                Log.d(tag, border);
                break;
            case I:
                Log.i(tag, border);
                break;
            case W:
                Log.w(tag, border);
                break;
            case E:
                Log.e(tag, border);
                break;
            case A:
                Log.wtf(tag, border);
                break;
        }
    }

    private static boolean isSpace(String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}