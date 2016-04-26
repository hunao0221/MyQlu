package com.hugo.myqlu.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @auther Hugo
 * Created on 2016/4/24 9:41.
 */
public class CourseHelper extends SQLiteOpenHelper {

    public CourseHelper(Context context, int version) {
        super(context, "course.db", null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table course(_id integer primary key autoincrement,name varchar(100),time varchar(50),timedetail varchar(200),teacher varchar(50),location varchar(200))");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
