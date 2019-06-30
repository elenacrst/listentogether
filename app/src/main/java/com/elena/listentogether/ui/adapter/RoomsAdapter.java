package com.elena.listentogether.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.elena.listentogether.R;
import com.elena.listentogether.data.local.entity.RoomEntity;
import com.elena.listentogether.ui.custom.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RoomsAdapter extends RecyclerView.Adapter<RoomsAdapter.RoomViewHolder>{

    private List<RoomEntity> roomList;
    private Context context;
    private RoomListener listener;

    public RoomsAdapter(List<RoomEntity> roomList, Context context) {
        this.roomList = roomList;
        this.context = context;
        this.listener = (RoomListener)context;
        notifyDataSetChanged();
    }

    @Override
    public RoomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_room, parent, false);

        return new RoomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RoomViewHolder holder, int position) {
        RoomEntity room = roomList.get(position);
        holder.title.setText(room.getName());
        holder.song.setText(room.getLastSong());
        Picasso.get()
                .load(room.getIconPath())
                .placeholder(R.drawable.placeholder_room)
                .into(holder.thumbnail);
        if (room.getMembersCount() > 2){
            holder.secondUser.setVisibility(View.VISIBLE);
            //todo load users for room
            holder.users.setVisibility(View.VISIBLE);
            holder.usersCount.setVisibility(View.VISIBLE);
            holder.usersCount.setText("+ "+(room.getMembersCount()-2));
        }else if (room.getMembersCount() == 2){
            holder.secondUser.setVisibility(View.VISIBLE);
            holder.users.setVisibility(View.GONE);
            holder.usersCount.setVisibility(View.GONE);
        }else{
            holder.secondUser.setVisibility(View.GONE);
            holder.users.setVisibility(View.GONE);
            holder.usersCount.setVisibility(View.GONE);
        }

        Picasso.get()
                .load(R.drawable.youtube)
                .placeholder(R.drawable.placeholder_room)
                .into(holder.source);
        Picasso.get()
                .load("https://www.southwales.ac.uk/media/images/Music_and_Sound_End_of.91ae7805.fill-900x682.format-jpeg.jpg")
                .placeholder(R.drawable.placeholder_room)
                .into(holder.thumbnail);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onRoomSelected(roomList.get(holder.getAdapterPosition()));

            }
        });

    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public class RoomViewHolder extends RecyclerView.ViewHolder {
        public TextView title, song;
        public RoundedImageView thumbnail;
        public CircleImageView firstUser, secondUser;
        public TextView usersCount;
        public CircleImageView users;
        public ImageView source;

        public RoomViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.text_room_name);
            song = view.findViewById(R.id.text_room_song);
            thumbnail = view.findViewById(R.id.image_room);
            firstUser = view.findViewById(R.id.image_first_user);
            secondUser = view.findViewById(R.id.image_second_user);
            usersCount = view.findViewById(R.id.text_users_count);
            users = view.findViewById(R.id.image_users);
            source = view.findViewById(R.id.image_source);
        }
    }
}