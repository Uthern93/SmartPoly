package com.android.smartpoly;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MessageRVAdapter extends RecyclerView.Adapter {


    private ArrayList<ChatModal> chatsModalArrayList;
    private Context context;
    @Override
    public int getItemViewType(int position) {
        ChatModal chat = chatsModalArrayList.get(position);
        if (chat.getSender().equals("user")) {
            return 0;
        } else {
            return 1;
        }
    }

    public MessageRVAdapter(ArrayList<ChatModal> chatsModalArrayList, Context context) {
        this.chatsModalArrayList = chatsModalArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 0:
                view=LayoutInflater.from(parent.getContext()).inflate(R.layout.user_msg, parent, false);
                return new UserViewHolder(view);
            case 1 :
                view=LayoutInflater.from(parent.getContext()).inflate(R.layout.bot_msg, parent, false);
                return new BotViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatModal chatsModal=chatsModalArrayList.get(position);
        switch (chatsModal.getSender()) {
            case "user":
                ((UserViewHolder)holder).userTV.setText(chatsModal.getMessage());
                break;
            case "bot" :
                ((BotViewHolder)holder).botMsgTV.setText(chatsModal.getMessage());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return chatsModalArrayList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userTV;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userTV=itemView.findViewById(R.id.TVUser);
        }
    }

    public static class BotViewHolder extends RecyclerView.ViewHolder {
        TextView botMsgTV;
        public BotViewHolder(@NonNull View itemView) {
            super(itemView);
            botMsgTV=itemView.findViewById(R.id.TVBot);
        }
    }
}