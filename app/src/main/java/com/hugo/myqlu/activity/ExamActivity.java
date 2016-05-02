package com.hugo.myqlu.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.hugo.myqlu.R;
import com.hugo.myqlu.bean.ExamBean;
import com.hugo.myqlu.dao.KaoshiDao;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ExamActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.exam_list)
    RecyclerView examList;
    @Bind(R.id.tv_info)
    TextView tvInfo;

    private Context mContext = this;
    private List<ExamBean> examInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_exam);
        ButterKnife.bind(this);
        toolbar.setTitle("考试");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initData();
        initUI();
        initLisitener();
    }

    private void initData() {
        KaoshiDao kaoshiDao = new KaoshiDao(mContext);
        examInfoList = kaoshiDao.queryAll();
        if (examInfoList.size() == 0) {
            tvInfo.setVisibility(View.VISIBLE);
        } else {
            if (tvInfo.getVisibility() == View.VISIBLE) {
                tvInfo.setVisibility(View.GONE);
            }
            initUI();
        }
    }

    private void initUI() {
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        examList.setLayoutManager(manager);
        MyAdapter adapter = new MyAdapter();
        examList.setAdapter(adapter);
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_kaoshi, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ExamBean examBean = examInfoList.get(position);
            holder.examName.setText(examBean.getExamName());
            holder.examTime.setText("考试时间 :" + examBean.getExamTime());
            holder.examLocation.setText("考试地点 :" + examBean.getExamLocation());
        }

        @Override
        public int getItemCount() {
            return examInfoList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView examName, examTime, examLocation;

            public ViewHolder(View itemView) {
                super(itemView);
                examName = ButterKnife.findById(itemView, R.id.tv_examname);
                examTime = ButterKnife.findById(itemView, R.id.tv_examtime);
                examLocation = ButterKnife.findById(itemView, R.id.tv_examlocation);
            }
        }
    }


    private void initLisitener() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
