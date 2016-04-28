package com.hugo.myqlu.utils;

import com.hugo.myqlu.bean.ChengjiBean;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 对url中的中文编码
 *
 * @auther Hugo
 * Created on 2016/4/21 12:01.
 */


public class HtmlUtils {
    private String response;

    public HtmlUtils(String response) {
        this.response = response;
    }

    public String encoder(String response) {
        //http://210.44.159.4/xscj.aspx?xh=201311011011&xm=胡洪源&gnmkdm=N121605

        Document document = Jsoup.parse(response);
        Elements links = document.select("a[href]");
        StringBuffer buffer = new StringBuffer();
        for (Element link : links) {
            if (link.text().equals("成绩查询")) {
                buffer.append(link.attr("href"));
            }
        }
        String url = buffer.toString();
        // String cjcxUrl = mainUrl + "/" + url;
        int indexXM = url.indexOf("xm");
        int indexStrart = url.indexOf("=", indexXM);
        int indexEnd = url.lastIndexOf("&");
        String name = url.substring(indexStrart + 1, indexEnd);
        try {
            String encodeName = URLEncoder.encode(name, "utf-8");
            url = url.replace(name, encodeName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return url;
    }

    public String getXhandName() {
        Document document = Jsoup.parse(response);
        Element xhxm = document.getElementById("xhxm");
        String text = xhxm.text();
        return text;
    }

    public List<ChengjiBean> parseCJTable() {
        List<ChengjiBean> cjList = new ArrayList<>();
        Document document = Jsoup.parse(response);
        Element dataGrid1 = document.getElementById("DataGrid1");
        Elements trs = dataGrid1.select("tbody").select("tr");
        for (int i = 0; i < trs.size(); i++) {
            ChengjiBean bean = new ChengjiBean();
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
            cjList.add(bean);
            System.out.println(bean.toString());
        }
        return cjList;
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
