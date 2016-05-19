package com.hugo.myqlu.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @auther Hugo
 * Created on 2016/4/28 12:48.
 */
public class ExamHelper extends SQLiteOpenHelper {

    public ExamHelper(Context context, int version) {
        super(context, "ksinfo.db", null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table kaoshiinfo(_id integer primary key autoincrement,examname varchar(20),examtime varchar(20),examlocation varcher(20))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
