package com.elena.listentogether.ui.viewmodel.room;

import androidx.lifecycle.MutableLiveData;
import android.content.Context;

import com.elena.listentogether.model.local.entity.RoomEntity;

import java.util.List;

public interface IRoom {
    void insertRoom(Context context, RoomEntity roomEntity);
    MutableLiveData<List<RoomEntity>>  getRooms(Context context);
    void updateVideo(Long roomId, String videoId, String videoTitle);

    MutableLiveData<RoomEntity> findRoom(Context context, Long id);
    MutableLiveData<List<RoomEntity>> findUserRooms(Context context, Long userId);
}
