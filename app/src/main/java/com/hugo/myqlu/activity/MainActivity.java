package com.hugo.myqlu.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hugo.myqlu.R;
import com.hugo.myqlu.bean.CourseBean;
import com.hugo.myqlu.dao.BaseInfoDao;
import com.hugo.myqlu.dao.CourseDao;
import com.hugo.myqlu.dao.KaoshiDao;
import com.hugo.myqlu.utils.SpUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Bind(R.id.couse_list)
    RecyclerView couseList;
    @Bind(R.id.tv_null_course)
    TextView tvNullCourse;

    private Toolbar toolbar;
    private Context mContext = this;
    private DrawerLayout drawer;
    private String stuXH;
    private String stuName;
    private TextView header_xh;
    private TextView header_name;
    private BaseInfoDao baseInfoDao;
    private CourseDao courseDao;
    private MyAdapter adapter;
    private List<CourseBean> allCourse;
    private List<CourseBean> startList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("课程");
        setSupportActionBar(toolbar);
        initData();
        initView();
        initUI();
    }

    private void initView() {

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View view = navigationView.getHeaderView(0);
        header_xh = (TextView) view.findViewById(R.id.header_xh);
        header_name = (TextView) view.findViewById(R.id.header_name);
    }


    /**
     * 从数据库中取数据
     */
    private void initData() {
        baseInfoDao = new BaseInfoDao(mContext);
        courseDao = new CourseDao(mContext);
        Map<String, String> infosMap = baseInfoDao.queryAll();
        stuXH = infosMap.get("stuXH");
        stuName = infosMap.get("stuName");
        allCourse = courseDao.queryAll();
        initDay();
    }

    //初始化课表数据
    public void initDay() {
        Calendar calendar = Calendar.getInstance();
        int flag = calendar.get(Calendar.DAY_OF_WEEK);
        startList = new ArrayList<>();
        if (flag == 1) {
            flag = 7;
        } else
            flag = flag - 1;
        for (int j = flag; j <= 7; j++) {
            List<CourseBean> list = courseDao.query(j + "");
            for (CourseBean item : list) {
                startList.add(item);
            }
            list = null;
        }
        for (CourseBean course : allCourse) {
            startList.add(course);
        }
    }


    private void initUI() {
        header_name.setText(stuName);
        header_xh.setText(stuXH);
        if (allCourse.size() == 0) {
            tvNullCourse.setVisibility(View.VISIBLE);
        }
        LinearLayoutManager manager = new LinearLayoutManager(this);
        couseList.setLayoutManager(manager);
        adapter = new MyAdapter();
        couseList.setAdapter(adapter);
        //日期问题没解决，暂时注释掉
        //  couseList.addOnScrollListener(new MyScrollListener());
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    toSearchCjActivity();
                    break;
                case 2:
                    Intent intent = new Intent(mContext, ExamActivity.class);
                    startActivity(intent);
                    break;
                case 3:
                    Intent cardIntent = new Intent(mContext, SchoolCardActivity.class);
                    startActivity(cardIntent);
                    break;

            }
        }
    };

    //注销登录弹窗
    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("注销登录");
        builder.setMessage("注销登录会清除已保存的课表数据...");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences sp = SpUtil.getSp(mContext, "config");
                //清除所有用户数据
                courseDao.deleteAll();
                baseInfoDao.deleteAll();
                KaoshiDao kaoshiDao = new KaoshiDao(mContext);
                kaoshiDao.deleteAll();
                //进入到loginActivity
                sp.edit().putBoolean("isFirstIn", true).commit();
                allCourse = null;
                startList = null;
                startActivity(new Intent(mContext, LoginActivity.class));
                finish();
            }
        });
        builder.setNegativeButton("cancle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    /**
     * 跳转到成绩查询页面
     */
    private void toSearchCjActivity() {
        Intent intent = new Intent(mContext, ScoreActivity.class);
        startActivity(intent);
    }


    class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int NORMAL_ITEM = 0;

        private static final int WEEK_ITEM = 1;

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == NORMAL_ITEM) {
                return new NormalViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_normal_course, parent, false));
            } else {
                return new WeekViewHOlder(LayoutInflater.from(mContext).inflate(R.layout.item_course, parent, false));
            }
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            CourseBean courseBean = startList.get(position);
            String week = setWeek(startList.get(position).getCourseTime());
            if (holder instanceof WeekViewHOlder) {
                ((WeekViewHOlder) holder).course_name.setText(courseBean.getCourseName());
                ((WeekViewHOlder) holder).course_info.setText(courseBean.getCourstTimeDetail() + "，" + courseBean.getCourseLocation());
                ((WeekViewHOlder) holder).course_week.setText(week);

            } else if (holder instanceof NormalViewHolder) {
                ((NormalViewHolder) holder).course_name.setText(courseBean.getCourseName());
                ((NormalViewHolder) holder).course_info.setText(courseBean.getCourstTimeDetail() + "，" + courseBean.getCourseLocation());
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return WEEK_ITEM;
            }
            String currentWeek = startList.get(position).getCourseTime();
            int prePosition = position - 1;
            boolean isDiff = startList.get(prePosition).getCourseTime().equals(currentWeek);
            return isDiff ? NORMAL_ITEM : WEEK_ITEM;
        }


        @Override
        public int getItemCount() {
            return startList.size();
        }

        class WeekViewHOlder extends RecyclerView.ViewHolder {

            TextView course_week, course_name, course_info;

            public WeekViewHOlder(View itemView) {
                super(itemView);
                course_week = ButterKnife.findById(itemView, R.id.course_week);
                course_name = ButterKnife.findById(itemView, R.id.course_name);
                course_info = ButterKnife.findById(itemView, R.id.course_info);
            }

        }

        class NormalViewHolder extends RecyclerView.ViewHolder {

            TextView course_name, course_info;

            public NormalViewHolder(View itemView) {
                super(itemView);
                course_name = ButterKnife.findById(itemView, R.id.course_name);
                course_info = ButterKnife.findById(itemView, R.id.course_info);
            }
        }

    }


    /**
     * 获得当前是星期几
     *
     * @param time
     * @return
     */
    public String setWeek(String time) {
        if (time.equals("1")) {
            time = "周一";
        } else if (time.equals("2")) {
            time = "周二";
        } else if (time.equals("3")) {
            time = "周三";
        } else if (time.equals("4")) {
            time = "周四";
        } else if (time.equals("5")) {
            time = "周五";
        } else if (time.equals("6")) {
            time = "周六";
        } else if (time.equals("7")) {
            time = "周日";
        }
        return time;
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
            startActivity(new Intent(mContext, SettingActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_search_cj) {
            //成绩查询
            handler.sendEmptyMessageDelayed(1, 500);
        } else if (id == R.id.nav_kscx) {
            //考试查询
            handler.sendEmptyMessageDelayed(2, 500);
        } else if (id == R.id.nav_school_card) {
            //一卡通
            handler.sendEmptyMessageDelayed(3, 500);
        } else if (id == R.id.nav_logout) {
            showLogoutDialog();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
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
