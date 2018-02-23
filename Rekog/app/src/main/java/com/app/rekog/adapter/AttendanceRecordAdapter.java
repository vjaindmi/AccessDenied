package com.app.rekog.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.rekog.R;
import com.app.rekog.base.Utility;
import com.app.rekog.beans.users.TimeStamp;

import java.util.ArrayList;

/**
 * Created by bhkhera
 */

public class AttendanceRecordAdapter extends RecyclerView.Adapter<AttendanceRecordAdapter.AttendanceRecordViewHolder> {
    private ArrayList<TimeStamp> timeStampList = new ArrayList<>();
    private Context context;

    public AttendanceRecordAdapter(ArrayList<TimeStamp> timeStampList) {
        this.timeStampList = timeStampList;
    }

    @Override
    public AttendanceRecordViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        context = viewGroup.getContext();
        return new AttendanceRecordViewHolder(((LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.row_attendance_record, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(AttendanceRecordViewHolder holder, final int position) {
        holder.mTimeTv.setText(Utility.getDate(Long.parseLong(timeStampList.get(position).getTime())));
    }

    @Override
    public int getItemCount() {
        return timeStampList.size();
    }

    class AttendanceRecordViewHolder extends RecyclerView.ViewHolder {
        TextView mNameTv, mEmailTv, mTimeTv;

        public AttendanceRecordViewHolder(View itemView) {
            super(itemView);
            mNameTv = itemView.findViewById(R.id.name_tv);
            mEmailTv = itemView.findViewById(R.id.email_tv);
            mTimeTv = itemView.findViewById(R.id.time_tv);
        }
    }
}
