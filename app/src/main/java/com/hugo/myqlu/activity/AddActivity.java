package com.hugo.myqlu.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.hugo.myqlu.R;
import com.hugo.myqlu.dao.CourseDao;
import com.hugo.myqlu.event.UpdateDataEvent;
import com.hugo.myqlu.utils.WeekUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.name)
    EditText name;
    @Bind(R.id.week)
    TextView tvWeek;
    @Bind(R.id.start)
    TextView start;
    @Bind(R.id.end)
    TextView end;
    @Bind(R.id.location)
    EditText location;
    @Bind(R.id.teacher)
    EditText teacher;

    private Context mContext = this;
    private ListView weekList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        ButterKnife.bind(this);
        toolbar.setTitle("添加");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initTime();
        initListener();
    }

    private void initListener() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initTime() {
        Calendar calendar = Calendar.getInstance();
        int i = calendar.get(Calendar.DAY_OF_WEEK);
        tvWeek.setText(WeekUtils.setWeek(i + ""));
    }


    @OnClick({R.id.fab, R.id.week, R.id.start, R.id.end})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.week:
                showWeekDialog();
                break;
            case R.id.start:
                showTimeDialog(start);
                break;
            case R.id.end:
                showTimeDialog(end);
                break;
            case R.id.fab:
                saveToDb();
                break;
        }
    }

    /**
     * 保存到数据库
     */
    private void saveToDb() {
        CourseDao courseDao = new CourseDao(mContext);
        String courseName = name.getText().toString().trim();
        String week = tvWeek.getText().toString();
        String startTIme = start.getText().toString();
        String endTime = end.getText().toString();
        String courseLocation = location.getText().toString().trim();
        String courseTeacher = teacher.getText().toString().trim();
        if (TextUtils.isEmpty(courseName) || TextUtils.isEmpty(courseLocation) || TextUtils.isEmpty(courseTeacher)) {
            Toast.makeText(mContext, "关键信息不能为空", Toast.LENGTH_SHORT).show();
        } else {
            //保存到数据库
            String timeDetail = startTIme + "-" + endTime;
            boolean add = courseDao.add(courseName, WeekUtils.setTime(week), timeDetail, courseTeacher, courseLocation);
            String info;
            if (add)
                info = "添加成功";
            else
                info = "添加失败，请重试";
            Toast.makeText(mContext, info, Toast.LENGTH_SHORT).show();
            EventBus.getDefault().post(new UpdateDataEvent(""));
        }
    }

    private void showTimeDialog(final TextView tvTime) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        final TimePicker timePicke = new TimePicker(mContext);
        timePicke.setIs24HourView(true);

        builder.setView(timePicke);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //获得新的时间
                tvTime.setText(getTimeFormat(timePicke.getHour()) + ":" + getTimeFormat(timePicke.getMinute()));
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private String getTimeFormat(int time) {
        String s = "" + time;
        if (s.length() == 1) {
            s = "0" + s;
        }
        return s;
    }

    private void showWeekDialog() {
        final String[] week = new String[]{"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        final View view = View.inflate(mContext, R.layout.week_dialog, null);
        weekList = (ListView) view.findViewById(R.id.week_list);
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
                View inflate = View.inflate(mContext, R.layout.item_week_dialog, null);
                TextView tvWeek = (TextView) inflate.findViewById(R.id.tv_item_week);
                tvWeek.setText(week[position]);
                return inflate;
            }
        });

        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        weekList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tvWeek.setText(week[position]);
                alertDialog.dismiss();
            }
        });
    }
}
