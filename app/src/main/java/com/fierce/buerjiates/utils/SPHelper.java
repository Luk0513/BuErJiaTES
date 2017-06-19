package com.fierce.buerjiates.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2016/11/29.
 */

public class SPHelper {

    private SharedPreferences sp;

    /**
     * @param context 上下文
     * @param name    数据库名称
     */
    public SPHelper(Context context, String name) {
        sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }


    //存储多少种数据
    //int boolean float long String
    //存储方法
    public void save(ContentValue... contentValue) {
        SharedPreferences.Editor et = sp.edit();
        for (ContentValue value : contentValue) {
            if (value.value instanceof String) {
                et.putString(value.key, (String) value.value);
            } else if (value.value instanceof Integer)
                et.putInt(value.key, Integer.parseInt(value.value.toString()));
            else if (value.value instanceof Boolean) {
                et.putBoolean(value.key, Boolean.parseBoolean(value.value.toString()));
            } else if (value.value instanceof Long) {
                et.putLong(value.key, Long.parseLong(value.value.toString()));
            }
        }
        et.apply();
    }

    public int getInt(String key) {
        return sp.getInt(key, -1);
    }

    public int getInt(String key, int defValue) {
        return sp.getInt(key, defValue);
    }

    public boolean getBoolean(String key) {
        return sp.getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean defValue) {
        return sp.getBoolean(key, defValue);
    }

    public String getString(String key) {
        return sp.getString(key, null);
    }

    public String getString(String key, String defValue) {
        return sp.getString(key, defValue);
    }

    public long getLong(String key) {
        return sp.getLong(key, -1);
    }

    public long getLong(String key, long defvalue) {
        return sp.getLong(key, defvalue);
    }


    public void clear() {
        sp.edit().clear().apply();
    }

    public static class ContentValue {
        String key;
        Object value;

        public ContentValue(String key, Object value) {
            this.key = key;
            this.value = value;
        }
    }

}
