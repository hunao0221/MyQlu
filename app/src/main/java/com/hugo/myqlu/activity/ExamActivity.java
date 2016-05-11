package com.hugo.myqlu.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.hugo.myqlu.R;
import com.hugo.myqlu.bean.ExamBean;
import com.hugo.myqlu.dao.BaseInfoDao;
import com.hugo.myqlu.dao.KaoshiDao;
import com.hugo.myqlu.utils.ParseExam;
import com.hugo.myqlu.utils.TextEncoderUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;

public class ExamActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.exam_list)
    RecyclerView examList;
    @Bind(R.id.tv_info)
    TextView tvInfo;
    @Bind(R.id.refresh)
    SwipeRefreshLayout refresh;

    private Context mContext = this;
    private List<ExamBean> examInfoList;
    private String stuCenterUrl;
    private String noCodeLoginUrl;
    private String noCodeVIEWSTATE;
    private String stuXH;
    private String password;
    private String kscxUrl;
    private List<ExamBean> refreshList;
    private String stuNameEncoding;
    private KaoshiDao kaoshiDao;
    private MyAdapter adapter;

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
        BaseInfoDao baseInfoDao = new BaseInfoDao(mContext);
        stuCenterUrl = baseInfoDao.query("StuCenterUrl");
        noCodeLoginUrl = baseInfoDao.query("noCodeLoginUrl");
        kscxUrl = baseInfoDao.query("kscxUrl");
        noCodeVIEWSTATE = getString(R.string.noCodeVIEWSTATE);
        //已保存的用户名和密码
        stuXH = baseInfoDao.query("stuXH");
        password = baseInfoDao.query("password");
        String stuName = baseInfoDao.query("stuName");
        stuNameEncoding = TextEncoderUtils.encoding(stuName);
        kaoshiDao = new KaoshiDao(mContext);
        examInfoList = kaoshiDao.queryAll();

        refresh.setColorSchemeResources(R.color.colorPrimary);
        if (examInfoList.size() == 0) {
            tvInfo.setVisibility(View.VISIBLE);
        } else {
            if (tvInfo.getVisibility() == View.VISIBLE) {
                tvInfo.setVisibility(View.GONE);
            }
        }
        initUI();
        //自动登录需要的数据
    }

    private void initUI() {
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        examList.setLayoutManager(manager);
        adapter = new MyAdapter();
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

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestLoginByNoCode();
            }
        });
    }

    /**
     * 自动登录
     */
    private void requestLoginByNoCode() {
        refresh.setRefreshing(true);
        OkHttpUtils.post().url(noCodeLoginUrl)
                .addParams("__VIEWSTATE", noCodeVIEWSTATE)
                .addParams("__VIEWSTATEGENERATOR", "89ADBA87")
                .addParams("tname", "")
                .addParams("tbtns", "")
                .addParams("tnameXw", "yhdl")
                .addParams("tbtnsXw", "yhdlyhdl|xwxsdl")
                .addParams("txtYhm", stuXH) //学号
                .addParams("txtXm", password) //不知道是什么，和密码一样
                .addParams("txtMm", password)
                .addParams("rblJs", "%D1%A7%C9%FA")
                .addParams("btnDl", "%B5%C7+%C2%BC")
                .addHeader("Referer", "http://210.44.159.4/default6.aspx")
                .addHeader("Host", "210.44.159.4")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.75 Safari/537.36")
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {
                //自动登录失败
                Toast.makeText(mContext, "登录失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response) {
                //登录成功
                System.out.println("登录成功");
                initKSData();
            }
        });
    }

    //获得考试
    private void initKSData() {
        OkHttpUtils.get().url(kscxUrl)
                .addParams("xh", stuXH)
                .addParams("xm", stuNameEncoding)
                .addParams("gnmkdm", "N121604")
                .addHeader("Host", "210.44.159.4")
                .addHeader("Referer", stuCenterUrl)
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.75 Safari/537.36")
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {
                //失败
                refresh.setRefreshing(false);
                Toast.makeText(mContext, "刷新失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response) {
                //解析html，得到考试信息，保存到数据库
                refreshList = ParseExam.parse(response);
                for (ExamBean examBean : refreshList) {
                    System.out.println(examBean.getExamName());
                    if (kaoshiDao.query(examBean.getExamName())) {
                        //更新数据
                        boolean update = kaoshiDao.update(examBean.getExamName(), examBean.getExamTime(), examBean.getExamLocation());
                    } else {
                        //插入数据库
                        boolean add = kaoshiDao.add(examBean.getExamName(), examBean.getExamTime(), examBean.getExamLocation());
                    }
                }
                examInfoList = kaoshiDao.queryAll();
                adapter.notifyDataSetChanged();
                refresh.setRefreshing(false);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
