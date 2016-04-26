package com.hugo.myqlu.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hugo.myqlu.R;
import com.hugo.myqlu.bean.SplashImgBean;
import com.hugo.myqlu.utils.SpUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;

public class SplashAvtivity extends AppCompatActivity {
    @Bind(R.id.iv_background)
    ImageView ivBackground;
    @Bind(R.id.tv_author)
    TextView tvAuthor;
    private String jsonUrl = "http://news-at.zhihu.com/api/4/start-image/1080*1776";
    private String author;
    private String imgUrl;
    private Context mContext = this;
    private SharedPreferences sp;
    private boolean isFirstIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash_avtivity);
        ButterKnife.bind(this);

        requestJson();
    }

    public void requestJson() {
        OkHttpUtils.get().url(jsonUrl)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        System.out.println("requestJson-onError :" + e.getMessage());
                        handler.sendEmptyMessageDelayed(0, 3000);
                    }

                    @Override
                    public void onResponse(String response) {
                        System.out.println("requestJson-onResponse");
                        SplashImgBean splashImgBean = new Gson().fromJson(response, SplashImgBean.class);
                        author = splashImgBean.getText();
                        imgUrl = splashImgBean.getImg();
                        initUi();
                    }
                });
    }

    private void initUi() {
        tvAuthor.setText(author);
        OkHttpUtils.get().url(imgUrl)
                .build()
                .connTimeOut(3000)
                .execute(new BitmapCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        System.out.println("initUi-onError :" + e.getMessage());
                        ivBackground.setBackgroundResource(R.drawable.splash);
                        handler.sendEmptyMessageDelayed(0, 3000);
                    }

                    @Override
                    public void onResponse(Bitmap response) {
                        ivBackground.setBackground(new BitmapDrawable(response));
                        handler.sendEmptyMessageDelayed(0, 3000);
                    }
                });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            enterHome();
        }
    };

    private void enterHome() {
        sp = SpUtil.getSp(mContext, "config");
        isFirstIn = sp.getBoolean("isFirstIn", true);
        Intent intent;
        if (isFirstIn) {
            intent = new Intent(mContext, LoginActivity.class);
        } else {
            intent = new Intent(mContext, MainActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
