package com.app.rekog.facetracker.ui;

/**
 * Created by Sdixit on 2/23/2018.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.app.rekog.R;
import com.app.rekog.beans.BitmapBean;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.MyViewHolder> {

    private ArrayList<BitmapBean> photoList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView progressImageView;

        public MyViewHolder(View view) {
            super(view);
            progressImageView = view.findViewById(R.id.photo_image);
        }
    }

    public PhotoAdapter(ArrayList<BitmapBean> photoList) {
        this.photoList = photoList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.photo_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String bitmap = photoList.get(position).filePath;
        Glide.with(holder.progressImageView.getContext()).load(bitmap).placeholder(R.drawable.placeholder_470x352)
                .into(holder.progressImageView);
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    public ArrayList<BitmapBean> getAllItems() {
        return photoList;
    }
}

