package com.android.smartpoly;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private ArrayList<DataClass> dataList;
    private Context context;


    public MyAdapter(ArrayList<DataClass> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(dataList.get(position).getImageURL()).into(holder.recyclerImg);
        holder.txtTitle.setText(dataList.get(position).getTitle());
        holder.txtDesc.setText(dataList.get(position).getCaption());
        holder.txtDate.setText(dataList.get(position).getEdate());
        holder.txtTime.setText(dataList.get(position).getEtime());
        holder.txtUDate.setText(dataList.get(position).getUploadTime());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView recyclerImg;
        TextView txtTitle, txtDesc, txtDate, txtTime, txtUDate;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            recyclerImg=itemView.findViewById(R.id.recyclerImg);
            txtTitle=itemView.findViewById(R.id.recyclerTitle);
            txtDesc=itemView.findViewById(R.id.recyclerDesc);
            txtDate=itemView.findViewById(R.id.recyclerDate);
            txtTime=itemView.findViewById(R.id.recyclerTime);
            txtUDate=itemView.findViewById(R.id.txtDate);
        }
    }

}
