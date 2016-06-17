package com.hugo.myqlu.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hugo.myqlu.R;
import com.hugo.myqlu.bean.ZhangBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;

/**
 * @author Hugo
 * Created on 2016/5/12 13:09.
 */
public class ConsumeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final static int NORMAL_TYPE = 0;
    private final static int TIME_TYPE = 1;
    private final static int TITLE_TYPE = 2;

    private List<ZhangBean> zhangList;
    private boolean isToday;
    private String titleInfo;
    private String total;

    public ConsumeAdapter(List<ZhangBean> zhangList, boolean isToday) {
        this.zhangList = zhangList;
        this.isToday = isToday;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public void setTitleInfo(String titleInfo) {
        this.titleInfo = titleInfo;
    }

    @Override
    public int getItemViewType(int position) {
        if (isToday) {
            if (position == 0) {
                return TITLE_TYPE;
            }
            return NORMAL_TYPE;
        }
        if (position == 0) {
            return TITLE_TYPE;
        }
        if (position == 1) {
            return TIME_TYPE;
        }
        int lastPosition = position - 2;
        ZhangBean currentZhang = zhangList.get(position - 1);
        ZhangBean lastZhang = zhangList.get(lastPosition);
        String currentTime = currentZhang.getTime().split(" ")[0];
        String lastTime = lastZhang.getTime().split(" ")[0];
        if (currentTime.equals(lastTime)) {
            return NORMAL_TYPE;
        } else {
            return TIME_TYPE;
        }
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == NORMAL_TYPE)
            return new NormalHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_zhang_normal, parent, false));
        else if (viewType == TIME_TYPE)
            return new TimeHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_zhang_time, parent, false));
        else
            return new TitleHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recyler_header, parent, false));
    }

    private ZhangBean zhangBean;
    private String[] times;
    private String terminal;

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (position != 0) {
            zhangBean = this.zhangList.get(position - 1);
            times = zhangBean.getTime().split(" ");
            terminal = zhangBean.getTerminal();
        }
        if (holder instanceof TimeHolder) {
            ((TimeHolder) holder).tv_zhang_time.setText(times[0]);
            ((TimeHolder) holder).tv_system.setText(terminal);
            ((TimeHolder) holder).tv_money.setText(zhangBean.getTurnover());
            ((TimeHolder) holder).tv_item_time.setText(times[1]);
            ((TimeHolder) holder).iv_type.setImageResource(getTypeImage(terminal, times[1]));
        } else if (holder instanceof NormalHolder) {
            ((NormalHolder) holder).tv_money.setText(zhangBean.getTurnover());
            ((NormalHolder) holder).tv_system.setText(terminal);
            ((NormalHolder) holder).tv_item_time.setText(times[1]);
            ((NormalHolder) holder).iv_type.setImageResource(getTypeImage(terminal, times[1]));
        } else {
            if (isToday) {
                titleInfo = "您今日消费了";
            }
            ((TitleHolder) holder).tv_title_info.setText(titleInfo);
            ((TitleHolder) holder).tv_title_num.setText(total);

        }
    }

    /**
     * @param terminal 系统名称
     * @param time     消费时间
     * @return 返回图片id
     */
    public int getTypeImage(String terminal, String time) {
        int drawableId = 0;
        if (terminal.contains("浴室")) {
            drawableId = R.mipmap.xizao;
        } else if (terminal.contains("食堂")) {
            drawableId = compareTime(time);
        } else if (terminal.contains("校医院")) {
            drawableId = R.mipmap.yaopinfei;
        } else if (terminal.equals("生活区商务子系统")) {
            drawableId = compareTime(time);
        } else if (terminal.contains("直饮水")) {
            drawableId = R.mipmap.shuifei;
        } else if (terminal.contains("纳博士")) {
            drawableId = R.mipmap.icon_zhichu_type_gouwu;
        } else if (terminal.contains("直通车")) {
            drawableId = R.mipmap.gonggongqiche;
        } else if (terminal.contains("开水房")) {
            drawableId = R.mipmap.shuifei;
        } else {
            drawableId = R.mipmap.icon_zhichu_type_yanjiuyinliao;
        }
        return drawableId;
    }

    /**
     * 时间比较，区别早餐，中餐，晚餐；
     *
     * @param time 消费时间
     * @return 返回图片id
     */
    public int compareTime(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh/mm/ss");
        time = time.replace(":", "/");
        try {
            //早餐时间
            Date breakfastStart = sdf.parse("06/00/00");
            Date breakfastEnd = sdf.parse("09/00/00");
            //午餐时间
            Date lunchStart = sdf.parse("11/00/00");
            Date lunchEnd = sdf.parse("13/00/00");
            //晚餐时间
            Date dinerStart = sdf.parse("16/00/00");
            Date dinerEnd = sdf.parse("20/00/00");
            Date thisTime = sdf.parse(time);
            if (thisTime.after(breakfastStart) && thisTime.before(breakfastEnd)) {
                return R.mipmap.zaocan;
            } else if (thisTime.after(lunchStart) && thisTime.before(lunchEnd)) {
                return R.mipmap.zhongfan;
            } else if (thisTime.after(dinerStart) && thisTime.before(dinerEnd)) {
                return R.mipmap.wanfan;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return R.mipmap.lingshi;
    }

    @Override
    public int getItemCount() {
        //由于添加了一个header，所以+1
        return this.zhangList.size() + 1;
    }

    class NormalHolder extends RecyclerView.ViewHolder {
        TextView tv_system;
        TextView tv_money;
        ImageView iv_type;
        TextView tv_item_time;

        public NormalHolder(View itemView) {
            super(itemView);
            tv_system = ButterKnife.findById(itemView, R.id.zhang_system);
            tv_money = ButterKnife.findById(itemView, R.id.zhang_money);
            iv_type = ButterKnife.findById(itemView, R.id.iv_type);
            tv_item_time = ButterKnife.findById(itemView, R.id.tv_item_time);
        }
    }

    class TimeHolder extends NormalHolder {

        TextView tv_zhang_time;

        public TimeHolder(View itemView) {
            super(itemView);
            tv_zhang_time = ButterKnife.findById(itemView, R.id.tv_zhang_time);
        }
    }

    class TitleHolder extends RecyclerView.ViewHolder {
        TextView tv_title_info, tv_title_num;

        public TitleHolder(View itemView) {
            super(itemView);
            tv_title_info = ButterKnife.findById(itemView, R.id.tv_title_info);
            tv_title_num = ButterKnife.findById(itemView, R.id.tv_title_num);
        }
    }
}
