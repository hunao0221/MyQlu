package com.hugo.myqlu.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 获取SharedPreferences
 *
 * @author Hugo
 * Created on 2016/4/22 19:23.
 */
public class SpUtil {
    public static SharedPreferences getSp(Context context, String name) {
        return context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }
}
