package com.hugo.myqlu.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.hugo.myqlu.R;
import com.hugo.myqlu.utils.HtmlUtils;
import com.hugo.myqlu.utils.SpUtil;
import com.hugo.myqlu.utils.TextEncoderUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String mainUrl = "http://210.44.159.4";
    private String codeUrl = "http://210.44.159.4/CheckCode.aspx";
    private String loginUrl = "http://210.44.159.4/default2.aspx";
    private static String StuCenterUrl = "http://210.44.159.4/xs_main.aspx?xh=stuxh";
    private static String cjcxUrl = "http://210.44.159.4/xscj.aspx?xh=stuxh&xm=stuname&gnmkdm=N121605";
    private static String kscxUrl = "http://210.44.159.4/xskscx.aspx?xh=stuxh&xm=stuname%90&gnmkdm=N121604";
    private static String kbcxUrl = "http://210.44.159.4/xskbcx.aspx?xh=stuxh&xm=stuname&gnmkdm=N121603";
    private String noCodeVIEWSTATE;
    private String noCodeLoginUrl = "http://210.44.159.4/default6.aspx";
    private String kbVIEWSTATE;
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
    private String stuXH;
    private String stuName;
    private TextView header_name;
    private TextView header_xh;
    private SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initData();
        initView();
        initListener();
        autoLogin();
    }

    private void initData() {
        kbVIEWSTATE = getString(R.string.KBVIEWSTATE);
        noCodeVIEWSTATE = getString(R.string.noCodeVIEWSTATE);
    }

    private void autoLogin() {
        sp = SpUtil.getSp(mContext, "privacy");
        username = sp.getString("username", null);
        password = sp.getString("password", null);
        System.out.println("用户名和密码   -----" + username + password);
        if (username != null && password != null) {
            System.out.println("请求自动登陆");
            requestLoginNocode();
        }
    }

    private void requestLoginNocode() {
        OkHttpUtils.post().url(noCodeLoginUrl)
                .addParams("__VIEWSTATE", noCodeVIEWSTATE)
                .addParams("__VIEWSTATEGENERATOR", "89ADBA87")
                .addParams("tname", "")
                .addParams("tbtns", "")
                .addParams("tnameXw", "yhdl")
                .addParams("tbtnsXw", "yhdlyhdl|xwxsdl")
                .addParams("txtYhm", username) //学号
                .addParams("txtXm", password) //不知道是什么，和密码一样
                .addParams("txtMm", password)
                .addParams("rblJs", "%D1%A7%C9%FA")
                .addParams("btnDl", "%B5%C7+%C2%BC")
                .addHeader("Referer", "http://210.44.159.4/default6.aspx")
                .addHeader("Host", "210.44.159.4")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.75 Safari/537.36")
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {
                System.out.println("onError");
            }

            @Override
            public void onResponse(String response) {
                //登陆成功，此时已经进入个人首页
                //抓取查询url
                Toast.makeText(mContext, "登陆成功", Toast.LENGTH_SHORT).show();
                HtmlUtils utils = new HtmlUtils(response);
                cjcxUrl = mainUrl + "/" + utils.encoder(response);
                String xhandName = utils.getXhandName();
                initUrlData(xhandName);
            }
        });
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
        header_name = (TextView) view.findViewById(R.id.header_name);
        header_xh = (TextView) view.findViewById(R.id.header_xh);
    }

    private void initListener() {
        iv_user_heead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
                //判断当前时候在线
                if (isOnLine()) {
                    //显示个人信息
                    Toast.makeText(mContext, "您已在线", Toast.LENGTH_SHORT).show();
                } else {
                    //登陆
                    if (username != null && password != null) {
                        requestLoginNocode();

                    }
                }
            }
        });
    }

    //判断当前是否在线-------------------------还有没处理完
    private boolean isOnLine() {
        OkHttpUtils.get().url(StuCenterUrl)
                .build().execute(new StringCallback() {
            boolean flag = false;

            @Override
            public void onError(Call call, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                if (response.contains(stuName)) {
                    flag = true;
                }
            }
        });
        return false;
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
        //显示验证码
        showCodeimage();
        MyOnClickListener listener = new MyOnClickListener();
        tvChange.setOnClickListener(listener);
        tvOk.setOnClickListener(listener);
        tvCancle.setOnClickListener(listener);
    }

    //加载验证码
    public void showCodeimage() {
        OkHttpUtils.get().url(codeUrl).build()
                .connTimeOut(5000)
                .execute(new BitmapCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        System.out.println("验证码加载失败");
                        //应该给验证码设置一个图片
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
        codeUrl += '?';
        showCodeimage();
    }

    //登陆前检查
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
     * 像服务器模拟登陆
     */
    private void requestLogin() {
        int visibility = tvError.getVisibility();
        if (visibility == View.VISIBLE) {
            tvError.setVisibility(View.INVISIBLE);
        }
        pbLogin.setVisibility(View.VISIBLE);
        final PostFormBuilder post = OkHttpUtils.post();
        post.url(loginUrl);
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
                            //登陆成功
                            System.out.println("登陆成功");
                            //保存用户名和密码，以便静默登陆

                            sp.edit().putString("username", username).commit();
                            sp.edit().putString("password", password).commit();
                            alertDialog.dismiss();
                            //抓取查询uti
                            HtmlUtils utils = new HtmlUtils(response);
                            cjcxUrl = mainUrl + "/" + utils.encoder(response);
                            String xhandName = utils.getXhandName();
                            initUrlData(xhandName);
                        }
                    }
                });
    }

    private void initUrlData(String xhandName) {
        //201311011034 田宇同学
        String[] split = xhandName.split(" ");
        stuXH = split[0]; //用户的学号
        stuName = split[1].replace("同学", "");  //用户的姓名
        //设置navigation header的ui
        header_name.setText(stuName);
        header_xh.setText(stuXH);
        //设置需要的url
        cjcxUrl = cjcxUrl.replace("stuxh", stuXH).replace("stuname", TextEncoderUtils.encoding(stuName));
        kbcxUrl = kbcxUrl.replace("stuxh", stuXH).replace("stuname", TextEncoderUtils.encoding(stuName));
        kscxUrl = kscxUrl.replace("stuxh", stuXH).replace("stuname", TextEncoderUtils.encoding(stuName));
        StuCenterUrl = StuCenterUrl.replace("stuxh", stuXH);

        System.out.println("cjcxUrl :" + cjcxUrl);
        System.out.println("kbcxUrl :" + kbcxUrl);
        System.out.println("kscxUrl :" + kscxUrl);
        System.out.println("StuCenterUrl :" + StuCenterUrl);

    }

    public void selectCJ(View view) {
        OkHttpUtils.post().url(kbcxUrl)
                .addParams("__EVENTTARGET", "xnd")
                .addParams("__EVENTARGUMENT", "")
                .addParams("__VIEWSTATE", kbVIEWSTATE)
                .addParams("__VIEWSTATEGENERATOR", "55530A43")
                .addParams("xnd", "2013-2014")
                .addParams("xqd", "1")
                .addHeader("Referer", kbcxUrl)
                .addHeader("Host", "210.44.159.4")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.75 Safari/537.36")
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {
                tvContent.setText(e.getMessage().toString());
            }

            @Override
            public void onResponse(String response) {
                tvContent.setText(response);
            }
        });
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

        if (id == R.id.nav_search_cj) {
            // Handle the camera action
            //成绩查询
            toSearchCjActivity();

        } else if (id == R.id.nav_news) {
            //新闻
            startActivity(new Intent(mContext, NewsActivity.class));
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /**
     * 跳转到成绩查询页面
     */
    private void toSearchCjActivity() {

        Intent intent = new Intent(mContext, SearchCjActivity.class);
        intent.putExtra("cjcxUrl", cjcxUrl);
        intent.putExtra("StuCenterUrl", StuCenterUrl);
        startActivity(intent);
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

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //让返回键实现home键的功能
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addCategory(Intent.CATEGORY_HOME);
            startActivity(i);
        }
    }

}
