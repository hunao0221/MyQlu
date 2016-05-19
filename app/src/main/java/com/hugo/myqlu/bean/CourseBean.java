package com.hugo.myqlu.bean;

/**
 * @auther Hugo
 * Created on 2016/4/24 9:50.
 */
public class CourseBean {
    private String courseName;
    private String courseTime;
    private String courstTimeDetail;
    private String courseTeacher;
    private String courseLocation;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCourseLocation() {
        return courseLocation;
    }

    public void setCourseLocation(String courseLocation) {
        this.courseLocation = courseLocation;
    }

    public String getCourstTimeDetail() {
        return courstTimeDetail;
    }

    public void setCourstTimeDetail(String courstTimeDetail) {
        this.courstTimeDetail = courstTimeDetail;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseTeacher() {
        return courseTeacher;
    }

    public void setCourseTeacher(String courseTeacher) {
        this.courseTeacher = courseTeacher;
    }

    public String getCourseTime() {
        return courseTime;
    }

    public void setCourseTime(String courseTime) {
        this.courseTime = courseTime;
    }

    @Override
    public String toString() {
        return getCourseName() + "-" + getCourseTime() + "-" + getCourstTimeDetail() + "-" + getCourseTeacher() + "-" + getCourseLocation();
    }
}
