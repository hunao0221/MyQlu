package com.hugo.myqlu.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hugo.myqlu.bean.CourseBean;
import com.hugo.myqlu.db.CourseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @auther Hugo
 * Created on 2016/4/24 10:16.
 */
public class CourseDao {
    private Context mContext;
    private CourseBean course;

    public CourseDao(Context mContext) {
        this.mContext = mContext;
    }

    public boolean add(String name, String time, String timedetail, String teacher, String location) {
        CourseHelper helper = new CourseHelper(mContext, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("time", time);
        values.put("timedetail", timedetail);
        values.put("teacher", teacher);
        values.put("location", location);
        long id = db.insert("course", null, values);
        return id != -1;
    }

    public List<CourseBean> query(String time) {
        CourseHelper helper = new CourseHelper(mContext, 1);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("course", null, "time=?", new String[]{time}, null, null, null);
        List<CourseBean> dayCoueseList = new ArrayList<>();
        while (cursor.moveToNext()) {
            CourseBean course = new CourseBean();
            course.setId(cursor.getInt(0));
            course.setCourseName(cursor.getString(1));
            course.setCourseTime(cursor.getString(2));
            course.setCourstTimeDetail(cursor.getString(3));
            course.setCourseTeacher(cursor.getString(4));
            course.setCourseLocation(cursor.getString(5));
            dayCoueseList.add(course);
        }
        cursor.close();
        db.close();
        return dayCoueseList;
    }

    public List<CourseBean> queryAll() {
        CourseHelper helper = new CourseHelper(mContext, 1);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("course", null, null, null, null, null, "time asc");
        List<CourseBean> allCourseList = new ArrayList<>();
        while (cursor.moveToNext()) {
            CourseBean course = new CourseBean();
            course.setId(cursor.getInt(0));
            course.setCourseName(cursor.getString(1));
            course.setCourseTime(cursor.getString(2));
            course.setCourstTimeDetail(cursor.getString(3));
            course.setCourseTeacher(cursor.getString(4));
            course.setCourseLocation(cursor.getString(5));
            allCourseList.add(course);
        }
        cursor.close();
        db.close();
        return allCourseList;
    }

    public CourseBean queryById(String id) {
        CourseHelper helper = new CourseHelper(mContext, 1);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("course", null, "_id=?", new String[]{id}, null, null, null);
        if (cursor.moveToNext()) {
            course = new CourseBean();
            course.setId(cursor.getInt(0));
            course.setCourseName(cursor.getString(1));
            course.setCourseTime(cursor.getString(2));
            course.setCourstTimeDetail(cursor.getString(3));
            course.setCourseTeacher(cursor.getString(4));
            course.setCourseLocation(cursor.getString(5));
        }
        cursor.close();
        db.close();
        return course;
    }

    public boolean update(String id, String name, String time, String timeDetail, String teacher, String location) {
        CourseHelper helper = new CourseHelper(mContext, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("time", time);
        values.put("timedetail", timeDetail);
        values.put("teacher", teacher);
        values.put("location", location);
        int course = db.update("course", values, "_id=?", new String[]{id});
        return course == 1;
    }

    public boolean deleteAll() {
        CourseHelper helper = new CourseHelper(mContext, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        int delete = db.delete("course", null, null);
        db.close();
        return delete != 0;
    }

    public boolean delete(String id) {
        CourseHelper helper = new CourseHelper(mContext, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        int delete = db.delete("course", "_id=?", new String[]{id});
        db.close();
        return delete != 0;
    }

}
