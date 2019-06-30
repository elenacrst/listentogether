package com.elena.listentogether.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.elena.listentogether.data.local.entity.RoomEntity;
import com.elena.listentogether.data.local.entity.UserEntity;
import com.elena.listentogether.ui.activity.RoomDetailActivity;
import com.elena.listentogether.utils.Constants;

public class NotificationReceiver  extends BroadcastReceiver {

    private NotificationListener mNotificationListener;

    public NotificationReceiver(){
    }

    /*public NotificationReceiver(NotificationListener listener) {
        mNotificationListener = listener;
    }*/

    public void setmNotificationListener(NotificationListener mNotificationListener) {
        this.mNotificationListener = mNotificationListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.wtf("received","receiver, intent : "+intent.getAction());
      //  Log.d(TAG, "onReceive() called with: context = [" + context + "], intent = [" + intent + "]");
       /* if (intent.getAction() != null){
            switch (intent.getAction()){
                case NotificationsService.ACTION_USER_TURN:
                    //here is my beacon impl
                    break;

                default:
                    break;

            }
        }*/
       if (mNotificationListener != null){
           switch (intent.getAction()){
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
                       //    intent.getParcelableExtra(NotificationsService.EXTRA_MESSAGE));
                   break;
           }

       }
    }


}