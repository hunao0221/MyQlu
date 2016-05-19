package com.hugo.myqlu.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hugo.myqlu.R;
import com.hugo.myqlu.bean.CourseBean;
import com.hugo.myqlu.dao.CourseDao;
import com.hugo.myqlu.event.UpdateDataEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;


public class CourseFragment extends Fragment {

    @Bind(R.id.name)
    TextView name;
    @Bind(R.id.week)
    TextView week;
    @Bind(R.id.start)
    TextView start;
    @Bind(R.id.end)
    TextView end;
    @Bind(R.id.location)
    TextView location;
    @Bind(R.id.teacher)
    TextView teacher;
    private FragmentActivity activity;
    private CourseDao courseDao;
    private CourseBean courseBean;
    private String courseName;
    private String courseLocation;
    private String courseTime;
    private String courseTeacher;
    private String courstTimeDetail;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        activity = getActivity();
        initData();
        EventBus.getDefault().register(this);
    }

    private void initData() {
        String id = activity.getIntent().getStringExtra("id");
        courseDao = new CourseDao(activity);
        courseBean = courseDao.queryById(id);
        courseName = courseBean.getCourseName();
        courseLocation = courseBean.getCourseLocation();
        courseTime = courseBean.getCourseTime();
        courseTeacher = courseBean.getCourseTeacher();
        courstTimeDetail = courseBean.getCourstTimeDetail();
    }

    @Subscribe
    public void onEvent(UpdateDataEvent event) {
        initData();
        initUI();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.course_info_framgment, container, false);
        ButterKnife.bind(this, view);
        initUI();
        return view;
    }


    private void initUI() {
        name.setText(courseName);
        week.setText(getWeek(courseTime));
        location.setText(courseLocation);
        teacher.setText(courseTeacher);
        String[] times = courstTimeDetail.split("-");
        start.setText(times[0]);
        end.setText(times[1]);
    }

    public String getWeek(String time) {
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
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }
}
