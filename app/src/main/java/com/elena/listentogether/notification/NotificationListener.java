package com.elena.listentogether.notification;

import com.elena.listentogether.model.local.entity.RoomEntity;
import com.elena.listentogether.model.local.entity.UserEntity;

public interface NotificationListener {
    void onRoomVideoSelection(RoomEntity roomEntity, UserEntity userEntity, String videoId);
    void onRoomInfoRefreshed(RoomEntity roomEntity);
    void onMessageReceived();
           //MessageEntity messageEntity);
}
