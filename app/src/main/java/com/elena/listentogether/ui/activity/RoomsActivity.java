package com.elena.listentogether.ui.activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.elena.listentogether.utils.ImageEncodingUtils;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.elena.listentogether.R;
import com.elena.listentogether.base.App;
import com.elena.listentogether.model.local.entity.ListenEntity;
import com.elena.listentogether.model.local.entity.RoomEntity;
import com.elena.listentogether.model.local.entity.UserEntity;
import com.elena.listentogether.ui.adapter.RoomListener;
import com.elena.listentogether.ui.adapter.RoomsAdapter;
import com.elena.listentogether.ui.custom.dialog.SearchCallback;
import com.elena.listentogether.ui.custom.dialog.SearchDialog;
import com.elena.listentogether.ui.viewmodel.listen.ListenViewModel;
import com.elena.listentogether.ui.viewmodel.room.RoomViewModel;
import com.elena.listentogether.utils.ConnectivityUtils;
import com.elena.listentogether.utils.Constants;
import com.elena.listentogether.utils.SharedPrefUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Retrofit;

public class RoomsActivity extends AppCompatActivity implements RoomListener, SearchCallback {

    private TextView mCreateRoomTextView;
    private FloatingActionButton mCreateRoomFab;
    private LinearLayout mRoomsContainer;
    private RecyclerView mRoomsRecyclerView;
    private ImageView mSearchButton;
    private ConstraintLayout mListContainer;
    private LinearLayout mNoInternetContainer;
   // private NavigationView mNavigationView;
    private TextView mProfileTextView, mMyRoomsTextView, mAllRoomsTextView, mSettingsTextView, mLogoutTextView;
    private CircleImageView mAvatarImageView;
    private TextView mUsernameTextView, mEmailTextView;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView mNavigationView;
    private Toolbar mToolbar;
    private TextView mTitleTextView;

    private List<RoomEntity> mRoomList = new ArrayList<>();
    private RoomsAdapter mAdapter;
    private SharedPrefUtils mSharedPrefUtils;
    private int mRetrievedListensCount;

    @Inject
    Retrofit mRetrofit;
    private RoomViewModel mRoomViewModel;
    private ListenViewModel mListenViewModel;
    private boolean mIsUserRooms;
    private GoogleSignInClient mGoogleSignInClient;

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
        setupDrawerToggle();
        mSearchButton.setOnClickListener(mOnClickListener);
        mCreateRoomFab.setOnClickListener(mOnClickListener);

        if (ConnectivityUtils.isConnected(this)){
            mListContainer.setVisibility(View.VISIBLE);
            mNoInternetContainer.setVisibility(View.GONE);//fixme add this container to any activity and show it if there s no connection
        }else{
            mListContainer.setVisibility(View.GONE);
            mNoInternetContainer.setVisibility(View.VISIBLE);
        }

