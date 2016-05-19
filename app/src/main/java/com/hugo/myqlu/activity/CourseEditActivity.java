package com.hugo.myqlu.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hugo.myqlu.R;
import com.hugo.myqlu.dao.CourseDao;
import com.hugo.myqlu.event.UpdateDataEvent;
import com.hugo.myqlu.fragment.CourseEditFramgment;
import com.hugo.myqlu.fragment.CourseFragment;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CourseEditActivity extends AppCompatActivity {

    @Bind(R.id.toolbar_layout)
    CollapsingToolbarLayout toolbarLayout;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    private Context mComtext = this;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    private FragmentManager fragmentManager;
    private boolean isEditor = true;
    boolean flag = true;
    private CourseEditFramgment editFragment;
    private static String id;
    private CourseDao courseDao = new CourseDao(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_edit);
        ButterKnife.bind(this);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        id = getIntent().getStringExtra("id");
        initFragment();
        initListener();
    }

    private void initFragment() {
        CourseFragment courseFragment = new CourseFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.edit_fragment, courseFragment).commit();
    }


    private void initListener() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @OnClick({R.id.fab})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                initFab();
                break;
        }
    }

    private void initFab() {
        if (flag) {
            toolbarLayout.setTitle("编辑");
            fab.setImageResource(R.mipmap.ic_done_white_24dp);
            editFragment = new CourseEditFramgment();
            fragmentManager.beginTransaction().replace(R.id.edit_fragment, editFragment).commit();
            isEditor = false;
        } else {
            toolbarLayout.setTitle("");
            fab.setImageResource(R.mipmap.ic_mode_edit_white_24dp);
            CourseFragment courseFragment = new CourseFragment();
            fragmentManager.beginTransaction().replace(R.id.edit_fragment, courseFragment).commit();
            isEditor = true;
        }

        if (isEditor) {
            //保存更新的数据
            System.out.println("保存数据");
            View view = editFragment.getView();
            EditText etName = ButterKnife.findById(view, R.id.name);
            String name = etName.getText().toString();
            TextView tvWeek = ButterKnife.findById(view, R.id.week);
            String week = tvWeek.getText().toString();
            TextView tvStart = ButterKnife.findById(view, R.id.start);
            String start = tvStart.getText().toString();
            TextView tvEnd = ButterKnife.findById(view, R.id.end);
            String end = tvEnd.getText().toString();
            String timeDetail = start + "-" + end;
            EditText etLocation = ButterKnife.findById(view, R.id.location);
            String location = etLocation.getText().toString();
            EditText etTeacher = ButterKnife.findById(view, R.id.teacher);
            String teacher = etTeacher.getText().toString();

            boolean update = courseDao.update(id, name, setTime(week), timeDetail, teacher, location);
            if (update) {
                //修改成功
                EventBus.getDefault().post(new UpdateDataEvent(""));
            } else {
                Toast.makeText(mComtext, "修改失败", Toast.LENGTH_SHORT).show();
            }
        }
        flag = !flag;
    }

    public String setTime(String time) {
        if (time.equals("周一")) {
            time = "1";
        } else if (time.equals("周二")) {
            time = "2";
        } else if (time.equals("周三")) {
            time = "3";
        } else if (time.equals("周四")) {
            time = "4";
        } else if (time.equals("周五")) {
            time = "5";
        } else if (time.equals("周六")) {
            time = "6";
        } else if (time.equals("周日")) {
            time = "7";
        }
        return time;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.course_edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                showDelete();
                break;
        }
        return true;
    }

    private void showDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mComtext);
        builder.setMessage("要删除当前课程吗？");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //删除数据
                courseDao.delete(id);
                dialog.dismiss();
                finish();
            }
        });
        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
