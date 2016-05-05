package com.hugo.myqlu.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.hugo.myqlu.R;
import com.hugo.myqlu.bean.ZhangBean;
import com.hugo.myqlu.dao.BaseInfoDao;
import com.hugo.myqlu.utils.HistoryDayUtil;
import com.hugo.myqlu.utils.ParseCardInfo;
import com.hugo.myqlu.utils.ParseHistoryInfo;
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
    @Bind(R.id.root_view)
    CoordinatorLayout rootView;
    @Bind(R.id.tv_total)
    TextView tvTotal;
    @Bind(R.id.tv_date)
    TextView tvDate;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.fab_query)
    FloatingActionButton fabQuery;
    @Bind(R.id.fab_menu)
    FloatingActionsMenu fabMenu;
    @Bind(R.id.tv_total_num)
    TextView tvTotalNum;

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
    private String historyUrl = "http://210.44.159.5/accounthisTrjn.action";
    private String host = "http://210.44.159.5";
    private String lasturl = "http://210.44.159.5/accounthisTrjn.action";
    private String nextPageUrl = "http://210.44.159.5/accountconsubBrows.action";
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
    private ListView list_history;
    private String mainAction;
    private List<String> dayList;
    private AlertDialog historyDialog;
    private String queryAction;
    private String lastAction;
    private List<ZhangBean> historyLiushui;
    private AlertDialog waitDialog;
    private int totalPages;
    private MyAdapte adapte = null;
    private String inputEndDate;
    private String inputStartDate;
    private int nextPage;
    private String stuXH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_card);
        ButterKnife.bind(this);
        toolbar.setTitle("一卡通");
        setSupportActionBar(toolbar);
        initData();
        showLoginDialog();
        initListener();
    }

    private void initData() {
        BaseInfoDao baseInfoDao = new BaseInfoDao(mContext);
        stuXH = baseInfoDao.query("stuXH");
    }

    private void showLoginDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setOnKeyListener(keylistener);
        builder.setCancelable(false);
        View view = View.inflate(mContext, R.layout.dialog_login, null);
        etUsername = ButterKnife.findById(view, R.id.et_username);
        etUsername.setText(stuXH);
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
                    getTodayData();
                    historyInfo();
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
    private void getTodayData() {
        OkHttpUtils.post().url(liushuiUrl)
                .addHeader("Host", "210.44.159.5")
                .addHeader("Origin", "http://210.44.159.5")
                .addHeader("Referer", liushuiUrl)
                .addParams("account", account)
                .addParams("inputObject", "15")
                .addParams("Submit", "+%C8%B7+%B6%A8+")
                .build().execute(new StringCallback() {

            @Override
            public void onError(Call call, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                ParseLiushui parseLiushui = new ParseLiushui(response);
                zhangList = parseLiushui.parse();
                if (zhangList.size() == 0) {
                    tvNullData.setVisibility(View.VISIBLE);
                } else {
                    String total = parseLiushui.getTotal().trim();
                    tvTotal.setText("您今日一共消费了 : ");
                    tvTotalNum.setText(total + "元");
                    initListview();
                }
            }
        });
    }

    /**
     * 账目list
     */
    private void initListview() {
        if (adapte != null) {
            adapte = null;
        }
        adapte = new MyAdapte(zhangList);
        listview.setAdapter(adapte);
    }

    class MyAdapte extends BaseAdapter {
        private List<ZhangBean> zhangList;

        public MyAdapte(List<ZhangBean> zhangList) {
            this.zhangList = zhangList;
        }

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

    class MyOnScrollListener implements AbsListView.OnScrollListener {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            switch (scrollState) {
                case SCROLL_STATE_FLING:
                    fabMenu.setVisibility(View.INVISIBLE);
                    break;
                case SCROLL_STATE_IDLE:
                    fabMenu.setVisibility(View.VISIBLE);
                    break;
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            View lastVisibleItemView = listview.getChildAt(listview.getChildCount() - 1);
            if ((firstVisibleItem + visibleItemCount) == totalItemCount) {
                if (lastVisibleItemView != null && lastVisibleItemView.getBottom() == listview.getHeight()) {
                    nextPage++;
                    requestNextPage();
                }
            }
        }
    }

    private void initListener() {
        MyClickListener listener = new MyClickListener();
        tv_change.setOnClickListener(listener);
        ivCodes.setOnClickListener(listener);
        tv_cancle.setOnClickListener(listener);
        tvOk.setOnClickListener(listener);
        fab.setOnClickListener(listener);
        fabMenu.setOnClickListener(listener);
        tvDate.setOnClickListener(listener);
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
        fabQuery.setOnClickListener(listener);
    }

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
                    clearFocus();
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
                case R.id.fab_query:
                    //显示历史查询弹窗
                    clearFocus();
                    if (dayList.size() == 0) {
                        Toast.makeText(mContext, "初始化数据,请稍后", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    showHistoryDialog();
                    break;
                case R.id.tv_date:
                    getTodayData();
                    break;
            }
        }

    }

    /**
     * 历史查询：
     * 1.获得主页的action,以及查询的type，15表示查询消费
     * 2.获得查询界面的action，以及查询的条件，三天，一周。。。
     * 3.请求，会进入等待页面，得到等待页面的action
     * 4.再次请求，即可得到历史数据
     */
    private void showHistoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View view = View.inflate(mContext, R.layout.dialog_history, null);
        list_history = (ListView) view.findViewById(R.id.list_history);
        builder.setView(view);
        historyDialog = builder.create();
        historyDialog.show();
        HistoryAdapter historyAdapter = new HistoryAdapter();
        list_history.setAdapter(historyAdapter);
        list_history.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                historyDialog.dismiss();
                showWaitDialog();
                int count = 0;
                switch (position) {
                    case 0:
                        //三天
                        tvTotal.setText("您" + dayList.get(position) + "消费了：");
                        count = 3;
                        break;
                    case 1:
                        //一周
                        tvTotal.setText("您" + dayList.get(position) + "消费了：");
                        count = 7;
                        break;
                    case 2:
                        //一月
                        tvTotal.setText("您" + dayList.get(position) + "消费了：");
                        count = 30;
                        break;
                    case 3:
                        //上月
                        tvTotal.setText("您" + dayList.get(position) + "消费了：");
                        count = 1;
                        break;
                    case 4:
                        //上上月
                        tvTotal.setText("您" + dayList.get(position) + "消费了：");
                        count = 2;
                        break;
                    case 5:
                        //上上上月
                        tvTotal.setText("您" + dayList.get(position) + "消费了：");
                        count = 3;
                        break;
                }
                if (position < 3) {
                    inputEndDate = HistoryDayUtil.getEndDate();
                    inputStartDate = HistoryDayUtil.getStartDate(count);
                } else {
                    inputEndDate = HistoryDayUtil.getMonthEnd(count);
                    inputStartDate = HistoryDayUtil.getMonthStart(count);
                }
                nextPage = 1;
                //查询历史消费
                queryHistoryMustInfo();
            }
        });
    }

    /**
     * 获得主页的action
     */
    private void historyInfo() {
        OkHttpUtils.get().url(historyUrl)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        Toast.makeText(mContext, "链接失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response) {
                        mainAction = ParseHistoryInfo.getMainAction(response);
                        getHistoryHomePage();
                    }
                });
    }

    /**
     * 获得查询的页面的action，以及查询条件
     */
    private void getHistoryHomePage() {
        OkHttpUtils.post().url(host + mainAction)
                .addHeader("Host", "210.44.159.5")
                .addHeader("Referer", historyUrl)
                .addParams("account", account)
                .addParams("inputObject", "15")
                .addParams("Submit", "+%C8%B7+%B6%A8+")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        System.out.println("获取失败");
                    }

                    @Override
                    public void onResponse(String response) {
                        queryAction = ParseHistoryInfo.getQueryAction(response);
                        dayList = ParseHistoryInfo.getDayList(response);
                    }
                });
    }

    /**
     * 请求下一页的数据
     */
    private synchronized void requestNextPage() {
        if (nextPage > totalPages) {
            return;
        }
        OkHttpUtils.post().url(nextPageUrl)
                .addHeader("Host", "210.44.159.5")
                .addHeader("Referer", "nextPageUrl")
                .addParams("inputStartDate", inputStartDate)
                .addParams("inputEndDate", inputEndDate)
                .addParams("pageNum", nextPage + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        Toast.makeText(mContext, "加载失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response) {
                        List<ZhangBean> nextInfo = ParseHistoryInfo.get(response);
                        for (ZhangBean next : nextInfo) {
                            historyLiushui.add(historyLiushui.size(), next);
                        }
                        adapte.notifyDataSetChanged();
                    }
                });
    }

    /**
     * 获得等待页面的action
     */
    private void queryHistoryMustInfo() {
        String cotinue = queryAction.substring(queryAction.indexOf("=") + 1, queryAction.length()).trim();
        OkHttpUtils.post().url(host + queryAction)
                .addHeader("Host", "210.44.159.5")
                .addHeader("Referer", host + mainAction)
                .addParams("inputStartDate", inputStartDate)
                .addParams("inputEndDate", inputEndDate)
                .addParams("__continue", cotinue)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e) {
                        Toast.makeText(mContext, "链接获取失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response) {
                        lastAction = ParseHistoryInfo.getLastAction(response);
                        queryHistoryConsumption();
                    }
                });
    }

    /**
     * 利用等待页面获取的action查询历史消费信息
     */
    private void queryHistoryConsumption() {
        OkHttpUtils.post().url(lasturl + lastAction)
                .addHeader("Host", "210.44.159.5")
                .addHeader("Referer", "lasturl+lastAction")
                .addParams("__continue", lastAction.substring(lastAction.indexOf("=") + 1, lastAction.length()).trim())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        Toast.makeText(mContext, "查询失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response) {
                        String historyTotal = ParseHistoryInfo.getHistoryTotal(response);
                        historyLiushui = ParseHistoryInfo.get(response);
                        totalPages = ParseHistoryInfo.getTotalPages(response);
                        waitDialog.dismiss();
                        tvTotalNum.setText(historyTotal + "元");
                        if (adapte != null) {
                            adapte = null;
                        }
                        adapte = new MyAdapte(historyLiushui);
                        listview.setAdapter(adapte);
                        listview.setOnScrollListener(new MyOnScrollListener());
                    }
                });
    }

    /**
     * 查询等待dialog
     */
    private void showWaitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        ProgressBar pb_wait = new ProgressBar(mContext);
        builder.setView(pb_wait);
        waitDialog = builder.create();
        waitDialog.show();
    }

    class HistoryAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return dayList.size();
        }

        @Override
        public Object getItem(int position) {
            return dayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(mContext, R.layout.item_history, null);
            TextView tv_history = (TextView) view.findViewById(R.id.tv_history);
            tv_history.setText(dayList.get(position));
            return view;
        }

    }

    public void clearFocus() {
        fab.clearFocus();
        fabMenu.clearFocus();
        fabMenu.collapse();
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
                        System.out.println(e.toString());
                    }

                    @Override
                    public void onResponse(String response) {
                        String messenge = "";
                        if (response.contains("操作成功")) {
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

    /**
     * 禁用返回键
     */
    private DialogInterface.OnKeyListener keylistener = new DialogInterface.OnKeyListener() {
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                return true;
            } else {
                return false;
            }
        }
    };
}
