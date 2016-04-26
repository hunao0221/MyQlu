package com.hugo.myqlu.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hugo.myqlu.R;
import com.hugo.myqlu.utils.PareseKbFromHtml;
import com.hugo.myqlu.utils.SpUtil;
import com.hugo.myqlu.utils.TextEncoderUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;

public class ClassScheduleActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.course_recylerview)
    RecyclerView courseRecylerview;
    private Context mContext = this;
    private String kbcxUrl;
    private String kbVIEWSTATE;
    private List<String> tempList = new ArrayList<>();
    private String noCodeLoginUrl = "http://210.44.159.4/default6.aspx";

    private MyAdapter.ViewHolder holder;
    private MyAdapter adapter;
    private SharedPreferences sp;
    private String stuName;
    private String stuXH;
    private String username;
    private String password;
    private String noCodeVIEWSTATE;
    private List<List<String>> allCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_schedule);
        ButterKnife.bind(this);
        toolbar.setTitle("课表");
        setSupportActionBar(toolbar);
        initListener();
        initData();
        if (tempList.size() == 0) {
            autoLoginData();
        }
    }

    private void autoLoginData() {
        sp = SpUtil.getSp(mContext, "privacy");
        username = sp.getString("username", null);
        password = sp.getString("password", null);
        stuName = sp.getString("stuName", null);
        stuXH = sp.getString("stuXH", null);
        sp = SpUtil.getSp(mContext, "privacy");
        kbcxUrl = getIntent().getStringExtra("kbcxUrl");
        kbVIEWSTATE = getString(R.string.KBVIEWSTATE);
        noCodeVIEWSTATE = getString(R.string.noCodeVIEWSTATE);
        System.out.println("用户名和密码   -----" + username + password + stuName + stuXH);
        if (username != null && password != null) {
            System.out.println("请求自动登陆");
            requestLoginNocode();
        }
    }

    private void requestLoginNocode() {
        OkHttpUtils.post().url(noCodeLoginUrl)
                .addParams("__VIEWSTATE", noCodeVIEWSTATE)
                .addParams("__VIEWSTATEGENERATOR", "89ADBA87")
                .addParams("tname", "")
                .addParams("tbtns", "")
                .addParams("tnameXw", "yhdl")
                .addParams("tbtnsXw", "yhdlyhdl|xwxsdl")
                .addParams("txtYhm", username) //学号
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
                //登陆成功，此时已经进入个人首页
                //抓取查询url
                if (response.contains(stuName + "同学")) {
                    System.out.println("自动登陆成功");
                    initData();
                }
            }
        });
    }

    private void initData() {
        if (tempList.size() > 0) {
            initUI();
            return;
        }

        if (stuName == null || stuXH == null) {
            return;
        }
        OkHttpUtils.post().url(kbcxUrl)
//                .addParams("__EVENTTARGET", "xnd")
//                .addParams("__EVENTARGUMENT", "")
//                .addParams("__VIEWSTATE", kbVIEWSTATE)
//                .addParams("__VIEWSTATEGENERATOR", "55530A43")
//                .addParams("xnd", "2015-2016")
//                .addParams("xqd", "1")
                .addHeader("gnmkdm", "N121603")
                .addParams("xm", TextEncoderUtils.encoding(stuName))
                .addHeader("Referer", kbcxUrl)
                .addHeader("Host", "210.44.159.4")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.75 Safari/537.36")
                .build().execute(new StringCallback() {


            @Override
            public void onError(Call call, Exception e) {
                //  tvContent.setText(e.getMessage().toString());
                System.out.println(e.toString());
            }

            @Override
            public void onResponse(String response) {
                // tvContent.setText(response);
                System.out.println("onResponse");
                PareseKbFromHtml.getKB(response);
//                if (allCourse.size() > 0) {
//                    for (List<String> weekList : allCourse) {
//                        for (String course : weekList) {
//                            tempList.add(course);
//                            initUI();
//                        }
//                    }
//                }
            }
        });
    }


    private void initUI() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        courseRecylerview.setLayoutManager(manager);
        adapter = new MyAdapter();
        courseRecylerview.setAdapter(adapter);
//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mCallback);
//        itemTouchHelper.attachToRecyclerView(courseRecylerview);
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course, parent, false);
            holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            String text = tempList.get(position);
            text = text.replace(" ", "\n");
            holder.tvCourseDetail.setText(text);
        }

        @Override
        public int getItemCount() {
            return tempList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvCourseDetail;

            public ViewHolder(View itemView) {
                super(itemView);
               // tvCourseDetail = ButterKnife.findById(itemView, R.id.course_detail);
            }
        }
    }

    /**
     * 监听上下移动，和向右滑动
     */
    ItemTouchHelper.Callback mCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT) {

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

//            int fromPosition = viewHolder.getAdapterPosition();
//            int toPosition = target.getAdapterPosition();
//            if (fromPosition < toPosition) {
//                //分别把中间所有的item的位置重新交换
//                for (int i = fromPosition; i < toPosition; i++) {
//                    Collections.swap(tempList, i, i + 1);
//                }
//            } else {
//                for (int i = fromPosition; i > toPosition; i--) {
//                    Collections.swap(tempList, i, i - 1);
//                }
//            }
//            //  adapter.notifyItemMoved(fromPosition, toPosition);
//            //返回true表示执行拖动
            return false;
        }

        /**
         * 滑动监听
         * @param viewHolder
         * @param direction
         */
        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            //deleteItem(position);
            adapter.notifyItemRemoved(position);
        }
    };

    private void initListener() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        tempList = null;
        allCourse = null;
        finish();
        super.onBackPressed();
    }
}
