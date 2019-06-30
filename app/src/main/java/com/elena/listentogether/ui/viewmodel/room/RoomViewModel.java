package com.elena.listentogether.ui.viewmodel.room;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.elena.listentogether.data.local.entity.RoomEntity;
import com.elena.listentogether.data.local.entity.UserEntity;
import com.elena.listentogether.ui.viewmodel.user.IUserImpl;

import java.util.List;

import retrofit2.Retrofit;

public class RoomViewModel extends ViewModel {
    //private MediatorLiveData<UserEntity> mUserData;
    private IRoom mIRoom;
    private MediatorLiveData<List<RoomEntity>> mRooms;
    private MediatorLiveData<RoomEntity> mCurrentRoom;
    private MediatorLiveData<List<RoomEntity>> mUserRooms;

    // No argument constructor
    public RoomViewModel() {//passed from activity where it's injected
      //  mUserData = new MediatorLiveData<>();
        mIRoom = new IRoomImpl();
        mRooms = new MediatorLiveData<>();
        mCurrentRoom = new MediatorLiveData<>();
        mUserRooms = new MediatorLiveData<>();
    }

    public void setRetrofit(Retrofit retrofit){
        mIRoom = new IRoomImpl(retrofit);
    }

    public void insertRoom(final Context context, RoomEntity roomEntity) {
        mIRoom.insertRoom(context, roomEntity);
    }

    public void loadRooms(final Context context) {
        mRooms.addSource(
                mIRoom.getRooms(context), new Observer<List<RoomEntity>>() {
                    @Override
                    public void onChanged(@Nullable List<RoomEntity> data) {
                        /*
                        if (userEntity == null){
                            Log.wtf("user","null");
                            handleError();
                        }else {*/
                        mRooms.setValue(data);
                        //  handleResponse(data, context, new MainActivity());
                        // }

                    }
                }
        );
    }

    public MediatorLiveData<List<RoomEntity>> getmRooms() {
        return mRooms;
    }

    public void updateVideo(Long roomId, String videoId, String videoTitle) {
        mIRoom.updateVideo(roomId, videoId, videoTitle);
    }

    public void loadRoom(final Context context, Long roomId) {
        mCurrentRoom.addSource(
                mIRoom.findRoom(context, roomId), new Observer<RoomEntity>() {
                    @Override
                    public void onChanged(@Nullable RoomEntity data) {
                        /*
                        if (userEntity == null){
                            Log.wtf("user","null");
                            handleError();
                        }else {*/
                        mCurrentRoom.setValue(data);
                        //  handleResponse(data, context, new MainActivity());
                        // }

                    }
                }
        );
    }

    public MediatorLiveData<RoomEntity> getmCurrentRoom() {
        return mCurrentRoom;
    }

    public void loadUserRooms(final Context context, Long userId) {
        mRooms.addSource(
                mIRoom.findUserRooms(context, userId), new Observer<List<RoomEntity>>() {
                    @Override
                    public void onChanged(@Nullable List<RoomEntity> data) {
                        /*
                        if (userEntity == null){
                            Log.wtf("user","null");
                            handleError();
                        }else {*/
                        mUserRooms.setValue(data);
                        //  handleResponse(data, context, new MainActivity());
                        // }
                    }
                }
        );
    }

    public MediatorLiveData<List<RoomEntity>> getmUserRooms() {
        return mUserRooms;
    }
}
