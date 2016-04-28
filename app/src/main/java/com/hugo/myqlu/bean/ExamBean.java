package com.hugo.myqlu.bean;


/**
 * @auther Hugo
 * Created on 2016/4/28 12:11.
 */
public class ExamBean {
    private String examName;
    private String examLocation;
    private String examTime;


    public String getExamLocation() {
        return examLocation;
    }

    public void setExamLocation(String examLocation) {
        this.examLocation = examLocation;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public String getExamTime() {
        return examTime;
    }

    public void setExamTime(String examTime) {
        this.examTime = examTime;
    }

    @Override
    public String toString() {
        return "ExamBean{" +
                ", examName='" + examName + '\'' +
                ", examLocation='" + examLocation + '\'' +
                ", examTime='" + examTime + '\'' +
                '}';
    }
}
