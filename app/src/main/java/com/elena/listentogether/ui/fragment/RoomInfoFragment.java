package com.elena.listentogether.ui.fragment;

import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.elena.listentogether.R;
import com.elena.listentogether.model.local.entity.RoomEntity;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Locale;


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
                    Picasso.get().load(R.drawable.placeholder_room)
                            .placeholder(R.drawable.placeholder_room).into(mThumbnailImageView);
                }
            });
            builder.build().load(mRoom.getIconPath()).placeholder(R.drawable.placeholder_room).into(mThumbnailImageView);
            mSongsCountTextView.setText(mRoom.getSongsCount()+"");
            //Date date = new Date(mRoom.getCreationDate().getTime ());
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(mRoom.getCreationDate());
            String date = DateFormat.format("EEE, d MMM yyyy", cal).toString();
            mCreationDateTextView.setText(date);
        }
        return view;
    }

    private void findViews(View view) {
        mMembersCountTextView = view.findViewById(R.id.text_member_count);
        mSongsCountTextView = view.findViewById(R.id.text_song_count);
        mCreationDateTextView = view.findViewById(R.id.text_creation_date);
        mThumbnailImageView = view.findViewById(R.id.image_thumbnail);
    }

    public RoomEntity getmRoom() {
        return mRoom;
    }
}
