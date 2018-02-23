package com.app.rekog.facetracker.ui;

/**
 * Created by Sdixit on 2/23/2018.
 */

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import com.kairos.android.example.R;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.MyViewHolder> {

    private List<Bitmap> photoList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView progressImageView;

        public MyViewHolder(View view) {
            super(view);
            progressImageView =  view.findViewById(R.id.photo_image);
        }
    }

    public PhotoAdapter(List<Bitmap> photoList) {
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
        Bitmap bitmap = photoList.get(position);
        holder.progressImageView.setImageBitmap(bitmap);
    }


    public void addItem(Bitmap photoItem){
        photoList.add(photoItem);
    }
    @Override
    public int getItemCount() {
        return photoList.size();
    }
}

