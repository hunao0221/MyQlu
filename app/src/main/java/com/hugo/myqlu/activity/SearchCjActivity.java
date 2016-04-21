package com.hugo.myqlu.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.hugo.myqlu.R;
import com.hugo.myqlu.bean.ChengjiBean;
import com.hugo.myqlu.utils.HtmlUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;

public class SearchCjActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    @Bind(R.id.spinner_year)
    Spinner spinnerYear;
    @Bind(R.id.spinner_xueqi)
    Spinner spinnerXueqi;
    @Bind(R.id.spinner_mode)
    Spinner spinnerMode;
    @Bind(R.id.list_cj)
    ListView listCj;
    private String cjcxUrl;
    private String stuCenterUrl;
    private Context mContext = this;
    private List<String> xueqi = new ArrayList<>();
    private List<String> mode = new ArrayList<>();
    ArrayAdapter<String> xueqiAdapter;
    ArrayAdapter<String> modeAdapter;
    ArrayAdapter<String> yearsAdapter;
    private List<String> yearList;
    private String ddlXN = null;
    private String ddlXQ = null;
    private String selectMode = null;
    private String VIEWSTATE = null;
    private String XQVIEWSTATE;
    private String JUSTZXVIEWSTATE;
    private String JUSTXNVIEWSTATE;
    private static String BUTTON_XQ = "%B0%B4%D1%A7%C6%DA%B2%E9%D1%AF";
    private static String BUTTON_XN = "%B0%B4%D1%A7%C4%EA%B2%E9%D1%AF";
    private static String BUTTON_ZX = "%D4%DA%D0%A3%D1%A7%CF%B0%B3%C9%BC%A8%B2%E9%D1%AF";
    private List<ChengjiBean> cjList;
    private ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_cj);
        ButterKnife.bind(this);
        intiData();
        listview = (ListView) findViewById(R.id.list_cj);
        cjcxUrl = getIntent().getStringExtra("cjcxUrl");
        stuCenterUrl = getIntent().getStringExtra("StuCenterUrl");
        initData();
        initYearList();
    }

    private void intiData() {
        XQVIEWSTATE = getString(R.string.XQVIEWSTATE);
        JUSTZXVIEWSTATE = getString(R.string.JUSTZXVIEWSTATE);
        JUSTXNVIEWSTATE = getString(R.string.JUSTXNVIEWSTATE);
    }

    private void initData() {
        xueqi.add("");
        xueqi.add("1");
        xueqi.add("2");
        xueqi.add("3");
        mode.add("");
        mode.add("按学期查询");
        mode.add("按学年查询");
        mode.add("在校学习成绩");
    }


    private void initYearList() {
        OkHttpUtils.get()
                .url(cjcxUrl)
                .addHeader("Host", "210.44.159.4")
                .addHeader("Referer", stuCenterUrl)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                // tvContent.setText(response);
                HtmlUtils utils = new HtmlUtils(response);
                yearList = utils.parseSelectList();
                initSpinner();
            }
        });
    }

    private void initSpinner() {
        xueqiAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, xueqi);
        xueqiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerXueqi.setAdapter(xueqiAdapter);
        modeAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, mode);
        modeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMode.setAdapter(modeAdapter);
        yearsAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, yearList);
        yearsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearsAdapter);
        //选择监听器
        spinnerYear.setOnItemSelectedListener(this);
        spinnerXueqi.setOnItemSelectedListener(this);
        spinnerMode.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spinner_year:
                ddlXN = yearList.get(position).toString();
                checkoutFromWeb();
                break;
            case R.id.spinner_xueqi:
                ddlXQ = xueqi.get(position).toString();
                System.out.println(ddlXQ);
                checkoutFromWeb();
                break;
            case R.id.spinner_mode:
                String modeString = mode.get(position).toString();
                if (modeString.equals("按学期查询")) {
                    selectMode = BUTTON_XQ;
                    VIEWSTATE = XQVIEWSTATE;
                } else if (modeString.equals("按学年查询")) {
                    selectMode = BUTTON_XN;
                    VIEWSTATE = JUSTXNVIEWSTATE;
                } else if (modeString.equals("在校学习成绩")) {
                    selectMode = BUTTON_ZX;
                    VIEWSTATE = JUSTZXVIEWSTATE;

                }
                checkoutFromWeb();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private void checkoutFromWeb() {
        if (ddlXN == null || ddlXQ == null || selectMode == null) {
            return;
        }
        if (selectMode.equals(BUTTON_ZX)) {
            ddlXN = "";
            ddlXQ = "";
        } else if (selectMode.equals(BUTTON_XN)) {
            ddlXQ = "";
        }

        System.out.println("开始查成绩了");
        System.out.println(ddlXN + "----" + ddlXQ + "----" + selectMode);
        final PostFormBuilder post = OkHttpUtils.post();
        post.url(cjcxUrl)
                .addHeader("Host", "210.44.159.4")
                .addHeader("Referer", cjcxUrl)
                .addParams("__VIEWSTATE", VIEWSTATE)
                .addParams("__VIEWSTATEGENERATOR", "8963BEEC")
                .addParams("ddlXN", ddlXN)
                .addParams("ddlXQ", ddlXQ)
                .addParams("txtQSCJ", "0")
                .addParams("txtZZCJ", "100")
                .addParams("Button1", "%B0%B4%D1%A7%C6%DA%B2%E9%D1%AF")
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {
                System.out.println("onError");
                System.out.println(e.getMessage().toString());
            }

            @Override
            public void onResponse(String response) {
                System.out.println("onResponse");
                HtmlUtils cjUtils = new HtmlUtils(response);
                cjList = cjUtils.parseCJTable();
                initUI();
                if (cjList.size() == 1) {
                    System.out.println("没有成绩哦");
                }
            }
        });
    }

    private void initUI() {
        listview.setAdapter(new MyAdapter());
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return cjList.size();
        }

        @Override
        public Object getItem(int position) {
            return cjList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            ViewHolder holder;
            if (convertView == null) {
                view = View.inflate(mContext, R.layout.item_cj, null);
                holder = new ViewHolder();
                holder.tvKeCheng = ButterKnife.findById(view, R.id.tv_kecheng);
                holder.tvCj = ButterKnife.findById(view, R.id.tv_chengji);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }
            holder.tvKeCheng.setText(cjList.get(position).getCourseName());
            holder.tvCj.setText(cjList.get(position).getCourseCj());
            return view;
        }
    }

    public class ViewHolder {
        TextView tvKeCheng;
        TextView tvCj;
    }
}
