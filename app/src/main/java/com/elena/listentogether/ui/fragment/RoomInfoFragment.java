package com.elena.listentogether.ui.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.elena.listentogether.R;
import com.elena.listentogether.data.local.entity.RoomEntity;
import com.elena.listentogether.data.local.entity.VideoItem;
import com.elena.listentogether.ui.activity.RoomDetailActivity;
import com.elena.listentogether.ui.adapter.VideosAdapter;
import com.elena.listentogether.youtube.YoutubeSearchListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RoomInfoFragment extends Fragment{
    private static final String ARG_ROOM = "ROOM";
    private RoomEntity mRoom;

    private TextView mMembersCountTextView, mSongsCountTextView, mCreationDateTextView;
    private ImageView mThumbnailImageView;

//    private OnFragmentInteractionListener mListener;

    public RoomInfoFragment() {
        // Required empty public constructor
    }

    public static RoomInfoFragment newInstance(RoomEntity roomEntity) {
        RoomInfoFragment fragment = new RoomInfoFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_ROOM, roomEntity);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRoom = getArguments().getParcelable(ARG_ROOM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_room_info, container,
                false);

        findViews(view);
        if (mRoom != null){
            mMembersCountTextView.setText(mRoom.getMembersCount()+"");
          /*  Picasso.get().load(mRoom.getIconPath())
                    //.placeholder(R.color.lightBlueRooms)
                    .into(mThumbnailImageView);*/
            Picasso.Builder builder = new Picasso.Builder(getContext());
            builder.listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    exception.printStackTrace();
                    Picasso.get().load("https://www.southwales.ac.uk/media/images/Music_and_Sound_End_of.91ae7805.fill-900x682.format-jpeg.jpg")
                            .placeholder(R.drawable.placeholder_room).into(mThumbnailImageView);
                }
            });
            builder.build().load(mRoom.getIconPath()).into(mThumbnailImageView);
            mSongsCountTextView.setText(mRoom.getSongsCount()+"");
            mCreationDateTextView.setText(mRoom.getCreationDate()+"");
        }
        return view;
    }

    private void findViews(View view) {
        mMembersCountTextView = view.findViewById(R.id.text_member_count);
        mSongsCountTextView = view.findViewById(R.id.text_song_count);
        mCreationDateTextView = view.findViewById(R.id.text_creation_date);
        mThumbnailImageView = view.findViewById(R.id.image_thumbnail);
    }

}
