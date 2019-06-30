package com.elena.listentogether.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.elena.listentogether.R;
import com.elena.listentogether.data.local.entity.MessageEntity;
import com.elena.listentogether.data.local.entity.VideoItem;
import com.elena.listentogether.utils.ImageEncodingUtils;
import com.elena.listentogether.utils.SharedPrefUtils;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>{

    private List<MessageEntity> messages;

    public MessagesAdapter(List<MessageEntity> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);

        return new MessageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        MessageEntity messageEntity = messages.get(position);
        holder.content.setText(messageEntity.getContent());
        if(messageEntity.getUser().getAvatar() != null){
            ImageEncodingUtils.decodeBase64AndSetImage(messageEntity.getUser().getAvatar(), holder.avatar);
        }else{

        }
        SimpleDateFormat format = new SimpleDateFormat("MMM d, hh:mm");
        String date = format.format(messageEntity.getDate());
        holder.date.setText(messageEntity.getUser().getUsername()+", "+date);

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView content, date;
        public ImageView avatar;

        public MessageViewHolder(View view) {
            super(view);
            content = view.findViewById(R.id.text_content);
            date = view.findViewById(R.id.text_date);
            avatar = view.findViewById(R.id.image_avatar);
        }
    }

}