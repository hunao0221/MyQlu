package com.hugo.myqlu.utils;

import com.hugo.myqlu.bean.ZhangBean;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Hugo
 * Created on 2016/5/4 20:06.
 */
public class ParseHistoryInfo {
    public static String getMainAction(String response) {
        Document document = Jsoup.parse(response);
        Element from = document.getElementById("accounthisTrjn1");
        return from.attr("action");
    }

    public static String getQueryAction(String response) {
        Document document = Jsoup.parse(response);
        Element from = document.getElementById("accounthisTrjn2");
        return from.attr("action");
    }

    public static List<String> getDayList(String response) {
        Document document = Jsoup.parse(response);
        Elements as = document.select("a");

        List<String> dayList = new ArrayList<>();
        for (Element a : as) {
            dayList.add(a.text().trim());
        }
        return dayList;
    }

    public static String getLastAction(String response) {
        Document document = Jsoup.parse(response);
        return document.select("form").attr("action");
    }

    public static String getHistoryTotal(String response) {
        Document document = Jsoup.parse(response);
        Element table = document.getElementById("tables");
        Elements tbody = table.select("tbody");
        Elements trs = tbody.select("tr");
        Element lastTr = trs.get(trs.size() - 1);
        String text = lastTr.text();
        text = text.substring(text.indexOf("总计交易额为:") + 7, text.indexOf("（元）")).trim();
        return text;
    }

    public static List<ZhangBean> get(String response) {
        Document document = Jsoup.parse(response);
        Element table = document.getElementById("tables");
        Elements tbody = table.select("tbody");
        Elements trs = tbody.select("tr");
        List<ZhangBean> zhangList = new ArrayList<>();
        for (int i = 1; i < trs.size() - 1; i++) {
            Element tr = trs.get(i);
            ZhangBean zhangBean = new ZhangBean();
            //交易时间
            String text = tr.child(0).text();
            zhangBean.setTime(text);
            //交易终端
            zhangBean.setTerminal(tr.child(4).text().trim());
            //交易额
            zhangBean.setTurnover(tr.child(5).text().trim());
            //现有余额
            zhangBean.setBalance(tr.child(6).text().trim());
            zhangList.add(zhangBean);
        }
        return zhangList;
    }

    /**
     * 获得总页数
     *
     * @param response
     * @return
     */
    public static int getTotalPages(String response) {
        Document document = Jsoup.parse(response);
        Element table = document.getElementById("tables");
        Elements tbody = table.select("tbody");
        Elements trs = tbody.select("tr");
        Element lastTr = trs.get(trs.size() - 1);
        String text = lastTr.text();
        text = text.replaceAll("\\s", "");
        String pages = text.substring(text.indexOf("（元）") + 4, text.indexOf("页"));
        return Integer.parseInt(pages);
    }

}
