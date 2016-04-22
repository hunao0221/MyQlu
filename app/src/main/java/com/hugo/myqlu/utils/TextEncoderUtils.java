package com.hugo.myqlu.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @auther Hugo
 * Created on 2016/4/22 23:10.
 */
public class TextEncoderUtils {
    public static String encoding(String text) {
        try {
            text = URLEncoder.encode(text, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return text;
    }
}
