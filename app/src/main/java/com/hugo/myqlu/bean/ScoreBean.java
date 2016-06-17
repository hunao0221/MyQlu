package com.hugo.myqlu.bean;

/**
 * @author Hugo
 * Created on 2016/4/21 14:31.
 */
public class ScoreBean {
    private String courseId;
    private String courseName;
    private String courseXz;
    private String courseCj;
    private String courseGs;
    private String courseBk;
    private String courseCx;
    private String courseXf;
    private String courseBj;

    public String getCourseBj() {
        return courseBj;
    }

    public void setCourseBj(String courseBj) {
        this.courseBj = courseBj;
    }

    public String getCourseBk() {
        return courseBk;
    }

    public void setCourseBk(String courseBk) {
        this.courseBk = courseBk;
    }

    public String getCourseCj() {
        return courseCj;
    }

    public void setCourseCj(String courseCj) {
        this.courseCj = courseCj;
    }

    public String getCourseCx() {
        return courseCx;
    }

    public void setCourseCx(String courseCx) {
        this.courseCx = courseCx;
    }

    public String getCourseGs() {
        return courseGs;
    }

    public void setCourseGs(String courseGs) {
        this.courseGs = courseGs;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseXf() {
        return courseXf;
    }

    public void setCourseXf(String courseXf) {
        this.courseXf = courseXf;
    }

    public String getCourseXz() {
        return courseXz;
    }

    public void setCourseXz(String courseXz) {
        this.courseXz = courseXz;
    }

    @Override
    public String toString() {
        return "ScoreBean{" +
                courseId + '\'' +
                courseBj + '\'' +
                courseName + '\'' +
                courseXz + '\'' +
                courseCj + '\'' +
                courseGs + '\'' +
                courseBk + '\'' +
                courseCx + '\'' +
                courseXf + '\''
                ;
    }
}
