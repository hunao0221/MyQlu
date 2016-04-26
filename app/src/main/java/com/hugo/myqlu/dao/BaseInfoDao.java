package com.hugo.myqlu.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hugo.myqlu.db.BaseInfoHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * @auther Hugo
 * Created on 2016/4/24 0:16.
 */
public class BaseInfoDao {
    private Context mContext;

    public BaseInfoDao(Context context) {
        mContext = context;
    }

    //添加url到数据库
    public boolean add(String key, String value) {
        BaseInfoHelper helper = new BaseInfoHelper(mContext);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("key", key);
        values.put("value", value);
        long id = db.insert("baseinfo", null, values);
        return id != -1;
    }

    public String query(String key) {
        BaseInfoHelper helper = new BaseInfoHelper(mContext);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("baseinfo", new String[]{"value"}, "key=?", new String[]{key}, null, null, null);
        String value = null;
        if (cursor.moveToNext()) {
            value = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return value;
    }

    public Map<String, String> queryAll() {
        BaseInfoHelper helper = new BaseInfoHelper(mContext);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("baseinfo", new String[]{"key", "value"}, null, null, null, null, null, null);
        Map<String, String> map = new HashMap<>();
        while (cursor.moveToNext()) {
            String key = cursor.getString(0);
            String value = cursor.getString(1);
            System.out.println(key + "  :" + value);
            map.put(key, value);
        }
        cursor.close();
        db.close();
        return map;
    }

    public boolean delete(String key) {
        BaseInfoHelper helper = new BaseInfoHelper(mContext);
        SQLiteDatabase db = helper.getWritableDatabase();
        int delete = db.delete("baseinfo", "key=?", new String[]{key});
        return delete != 0;
    }

    public boolean deleteAll() {
        BaseInfoHelper helper = new BaseInfoHelper(mContext);
        SQLiteDatabase db = helper.getWritableDatabase();
        int delete = db.delete("baseinfo", null, null);
        return delete != 0;
    }

}
