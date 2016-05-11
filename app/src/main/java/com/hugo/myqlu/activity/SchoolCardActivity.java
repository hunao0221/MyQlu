package com.hugo.myqlu.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.hugo.myqlu.R;
import com.hugo.myqlu.bean.ZhangBean;
import com.hugo.myqlu.dao.BaseInfoDao;
import com.hugo.myqlu.utils.DividerItemDecoration;
import com.hugo.myqlu.utils.HistoryDayUtil;
import com.hugo.myqlu.utils.ParseCardInfo;
import com.hugo.myqlu.utils.ParseHistoryInfo;
import com.hugo.myqlu.utils.ParseLiushui;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.StringCallback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
    @Bind(R.id.tv_total_num)
    TextView tvTotalNum;
    @Bind(R.id.fab_menu)
    FloatingActionMenu fabMenu;
    @Bind(R.id.zhang_list)
    RecyclerView recylerView;
    @Bind(R.id.rl_date)
    RelativeLayout rlDate;

    private Context mContext = this;
    private EditText etUsername;
    private EditText etPassword;
    private EditText etCode;
    private ImageView ivCodes;

    //验证码
    private String codeUrl = "http://210.44.159.5/getCheckpic.action?rand=?,Math.random()";
    //登陆
    private String loginUrl = "http://210.44.159.5/loginstudent.action";
    //
    private String mainUrl = "http://210.44.159.5/accountcardUser.action";
    //今日流水
    private String liushuiUrl = "http://210.44.159.5/accounttodatTrjnObject.action";
    //挂失
    private String doLostUrl = "http://210.44.159.5/accountDoLoss.action";
    //消费历史查询
    private String historyUrl = "http://210.44.159.5/accounthisTrjn.action";

    private String host = "http://210.44.159.5";
    //等待页面
    private String waitUrl = "http://210.44.159.5/accounthisTrjn.action";
    //流水下一页
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
    private List<ZhangBean> todayZhangList = null;
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
    private ProgressDialog waitDialog;
    private int totalPages;
    private String inputEndDate;
    private String inputStartDate;
    private int nextPage;
    private String stuXH;
    private MyRecylerAdapter recylerAdapter;
    private LinearLayoutManager layoutManager;
    private String total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_card);
        ButterKnife.bind(this);
        toolbar.setTitle("一卡通");
        setSupportActionBar(toolbar);
        initVIew();
        initData();
        showLoginDialog();
        initListener();
    }

    private void initVIew() {
        layoutManager = new LinearLayoutManager(mContext);
        recylerView.setLayoutManager(layoutManager);
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
        if (tv_error.getVisibility() == View.VISIBLE) {
            tv_error.setVisibility(View.GONE);
        }
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
                        System.out.println(e.getMessage());
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
                todayZhangList = parseLiushui.parse();
                if (todayZhangList.size() == 0) {
                    tvNullData.setVisibility(View.VISIBLE);
                }
                total = parseLiushui.getTotal().trim();
                tvTotal.setText("您今日一共消费了 : ");
                tvTotalNum.setText(total + "元");
                //initListview();
                initRecyler();
            }
        });
    }

    private void initRecyler() {
        recylerAdapter = new MyRecylerAdapter(todayZhangList, true);
        recylerView.setItemAnimator(new DefaultItemAnimator());
        recylerView.addOnScrollListener(new MyOnScrollListener());
        recylerView.addItemDecoration(new DividerItemDecoration(mContext, LinearLayoutManager.VERTICAL));
        recylerView.setAdapter(recylerAdapter);
    }

    class MyOnScrollListener extends RecyclerView.OnScrollListener {
        private static final int HIDE_THRESHOLD = 20;
        private int scrolledDistance = 0;
        private boolean controlsVisible = true;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
            if (lastVisibleItemPosition == layoutManager.getItemCount() - 1) {
                nextPage++;
                requestNextPage();
            }
            if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {
                //隐藏
                fabMenu.hideMenu(true);
                controlsVisible = false;
                scrolledDistance = 0;
            } else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {
                // 显示
                fabMenu.showMenu(true);
                controlsVisible = true;
                scrolledDistance = 0;
            }
            if ((controlsVisible && dy > 0) || (!controlsVisible && dy < 0)) {
                scrolledDistance += dy;
            }
            super.onScrolled(recyclerView, dx, dy);
        }
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
                        recylerAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void initListener() {
        MyClickListener listener = new MyClickListener();
        tv_change.setOnClickListener(listener);
        ivCodes.setOnClickListener(listener);
        tv_cancle.setOnClickListener(listener);
        rlDate.setOnClickListener(listener);
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
                    attemptLogin();
                    break;
                case R.id.tv_cancle:
                    exit();
                    break;
                case R.id.fab:
                    fabMenu.close(true);

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
                    fabMenu.close(true);
                    if (dayList == null) {
                        Toast.makeText(mContext, "初始化数据,请稍后", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    showHistoryDialog();
                    break;
                case R.id.rl_date:
                    if (todayZhangList != null) {
                        recylerAdapter = new MyRecylerAdapter(todayZhangList, true);
                        recylerView.setAdapter(recylerAdapter);
                        tvTotal.setText("您今日一共消费了 : ");
                        tvTotalNum.setText(total);
                    } else {
                        getTodayData();
                    }
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
                        Toast.makeText(mContext, "关键信息获取失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response) {
                        queryAction = ParseHistoryInfo.getQueryAction(response);
                        dayList = ParseHistoryInfo.getDayList(response);
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
        OkHttpUtils.post().url(waitUrl + lastAction)
                .addHeader("Host", "210.44.159.5")
                .addHeader("Referer", "waitUrl+lastAction")
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

                        if (historyLiushui.size() > 0) {
                            tvNullData.setVisibility(View.INVISIBLE);
                        }

                        recylerAdapter = new MyRecylerAdapter(historyLiushui, false);
                        recylerView.setAdapter(recylerAdapter);
                    }
                });
    }

    /**
     * 查询等待dialog
     */
    private void showWaitDialog() {
        waitDialog = new ProgressDialog(mContext);
        waitDialog.setTitle("正在查询");
        waitDialog.setMessage("Loading...");
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


    class MyRecylerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final static int NORMAL_TYPE = 0;
        private final static int TIME_TYPE = 1;

        private List<ZhangBean> zhangList;
        private boolean isToday;

        public MyRecylerAdapter(List<ZhangBean> zhangList, boolean isToday) {
            this.zhangList = zhangList;
            this.isToday = isToday;
        }

        @Override
        public int getItemViewType(int position) {
            if (isToday) {
                return NORMAL_TYPE;
            }
            if (position == 0) {
                return TIME_TYPE;
            }
            int lastPosition = position - 1;
            ZhangBean currentZhang = zhangList.get(position);
            ZhangBean lastZhang = zhangList.get(lastPosition);
            String currentTime = currentZhang.getTime().split(" ")[0];
            String lastTime = lastZhang.getTime().split(" ")[0];
            if (currentTime.equals(lastTime)) {
                return NORMAL_TYPE;
            } else {
                return TIME_TYPE;
            }
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            if (viewType == NORMAL_TYPE)
                return new NormalHolder(LayoutInflater.from(mContext).inflate(R.layout.item_zhang_normal, parent, false));
            else
                return new TimeHolder(LayoutInflater.from(mContext).inflate(R.layout.item_zhang_time, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ZhangBean zhangBean = this.zhangList.get(position);
            String[] times = zhangBean.getTime().split(" ");
            String terminal = zhangBean.getTerminal();
            if (holder instanceof TimeHolder) {
                ((TimeHolder) holder).tv_zhang_time.setText(times[0]);
                ((TimeHolder) holder).tv_system.setText(terminal);
                ((TimeHolder) holder).tv_money.setText(zhangBean.getTurnover());
                ((TimeHolder) holder).tv_item_time.setText(times[1]);
                ((TimeHolder) holder).iv_type.setImageResource(getTypeImage(terminal, times[1]));
            } else {
                ((NormalHolder) holder).tv_money.setText(zhangBean.getTurnover());
                ((NormalHolder) holder).tv_system.setText(terminal);
                ((NormalHolder) holder).tv_item_time.setText(times[1]);
                ((NormalHolder) holder).iv_type.setImageResource(getTypeImage(terminal, times[1]));
            }
        }

        /**
         * @param terminal 系统名称
         * @param time     消费时间
         * @return 返回图片id
         */
        public int getTypeImage(String terminal, String time) {
            int drawableId = 0;
            if (terminal.equals("长清水控浴室")) {
                drawableId = R.mipmap.xizao;
            } else if (terminal.equals("三餐商务")) {
                drawableId = compareTime(time);
            } else if (terminal.equals("一餐商务")) {
                drawableId = compareTime(time);
            } else if (terminal.equals("医疗子系统")) {
                drawableId = R.mipmap.yaopinfei;
            } else if (terminal.equals("生活区商务子系统")) {
                drawableId = compareTime(time);
            } else if (terminal.equals("直饮水")) {
                drawableId = R.mipmap.shuifei;
            } else {
                drawableId = R.mipmap.icon_zhichu_type_yanjiuyinliao;
            }
            return drawableId;
        }

        /**
         * 时间比较，区别早餐，中餐，晚餐；
         *
         * @param time 消费时间
         * @return 返回图片id
         */
        public int compareTime(String time) {
            SimpleDateFormat sdf = new SimpleDateFormat("hh/mm/ss");
            time = time.replace(":", "/");
            try {
                //早餐时间
                Date breakfastStart = sdf.parse("06/00/00");
                Date breakfastEnd = sdf.parse("09/00/00");
                //午餐时间
                Date lunchStart = sdf.parse("11/00/00");
                Date lunchEnd = sdf.parse("13/00/00");
                //晚餐时间
                Date dinerStart = sdf.parse("16/00/00");
                Date dinerEnd = sdf.parse("20/00/00");
                Date thisTime = sdf.parse(time);
                if (thisTime.after(breakfastStart) && thisTime.before(breakfastEnd)) {
                    return R.mipmap.zaocan;
                } else if (thisTime.after(lunchStart) && thisTime.before(lunchEnd)) {
                    return R.mipmap.zhongfan;
                } else if (thisTime.after(dinerStart) && thisTime.before(dinerEnd)) {
                    return R.mipmap.wanfan;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return R.mipmap.lingshi;
        }

        @Override
        public int getItemCount() {
            return this.zhangList.size();
        }

        class NormalHolder extends RecyclerView.ViewHolder {
            TextView tv_system;
            TextView tv_money;
            ImageView iv_type;
            TextView tv_item_time;

            public NormalHolder(View itemView) {
                super(itemView);
                tv_system = ButterKnife.findById(itemView, R.id.zhang_system);
                tv_money = ButterKnife.findById(itemView, R.id.zhang_money);
                iv_type = ButterKnife.findById(itemView, R.id.iv_type);
                tv_item_time = ButterKnife.findById(itemView, R.id.tv_item_time);
            }
        }

        class TimeHolder extends NormalHolder {

            TextView tv_zhang_time;

            public TimeHolder(View itemView) {
                super(itemView);
                tv_zhang_time = ButterKnife.findById(itemView, R.id.tv_zhang_time);
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
                        Toast.makeText(mContext, "挂失失败", Toast.LENGTH_SHORT).show();
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