        mSharedPrefUtils = new SharedPrefUtils(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    private void populateDrawer() {
        mUsernameTextView.setText((TextUtils.isEmpty(mSharedPrefUtils.getString(SharedPrefUtils.KEY_PROFILE_USERNAME,""))?
                (!TextUtils.isEmpty(getIntent().getStringExtra("username"))?getIntent().getStringExtra("username"):"No username"):
                mSharedPrefUtils.getString(SharedPrefUtils.KEY_PROFILE_USERNAME, "")));
        mEmailTextView.setText((TextUtils.isEmpty(mSharedPrefUtils.getString(SharedPrefUtils.KEY_PROFILE_EMAIL,""))?
                (!TextUtils.isEmpty(getIntent().getStringExtra("email"))?getIntent().getStringExtra("email"):"No email"):
                mSharedPrefUtils.getString(SharedPrefUtils.KEY_PROFILE_EMAIL, "")));
        if (!TextUtils.isEmpty(mSharedPrefUtils.getString(SharedPrefUtils.KEY_PROFILE_AVATAR, ""))){
            ImageEncodingUtils.decodeBase64AndSetImage(mSharedPrefUtils.getString(SharedPrefUtils.KEY_PROFILE_AVATAR, ""), mAvatarImageView);
        }
    }

    private void setupDrawerToggle() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
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

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.navigation_drawer);
        mToolbar = findViewById(R.id.toolbar);
        mTitleTextView = findViewById(R.id.text_title);
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
        EditText editIcon = deleteDialogView.findViewById(R.id.edit_room_icon);
        deleteDialog.setView(deleteDialogView);
        deleteDialogView.findViewById(R.id.btn_create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editName.getText()) ||
                        TextUtils.isEmpty(editIcon.getText())){
                    Toast.makeText(RoomsActivity.this, R.string.msg_complete_fields,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                RoomEntity roomEntity = new RoomEntity();
                SharedPrefUtils sharedPrefUtils = new SharedPrefUtils(RoomsActivity.this);
                UserEntity userEntity = new UserEntity();
                userEntity.setId(sharedPrefUtils.getLong(SharedPrefUtils.KEY_PROFILE_ID));
                roomEntity.setAuthor(userEntity);
                roomEntity.setSource( Constants.SOURCE_YOUTUBE );
                roomEntity.setIconPath(editIcon.getText().toString());
                roomEntity.setName(editName.getText().toString());
                roomEntity.setMembersCount(1);
                roomEntity.setCreationDate(System.currentTimeMillis());
                roomEntity.setSongsCount(0);
                mRoomViewModel.insertRoom(RoomsActivity.this,roomEntity);
                deleteDialog.dismiss();
                Intent openRoomDetailActivity = new Intent(RoomsActivity.this
                        ,RoomDetailActivity.class);
                openRoomDetailActivity.putExtra(RoomDetailActivity.EXTRA_ROOM, roomEntity);
                openRoomDetailActivity.putExtra("new",true);
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

    //fixme change author here & in backend to 'dj' = player who's the next one to choose a track
    @Override
    public void onRoomSelected(RoomEntity roomEntity) {
        Intent openRoomDetailsActivity = new Intent(this, RoomDetailActivity.class);
        UserEntity author = new UserEntity();
        author.setId(roomEntity.getAuthor().getId());
        roomEntity.setAuthor(author);
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
        SharedPrefUtils sharedPrefUtils = new SharedPrefUtils(this);
        switch (sharedPrefUtils.getString(SharedPrefUtils.KEY_PROFILE_TYPE, Constants.PROFILE_TYPE_BASIC)){
            case Constants.PROFILE_TYPE_GOOGLE:
                mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //On Succesfull signout we navigate the user back to LoginActivity
                        logout();
                    }
                });
                break;
            case Constants.PROFILE_TYPE_FACEBOOK:
                LoginManager.getInstance().logOut();
                logout();
                break;
            default:
                logout();
                break;
        }
    }

    private void logout() {
        SharedPrefUtils sharedPrefUtils = new SharedPrefUtils(this);
        sharedPrefUtils.clear();
        Intent intent=new Intent(RoomsActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void onSettings(View view) {//fixme selectors
        Intent openSettingsActivity = new Intent(this, SettingsActivity.class);
        startActivity(openSettingsActivity);
    }

    public void onAllRooms(View view) {
        mDrawerLayout.closeDrawer(GravityCompat.START);
        mTitleTextView.setText(getString(R.string.title_all_rooms));
        mSearchButton.setVisibility(View.VISIBLE);
        mIsUserRooms = false;
        mRoomViewModel.loadRooms(this);
        mRoomViewModel.getmRooms().observe(this, new Observer<List<RoomEntity>>() {
            @Override
            public void onChanged(@Nullable List<RoomEntity> roomEntities) {
                mRoomList = new ArrayList<>();
                if (roomEntities == null){
                    setupRecyclerView();
                    return;
                }
                for (RoomEntity r: roomEntities){
                    if (r != null){
                        mRoomList.add(r);
                    }
                }
                setupRecyclerView();
                if (mRoomList == null || mRoomList.size() == 0){
                    return;
                }

                mRetrievedListensCount = 0;
                Map<Long, List<UserEntity>> usersInRoom = new HashMap<>();
                for (RoomEntity roomEntity : mRoomList){
                    mListenViewModel.loadListens(RoomsActivity.this, roomEntity.getId());
                    mListenViewModel.getmListens().observe(RoomsActivity.this, new Observer<List<ListenEntity>>() {
                        @Override
                        public void onChanged(List<ListenEntity> listenEntities) {
                            mRetrievedListensCount++;
                            if (listenEntities == null || listenEntities.size() == 0){
                                return;
                            }
                            List<UserEntity> users = new ArrayList<>();
                            for (ListenEntity l: listenEntities){
                                users.add(l.getUser());
                            }
                            usersInRoom.put(listenEntities.get(0).getRoom().getId(), users);
                            if (mRetrievedListensCount == roomEntities.size()){
                                mAdapter.setUsersInRooms(usersInRoom);
                            }
                        }
                    });
                }

            }
        });
    }

    public void onMyRooms(View view) {
        mDrawerLayout.closeDrawer(GravityCompat.START);
        mTitleTextView.setText(getString(R.string.title_my_rooms));
        mSearchButton.setVisibility(View.INVISIBLE);
        mIsUserRooms = true;
        SharedPrefUtils sharedPrefUtils = new SharedPrefUtils(this);
        mRoomViewModel.loadUserRooms(this,sharedPrefUtils.getLong(SharedPrefUtils.KEY_PROFILE_ID));
        mRoomViewModel.getmUserRooms().observe(this, new Observer<List<RoomEntity>>() {
            @Override
            public void onChanged(@Nullable List<RoomEntity> roomEntities) {
                mRoomList = new ArrayList<>();
                if (roomEntities == null){
                    setupRecyclerView();
                    return;
                }
                for (RoomEntity r: roomEntities){
                    if (r != null){
                        mRoomList.add(r);
                    }
                }
                setupRecyclerView();
                if (mRoomList == null || mRoomList.size() == 0){
                    return;
                }

                mRetrievedListensCount = 0;
                Map<Long, List<UserEntity>> usersInRoom = new HashMap<>();
                for (RoomEntity roomEntity : mRoomList){
                    mListenViewModel.loadListens(RoomsActivity.this, roomEntity.getId());
                    mListenViewModel.getmListens().observe(RoomsActivity.this, new Observer<List<ListenEntity>>() {
                        @Override
                        public void onChanged(List<ListenEntity> listenEntities) {
                            mRetrievedListensCount++;
                            if (listenEntities == null || listenEntities.size() == 0){
                                return;
                            }
                            List<UserEntity> users = new ArrayList<>();
                            for (ListenEntity l: listenEntities){
                                users.add(l.getUser());
                            }
                            usersInRoom.put(listenEntities.get(0).getRoom().getId(), users);
                            if (mRetrievedListensCount == roomEntities.size()){
                                mAdapter.setUsersInRooms(usersInRoom);
                            }
                        }
                    });
                }
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
        populateDrawer();
        if(mIsUserRooms){
            /*SharedPrefUtils sharedPrefUtils = new SharedPrefUtils(this);
            mRoomViewModel.loadUserRooms(this, sharedPrefUtils.getLong(SharedPrefUtils.KEY_PROFILE_ID));
            mRoomViewModel.getmUserRooms().observe(this, new Observer<List<RoomEntity>>() {
                @Override
                public void onChanged(@Nullable List<RoomEntity> roomEntities) {
                  //  if (roomEntities != null) {
                        mRoomList = new ArrayList<>(roomEntities);
                   /* }else{
                        mRoomList = null;
                    }/
                    setupRecyclerView();
                }
            });*/
            onMyRooms(null);
        }else{
            onAllRooms(null);
        /*    mRoomViewModel.loadRooms(this);
            mRoomViewModel.getmRooms().observe(this, new Observer<List<RoomEntity>>() {
                @Override
                public void onChanged(@Nullable List<RoomEntity> roomEntities) {
                   // if (roomEntities != null) {
                        mRoomList = new ArrayList<>(roomEntities);
                   /* }else{
                        mRoomList = null;
                    }*
                    setupRecyclerView();
                }
            });*/
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            Log.wtf("onclick", "drawer "+mDrawerLayout.isDrawerOpen(GravityCompat.START));
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }
}
//fixme password for room

