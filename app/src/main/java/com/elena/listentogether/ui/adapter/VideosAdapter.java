package com.elena.listentogether.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.elena.listentogether.R;
import com.elena.listentogether.data.local.entity.RoomEntity;
import com.elena.listentogether.data.local.entity.VideoItem;
import com.elena.listentogether.ui.custom.roundedimageview.RoundedImageView;
import com.elena.listentogether.utils.Constants;
import com.elena.listentogether.utils.SharedPrefUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.VideoViewHolder>{

    private List<VideoItem> videos;
    private VideoListener listener;
    private Context context;

    public VideosAdapter(List<VideoItem> videos,  VideoListener listener, Context context) {
        this.videos = videos;
        this.listener = listener;
        this.context = context;
        notifyDataSetChanged();
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_result_vid, parent, false);

        return new VideoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        VideoItem video = videos.get(position);
        holder.title.setText(video.getTitle());
        Picasso.get()
                .load(video.getThumbnailUrl())
                .into(holder.source);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPrefUtils sharedPrefUtils = new SharedPrefUtils(context);
                if (sharedPrefUtils.getBoolean(SharedPrefUtils.KEY_CURRENT_USER_TURN, false)){
                    listener.onVideoSelected(videos.get(holder.getAdapterPosition()));
                }else {
                    Toast.makeText(context, R.string.msg_not_turn, Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView source;

        public VideoViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.text_title);
            source = view.findViewById(R.id.image_thumbnail);
        }
    }

    //todo format titles received from yt data api. e.g. when searching for california, strange chars
}