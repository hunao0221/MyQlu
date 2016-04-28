package com.hugo.myqlu.utils;

import com.hugo.myqlu.bean.ExamBean;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * @auther Hugo
 * Created on 2016/4/28 12:04.
 */
public class ParseKSInfoFromHtml {
    public static List<ExamBean> parse(String response) {
        Document document = Jsoup.parse(response);
        Element table = document.getElementById("DataGrid1");
        Elements tbody = table.select("tbody");
        Elements trs = tbody.select("tr");
        trs.get(0).remove();
        List<ExamBean> ksList = new ArrayList<>();
        for (int i = 1; i < trs.size(); i++) {
            Element tr = trs.get(i);
            ExamBean examBean = new ExamBean();
            examBean.setExamName(tr.child(1).text());
            examBean.setExamTime(tr.child(3).text());
            examBean.setExamLocation(tr.child(4).text());
            ksList.add(examBean);
        }
        return ksList;
    }
}
