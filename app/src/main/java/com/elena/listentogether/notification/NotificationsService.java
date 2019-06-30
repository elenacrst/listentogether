package com.elena.listentogether.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.elena.listentogether.R;
import com.elena.listentogether.data.local.entity.MessageEntity;
import com.elena.listentogether.data.local.entity.RoomEntity;
import com.elena.listentogether.data.local.entity.UserEntity;
import com.elena.listentogether.ui.activity.MainActivity;
import com.elena.listentogether.ui.activity.RoomDetailActivity;
import com.elena.listentogether.utils.Constants;
import com.elena.listentogether.utils.SharedPrefUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

public class NotificationsService extends FirebaseMessagingService {

 //   public static final String ACTION_USER_JOINED = "ACTION_USER_JOINED";
  //  public static final String ACTION_USER_TURN = "ACTION_USER_TURN";
    public static final String EXTRA_USER = "USER";
    public static final String EXTRA_MESSAGE = "MESSAGE" ;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getData().size() > 0) {
            Log.d("notif", "Message data payload: " + remoteMessage.getData());
            String title = remoteMessage.getData().get("title");
            String detail = remoteMessage.getData().get("detail");
            //String notificationType = remoteMessage.getData().get("notificationType");
            String video = "";
            try{
                video = remoteMessage.getData().get("videoId");
            }catch (Exception e){
                e.printStackTrace();
            }

            JsonParser parser = new JsonParser();
            JsonElement mJson =  parser.parse(remoteMessage.getData().get("room"));
            Gson gson = new Gson();
            RoomEntity room = gson.fromJson(mJson, RoomEntity.class);



            String action = remoteMessage.getData().get("action");
            Intent intent;
            switch (action){
                case Constants.ACTION_ROOM_INFO://<=> new member
                    JsonElement userJsonData =  parser.parse(remoteMessage.getData().get("user"));
                    UserEntity user = gson.fromJson(userJsonData, UserEntity.class);

                    intent = new Intent(this, RoomDetailActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.setAction(Constants.ACTION_ROOM_INFO);
                   // intent.putExtra(EXTRA_USER, userEntity);
                    intent.putExtra(RoomDetailActivity.EXTRA_ROOM, room);
                  //  intent.putExtra("video", video);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

                    SharedPrefUtils sharedPrefUtils = new SharedPrefUtils(this);

                    if (!sharedPrefUtils.getString(SharedPrefUtils.KEY_PROFILE_USERNAME, user.getUsername()).equals(user.getUsername())
                            && sharedPrefUtils.getBoolean(SharedPrefUtils.KEY_NOTIFICATION_MEMBERS, false)){
                        sendNotification(getString(R.string.title_new_member),
                                getString(R.string.body_new_member, user.getUsername(), room.getName()),
                                this
                                );
                    }

                    break;
                case Constants.ACTION_USER_TURN:
                    JsonElement userJson =  parser.parse(remoteMessage.getData().get("user"));
                    UserEntity userEntity = gson.fromJson(userJson, UserEntity.class);
                  //  sendNotification(title, detail, video, room, userEntity);
                    intent = new Intent(this, RoomDetailActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.setAction(Constants.ACTION_USER_TURN);
                    intent.putExtra(EXTRA_USER, userEntity);
                    intent.putExtra(RoomDetailActivity.EXTRA_ROOM, room);
                    intent.putExtra("video", video);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                    break;
                case Constants.ACTION_MESSAGE:
                    JsonElement messageJson =  parser.parse(remoteMessage.getData().get("message"));
                    MessageEntity messageEntity = gson.fromJson(messageJson, MessageEntity.class);
                    //todo send notif with message
                    intent = new Intent(this, RoomDetailActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.setAction(Constants.ACTION_MESSAGE);
                 //   intent.putExtra(EXTRA_USER, userEntity);
                    intent.putExtra(RoomDetailActivity.EXTRA_ROOM, room);
                   // intent.putExtra("video", video);

                    sendNotification(getString(R.string.title_new_message), getString(R.string.msg_message,
                            messageEntity.getUser().getUsername(), room.getName()), this);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                    break;
            }
        }
    }

    public static void sendNotification(String title, String detail, Context context) {
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.user_icon);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                context, SharedPrefUtils.KEY_NOTIFICATION_MEMBERS)
                .setContentTitle(title)
                .setContentText(detail)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentInfo(title)
                .setLargeIcon(icon)
                .setColor(Color.RED)
                .setLights(Color.RED, 1000, 300)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setSmallIcon(R.drawable.user_icon);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Notification Channel is required for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    SharedPrefUtils.KEY_NOTIFICATION_MEMBERS,  SharedPrefUtils.KEY_NOTIFICATION_MEMBERS, NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription(context.getString(R.string.label_new_member));
            channel.setShowBadge(true);
            channel.canShowBadge();
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500});
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }
}
