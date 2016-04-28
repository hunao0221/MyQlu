package com.hugo.myqlu.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.hugo.myqlu.R;
import com.hugo.myqlu.bean.CourseBean;
import com.hugo.myqlu.dao.BaseInfoDao;
import com.hugo.myqlu.dao.CourseDao;
import com.hugo.myqlu.dao.KaoshiDao;
import com.hugo.myqlu.utils.SpUtil;
import com.hugo.myqlu.utils.WeekUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static String kbcxUrl;
    @Bind(R.id.couse_list)
    RecyclerView couseList;

    private Toolbar toolbar;
    private ImageView iv_user_heead;
    private Context mContext = this;
    private DrawerLayout drawer;
    private String stuXH;
    private String password;
    private String stuName;
    private TextView header_xh;
    private TextView header_name;
    private BaseInfoDao baseInfoDao;
    private CourseDao courseDao;
    private MyAdapter adapter;
    private List<CourseBean> tempList;
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
        initListener();
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
        iv_user_heead = (ImageView) view.findViewById(R.id.iv_user_head);
        header_xh = (TextView) view.findViewById(R.id.header_xh);
        header_name = (TextView) view.findViewById(R.id.header_name);
    }

    private void initData() {
        baseInfoDao = new BaseInfoDao(mContext);
        courseDao = new CourseDao(mContext);
        Map<String, String> infosMap = baseInfoDao.queryAll();
        kbcxUrl = infosMap.get("kbcxUrl");
        stuXH = infosMap.get("stuXH");
        password = infosMap.get("password");
        stuName = infosMap.get("stuName");
        tempList = courseDao.queryAll();
        initDay();
        System.out.println(stuName + "--" + stuXH + "--" + password);
    }

    private void initUI() {
        header_name.setText(stuName);
        header_xh.setText(stuXH);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        couseList.setLayoutManager(manager);
        adapter = new MyAdapter();
        couseList.setAdapter(adapter);
        couseList.addOnScrollListener(new MyScrollListener());
    }


    private void initListener() {
        iv_user_heead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
        } else if (id == R.id.nav_kscx) {
            //新闻
            Intent intent = new Intent(mContext, ExamActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_class_schedule) {
            //课表
            Intent intent = new Intent(mContext, ExamActivity.class);
            intent.putExtra("kbcxUrl", kbcxUrl);
            startActivity(intent);
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_logout) {
            showLogoutDialog();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //注销登录近视框
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
                tempList = null;
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
        Intent intent = new Intent(mContext, SearchCjActivity.class);
        startActivity(intent);
    }

    class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

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

        int i = 0;

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            String week = setWeek(startList.get(position).getCourseTime());
            if (holder instanceof WeekViewHOlder) {
                ((WeekViewHOlder) holder).course_1.setText(startList.get(position).getCourseName());
                ((WeekViewHOlder) holder).course_week.setText(week);
                String course_week_text = ((WeekViewHOlder) holder).course_week.getText().toString();
                String tv_day_text = ((WeekViewHOlder) holder).tv_day.getText().toString();
                if (course_week_text.contains("周")) {
                    if (tv_day_text.equals("day")) {
                        int dayOfWeek = getDayOfWeek(course_week_text);
                        String date = returnDate(dayOfWeek);
                        ((WeekViewHOlder) holder).tv_day.setText(date);
                    }
                }
            } else if (holder instanceof NormalViewHolder) {
                ((NormalViewHolder) holder).course_name.setText(startList.get(position).getCourseName());
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
            TextView course_week, course_1, tv_day;

            public WeekViewHOlder(View itemView) {
                super(itemView);
                course_week = ButterKnife.findById(itemView, R.id.course_week);
                course_1 = ButterKnife.findById(itemView, R.id.course_1);
                tv_day = ButterKnife.findById(itemView, R.id.tv_day);
            }
        }

        class NormalViewHolder extends RecyclerView.ViewHolder {
            TextView course_name;

            public NormalViewHolder(View itemView) {
                super(itemView);
                course_name = ButterKnife.findById(itemView, R.id.course_1);
            }
        }
    }

    public class MyScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int lastVisibleItemPosition = manager.findLastVisibleItemPosition();
                if (lastVisibleItemPosition == manager.getItemCount() - 1) {
                    for (CourseBean temp : tempList) {
                        startList.add(temp);
                        adapter.notifyItemInserted(lastVisibleItemPosition + 1);
                    }
                }
            }
            super.onScrollStateChanged(recyclerView, newState);
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

    /**
     * 通过星期获得DayOfweek
     *
     * @param week
     * @return
     */
    public int getDayOfWeek(String week) {
        if (week.equals("周一")) {
            return 2;
        } else if (week.equals("周二")) {
            return 3;
        } else if (week.equals("周三")) {
            return 4;
        } else if (week.equals("周四")) {
            return 5;
        } else if (week.equals("周五")) {
            return 6;
        } else if (week.equals("周六")) {
            return 7;
        } else if (week.equals("周日")) {
            return 1;
        } else {
            return -1;
        }
    }

    int count = 0;

    /**
     * 返回日期
     *
     * @return
     */
    public String returnDate(int dayOfWeek) {
        String format = null;
        Calendar strDate = Calendar.getInstance();
        strDate.add(strDate.DATE, count);
        int i = strDate.get(Calendar.DAY_OF_WEEK);
        //如果获得日期的星期与传进来的不一样，就调用自己
        if (i != dayOfWeek) {
            count++;
            returnDate(dayOfWeek);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd");
            count++;
            Date time = strDate.getTime();
            format = sdf.format(time);
        }
        return format;
    }


    public void initDay() {
        Calendar calendar = Calendar.getInstance();
        int flag = calendar.get(Calendar.DAY_OF_WEEK);
        String currentWeek = WeekUtils.getWeek(flag);
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
    }
}
