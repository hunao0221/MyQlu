package com.hugo.myqlu.utils;

import com.hugo.myqlu.bean.ZhangBean;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * @auther Hugo
 * Created on 2016/4/28 22:00.
 */
public class ParseLiushui {
    private String response;

    public ParseLiushui(String response) {
        this.response = response;
    }

    public List<ZhangBean> parse() {
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
            zhangBean.setTurnover(tr.child(6).text().trim());
            //现有余额
            zhangBean.setBalance(tr.child(7).text().trim());
            zhangList.add(zhangBean);
        }
        return zhangList;
    }

    public String getTotal() {
        Document document = Jsoup.parse(response);
        Element table = document.getElementById("tables");
        Elements tbody = table.select("tbody");
        Elements trs = tbody.select("tr");
        Element lastTr = trs.get(trs.size() - 1);
        String text = lastTr.text();
        text = text.substring(text.indexOf("总交易额为:") + 6, text.indexOf("（元）")).trim();
        System.out.println("text :" + text);
        return text;
    }
}
