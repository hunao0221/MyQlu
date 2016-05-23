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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.hugo.myqlu.R;
import com.hugo.myqlu.adapter.CourseAdapter;
import com.hugo.myqlu.bean.CourseBean;
import com.hugo.myqlu.dao.BaseInfoDao;
import com.hugo.myqlu.dao.CourseDao;
import com.hugo.myqlu.dao.ExamDao;
import com.hugo.myqlu.event.UpdateDataEvent;
import com.hugo.myqlu.utils.SpUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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
    private CourseAdapter adapter;
    private List<CourseBean> allCourse;
    private static List<CourseBean> startList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_couse);
        setSupportActionBar(toolbar);
        EventBus.getDefault().register(this);
        initData();
        initView();
        initUI();
    }


    @Subscribe
    public void onEvent(UpdateDataEvent event) {
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
        adapter = new CourseAdapter(startList);
        couseList.setAdapter(adapter);
        adapter.setOnItemClickListener(new CourseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                int id = startList.get(position).getId();
                Intent intent = new Intent(mContext, CourseEditActivity.class);
                intent.putExtra("id", id + "");
                startActivity(intent);
            }
        });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    toScoreActivity();
                    break;
                case 2:
                    Intent intent = new Intent(mContext, ExamActivity.class);
                    startActivity(intent);
                    break;
                case 3:
                    Intent cardIntent = new Intent(mContext, SchoolCardActivity.class);
                    startActivity(cardIntent);
                    break;
                case 4:
                    startActivity(new Intent(mContext, SettingActivity.class));
                    break;
            }
        }
    };

    /**
     * 跳转到成绩查询页面
     */
    private void toScoreActivity() {
        Intent intent = new Intent(mContext, ScoreActivity.class);
        startActivity(intent);
    }

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
                ExamDao examDao = new ExamDao(mContext);
                examDao.deleteAll();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * 菜单
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.add:
                startActivity(new Intent(mContext, AddActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * 分享app
     */
    private void shareApp() {
        String shareInfo = getString(R.string.share_info);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareInfo);
        shareIntent.setType("text/plain");

        //设置分享列表的标题，并且每次都显示分享列表
        startActivity(Intent.createChooser(shareIntent, "分享到"));
    }


    /**
     * 侧边栏点击事件
     *
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_score) {
            //成绩查询
            handler.sendEmptyMessageDelayed(1, 500);
        } else if (id == R.id.nav_exam) {
            //考试查询
            handler.sendEmptyMessageDelayed(2, 500);
        } else if (id == R.id.nav_school_card) {
            //一卡通
            handler.sendEmptyMessageDelayed(3, 500);
        } else if (id == R.id.nav_logout) {
            //注销
            showLogoutDialog();
        } else if (id == R.id.nav_settings) {
            //设置
            handler.sendEmptyMessageDelayed(4, 500);
        } else if (id == R.id.nav_share) {
            //分享
            shareApp();
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
