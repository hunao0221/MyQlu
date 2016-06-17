package com.hugo.myqlu.utils;

import com.hugo.myqlu.bean.ScoreBean;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * 对url中的中文编码
 *
 * @author Hugo
 * Created on 2016/4/21 12:01.
 */


public class HtmlUtils {
    private String response;

    public HtmlUtils(String response) {
        this.response = response;
    }


    public String getXhandName() {
        Document document = Jsoup.parse(response);
        Element xhxm = document.getElementById("xhxm");
        String text = xhxm.text();
        return text;
    }

    public List<ScoreBean> parseScore() {
        List<ScoreBean> scoreList = new ArrayList<>();
        Document document = Jsoup.parse(response);
        Element dataGrid1 = document.getElementById("DataGrid1");
        Elements trs = dataGrid1.select("tbody").select("tr");
        for (int i = 0; i < trs.size(); i++) {
            ScoreBean bean = new ScoreBean();
            Elements tds = trs.get(i).select("td");
            for (int j = 0; j < tds.size(); j++) {
                switch (j) {
                    case 0:
                        bean.setCourseId(tds.get(j).text());
                        break;
                    case 1:
                        bean.setCourseName(tds.get(j).text());
                        break;
                    case 2:
                        bean.setCourseXz(tds.get(j).text());
                        break;
                    case 3:
                        bean.setCourseCj(tds.get(j).text());
                        break;
                    case 4:
                        bean.setCourseGs(tds.get(j).text());
                        break;
                    case 5:
                        bean.setCourseBk(tds.get(j).text());
                        break;
                    case 6:
                        bean.setCourseCx(tds.get(j).text());
                        break;
                    case 7:
                        bean.setCourseXf(tds.get(j).text());
                        break;
                    case 8:
                        bean.setCourseBj(tds.get(j).text());
                        break;
                }
            }
            scoreList.add(bean);
        }
        return scoreList;
    }

    /**
     * 返回成绩查询页面的年份集合
     *
     * @return
     */
    public List<String> parseSelectYearList() {
        Document document = Jsoup.parse(response);
        Element select = document.getElementById("ddlXN");
        Elements options = select.select("option");
        List<String> tempList = new ArrayList<>();
        List<String> yearList = new ArrayList<>();
        for (Element option : options) {
            tempList.add(option.text());
        }
        for (int j = tempList.size() - 1; j > 0; j--) {
            yearList.add(tempList.get(j));
        }
        return yearList;
    }

}
