package com.elena.listentogether.ui.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.elena.listentogether.R;
import com.elena.listentogether.base.App;
import com.elena.listentogether.data.local.entity.MessageEntity;
import com.elena.listentogether.data.local.entity.RoomEntity;
import com.elena.listentogether.data.local.entity.UserEntity;
import com.elena.listentogether.data.local.entity.VideoItem;
import com.elena.listentogether.ui.activity.RoomDetailActivity;
import com.elena.listentogether.ui.adapter.MessagesAdapter;
import com.elena.listentogether.ui.adapter.VideosAdapter;
import com.elena.listentogether.ui.viewmodel.listen.ListenViewModel;
import com.elena.listentogether.ui.viewmodel.message.MessageViewModel;
import com.elena.listentogether.ui.viewmodel.room.RoomViewModel;

import java.sql.Timestamp;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Retrofit;

public class ChatFragment extends Fragment {
    private static final String ARG_ROOM_ID = "ROOM_ID";
    private static final String ARG_USER_ID = "USER_ID";

    private RecyclerView mRecyclerView;
    private MessagesAdapter mAdapter;
    private EditText mMessageEditText;
    private FrameLayout mSendContainer;

    private MessageViewModel mMessageViewModel;
    @Inject
    Retrofit mRetrofit;
    private Long mRoomId;
    private Long mUserId;

    public ChatFragment() {
        // Required empty public constructor
    }

    public static ChatFragment newInstance(Long roomId, Long userId) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_ROOM_ID, roomId);
        args.putLong(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            mRoomId = getArguments().getLong(ARG_ROOM_ID);
            mUserId = getArguments().getLong(ARG_USER_ID);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container,
                false);
        findViews(view);

        checkoutMessages();

        mSendContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(mMessageEditText.getText().toString())){
                    MessageEntity messageEntity = new MessageEntity();
                    messageEntity.setContent(mMessageEditText.getText().toString());
                    messageEntity.setDate(System.currentTimeMillis());
                    RoomEntity roomEntity = new RoomEntity();
                    roomEntity.setId(mRoomId);
                    messageEntity.setRoom(roomEntity);
                    UserEntity userEntity = new UserEntity();
                    userEntity.setId(mUserId);
                    messageEntity.setUser(userEntity);
                    mMessageViewModel.insertMessage(getContext(), messageEntity);
                    mMessageEditText.setText("");
                }
            }
        });

        return view;
    }

    public void checkoutMessages() {
        setupViewModel();
        mMessageViewModel.loadMessages(getContext(), mRoomId);
        mMessageViewModel.getmMessages().observe(this, new Observer<List<MessageEntity>>() {
            @Override
            public void onChanged(@Nullable List<MessageEntity> messageEntities) {
                setupRecyclerView(messageEntities);
            }
        });
    }

    private void findViews(View view) {
        mRecyclerView = view.findViewById(R.id.recyclerview_chat);
        mMessageEditText = view.findViewById(R.id.edit_message);
        mSendContainer = view.findViewById(R.id.container_send);
    }

    public void setupRecyclerView(List<MessageEntity> messages) {
        mAdapter = new MessagesAdapter(messages);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.smoothScrollToPosition(messages.size()-1);
    }

    private void setupViewModel() {
        mMessageViewModel = ViewModelProviders.of(this)
                .get(MessageViewModel.class);
        ((App) getActivity().getApplication()).getNetComponent().injectChatFragment(this);//dagger
        mMessageViewModel.setRetrofit(mRetrofit);
    }

}
