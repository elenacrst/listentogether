package com.elena.listentogether.ui.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.elena.listentogether.R;
import com.elena.listentogether.model.local.entity.RoomEntity;
import com.elena.listentogether.model.local.entity.UserEntity;
import com.elena.listentogether.ui.custom.roundedimageview.RoundedImageView;
import com.elena.listentogether.utils.ImageEncodingUtils;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RoomsAdapter extends RecyclerView.Adapter<RoomsAdapter.RoomViewHolder>{

    private List<RoomEntity> roomList;
    private Context context;
    private RoomListener listener;
    private HashMap<Long, List<UserEntity>> usersInRooms = new HashMap<>();

    public RoomsAdapter(List<RoomEntity> roomList, Context context) {
        this.roomList = roomList;
        this.context = context;
        this.listener = (RoomListener)context;
        notifyDataSetChanged();
    }

    public void setUsersInRooms(Map<Long, List<UserEntity>> map){
        usersInRooms = new HashMap<>(map);
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
        if (room.getMembersCount() > 2){//todo request get listens for room, then get user avatars
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

        if (usersInRooms.get(room.getId()) != null){
            if (usersInRooms.get(room.getId()).size() > 1){
                ImageEncodingUtils.decodeBase64AndSetImage(usersInRooms.get(room.getId()).get(1).getAvatar(), holder.secondUser);
            }
            if (usersInRooms.get(room.getId()).size() > 0){
                ImageEncodingUtils.decodeBase64AndSetImage(usersInRooms.get(room.getId()).get(0).getAvatar(), holder.firstUser);
            }
        }


        Picasso.get()
                .load(R.drawable.youtube)
                .placeholder(R.drawable.placeholder_room)
                .into(holder.source);
        Picasso.get()
                .load(room.getIconPath())
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