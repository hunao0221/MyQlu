package com.hugo.myqlu.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import com.hugo.myqlu.utils.SpUtil;
import com.hugo.myqlu.utils.WeekUtils;

import java.util.ArrayList;
import java.util.Calendar;
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
    private SharedPreferences sp;
    private TextView header_xh;
    private TextView header_name;
    private BaseInfoDao baseInfoDao;
    private CourseDao courseDao;
    private List<CourseBean> monList;
    private List<CourseBean> tueList;
    private List<CourseBean> wenList;
    private List<CourseBean> thuList;
    private List<CourseBean> friList;
    private List<CourseBean> satList;
    private List<CourseBean> sunList;
    private int mWay;
    private List<String> weekList;
    private List<CourseBean> allCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initData();
        initView();
        initUI();
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
        monList = courseDao.query("周一");
        tueList = courseDao.query("周二");
        wenList = courseDao.query("周三");
        thuList = courseDao.query("周四");
        friList = courseDao.query("周五");
        satList = courseDao.query("周六");
        sunList = courseDao.query("周日");
        allCourse = courseDao.queryAll();
        final Calendar c = Calendar.getInstance();
        mWay = c.get(Calendar.DAY_OF_WEEK);
        weekList = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            weekList.add(WeekUtils.getWeek((mWay++) % 7));
        }
        WeekUtils.getWeek(mWay);
        System.out.println(stuName + "--" + stuXH + "--" + password);
    }

    private void initUI() {
        header_name.setText(stuName);
        header_xh.setText(stuXH);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        couseList.setLayoutManager(manager);
        MyAdapter adapter = new MyAdapter();
        couseList.setAdapter(adapter);
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
        } else if (id == R.id.nav_news) {
            //新闻
            Intent intent = new Intent(mContext, NewsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_class_schedule) {
            //课表
            Intent intent = new Intent(mContext, ClassScheduleActivity.class);
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
                //清除所有用户数据
                courseDao.deleteAll();
                baseInfoDao.deleteAll();
                //进入到loginActivity
                SharedPreferences sp = SpUtil.getSp(mContext, "config");
                sp.edit().putBoolean("isFirstIn", true).commit();
                startActivity(new Intent(mContext, LoginActivity.class));
                //finish();
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

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            System.out.println("onCreateViewHolder");
            View view = getLayoutInflater().inflate(R.layout.item_course, parent, false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            CourseBean course = allCourse.get(position);
            System.out.println("onBindViewHolder");
            String courseDetail = course.getCourseName() + "\n" + course.getCourstTimeDetail() + "\n" + course.getCourseLocation();
            holder.course_1.setText(courseDetail);
            String time = setWeek(course.getCourseTime());
            holder.course_week.setText(time);
        }

        @Override
        public int getItemCount() {
            System.out.println("getItemCount");
            return allCourse.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView course_week, course_1;

            public ViewHolder(View itemView) {
                super(itemView);
                course_week = ButterKnife.findById(itemView, R.id.course_week);
                course_1 = ButterKnife.findById(itemView, R.id.course_1);
            }
        }
    }

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

}
