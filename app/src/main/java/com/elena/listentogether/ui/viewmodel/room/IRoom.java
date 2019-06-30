package com.elena.listentogether.ui.viewmodel.room;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;

import com.elena.listentogether.data.local.entity.RoomEntity;
import com.elena.listentogether.data.local.entity.UserEntity;

import java.util.List;

public interface IRoom {
    void insertRoom(Context context, RoomEntity roomEntity);
    MutableLiveData<List<RoomEntity>>  getRooms(Context context);
    void updateVideo(Long roomId, String videoId, String videoTitle);

    MutableLiveData<RoomEntity> findRoom(Context context, Long id);
    MutableLiveData<List<RoomEntity>> findUserRooms(Context context, Long userId);
}
