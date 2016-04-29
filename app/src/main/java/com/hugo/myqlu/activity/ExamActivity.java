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
import com.hugo.myqlu.dao.BaseInfoDao;
import com.hugo.myqlu.dao.KaoshiDao;
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


    private Context mContext = this;
    private String kscxUrl;
    private String stuCenterUrl;
    private String noCodeLoginUrl;
    private String mainUrl;
    private String xqviewstate;
    private String justzxviewstate;
    private String justxnviewstate;
    private String noCodeVIEWSTATE;
    private String stuXH;
    private String password;
    private String stuName;
    private String stuNameEncoding;
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
        BaseInfoDao baseInfoDao = new BaseInfoDao(mContext);
        kscxUrl = baseInfoDao.query("kscxUrl");
        stuCenterUrl = baseInfoDao.query("StuCenterUrl");
        noCodeLoginUrl = baseInfoDao.query("noCodeLoginUrl");
        mainUrl = baseInfoDao.query("mainUrl");
        xqviewstate = getString(R.string.XQVIEWSTATE);
        justzxviewstate = getString(R.string.JUSTZXVIEWSTATE);
        justxnviewstate = getString(R.string.JUSTXNVIEWSTATE);
        noCodeVIEWSTATE = getString(R.string.noCodeVIEWSTATE);
        //已保存的用户名和密码
        stuXH = baseInfoDao.query("stuXH");
        stuName = baseInfoDao.query("stuName");
        stuNameEncoding = TextEncoderUtils.encoding(stuName);
        password = baseInfoDao.query("password");
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

    private void requestLoginByNoCode() {

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
                System.out.println("onError");
            }

            @Override
            public void onResponse(String response) {
                queryKS();
            }
        });
    }

    private void queryKS() {
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

            }

            @Override
            public void onResponse(String response) {

            }
        });
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
