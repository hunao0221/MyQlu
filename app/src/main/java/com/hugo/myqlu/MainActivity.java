package com.hugo.myqlu;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String codeUri = "http://210.44.159.4/CheckCode.aspx";
    String loginUri = "http://210.44.159.4/default2.aspx";
    ////xscj.aspx?xh=201311011011&xm=胡洪源&gnmkdm=N121605

    String chengjiUri = "http://210.44.159.4/xscj.aspx?xh=201311011011&xm=%BA%FA%BA%E9%D4%B4&gnmkdm=N121605";
    String chengjitou = "dDwtNjI5MTUzMDY1O3Q8O2w8aTwxPjs%2BO2w8dDw7bDxpPDE%2BO2k8MTU%2BO2k8MTc%2BO2k8MjM%2BO2k8MjU%2BO2k8Mjc%2BO2k8Mjk%2BO2k8MzA%2BO2k8MzI%2BO2k8MzQ%2BO2k8MzY%2BO2k8NDY%2BO2k8NTA%2BOz47bDx0PHQ8O3Q8aTwxNz47QDxcZTsyMDAxLTIwMDI7MjAwMi0yMDAzOzIwMDMtMjAwNDsyMDA0LTIwMDU7MjAwNS0yMDA2OzIwMDYtMjAwNzsyMDA3LTIwMDg7MjAwOC0yMDA5OzIwMDktMjAxMDsyMDEwLTIwMTE7MjAxMS0yMDEyOzIwMTItMjAxMzsyMDEzLTIwMTQ7MjAxNC0yMDE1OzIwMTUtMjAxNjsyMDE2LTIwMTc7PjtAPFxlOzIwMDEtMjAwMjsyMDAyLTIwMDM7MjAwMy0yMDA0OzIwMDQtMjAwNTsyMDA1LTIwMDY7MjAwNi0yMDA3OzIwMDctMjAwODsyMDA4LTIwMDk7MjAwOS0yMDEwOzIwMTAtMjAxMTsyMDExLTIwMTI7MjAxMi0yMDEzOzIwMTMtMjAxNDsyMDE0LTIwMTU7MjAxNS0yMDE2OzIwMTYtMjAxNzs%2BPjs%2BOzs%2BO3Q8cDw7cDxsPG9uY2xpY2s7PjtsPHByZXZpZXcoKVw7Oz4%2BPjs7Pjt0PHA8O3A8bDxvbmNsaWNrOz47bDx3aW5kb3cuY2xvc2UoKVw7Oz4%2BPjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOWtpuWPt%2B%2B8mjIwMTMxMTAxMTAxMTs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w85aeT5ZCN77ya6IOh5rSq5rqQOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzlrabpmaLvvJrnkIblrabpmaI7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOS4k%2BS4mu%2B8mjs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w85L%2Bh5oGv5LiO6K6h566X56eR5a2mOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzooYzmlL%2Fnj63vvJrkv6HorqExMy0xOz4%2BOz47Oz47dDxAMDw7Ozs7Ozs7Ozs7Pjs7Pjt0PEAwPDs7Ozs7Ozs7Ozs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8U0RJTEk7Pj47Pjs7Pjt0PEAwPDs7Ozs7Ozs7Ozs%2BOzs%2BOz4%2BOz4%2BOz4gupjjgeaqDTeBZAvvjfAD7Ze0rw%3D%3D";
    @Bind(R.id.tv_content)
    TextView tvContent;

    private Toolbar toolbar;
    private ImageView iv_user_heead;
    private Context mContext = this;
    private DrawerLayout drawer;
    private EditText et_username;
    private EditText et_password;
    private EditText etCode;
    private TextView tvChange;
    private ImageView ivCode;
    private TextView tvCancle;
    private TextView tvOk;
    private String username;
    private String password;
    private String tvCode;
    private AlertDialog alertDialog;
    private TextView tvError;
    private ProgressBar pbLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initView();
        initListener();
    }


    private void initView() {

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View view = navigationView.getHeaderView(0);
        iv_user_heead = (ImageView) view.findViewById(R.id.iv_user_head);

    }

    private void initListener() {
        iv_user_heead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
                showLoginDialog();
            }
        });

    }

    /**
     * 登陆弹窗
     */
    public void showLoginDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View view = View.inflate(mContext, R.layout.dialog_login, null);
        initDialogView(view);
        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);
    }

    private void initDialogView(View view) {
        et_username = ButterKnife.findById(view, R.id.et_username);
        et_password = ButterKnife.findById(view, R.id.et_password);
        etCode = ButterKnife.findById(view, R.id.et_code);
        tvChange = ButterKnife.findById(view, R.id.tv_change);
        ivCode = ButterKnife.findById(view, R.id.iv_codes);
        tvCancle = ButterKnife.findById(view, R.id.tv_cancle);
        tvOk = ButterKnife.findById(view, R.id.tv_ok);
        tvError = ButterKnife.findById(view, R.id.tv_error);
        pbLogin = ButterKnife.findById(view, R.id.pb_login);
        showCodeimage();
        MyOnClickListener listener = new MyOnClickListener();
        tvChange.setOnClickListener(listener);
        tvOk.setOnClickListener(listener);
        tvCancle.setOnClickListener(listener);
    }

    class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_change:
                    changCodeImage();
                    break;
                case R.id.tv_ok:
                    attemptLogin();
                    break;
                case R.id.tv_cancle:
                    alertDialog.dismiss();
                    break;
            }
        }
    }

    //加载验证码
    public void showCodeimage() {
        OkHttpUtils.get().url(codeUri).build()
                .connTimeOut(5000)
                .execute(new BitmapCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        System.out.println("验证码加载失败");
                    }

                    @Override
                    public void onResponse(Bitmap response) {
                        System.out.println("验证码加载成功");
                        ivCode.setImageBitmap(response);
                    }
                });
    }

    //切换验证码
    public void changCodeImage() {
        codeUri += '?';
        showCodeimage();
    }

    private void attemptLogin() {

        View focusView = null;
        System.out.println("检查登陆");
        username = this.et_username.getText().toString().trim();
        password = this.et_password.getText().toString().trim();
        tvCode = etCode.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            this.et_username.setError("学号不能为空");
            focusView = et_username;
        } else if (TextUtils.isEmpty(password)) {
            this.et_password.setError("密码不能为空");
            focusView = et_password;
        } else if (TextUtils.isEmpty(tvCode)) {
            etCode.setError("验证码不能为空");
            focusView = etCode;
        }
        if (focusView != null) {
            focusView.requestFocus();
        } else {
            //向服务器请求登陆
            System.out.println("请求服务器");
            requestLogin();
        }
    }

    /**
     * 想服务器模拟登陆
     */
    private void requestLogin() {
        int visibility = tvError.getVisibility();
        if (visibility == View.VISIBLE) {
            tvError.setVisibility(View.INVISIBLE);
        }
        pbLogin.setVisibility(View.VISIBLE);
        final PostFormBuilder post = OkHttpUtils.post();
        post.url(loginUri);
        post.addParams("__VIEWSTATE", "dDwtMTMxNjk0NzYxNTs7PpK7CYMIAY8gja8M8G8YpGL8ZEAL");
        post.addParams("__VIEWSTATEGENERATOR", "92719903");
        post.addParams("txtUserName", username);
        post.addParams("TextBox2", password);
        post.addParams("txtSecretCode", tvCode);
        post.addParams("RadioButtonList1", "%D1%A7%C9%FA");
        post.addParams("Button1", "");
        post.addParams("lbLanguage", "");
        post.addHeader("Host", "210.44.159.4");
        post.addHeader("Referer", "//210.44.159.4/default2.aspx");
        post.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.75 Safari/537.36");
        post.build()
                .connTimeOut(5000)
                .execute(new StringCallback() {

                    @Override
                    public String parseNetworkResponse(Response response) throws IOException {
                        String cookie = response.header("Cookie");
                        System.out.println("下面是Cookie");
                        System.out.println("Cookie :"+cookie);
                        return super.parseNetworkResponse(response);
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        pbLogin.setVisibility(View.GONE);
                        tvError.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onResponse(String response) {
                        View focusView = null;
                        System.out.println("onResponse");
                        if (response.contains("验证码不正确")) {
                            etCode.setError("验证码错误");
                            focusView = etCode;
                        } else if (response.contains("密码错误")) {
                            et_password.setError("密码错误");
                            focusView = et_password;
                        } else if (response.contains("用户名不存在")) {
                            et_username.setError("用户名不存在");
                            focusView = et_username;
                        }
                        if (focusView != null) {
                            focusView.requestFocus();
                            changCodeImage();
                            pbLogin.setVisibility(View.INVISIBLE);
                        } else {
                            System.out.println("登陆成功");
                            alertDialog.dismiss();
                            tvContent.setText(response);
                        }
                    }
                });
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        //   DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //此处有问题，有待解决
    public void select(View view) {
        final PostFormBuilder post = OkHttpUtils.post();
        //xscj.aspx?xh=201311011011&xm=胡洪源&gnmkdm=N121605

        post.url(chengjiUri)
                .addHeader("Host", "210.44.159.4")
                .addHeader("Referer", "http://210.44.159.4/xscj.aspx?xh=201311011011&xm=%BA%FA%BA%E9%D4%B4&gnmkdm=N121605")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.8")
                .addParams("__VIEWSTATE", chengjitou)
                .addParams("__VIEWSTATEGENERATOR", "8963BEEC")
                .addParams("ddlXN", "2015-2016")
                .addParams("ddlXQ", "1")
                .addParams("txtQSCJ", "0")
                .addParams("txtZZCJ", "100")
                .addParams("Button1", "%B0%B4%D1%A7%C6%DA%B2%E9%D1%AF")
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {
                System.out.println("onError");
                System.out.println(e.getMessage().toString());
            }

            @Override
            public void onResponse(String response) {
                System.out.println("onResponse");
                tvContent.setText(response);
            }
        });

    }
}
