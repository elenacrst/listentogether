package com.elena.listentogether.ui.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.elena.listentogether.R;
import com.elena.listentogether.base.App;
import com.elena.listentogether.data.local.entity.ListenEntity;
import com.elena.listentogether.data.local.entity.RoomEntity;
import com.elena.listentogether.data.local.entity.UserEntity;
import com.elena.listentogether.ui.adapter.RoomListener;
import com.elena.listentogether.ui.adapter.RoomsAdapter;
import com.elena.listentogether.ui.custom.dialog.SearchCallback;
import com.elena.listentogether.ui.custom.dialog.SearchDialog;
import com.elena.listentogether.ui.viewmodel.listen.ListenViewModel;
import com.elena.listentogether.ui.viewmodel.room.RoomViewModel;
import com.elena.listentogether.utils.ConnectivityUtils;
import com.elena.listentogether.utils.Constants;
import com.elena.listentogether.utils.SharedPrefUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Retrofit;

public class RoomsActivity extends AppCompatActivity implements RoomListener, SearchCallback {

    private TextView mCreateRoomTextView;
    private FloatingActionButton mCreateRoomFab;
    private ConstraintLayout mRoomsContainer;
    private RecyclerView mRoomsRecyclerView;
    private ImageView mSearchButton;
    private ConstraintLayout mListContainer;
    private LinearLayout mNoInternetContainer;
   // private NavigationView mNavigationView;
    private TextView mProfileTextView, mMyRoomsTextView, mAllRoomsTextView, mSettingsTextView
            , mLogoutTextView;
    private CircleImageView mAvatarImageView;
    private TextView mUsernameTextView, mEmailTextView;

    private List<RoomEntity> mRoomList = new ArrayList<>();
    private RoomsAdapter mAdapter;

