package com.hugo.myqlu.utils;

import com.hugo.myqlu.bean.CourseBean;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * @auther Hugo
 * Created on 2016/4/23 8:56.
 */
public class PareseKbFromHtml {

    private static List<CourseBean> courseList = new ArrayList<>();

    public static List<CourseBean> getKB(String response) {
        Document document = Jsoup.parse(response);
        Element table1 = document.getElementById("Table1");
        //拿到tbody
        Element tbody = table1.select("tbody").get(0);
        //去除前面两行 ，时间和早晨
        tbody.child(0).remove();
        tbody.child(0).remove();
        //去除上午，下午，晚上
        tbody.child(0).child(0).remove();
        tbody.child(4).child(0).remove();
        tbody.child(8).child(0).remove();
        //去除无用行之后，还剩余
        Elements trs = tbody.select("tr");
        int rowNum = trs.size();
        //用map储存数据
        for (int i = 0; i < rowNum; i++) {
            if (i % 2 == 0) {
                //拿到每一行
                Element tr = trs.get(i);
                //一共有多少列
                int columnNum = tr.childNodeSize() - 1;
                for (int j = 1; j < columnNum - 1; j++) {
                    Element colum = tr.child(j);
                    if (colum.hasAttr("rowspan")) {
                        CourseBean course = new CourseBean();
                        String text = colum.text();
                        System.out.println("所有课表 ：" + text);
                        //基于java的web开发(JSP/Sevlet) 周三第5,6节{第1-16周} 尹红丽 1号公教楼602
                        String[] strings = text.split(" ");
                        course.setCourseName(strings[0]);
                        String timeInfos = strings[1];
                        String time = timeInfos.substring(0, 2);
                        course.setCourseTime(setWeek(time));
                        course.setCourstTimeDetail(timeInfos.substring(2, timeInfos.length()).replace(",", "-"));
                        course.setCourseTeacher(strings[2]);
                        course.setCourseLocation(strings[3]);
                        courseList.add(course);
                    }
                }
            }
        }

        for (CourseBean course : courseList) {
            System.out.println(course.toString());
        }
        return courseList;
    }

    public static String setWeek(String text) {
        if (text.equals("周一")) {
            text = "1";
        } else if (text.equals("周二")) {
            text = "2";

        } else if (text.equals("周三")) {
            text = "3";

        } else if (text.equals("周四")) {
            text = "4";

        } else if (text.equals("周五")) {
            text = "5";

        } else if (text.equals("周六")) {
            text = "6";

        } else if (text.equals("周日")) {
            text = "7";
        }
        return text;
    }
}