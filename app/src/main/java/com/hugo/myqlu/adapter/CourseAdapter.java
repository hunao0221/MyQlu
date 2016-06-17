package com.hugo.myqlu.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hugo.myqlu.R;
import com.hugo.myqlu.bean.CourseBean;
import com.hugo.myqlu.utils.WeekUtils;

import java.util.List;

import butterknife.ButterKnife;

/**
 * @author Hugo
 * Created on 2016/5/12 10:46.
 */
public class CourseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //不含有星期的item
    private static final int NORMAL_ITEM = 0;
    //带星期的item
    private static final int WEEK_ITEM = 1;

    private List<CourseBean> startList;

    public CourseAdapter(List<CourseBean> startList) {
        this.startList = startList;
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }


    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == NORMAL_ITEM) {
            return new NormalViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_normal_course, parent, false));
        } else {
            return new WeekViewHOlder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CourseBean courseBean = startList.get(position);
        String week = WeekUtils.getWeek(startList.get(position).getCourseTime());
        if (holder instanceof WeekViewHOlder) {
            ((WeekViewHOlder) holder).course_name.setText(courseBean.getCourseName());
            ((WeekViewHOlder) holder).course_info.setText(courseBean.getCourstTimeDetail() + "，" + courseBean.getCourseLocation());
            ((WeekViewHOlder) holder).course_week.setText(week);

        } else if (holder instanceof NormalViewHolder) {
            ((NormalViewHolder) holder).course_name.setText(courseBean.getCourseName());
            ((NormalViewHolder) holder).course_info.setText(courseBean.getCourstTimeDetail() + "，" + courseBean.getCourseLocation());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return WEEK_ITEM;
        }
        String currentWeek = startList.get(position).getCourseTime();
        int prePosition = position - 1;
        boolean isDiff = startList.get(prePosition).getCourseTime().equals(currentWeek);
        return isDiff ? NORMAL_ITEM : WEEK_ITEM;
    }

    @Override
    public int getItemCount() {
        return startList.size();
    }

    class WeekViewHOlder extends NormalViewHolder {

        TextView course_week;

        public WeekViewHOlder(View itemView) {
            super(itemView);
            course_week = ButterKnife.findById(itemView, R.id.course_week);
        }
    }

    class NormalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView course_name, course_info;
        CardView course_card;

        public NormalViewHolder(View itemView) {
            super(itemView);
            course_name = ButterKnife.findById(itemView, R.id.course_name);
            course_info = ButterKnife.findById(itemView, R.id.course_info);
            course_card = ButterKnife.findById(itemView, R.id.card_course);
            course_card.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.card_course) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, getAdapterPosition());
                }
            }
        }
    }
}
