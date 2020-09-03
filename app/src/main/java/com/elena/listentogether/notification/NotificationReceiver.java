package com.elena.listentogether.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.elena.listentogether.ui.activity.RoomDetailActivity;
import com.elena.listentogether.utils.Constants;

import java.util.Objects;

public class NotificationReceiver  extends BroadcastReceiver {
    private NotificationListener mNotificationListener;

    public NotificationReceiver(){ }

    public void setmNotificationListener(NotificationListener mNotificationListener) {
        this.mNotificationListener = mNotificationListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
       if (mNotificationListener != null){
           switch (Objects.requireNonNull(intent.getAction())){
               case Constants.ACTION_USER_TURN:
                   mNotificationListener.onRoomVideoSelection(intent.getParcelableExtra(RoomDetailActivity.EXTRA_ROOM),
                           intent.getParcelableExtra(NotificationsService.EXTRA_USER),
                           intent.getStringExtra("video"));
                   break;
               case Constants.ACTION_ROOM_INFO:
                   mNotificationListener.onRoomInfoRefreshed(intent.getParcelableExtra(RoomDetailActivity.EXTRA_ROOM));
                   break;
               case Constants.ACTION_MESSAGE:
                   mNotificationListener.onMessageReceived();
                   break;
           }
       }
    }
}