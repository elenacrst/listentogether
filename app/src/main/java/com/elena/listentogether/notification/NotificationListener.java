package com.elena.listentogether.notification;

import android.content.Intent;

import com.elena.listentogether.data.local.entity.MessageEntity;
import com.elena.listentogether.data.local.entity.RoomEntity;
import com.elena.listentogether.data.local.entity.UserEntity;

public interface NotificationListener {
    void onRoomVideoSelection(RoomEntity roomEntity, UserEntity userEntity, String videoId);
    void onRoomInfoRefreshed(RoomEntity roomEntity);
    void onMessageReceived();
           //MessageEntity messageEntity);
}
