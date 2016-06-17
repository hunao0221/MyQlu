package com.hugo.myqlu.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author Hugo
 * Created on 2016/4/24 0:09.
 */
public class BaseInfoHelper extends SQLiteOpenHelper {
    public BaseInfoHelper(Context context) {
        super(context, "baseinfo.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table baseinfo(_id integer primary key autoincrement,key varchar(50),value varchar(200))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
