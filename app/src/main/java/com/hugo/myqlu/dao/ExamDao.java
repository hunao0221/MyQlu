package com.hugo.myqlu.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hugo.myqlu.bean.ExamBean;
import com.hugo.myqlu.db.ExamHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @auther Hugo
 * Created on 2016/4/28 13:23.
 */
public class ExamDao {
    private Context mContext;

    public ExamDao(Context mContext) {
        this.mContext = mContext;
    }

    public boolean add(String examname, String examtime, String examlocation) {
        ExamHelper helper = new ExamHelper(mContext, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("examname", examname);
        values.put("examtime", examtime);
        values.put("examlocation", examlocation);
        long kaosiinfo = db.insert("kaoshiinfo", null, values);
        return kaosiinfo != -1;
    }

    public boolean query(String name) {
        boolean flag = false;
        ExamHelper helper = new ExamHelper(mContext, 1);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("kaoshiinfo", null, "examname=?", new String[]{name}, null, null, null, null);
        if (cursor.moveToNext()) {
            flag = true;
        }
        cursor.close();
        db.close();
        return flag;
    }


    public List<ExamBean> queryAll() {
        ExamHelper helper = new ExamHelper(mContext, 1);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("kaoshiinfo", null, null, null, null, null, null);
        List<ExamBean> examList = new ArrayList<>();
        while (cursor.moveToNext()) {
            ExamBean examBean = new ExamBean();
            String examName = cursor.getString(1);
            String examtime = cursor.getString(2);
            String examlocation = cursor.getString(3);
            examBean.setExamName(examName);
            examBean.setExamTime(examtime);
            examBean.setExamLocation(examlocation);
            examList.add(examBean);
        }
        cursor.close();
        db.close();
        return examList;
    }

    public boolean update(String name, String time, String location) {
        ExamHelper helper = new ExamHelper(mContext, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("examtime", time);
        values.put("examlocation", location);
        int update = db.update("kaoshiinfo", values, "examname=?", new String[]{name});
        return update != 0;
    }

    public void deleteAll() {
        ExamHelper helper = new ExamHelper(mContext, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("kaoshiinfo", null, null);
    }
}