    @Inject
    Retrofit mRetrofit;
    private RoomViewModel mRoomViewModel;
    private ListenViewModel mListenViewModel;
    private boolean mIsUserRooms;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_search:
                    showSearchDialog();
                    break;
                case R.id.btn_create_room:
                    showCreateRoomDialog();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_rooms);
        findViews();
        setupGradient();
        setupRecyclerView();
        mSearchButton.setOnClickListener(mOnClickListener);
        mCreateRoomFab.setOnClickListener(mOnClickListener);

        if (ConnectivityUtils.isConnected(this)){
            mListContainer.setVisibility(View.VISIBLE);
            mNoInternetContainer.setVisibility(View.GONE);//todo add this container to any activity and show it if there s no connection
        }else{
            mListContainer.setVisibility(View.GONE);
            mNoInternetContainer.setVisibility(View.VISIBLE);
        }

        SharedPrefUtils sharedPrefUtils = new SharedPrefUtils(this);
        if (!TextUtils.isEmpty(sharedPrefUtils.getString(SharedPrefUtils.KEY_PROFILE_AVATAR, ""))){
            Picasso.get().load(sharedPrefUtils.getString(SharedPrefUtils.KEY_PROFILE_AVATAR, ""))
                    .into(mAvatarImageView);
        }

        mUsernameTextView.setText(!TextUtils.isEmpty(sharedPrefUtils.getString(SharedPrefUtils.KEY_PROFILE_USERNAME, "No username"))?
                sharedPrefUtils.getString(SharedPrefUtils.KEY_PROFILE_USERNAME, "No username"):
                "No username");
        mEmailTextView.setText(!TextUtils.isEmpty(sharedPrefUtils.getString(SharedPrefUtils.KEY_PROFILE_EMAIL, "No email"))?
                sharedPrefUtils.getString(SharedPrefUtils.KEY_PROFILE_EMAIL, "No email"):
                "No email");

    }

    private void setupRecyclerView() {
        mAdapter = new RoomsAdapter(mRoomList, this);
        RecyclerView.LayoutManager mLayoutManager = 
                new LinearLayoutManager(getApplicationContext());
        mRoomsRecyclerView.setLayoutManager(mLayoutManager);
        mRoomsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRoomsRecyclerView.setAdapter(mAdapter);

       // initRoomsData();
    }

    private void initRoomsData() {
        /*RoomEntity roomEntity = new RoomEntity(1,"Heavy Metal",1,"19/05/2019",1,null,1,"Anything but ordinary","Youtube");
        mRoomList.add(roomEntity);
        roomEntity = new RoomEntity(2,"Violin",1,"19/05/2019",
                2,null,1,"Things I'll never say",
                "Youtube");
        mRoomList.add(roomEntity);
        roomEntity = new RoomEntity(3,"Avril Lavigne",1,
                "19/05/2019",3,null,1,
                "Fall to Pieces","Youtube");
        mRoomList.add(roomEntity);
        mAdapter.notifyDataSetChanged();
*/
    }

    private void setupGradient() {
        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{ContextCompat.getColor(this, R.color.darkBlueRooms),
                        ContextCompat.getColor(this, R.color.lightBlueRooms),
                        ContextCompat.getColor(this, R.color.lightBlueRooms),
                        ContextCompat.getColor(this, R.color.midRedRoom)});
        mRoomsContainer.setBackground(gradientDrawable);
    }

    private void findViews() {
        mCreateRoomFab = findViewById(R.id.btn_create_room);
        mCreateRoomTextView = findViewById(R.id.text_create_room);
        mRoomsContainer = findViewById(R.id.container_rooms);
        mRoomsRecyclerView = findViewById(R.id.recyclerview_rooms);
        mSearchButton = findViewById(R.id.btn_search);

        mListContainer = findViewById(R.id.container_list);
        mNoInternetContainer = findViewById(R.id.container_no_internet);

        //mNavigationView = findViewById(R.id.navigation_drawer);
        mProfileTextView = //mNavigationView.
                findViewById(R.id.text_profile);
        mMyRoomsTextView =// mNavigationView.
                findViewById(R.id.text_my_rooms);
        mAllRoomsTextView = //mNavigationView.
                findViewById(R.id.text_all_rooms);
        mSettingsTextView = //mNavigationView.
                findViewById(R.id.text_settings);
        mLogoutTextView = //mNavigationView.
                findViewById(R.id.text_logout);

        mAvatarImageView = findViewById(R.id.image_avatar);
        mUsernameTextView = findViewById(R.id.text_username);
        mEmailTextView = findViewById(R.id.text_email);
    }

    private void showSearchDialog(){
        SearchDialog alert = new SearchDialog();
        alert.showDialog(RoomsActivity.this, this, getString(R.string.hint_search_room));
    }

    private void showCreateRoomDialog() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View deleteDialogView = factory.inflate(R.layout.dialog_create_room, null);
        final AlertDialog deleteDialog = new AlertDialog.Builder(this).create();
        EditText editName = deleteDialogView.findViewById(R.id.edit_room_name);
      //  EditText editPass = deleteDialogView.findViewById(R.id.edit_room_password);
        EditText editIcon = deleteDialogView.findViewById(R.id.edit_room_icon);
        //RadioButton radioYoutube = deleteDialogView.findViewById(R.id.radio_youtube);
       // RadioButton radioSpotify = deleteDialogView.findViewById(R.id.radio_spotify);
        deleteDialog.setView(deleteDialogView);
        deleteDialogView.findViewById(R.id.btn_create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editName.getText()) ||
        //                TextUtils.isEmpty(editPass.getText()) ||
                        TextUtils.isEmpty(editIcon.getText())){
                    Toast.makeText(RoomsActivity.this, R.string.msg_complete_fields, Toast.LENGTH_SHORT).show();
                    return;
                }
                RoomEntity roomEntity = new RoomEntity();
                SharedPrefUtils sharedPrefUtils = new SharedPrefUtils(RoomsActivity.this);
                UserEntity userEntity = new UserEntity();
                userEntity.setId(sharedPrefUtils.getLong(SharedPrefUtils.KEY_PROFILE_ID));
                roomEntity.setAuthor(userEntity);//todo change author here & in backend to 'dj' = player who's the next one to choose a track
                roomEntity.setSource( Constants.SOURCE_YOUTUBE );
          //      roomEntity.setPassword(editPass.getText().toString());
                roomEntity.setIconPath(editIcon.getText().toString());
                roomEntity.setName(editName.getText().toString());

                mRoomViewModel.insertRoom(RoomsActivity.this,roomEntity);

                deleteDialog.dismiss();

                Intent openRoomDetailActivity = new Intent(RoomsActivity.this,RoomDetailActivity.class);
                openRoomDetailActivity.putExtra(RoomDetailActivity.EXTRA_ROOM, roomEntity);
                startActivity(openRoomDetailActivity);
            }
        });
        deleteDialogView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.dismiss();
            }
        });

        deleteDialog.show();
    }

    private void setupViewModel() {
        mRoomViewModel = ViewModelProviders.of(this)
                .get(RoomViewModel.class);
        ((App) getApplication()).getNetComponent().injectRooms(this);//dagger
        mRoomViewModel.setRetrofit(mRetrofit);

        mListenViewModel = ViewModelProviders.of(this).get(ListenViewModel.class);
        mListenViewModel.setRetrofit(mRetrofit);
    }

    @Override
    public void onRoomSelected(RoomEntity roomEntity) {
        Intent openRoomDetailsActivity = new Intent(this, RoomDetailActivity.class);
        openRoomDetailsActivity.putExtra(RoomDetailActivity.EXTRA_ROOM, roomEntity);
        startActivity(openRoomDetailsActivity);
        ListenEntity listenEntity = new ListenEntity();
        listenEntity.setRoom(roomEntity);
        SharedPrefUtils sharedPrefUtils = new SharedPrefUtils(RoomsActivity.this);
        UserEntity userEntity = new UserEntity();
        userEntity.setId(sharedPrefUtils.getLong(SharedPrefUtils.KEY_PROFILE_ID));
        listenEntity.setUser(userEntity);
        mListenViewModel.insertListen(RoomsActivity.this, listenEntity);
    }

    @Override
    public void onSearch(String query) {

    }

    public void onLogout(View view) {

    }

    public void onSettings(View view) {//todo selectors
        Intent openSettingsActivity = new Intent(this, SettingsActivity.class);
        startActivity(openSettingsActivity);
    }

    public void onAllRooms(View view) {
        mIsUserRooms = false;
        mRoomViewModel.loadRooms(this);
        mRoomViewModel.getmRooms().observe(this, new Observer<List<RoomEntity>>() {
            @Override
            public void onChanged(@Nullable List<RoomEntity> roomEntities) {
                mRoomList = roomEntities;
                setupRecyclerView();
            }
        });
    }

    public void onMyRooms(View view) {
        mIsUserRooms = true;
        SharedPrefUtils sharedPrefUtils = new SharedPrefUtils(this);
        mRoomViewModel.loadUserRooms(this,sharedPrefUtils.getLong(SharedPrefUtils.KEY_PROFILE_ID));
        mRoomViewModel.getmUserRooms().observe(this, new Observer<List<RoomEntity>>() {
            @Override
            public void onChanged(@Nullable List<RoomEntity> roomEntities) {
                mRoomList = roomEntities;
                setupRecyclerView();
            }
        });
    }

    public void onProfile(View view) {
        Intent openProfileActivity = new Intent(this, ProfileActivity.class);
        startActivity(openProfileActivity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupViewModel();
        if(mIsUserRooms){
            SharedPrefUtils sharedPrefUtils = new SharedPrefUtils(this);
            mRoomViewModel.loadUserRooms(this, sharedPrefUtils.getLong(SharedPrefUtils.KEY_PROFILE_ID));
            mRoomViewModel.getmUserRooms().observe(this, new Observer<List<RoomEntity>>() {
                @Override
                public void onChanged(@Nullable List<RoomEntity> roomEntities) {
                    if (roomEntities != null) {
                        mRoomList = new ArrayList<>(roomEntities);
                    }else{
                        mRoomList = null;
                    }
                    setupRecyclerView();
                }
            });
        }else{
            mRoomViewModel.loadRooms(this);
            mRoomViewModel.getmRooms().observe(this, new Observer<List<RoomEntity>>() {
                @Override
                public void onChanged(@Nullable List<RoomEntity> roomEntities) {
                    if (roomEntities != null) {
                        mRoomList = new ArrayList<>(roomEntities);
                    }else{
                        mRoomList = null;
                    }
                    setupRecyclerView();
                }
            });
        }

    }
}
//todo password for room

