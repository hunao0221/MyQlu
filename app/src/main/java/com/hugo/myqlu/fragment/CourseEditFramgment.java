package com.hugo.myqlu.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.hugo.myqlu.R;
import com.hugo.myqlu.bean.CourseBean;
import com.hugo.myqlu.dao.CourseDao;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CourseEditFramgment extends Fragment {

    @Bind(R.id.name)
    EditText name;
    @Bind(R.id.week)
    TextView week;
    @Bind(R.id.start)
    TextView start;
    @Bind(R.id.end)
    TextView end;
    @Bind(R.id.location)
    EditText location;
    @Bind(R.id.teacher)
    EditText teacher;
    private FragmentActivity activity;
    private CourseDao courseDao;
    private CourseBean courseBean;
    private String courseName;
    private String courseLocation;
    private String courseTime;
    private String courseTeacher;
    private String courstTimeDetail;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        activity = getActivity();
        initData();
    }

    private void initData() {
        String id = activity.getIntent().getStringExtra("id");
        System.out.println("id ---->" + id);
        courseDao = new CourseDao(activity);
        courseBean = courseDao.queryById(id + "");
        courseName = courseBean.getCourseName();
        courseLocation = courseBean.getCourseLocation();
        courseTime = courseBean.getCourseTime();
        courseTeacher = courseBean.getCourseTeacher();
        courstTimeDetail = courseBean.getCourstTimeDetail();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.course_edit_fragment, container, false);
        ButterKnife.bind(this, view);
        initListener();
        return view;
    }

    private void initListener() {
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                TimePicker timePicke = new TimePicker(activity);
                timePicke.setIs24HourView(true);
                builder.setView(timePicke);
                builder.show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        initUI();
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
    }

    @OnClick({R.id.week, R.id.start, R.id.end})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.week:
                showWeekDialog();
                break;
            case R.id.start:
                break;
            case R.id.end:
                break;
        }
    }

    private void showWeekDialog() {
        final String[] week = new String[]{"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final View view = View.inflate(activity, R.layout.week_dialog, null);
        ListView weekList = (ListView) view.findViewById(R.id.week_list);
        weekList.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return week.length;
            }

            @Override
            public Object getItem(int position) {
                return week[position];
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View inflate = View.inflate(activity, R.layout.item_week_dialog, null);
                TextView tvWeek = (TextView) inflate.findViewById(R.id.tv_item_week);
                tvWeek.setText(week[position]);
                return inflate;
            }
        });


        builder.setView(view);
        builder.show();
    }
}
