package com.hugo.myqlu.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Hugo
 * Created on 2016/4/28 21:10.
 */
public class ParseCardInfo {
    public static Map<String, String> parse(String response) {
        Document document = Jsoup.parse(response);
        Elements divs = document.select("div");
        Map<String, String> infoMap = new HashMap<>();
        String name =  divs.get(2).text();
        infoMap.put("name", name);
        String cardId = divs.get(4).text();
        infoMap.put("cardId", cardId);
        Elements trs = document.select("tr");
        Element tr = trs.get(trs.size() - 1);
        String text = tr.text();
        String yuer = text.substring(text.indexOf("：") + 1, text.indexOf("元") + 1).trim();
        String statu = text.substring(text.lastIndexOf("：") + 1, text.length()).trim();
        infoMap.put("statu", statu);
        infoMap.put("yuer", yuer);
        return infoMap;
    }
}
