package com.hugo.myqlu.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hugo.myqlu.bean.ExamBean;
import com.hugo.myqlu.db.KsHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @auther Hugo
 * Created on 2016/4/28 13:23.
 */
public class KaoshiDao {
    private Context mContext;

    public KaoshiDao(Context mContext) {
        this.mContext = mContext;
    }

    public boolean add(String examname, String examtime, String examlocation) {
        KsHelper helper = new KsHelper(mContext, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("examname", examname);
        values.put("examtime", examtime);
        values.put("examlocation", examlocation);
        long kaosiinfo = db.insert("kaoshiinfo", null, values);
        return kaosiinfo != -1;
    }

    public List<ExamBean> queryAll() {
        KsHelper helper = new KsHelper(mContext, 1);
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

    public void deleteAll() {
        KsHelper helper = new KsHelper(mContext, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        int kaoshi = db.delete("kaoshiinfo", null, null);
    }
}
