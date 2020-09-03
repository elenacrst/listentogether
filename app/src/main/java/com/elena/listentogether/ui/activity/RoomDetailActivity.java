package com.elena.listentogether.ui.activity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.elena.listentogether.R;
import com.elena.listentogether.base.App;
import com.elena.listentogether.model.local.entity.ListenEntity;
import com.elena.listentogether.model.local.entity.RoomEntity;
import com.elena.listentogether.model.local.entity.UserEntity;
import com.elena.listentogether.model.local.entity.VideoItem;
import com.elena.listentogether.notification.NotificationListener;
import com.elena.listentogether.notification.NotificationReceiver;
import com.elena.listentogether.ui.adapter.VideoListener;
import com.elena.listentogether.ui.custom.dialog.SearchCallback;
import com.elena.listentogether.ui.custom.dialog.SearchDialog;
import com.elena.listentogether.ui.fragment.ChatFragment;
import com.elena.listentogether.ui.fragment.RoomInfoFragment;
import com.elena.listentogether.ui.fragment.SearchResultsFragment;
import com.elena.listentogether.ui.viewmodel.listen.ListenViewModel;
import com.elena.listentogether.ui.viewmodel.room.RoomViewModel;
import com.elena.listentogether.utils.Constants;
import com.elena.listentogether.utils.SharedPrefUtils;
import com.elena.listentogether.youtube.YoutubeAsyncTask;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Retrofit;

