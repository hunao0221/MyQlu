package com.hugo.myqlu.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hugo.myqlu.R;
import com.hugo.myqlu.bean.ZhangBean;
import com.hugo.myqlu.utils.ParseCardInfo;
import com.hugo.myqlu.utils.ParseLiushui;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;

public class SchoolCardActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.tv_cardid)
    TextView tvCardId;
    @Bind(R.id.tv_userId)
    TextView tvUserId;
    @Bind(R.id.tv_yuer)
    TextView tvYuer;
    @Bind(R.id.listview)
    ListView listview;
    @Bind(R.id.tv_null_data)
    TextView tvNullData;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.root_view)
    CoordinatorLayout rootView;
    @Bind(R.id.tv_total)
    TextView tvTotal;
    @Bind(R.id.tv_date)
    TextView tvDate;

    private Context mContext = this;
    private EditText etUsername;
    private EditText etPassword;
    private EditText etCode;
    private ImageView ivCodes;

    private String codeUrl = "http://210.44.159.5/getCheckpic.action?rand=?,Math.random()";
    private String loginUrl = "http://210.44.159.5/loginstudent.action";
    private String mainUrl = "http://210.44.159.5/accountcardUser.action";
    private String liushuiUrl = "http://210.44.159.5/accounttodatTrjnObject.action";
    private String doLostUrl = "http://210.44.159.5/accountDoLoss.action";
    private TextView tv_change;
    private TextView tvOk;
    private String userId;
    private String password;
    private String code;
    private AlertDialog dialog;
    private ProgressBar pbLogin;
    private TextView tv_error;
    private Map<String, String> baseInfoMap = null;
    private List<ZhangBean> zhangList;
    private String account;
    private TextView tv_cancle;
    private EditText lost_account;
    private EditText lost_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_card);
        ButterKnife.bind(this);
        toolbar.setTitle("今日消费");
        setSupportActionBar(toolbar);
        showLoginDialog();
        initListener();
    }

    private void showLoginDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setOnKeyListener(keylistener);
        builder.setCancelable(false);
        View view = View.inflate(mContext, R.layout.dialog_login, null);
        etUsername = ButterKnife.findById(view, R.id.et_username);
        etPassword = ButterKnife.findById(view, R.id.et_password);
        etCode = ButterKnife.findById(view, R.id.et_code);
        ivCodes = ButterKnife.findById(view, R.id.iv_codes);
        tv_change = ButterKnife.findById(view, R.id.tv_change);
        tvOk = ButterKnife.findById(view, R.id.tv_ok);
        tv_cancle = ButterKnife.findById(view, R.id.tv_cancle);
        pbLogin = ButterKnife.findById(view, R.id.pb_login);
        tv_error = ButterKnife.findById(view, R.id.tv_error);
        builder.setView(view);
        dialog = builder.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        initCodeImg();
    }

    private void initCodeImg() {
        //加载验证码
        OkHttpUtils.get().url(codeUrl).build()
                .connTimeOut(3000)
                .execute(new BitmapCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        //应该给验证码设置一个图片
                        //不在校园网环境
                        System.out.println("验证码无法加载");
                        if (tv_error.getVisibility() == View.INVISIBLE) {
                            tv_error.setVisibility(View.VISIBLE);
                        }
                        tv_error.setText("当前不是校园网环境");
                    }

                    @Override
                    public void onResponse(Bitmap response) {
                        ivCodes.setImageBitmap(response);
                    }
                });
    }


    //登陆前检查
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
            //向服务器请求登陆
            requestLogin();
        }
    }

    /**
     * 登录
     */
    private void requestLogin() {
        pbLogin.setVisibility(View.VISIBLE);
        OkHttpUtils.post().url(loginUrl)
                .addHeader("Host", "210.44.159.5")
                .addHeader("Referer", "http://210.44.159.5/homeLogin.action")
                .addParams("name", userId)
                .addParams("userType", "1")
                .addParams("passwd", password)
                .addParams("loginType", "2")
                .addParams("rand", code)
                .addParams("imageField.x", "23")
                .addParams("imageField.y", "11")
                .build()
                .connTimeOut(5000)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        if (pbLogin.getVisibility() == View.VISIBLE) {
                            pbLogin.setVisibility(View.GONE);
                        }
                        System.out.println("好像出错了哦");
                        if (tv_error.getVisibility() == View.INVISIBLE) {
                            tv_error.setVisibility(View.VISIBLE);
                        }
                        tv_error.setText("登陆失败");
                    }

                    @Override
                    public void onResponse(String response) {
                        if (pbLogin.getVisibility() == View.VISIBLE) {
                            pbLogin.setVisibility(View.GONE);
                        }
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
                        } else if (response.contains("请10秒后重试")) {
                            tv_error.setText("登录太频繁，请10后重试");
                            focusView = tv_error;
                        }
                        if (focusView != null) {
                            focusView.requestFocus();
                            pbLogin.setVisibility(View.INVISIBLE);
                            initCodeImg();
                        } else {
                            //登陆成功
                            pbLogin.setVisibility(View.INVISIBLE);
                            //初始化用户数据
                            System.out.println("登录成功");
                            dialog.dismiss();
                            getUserInfo();
                        }
                    }
                });
    }

    /**
     * 获得用户基本信息
     */
    private void getUserInfo() {
        OkHttpUtils.get().url(mainUrl)
                .addHeader("Referer", mainUrl)
                .addHeader("Host", "210.44.159.5")
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {
                System.out.println("出错了o");
            }

            @Override
            public void onResponse(String response) {
                //解析html，获得个人信息，主要获得账号
                baseInfoMap = ParseCardInfo.parse(response);
                if (baseInfoMap.size() != 0) {
                    initTitleUI();
                    getCardData();
                }
            }
        });
    }

    private void initTitleUI() {
        tvName.setText("姓名 : " + baseInfoMap.get("name"));
        account = baseInfoMap.get("cardId");
        tvCardId.setText("账号 : " + account);
        tvUserId.setText("学号 : " + userId);
        tvYuer.setText("余额 : " + baseInfoMap.get("yuer"));
        tvDate.setText(getDate() + "");
    }

    /**
     * 获得当日流水
     */
    private void getCardData() {
        OkHttpUtils.post().url(liushuiUrl)
                .addHeader("Host", "210.44.159.5")
                .addHeader("Origin", "http://210.44.159.5")
                .addHeader("Referer", liushuiUrl)
                .addParams("account", account)
                .addParams("inputObject", "all")
                .addParams("Submit", "+%C8%B7+%B6%A8+")
                .build().execute(new StringCallback() {

            @Override
            public void onError(Call call, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                zhangList = ParseLiushui.parse(response);
                if (zhangList.size() == 0) {
                    tvNullData.setVisibility(View.VISIBLE);
                } else {
                    float total = 0;
                    for (ZhangBean zhang : zhangList) {
                        String turnover = zhang.getTurnover();
                        float aFloat = Float.parseFloat(turnover);
                        if (aFloat < 0.00) {
                            total += aFloat;
                        }
                    }
                    System.out.println("今日消费 ：" + total);
                    tvTotal.setText("您今日一共消费了 ： " + total + "元");
                    initListview();
                }
            }
        });

    }

    /**
     * 账目list
     */
    private void initListview() {
        listview.setAdapter(new MyAdapte());
    }

    class MyAdapte extends BaseAdapter {

        @Override
        public int getCount() {
            return zhangList.size();
        }

        @Override
        public Object getItem(int position) {
            return zhangList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            View view;
            if (convertView == null) {
                holder = new ViewHolder();
                view = View.inflate(mContext, R.layout.item_zhang_list, null);
                holder.time = (TextView) view.findViewById(R.id.tv_time);
                holder.terminal = (TextView) view.findViewById(R.id.terminal);
                holder.balance = (TextView) view.findViewById(R.id.balance);
                holder.turnover = (TextView) view.findViewById(R.id.turnover);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }
            ZhangBean zhangInfo = zhangList.get(position);
            holder.time.setText(zhangInfo.getTime());
            holder.terminal.setText(zhangInfo.getTerminal());
            holder.balance.setText(zhangInfo.getBalance());
            holder.turnover.setText(zhangInfo.getTurnover());
            return view;
        }
    }

    class ViewHolder {
        TextView time;
        TextView terminal;
        TextView balance;
        TextView turnover;
    }

    private void initListener() {
        MyClickListener listener = new MyClickListener();
        tv_change.setOnClickListener(listener);
        ivCodes.setOnClickListener(listener);
        tv_cancle.setOnClickListener(listener);
        tvOk.setOnClickListener(listener);
        fab.setOnClickListener(listener);
        final InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //监听软键盘回车时间
        etCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //关闭软键盘
                    imm.toggleSoftInput(
                            InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    attemptLogin();
                }
                return true;
            }
        });
    }

    private DialogInterface.OnKeyListener keylistener = new DialogInterface.OnKeyListener() {
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                return true;
            } else {
                return false;
            }
        }
    };


    class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_change:
                    initCodeImg();
                    break;
                case R.id.iv_codes:
                    initCodeImg();
                    break;
                case R.id.tv_ok:
                    System.out.println("你点击了登录");
                    attemptLogin();
                    break;
                case R.id.tv_cancle:
                    exit();
                    break;
                case R.id.fab:
                    if (baseInfoMap == null) {
                        if (baseInfoMap == null) {
                            Snackbar.make(rootView, "您还没有登录", Snackbar.LENGTH_LONG).setAction("登录", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showLoginDialog();
                                }
                            }).show();
                        }
                    } else
                        showLockCardDialog();
                    break;
            }
        }

    }

    /**
     * 饭卡挂失
     */
    private void showLockCardDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("饭卡挂失");
        View view = View.inflate(mContext, R.layout.dialog_dolost, null);
        lost_account = ButterKnife.findById(view, R.id.lost_account);
        lost_password = ButterKnife.findById(view, R.id.lost_passwd);
        builder.setView(view);
        lost_account.setText(baseInfoMap.get("cardId"));
        builder.setPositiveButton("挂失", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //确定挂失
                if (lost_account.getText() != null && lost_password.getText() != null) {
                    doLost();
                } else {
                    Toast.makeText(mContext, "账号或密码不能为空", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    /**
     * 请求挂失
     */
    private void doLost() {
        OkHttpUtils.post().url(doLostUrl)
                .addHeader("Referer", "http://210.44.159.5/accountloss.action")
                .addHeader("Host", "210.44.159.5")
                .addParams("account", lost_account.getText().toString())
                .addParams("passwd", lost_password.getText().toString())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        System.out.println("错误");
                        System.out.println(e.toString());
                    }

                    @Override
                    public void onResponse(String response) {
                        String messenge = "";
                        if (response.contains("操作成功")) {
                            System.out.println("挂失成功");
                            messenge = "您已挂失成功";
                        } else if (response.contains("密码错误")) {
                            messenge = "密码错误";
                            Toast.makeText(mContext, "密码错误", Toast.LENGTH_SHORT).show();
                        } else if (response.contains("已挂失")) {
                            messenge = "您的饭卡已经处于挂失状态";
                        }
                        Toast.makeText(mContext, messenge, Toast.LENGTH_LONG).show();
                    }
                });
    }

    public int getDate() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return day;
    }

    //退出
    public void exit() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
