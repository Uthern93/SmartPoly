package com.jtmk.smartpoly;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.smartpoly.R;
import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private ArrayList<DataClass> dataList;
    private Context context;
    String key="";
    String imageUrl="";


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
        holder.txtDate.setText("Date : "+dataList.get(position).getEdate());
        holder.txtTime.setText("Time : "+dataList.get(position).getEtime());
        holder.txtUDate.setText(dataList.get(position).getUploadTime());
        holder.FAMenu.bringToFront();
        key=dataList.get(position).getKey().toString();
        imageUrl=dataList.get(position).getImageURL().toString();


        holder.deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Notice Board");
                FirebaseStorage storage=FirebaseStorage.getInstance();

                StorageReference storageReference=storage.getReferenceFromUrl(imageUrl);
                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        reference.child(key).removeValue();
                        Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        holder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, UpdateActivity.class);
                intent.putExtra("image", dataList.get(holder.getAdapterPosition()).getImageURL());
                intent.putExtra("title", dataList.get(holder.getAdapterPosition()).getTitle());
                intent.putExtra("description", dataList.get(holder.getAdapterPosition()).getCaption());
                intent.putExtra("Edate", dataList.get(holder.getAdapterPosition()).getEdate());
                intent.putExtra("Etime", dataList.get(holder.getAdapterPosition()).getEtime());
                intent.putExtra("uploadTime", dataList.get(holder.getAdapterPosition()).getUploadTime());
                intent.putExtra("key", dataList.get(holder.getAdapterPosition()).getKey());
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void searchDataList(ArrayList<DataClass> searchList) {
        dataList = searchList;
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView recyclerImg;
        TextView txtTitle, txtDesc, txtDate, txtTime, txtUDate;
        FloatingActionButton deletebtn, editBtn;
        FloatingActionMenu FAMenu;
        private FirebaseAuth auth;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            recyclerImg=itemView.findViewById(R.id.recyclerImg);
            txtTitle=itemView.findViewById(R.id.recyclerTitle);
            txtDesc=itemView.findViewById(R.id.recyclerDesc);
            txtDate=itemView.findViewById(R.id.recyclerDate);
            txtTime=itemView.findViewById(R.id.recyclerTime);
            txtUDate=itemView.findViewById(R.id.txtDate);
            deletebtn=itemView.findViewById(R.id.deleteBtn);
            editBtn=itemView.findViewById(R.id.editBtn);
            FAMenu=itemView.findViewById(R.id.FAMbtn);
            auth=FirebaseAuth.getInstance();

        }
    }


}
