package com.hugo.myqlu.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hugo.myqlu.R;
import com.hugo.myqlu.bean.CourseBean;
import com.hugo.myqlu.bean.ExamBean;
import com.hugo.myqlu.dao.BaseInfoDao;
import com.hugo.myqlu.dao.CourseDao;
import com.hugo.myqlu.dao.KaoshiDao;
import com.hugo.myqlu.utils.HtmlUtils;
import com.hugo.myqlu.utils.ParseKSInfoFromHtml;
import com.hugo.myqlu.utils.ParseKbFromHtml;
import com.hugo.myqlu.utils.SpUtil;
import com.hugo.myqlu.utils.TextEncoderUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;

import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

public class LoginActivity extends Activity implements View.OnClickListener {

    @Bind(R.id.et_username)
    EditText etUsername;
    @Bind(R.id.et_password)
    EditText etPassword;
    @Bind(R.id.et_code)
    EditText etCode;
    @Bind(R.id.iv_codes)
    ImageView ivCodes;
    @Bind(R.id.tv_change)
    TextView tvChange;
    @Bind(R.id.pb_login)
    ProgressBar pbLogin;
    @Bind(R.id.tv_error)
    TextView tvError;
    @Bind(R.id.rootView)
    LinearLayout rootView;
    @Bind(R.id.bt_login)
    Button btLogin;
    private String mainUrl = "http://210.44.159.4";
    //验证码
    private String codeUrl = "http://210.44.159.4/CheckCode.aspx";
    //登录
    private String loginUrl = "http://210.44.159.4/default2.aspx";
    //登出
    private String logoutUrl = "http://210.44.159.4/logout.aspx";
    //无需验证码登陆的url
    private String noCodeLoginUrl = "http://210.44.159.4/default6.aspx";
    //下面的url都需要进行替换修改
    //个人中心
    private static String StuCenterUrl = "http://210.44.159.4/xs_main.aspx?xh=stuxh";
    //成绩查询
    private static String cjcxUrl = "http://210.44.159.4/xscj.aspx?xh=stuxh&xm=stuname&gnmkdm=N121605";
    //考试查询
    private static String kscxUrl = "http://210.44.159.4/xskscx.aspx?xh=stuxh&xm=stuname%90&gnmkdm=N121604";
    //课程表查询
    private static String kbcxUrl = "http://210.44.159.4/xskbcx.aspx?xh=stuxh&xm=stuname&gnmkdm=N121603";
    private String userId;
    private String password;
    private String code;
    private Context mContext = this;
    private String stuXH;
    private String stuName;
    //所有课程
    private List<CourseBean> allCourseList;
    private SharedPreferences sp;
    //中文姓名的编码
    private String stuNameEncoding;
    private List<ExamBean> ksInfoList;
    private ProgressDialog waitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initData();
        initListener();
        showCodeimage();
    }

    private void initData() {
        sp = SpUtil.getSp(mContext, "config");
    }

    private void initListener() {
        tvChange.setOnClickListener(this);
        ivCodes.setOnClickListener(this);
        btLogin.setOnClickListener(this);
        final InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //监听软键盘回车键
        etCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //关闭软键盘
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    attemptLogin();
                }
                return true;
            }
        });
    }

    //加载验证码
    public void showCodeimage() {
        OkHttpUtils.get().url(codeUrl).build()
                .connTimeOut(5000)
                .execute(new BitmapCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        //应该给验证码设置一个默认图片（错误图片）；
                    }

                    @Override
                    public void onResponse(Bitmap response) {
                        //设置验证码
                        ivCodes.setImageBitmap(response);
                    }
                });
    }

    //登录前检查输入的数据
    private void attemptLogin() {
        View focusView = null;
        userId = etUsername.getText().toString().trim();
        password = etPassword.getText().toString().trim();
        code = etCode.getText().toString().trim();
        if (TextUtils.isEmpty(userId)) {
            etUsername.setError("学号不能为空");
            focusView = etUsername;
        } else if (TextUtils.isEmpty(password)) {
            etPassword.setError("密码不能为空");
            focusView = etPassword;
        } else if (TextUtils.isEmpty(code)) {
            etCode.setError("验证码不能为空");
            focusView = etCode;
        }
        if (focusView != null) {
            focusView.requestFocus();
        } else {
            //向服务器请求登录
            requestLogin();
        }
    }

    /**
     * 向服务器模拟登陆
     */
    private void requestLogin() {
        int visibility = tvError.getVisibility();
        if (visibility == View.VISIBLE) {
            tvError.setVisibility(View.INVISIBLE);
        }

        pbLogin.setVisibility(View.VISIBLE);
        final PostFormBuilder post = OkHttpUtils.post();
        post.url(loginUrl);
        post.tag(this);
        //下面数据抓包可以得到
        post.addParams("__VIEWSTATE", "dDwtMTMxNjk0NzYxNTs7PpK7CYMIAY8gja8M8G8YpGL8ZEAL");
        post.addParams("__VIEWSTATEGENERATOR", "92719903");
        post.addParams("txtUserName", userId);
        post.addParams("TextBox2", password);
        post.addParams("txtSecretCode", code);
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
                            etPassword.setError("密码错误");
                            focusView = etPassword;
                        } else if (response.contains("用户名不存在")) {
                            etUsername.setError("用户名不存在");
                            focusView = etUsername;
                        }
                        if (focusView != null) {
                            focusView.requestFocus();
                            pbLogin.setVisibility(View.INVISIBLE);
                            //切换验证码
                            changCodeImage();
                        } else {
                            //登录成功
                            pbLogin.setVisibility(View.INVISIBLE);
                            //初始化用户数据
//                            initURL(response);
                            System.out.println("登录成功");
                            showSaveDataDialog(response);
                        }
                    }
                });
    }

    private void showSaveDataDialog(String response) {

        waitDialog = new ProgressDialog(mContext);
        waitDialog.setTitle("请稍后");
        waitDialog.setMessage("Loading...");
        waitDialog.show();
        initURL(response);
        initCourseData();
    }

    /**
     * 初始化用户数据
     *
     * @param response
     */
    private void initURL(String response) {
        HtmlUtils utils = new HtmlUtils(response);
        cjcxUrl = mainUrl + "/" + utils.encoder(response);
        String xhandName = utils.getXhandName();
        //initUrlData(xhandName);
        String[] split = xhandName.split(" ");
        //用户的学号
        stuXH = split[0];
        //用户的姓名
        stuName = split[1].replace("同学", "");
        stuNameEncoding = TextEncoderUtils.encoding(stuName);
        //需要的url
        cjcxUrl = cjcxUrl.replace("stuxh", stuXH).replace("stuname", TextEncoderUtils.encoding(stuName));
        kbcxUrl = kbcxUrl.replace("stuxh", stuXH).replace("stuname", TextEncoderUtils.encoding(stuName));
        kscxUrl = kscxUrl.replace("stuxh", stuXH).replace("stuname", TextEncoderUtils.encoding(stuName));
        StuCenterUrl = StuCenterUrl.replace("stuxh", stuXH);
    }

    /**
     * 获得课表
     */
    private void initCourseData() {
        if (stuName == null || stuXH == null) {
            return;
        }
        OkHttpUtils.post().url(kbcxUrl)
                .tag(this)
                .addHeader("gnmkdm", "N121603")
                .addParams("xm", TextEncoderUtils.encoding(stuName))
                .addHeader("Referer", kbcxUrl)
                .addHeader("Host", "210.44.159.4")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.75 Safari/537.36")
                .build()
                .connTimeOut(5000)
                .readTimeOut(5000)
                .execute(new StringCallback() {

                    @Override
                    public String parseNetworkResponse(Response response) throws IOException {
                        System.out.println("课表 ：" + response.code());

                        return super.parseNetworkResponse(response);
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        waitDialog.dismiss();
                        Snackbar.make(rootView, "未知错误，重启APP", Snackbar.LENGTH_LONG).setAction("重启", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //重新启动app
                                Intent i = getBaseContext().getPackageManager()
                                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                            }
                        }).show();

                    }

                    @Override
                    public void onResponse(String response) {

                        allCourseList = ParseKbFromHtml.getKB(response);
                        if (allCourseList == null) {
                            waitDialog.dismiss();
                            Toast.makeText(mContext, "同步失败", Toast.LENGTH_SHORT).show();
                        } else {
                            initKSData();
                        }
                    }
                });
    }

    //获得考试
    private void initKSData() {
        OkHttpUtils.get().url(kscxUrl)
                .addParams("xh", stuXH)
                .addParams("xm", stuNameEncoding)
                .addParams("gnmkdm", "N121604")
                .addHeader("Host", "210.44.159.4")
                .addHeader("Referer", StuCenterUrl)
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.75 Safari/537.36")
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {
                //失败
            }

            @Override
            public void onResponse(String response) {
                //解析html，得到考试信息，保存到数据库
                ksInfoList = ParseKSInfoFromHtml.parse(response);
                saveDataToDB();
            }
        });
    }


    /**
     * 保存数据数据库中
     */
    private void saveDataToDB() {
        //基本信息
        BaseInfoDao baseInfoDao = new BaseInfoDao(mContext);
        baseInfoDao.add("cjcxUrl", LoginActivity.cjcxUrl);
        baseInfoDao.add("kbcxUrl", LoginActivity.kbcxUrl);
        baseInfoDao.add("kscxUrl", LoginActivity.kscxUrl);
        baseInfoDao.add("StuCenterUrl", StuCenterUrl);
        baseInfoDao.add("stuName", stuName);
        baseInfoDao.add("stuXH", stuXH);
        baseInfoDao.add("noCodeLoginUrl", noCodeLoginUrl);
        baseInfoDao.add("stuXH", stuXH);
        baseInfoDao.add("mainUrl", mainUrl);
        baseInfoDao.add("logoutUrl", logoutUrl);
        baseInfoDao.add("password", password);


        //课表
        CourseDao courseDao = new CourseDao(mContext);
        boolean saveSucess = true;
        for (CourseBean course : allCourseList) {
            String courseName = course.getCourseName();
            String courseTime = course.getCourseTime();
            String courstTimeDetail = course.getCourstTimeDetail();
            String courseTeacher = course.getCourseTeacher();
            String courseLocation = course.getCourseLocation();
            boolean isSucess = courseDao.add(courseName, courseTime, courstTimeDetail, courseTeacher, courseLocation);
            if (!isSucess) {
                saveSucess = false;
                Toast.makeText(mContext, "保存课表失败", Toast.LENGTH_SHORT).show();
                break;
            }
        }

        //保存考试信息
        KaoshiDao kaoshiDao = new KaoshiDao(mContext);
        for (ExamBean exam : ksInfoList) {
            String examName = exam.getExamName();
            String examTime = exam.getExamTime();
            String examLocation = exam.getExamLocation();
            boolean addSuccess = kaoshiDao.add(examName, examTime, examLocation);
            if (!addSuccess) {
                waitDialog.dismiss();
                saveSucess = false;
                Toast.makeText(mContext, "保存考试信息失败", Toast.LENGTH_SHORT).show();
                break;
            }
        }

        //数据保存成功
        if (saveSucess) {
            System.out.println("saveSucess");
            sp.edit().putBoolean("isFirstIn", false).commit();
            allCourseList = null;
            startActivity(new Intent(mContext, MainActivity.class));
            waitDialog.dismiss();
            finish();
        }
    }

    //切换验证码
    public void changCodeImage() {
        codeUrl += '?';
        showCodeimage();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_login:
                attemptLogin();
                break;
            case R.id.tv_change:
                changCodeImage();
                break;
            case R.id.iv_codes:
                changCodeImage();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RequestCall call = OkHttpUtils.get().url(kbcxUrl).build();
        call.cancel();
    }
}
