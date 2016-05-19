package com.hugo.myqlu.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.hugo.myqlu.R;
import com.hugo.myqlu.dao.CourseDao;
import com.hugo.myqlu.fragment.CourseEditFramgment;
import com.hugo.myqlu.fragment.CourseFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CourseEditActivity extends AppCompatActivity {

    @Bind(R.id.toolbar_layout)
    CollapsingToolbarLayout toolbarLayout;
    @Bind(R.id.app_bar)
    AppBarLayout appBar;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    private Context mComtext = this;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    private FragmentManager fragmentManager;
    private boolean isEditor = true;
    boolean flag = true;
    private CourseDao courseDao = new CourseDao(mComtext);
    private CourseEditFramgment fragment;
    private static String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_edit);
        ButterKnife.bind(this);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        id = getIntent().getStringExtra("id");
        System.out.println("Activiry id ---->" + id);
        initFragment();
        initListener();
    }

    private void initFragment() {
        CourseFragment courseFragment = new CourseFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment, courseFragment).commit();
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
            fragment = new CourseEditFramgment();
            fragmentManager.beginTransaction().replace(R.id.fragment, fragment).commit();
            isEditor = false;
        } else {
            toolbarLayout.setTitle("");
            fab.setImageResource(R.mipmap.ic_mode_edit_white_24dp);
            CourseFragment courseFragment = new CourseFragment();
            fragmentManager.beginTransaction().replace(R.id.fragment, courseFragment).commit();
            isEditor = true;
        }

        if (isEditor) {
            //保存更新的数据
            System.out.println("保存数据");
        }
        flag = !flag;
    }
}