public class RoomDetailActivity extends FragmentActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener, SearchCallback,
        NotificationListener, VideoListener {

    public static final String EXTRA_ROOM = "ROOM";

    private static final String TAG_ROOM_INFO_FRAGMENT = "ROOM_INFO_FRAGMENT";
    private static final String TAG_SEARCH_RESULTS_FRAGMENT = "SEARCH_RESULTS_FRAGMENT";
    private static final String TAG_CHAT_FRAGMENT = "CHAT_FRAGMENT";

    private List<String> mFragments = new ArrayList<>();
    private String mCurrentTagDisplayed;
    private int mCurrentFragmentIndex;

    private BottomNavigationView mNavigationView;
    private SearchResultsFragment mSearchResultsFragment;
    private ImageButton mSearchImageButton;
    private YouTubePlayer mPlayer;
    private TextView mRoomTitleTextView;
    private TextView mCurrentSongTextView;
    private RoomInfoFragment mRoomInfoFragment;

    @Inject
    Retrofit mRetrofit;
    private ListenViewModel mListenViewModel;

    private RoomEntity mCurrentRoom;
    private SharedPrefUtils mSharedPrefUtils;
    private long mCurrentUserId;

    private boolean mIsCurrentUsersTurn;

    private BroadcastReceiver mReceiver;
    private SearchDialog mVideoSearchDialog;

    private RoomViewModel mRoomViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_detail);
        //fixme dialog if user doesnt have play services - firebase messaging requires it

        setupViewModel();
        findViews();
        setupYoutubeFragment();

        mSharedPrefUtils = new SharedPrefUtils(this);
        mCurrentUserId = mSharedPrefUtils.getLong(SharedPrefUtils.KEY_PROFILE_ID);

        if (getIntent().getParcelableExtra(EXTRA_ROOM) != null){
            mCurrentRoom = getIntent().getParcelableExtra(EXTRA_ROOM);
            if (getIntent().hasExtra("new")){
                mIsCurrentUsersTurn = true;
            }
            onRoomInfoRefreshed(mCurrentRoom);
            mRoomTitleTextView.setText(mCurrentRoom.getName());
            FirebaseMessaging.getInstance().subscribeToTopic("room"+mCurrentRoom.getId());

            mListenViewModel.loadListens(this, mCurrentRoom.getId());
            mListenViewModel.getmListens().observe(this, new Observer<List<ListenEntity>>() {
                @Override
                public void onChanged(@Nullable List<ListenEntity> listenEntities) {
                    for (ListenEntity l: listenEntities){
                        Log.wtf("listen", l.getId()+" "+l.getRoom()+" "+l.getUser().getId()+" "+l.getUser().getUsername());
                    }
                }
            });

           /* if (mCurrentRoom.getAuthor().getId() == mCurrentUserId){
                mSharedPrefUtils.saveBoolean(SharedPrefUtils.KEY_CURRENT_USER_TURN, true);
                mNavigationView.setSelectedItemId(R.id.action_search);
                mIsCurrentUsersTurn = true;
                showSearchDialog();
            }*/

            mNavigationView.setOnNavigationItemSelectedListener(this);
            mSearchResultsFragment = SearchResultsFragment.newInstance();
            mRoomInfoFragment = RoomInfoFragment.newInstance(mCurrentRoom);
            createOrShowFragment(mRoomInfoFragment, TAG_ROOM_INFO_FRAGMENT);

            mRoomViewModel.loadRoom(this, mCurrentRoom.getId());
            mRoomViewModel.getmCurrentRoom().observe(this, new Observer<RoomEntity>() {
                @Override
                public void onChanged(@Nullable RoomEntity roomEntity) {
                    mCurrentRoom = roomEntity;
                    //mIsCurrentUsersTurn = roomEntity != null && roomEntity.getAuthor() != null &&
                      //      mCurrentUserId == roomEntity.getAuthor().getId();
                   // mSharedPrefUtils.saveBoolean(SharedPrefUtils.KEY_CURRENT_USER_TURN, mIsCurrentUsersTurn);
                    onRoomInfoRefreshed(roomEntity);

                    if (mCurrentRoom.getAuthor().getId() == mCurrentUserId){
                        mSharedPrefUtils.saveBoolean(SharedPrefUtils.KEY_CURRENT_USER_TURN, true);
                        mNavigationView.setSelectedItemId(R.id.action_search);
                      //  mIsCurrentUsersTurn = true;
                        showSearchDialog();
                    }
                }
            });
        }
    }


    private void setupYoutubeFragment() {
        YouTubePlayerSupportFragment youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
        youTubePlayerFragment.initialize("DEVELOPER_KEY", new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
                                                boolean wasRestored) {
                if (!wasRestored) {
                    mPlayer = player;
                    mPlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider arg0, YouTubeInitializationResult arg1) {

            }
        });

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.container_player, youTubePlayerFragment).commit();
    }


    @Override
    protected void onDestroy() {
//      if (mReceiver != null){
//         //   unregisterReceiver(mReceiver);
//            LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
//        }
      //  FirebaseMessaging.getInstance().unsubscribeFromTopic("room"+mCurrentRoom.getId());
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mReceiver = new NotificationReceiver();
        ((NotificationReceiver) mReceiver).setmNotificationListener(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_USER_TURN);
        intentFilter.addAction(Constants.ACTION_ROOM_INFO);
        intentFilter.addAction(Constants.ACTION_MESSAGE);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, intentFilter);
    }

    private void setupViewModel() {
        mListenViewModel = ViewModelProviders.of(this)
                .get(ListenViewModel.class);
        ((App) getApplication()).getNetComponent().injectRoomDetails(this);//dagger
        mListenViewModel.setRetrofit(mRetrofit);

        mRoomViewModel = ViewModelProviders.of(this)
                .get(RoomViewModel.class);
        mRoomViewModel.setRetrofit(mRetrofit);

    }

    private void findViews() {
        mNavigationView = findViewById(R.id.bottom_navigation);
        mSearchImageButton = findViewById(R.id.btn_search_video);
        mRoomTitleTextView = findViewById(R.id.text_room_title);
        mCurrentSongTextView = findViewById(R.id.text_current_song);
    }

    public void createOrShowFragment(Fragment fragment, String tag) {
        synchronized(this) {
            if (mFragments.indexOf(tag) >= 0) {
                String s = mFragments.get(mCurrentFragmentIndex);
                mFragments.set(mFragments.size() - 1, tag);
                mFragments.set(mCurrentFragmentIndex, s);
                mCurrentFragmentIndex = mFragments.size() - 1;
                bringFragmentToTop(tag);
                return;
            }
            mFragments.add(tag);
            mCurrentFragmentIndex = mFragments.size() - 1;
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction();
            transaction.add(R.id.container, fragment, tag);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.setCustomAnimations(android.R.anim.fade_in,
                    android.R.anim.fade_out,
                    android.R.anim.fade_in,
                    android.R.anim.fade_out);
            transaction.commit();
            getSupportFragmentManager().executePendingTransactions();
            bringFragmentToTop(tag);
        }
    }

    public void removeFragment(String tag) {
        synchronized(this) {
            if (!mFragments.contains(tag)) {
                return;
            }
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
            if (fragment != null) {
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                getSupportFragmentManager().executePendingTransactions();
            }
            mFragments.remove(mFragments.indexOf(tag));
            if (mFragments.size() > 0) {
                String s = mFragments.get(mFragments.size() - 1);
                mCurrentFragmentIndex = mFragments.size() - 1;
                bringFragmentToTop(s);
            }
        }
    }
    private void bringFragmentToTop(String tag) {
        mCurrentTagDisplayed = tag;
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        for(int i = 0; i < mFragments.size(); i++) {
            Fragment fragment = fragmentManager.findFragmentByTag(mFragments.get(i));
            if(mFragments.get(i).compareTo(tag) == 0){
                fragmentTransaction.show(fragment);
            } else {
                fragmentTransaction.hide(fragment);
            }
        }
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out,
                android.R.anim.fade_in,
                android.R.anim.fade_out);
        fragmentTransaction.commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_info:
               // mNavigationView.setOnNavigationItemSelectedListener(this);
               // mSearchResultsFragment = SearchResultsFragment.newInstance();
               // mRoomInfoFragment = RoomInfoFragment.newInstance(mCurrentRoom);
               // removeFragment(TAG_ROOM_INFO_FRAGMENT);
                createOrShowFragment(mRoomInfoFragment, TAG_ROOM_INFO_FRAGMENT);
                return true;
            case R.id.action_search:
                createOrShowFragment(mSearchResultsFragment, TAG_SEARCH_RESULTS_FRAGMENT);
                return true;
            case R.id.action_chat:
                ChatFragment chatFragment = ChatFragment.newInstance(mCurrentRoom.getId(), mCurrentUserId);
                createOrShowFragment(chatFragment, TAG_CHAT_FRAGMENT);
                return true;
            case R.id.action_leave:
                //fixme show dialog
                ListenEntity listenEntity = new ListenEntity();
                RoomEntity roomEntity = new RoomEntity();
                roomEntity.setId(mCurrentRoom.getId());
                listenEntity.setRoom(roomEntity);
                UserEntity userEntity = new UserEntity();
                userEntity.setId(mCurrentUserId);
                listenEntity.setUser(userEntity);
                Log.wtf("deleting", "room "+mCurrentRoom.getId()+", user "+mCurrentUserId);
                mListenViewModel.deleteListen(RoomDetailActivity.this, listenEntity);
                Toast.makeText(this,R.string.msg_left, Toast.LENGTH_SHORT).show();
                finish();
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (mFragments.size() > 1){
            removeFragment(mCurrentTagDisplayed);
            switch (mCurrentTagDisplayed){
                case TAG_SEARCH_RESULTS_FRAGMENT:
                    mNavigationView.setOnNavigationItemSelectedListener(null);
                    mNavigationView.setSelectedItemId(R.id.action_search);
                    mNavigationView.setOnNavigationItemSelectedListener(this);
                    break;
                case TAG_ROOM_INFO_FRAGMENT:
                    mNavigationView.setOnNavigationItemSelectedListener(null);
                    mNavigationView.setSelectedItemId(R.id.action_info);
                    mNavigationView.setOnNavigationItemSelectedListener(this);
                    break;
                case TAG_CHAT_FRAGMENT:
                    mNavigationView.setOnNavigationItemSelectedListener(null);
                    mNavigationView.setSelectedItemId(R.id.action_chat);
                    mNavigationView.setOnNavigationItemSelectedListener(this);
                    break;
            }
        }else {
            super.onBackPressed();
        }
    }

    private void showSearchDialog(){
        if (mVideoSearchDialog == null){
            mVideoSearchDialog = new SearchDialog();
        }
        mVideoSearchDialog.showDialog(RoomDetailActivity.this, this, getString(R.string.hint_search_track));
    }

    @Override
    public void onSearch(String query) {
        createOrShowFragment(mSearchResultsFragment, TAG_SEARCH_RESULTS_FRAGMENT);
        YoutubeAsyncTask asyncTask = new YoutubeAsyncTask(mSearchResultsFragment);
        asyncTask.execute(query);
        mVideoSearchDialog.dismiss();
    }

    @Override
    public void onRoomVideoSelection(RoomEntity roomEntity, UserEntity userEntity, String videoId) {
        Log.wtf("received","details");
        onRoomInfoRefreshed(roomEntity);
        Long roomId = roomEntity != null ? roomEntity.getId() : 0;
        if (mCurrentRoom.getId() == roomId){
            //todo update room info [get request] & update info in ui
            if (roomEntity != null){
                mCurrentSongTextView.setText(roomEntity.getLastSong());
            }
            if (userEntity.getId() == mCurrentUserId){
                mIsCurrentUsersTurn = true;
                mSharedPrefUtils.saveBoolean(SharedPrefUtils.KEY_CURRENT_USER_TURN, true);
            }
            if (mPlayer != null &&  videoId != null){
                mPlayer.loadVideo(videoId);
                mPlayer.play();
                mPlayer.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                    @Override
                    public void onLoading() {

                    }

                    @Override
                    public void onLoaded(String s) {

                    }

                    @Override
                    public void onAdStarted() {

                    }

                    @Override
                    public void onVideoStarted() {

                    }

                    @Override
                    public void onVideoEnded() {
                        if (userEntity.getId() == mCurrentUserId){
                            showSearchDialog();
                        }
                    }

                    @Override
                    public void onError(YouTubePlayer.ErrorReason errorReason) {

                    }
                });
            }
        }

       // mRoomViewModel.loadRoom(this, mCurrentRoom.getId());

    }

    public void onSearchButtonClick(View view) {
        if (mIsCurrentUsersTurn){
            showSearchDialog();
        }else{
            Toast.makeText(this, R.string.msg_not_turn, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoSearchDialog != null){
            mVideoSearchDialog.dismiss();
        }
        if (mReceiver != null){
            //   unregisterReceiver(mReceiver);
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        }
    }

    @Override
    public void onVideoSelected(VideoItem videoItem) {
        mCurrentSongTextView.setText(videoItem.getTitle());
        if (mPlayer != null){
         //   mPlayer.cueVideo(videoItem.getVideoId());
         //   mPlayer.play();
            mSharedPrefUtils.saveBoolean(SharedPrefUtils.KEY_CURRENT_USER_TURN, false);
            mRoomViewModel.updateVideo(mCurrentRoom.getId(), videoItem.getVideoId(),
                    videoItem.getTitle());

        }
    }

    @Override
    public void onRoomInfoRefreshed(RoomEntity roomEntity) {
        boolean isNewUser = true;
        if (mRoomInfoFragment != null){
            if (mRoomInfoFragment.getmRoom() != null &&
                    roomEntity.getMembersCount() == mRoomInfoFragment.getmRoom().getMembersCount()){
                isNewUser = false;
            }
        }

        if (isNewUser){

        }

        mCurrentRoom = roomEntity;
        mRoomInfoFragment = RoomInfoFragment.newInstance(mCurrentRoom);
        removeFragment(TAG_ROOM_INFO_FRAGMENT);
        createOrShowFragment(mRoomInfoFragment, TAG_ROOM_INFO_FRAGMENT);
    }

   /* @Override
    public void onSearchCompleted(List<VideoItem> videoItems) {

    }*/

    @Override
    public void onMessageReceived() {
        if (mFragments.get(mCurrentFragmentIndex).equals(TAG_CHAT_FRAGMENT)){
            ChatFragment chatFragment = (ChatFragment)getSupportFragmentManager().findFragmentByTag(TAG_CHAT_FRAGMENT);
            chatFragment.checkoutMessages();
        }else{
            //eventually open chat fragment when a new message is received
        }
    }
}
