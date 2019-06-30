package com.elena.listentogether.reminder;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.elena.listentogether.R;
import com.elena.listentogether.notification.NotificationsService;
import com.elena.listentogether.ui.activity.MainActivity;
import com.elena.listentogether.utils.Constants;
import com.elena.listentogether.utils.SharedPrefUtils;

import static android.app.AlarmManager.INTERVAL_DAY;

public class AlarmReceiver extends BroadcastReceiver {

    String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        //Trigger the notification
       /* NotificationScheduler.showNotification(context, MainActivity.class,
                "You have 5 unwatched videos", "Watch them now?");*/
        if (intent.getAction() != null && context != null) {
            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
                // Set the alarm here.
                Log.d(TAG, "onReceive: BOOT_COMPLETED");
                //LocalData localData = new LocalData(context);
                /*NotificationScheduler.setReminder(context, AlarmReceiver.class,
                        localData.get_hour(), localData.get_min());*/
                long interval;// = INTERVAL_DAY;
                SharedPrefUtils sharedPrefUtils = new SharedPrefUtils(context);
                switch (sharedPrefUtils.getString(SharedPrefUtils.KEY_REMINDER, Constants.SETTING_REMINDER_DAILY)){
                    case Constants.SETTING_REMINDER_DAILY:
                        interval = INTERVAL_DAY;
                        AlarmUtils.setReminder(context, AlarmReceiver.class, 12, 0, interval);
                        break;
                    case Constants.SETTING_REMINDER_WEEKLY:
                        interval = INTERVAL_DAY * 7;
                        AlarmUtils.setReminder(context, AlarmReceiver.class, 12, 0, interval);
                        break;
                    default:
                        break;
                }

                return;
            }
        }
        NotificationsService.sendNotification(context.getString(R.string.title_reminder_notification),
                context.getString(R.string.msg_reminder_notification),
                context);
    }
}