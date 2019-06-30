package com.elena.listentogether.ui.activity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.elena.listentogether.R;
import com.elena.listentogether.reminder.AlarmReceiver;
import com.elena.listentogether.reminder.AlarmUtils;
import com.elena.listentogether.ui.custom.expandablelayout.ExpandableLinearLayout;
import com.elena.listentogether.utils.Constants;
import com.elena.listentogether.utils.SharedPrefUtils;

import java.util.ArrayList;
import java.util.List;

import static android.app.AlarmManager.INTERVAL_DAY;

public class SettingsActivity extends AppCompatActivity {

    private ExpandableLinearLayout mExpandableContainer;
    private RelativeLayout mNotificationsGroupContainer;
    private TextView mNotificationsTextView;
    private ImageView mNotificationImageView;
    private LinearLayout mReminderContainer;
    private SwitchCompat mMessagingSwitch, mMemberSwitch;
    private TextView mReminderValueTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        findViews();
        mExpandableContainer.setExpandable(true);
        mExpandableContainer.setOnExpandListener(new ExpandableLinearLayout.OnExpandListener() {
            @Override
            public void onExpandableStateChanged(boolean isExpanded) {
                mNotificationsGroupContainer.setBackgroundColor(isExpanded ? getResources().getColor(R.color.blueIndicator) : getResources().getColor(android.R.color.white));
                mNotificationsTextView.setTextColor(isExpanded ? getResources().getColor(android.R.color.white) : getResources().getColor(android.R.color.black));
                if (isExpanded){
                    mNotificationImageView.setColorFilter(Color.argb(255,255,255,255));//white
                }else{
                    mNotificationImageView.setColorFilter(getResources().getColor(R.color.blueIndicator));
                }

            }
        });

        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle(R.string.title_settings);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blueIndicator)));
        }

        mReminderContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showReminderDialog();
            }
        });

        SharedPrefUtils sharedPrefUtils = new SharedPrefUtils(this);

        mMessagingSwitch.setChecked(sharedPrefUtils.getBoolean(SharedPrefUtils.KEY_NOTIFICATION_MESSAGING, true));
        mMemberSwitch.setChecked(sharedPrefUtils.getBoolean(SharedPrefUtils.KEY_NOTIFICATION_MEMBERS, true));
        switch (sharedPrefUtils.getString(SharedPrefUtils.KEY_REMINDER, Constants.SETTING_REMINDER_DAILY)){
            case Constants.SETTING_REMINDER_DAILY:
                mReminderValueTextView.setText(R.string.btn_daily);
                sharedPrefUtils.saveString(SharedPrefUtils.KEY_REMINDER, Constants.SETTING_REMINDER_DAILY);
                AlarmUtils.setReminder(this, SettingsActivity.class, 12, 0, INTERVAL_DAY);
                break;
            case Constants.SETTING_REMINDER_WEEKLY:
                mReminderValueTextView.setText(R.string.btn_weekly);
                sharedPrefUtils.saveString(SharedPrefUtils.KEY_REMINDER, Constants.SETTING_REMINDER_WEEKLY);
                AlarmUtils.setReminder(this, SettingsActivity.class, 12, 0, INTERVAL_DAY * 7);
                break;
            case Constants.SETTING_REMINDER_NEVER:
                mReminderValueTextView.setText(R.string.btn_never);
                sharedPrefUtils.saveString(SharedPrefUtils.KEY_REMINDER, Constants.SETTING_REMINDER_NEVER);
                AlarmUtils.cancelReminder(this,AlarmReceiver.class);
                break;
        }

        mMessagingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                sharedPrefUtils.saveBoolean(SharedPrefUtils.KEY_NOTIFICATION_MESSAGING, b);
            }
        });
        mMemberSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                sharedPrefUtils.saveBoolean(SharedPrefUtils.KEY_NOTIFICATION_MEMBERS, b);
            }
        });

    }

    private void showReminderDialog() {
        SharedPrefUtils sharedPrefUtils = new SharedPrefUtils(this);

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_reminder);

        RadioGroup rg =  dialog.findViewById(R.id.radio_group);
        dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        RadioButton dailyBtn, weeklyBtn, neverBtn;
        dailyBtn = dialog.findViewById(R.id.btn_daily);
        weeklyBtn = dialog.findViewById(R.id.btn_weekly);
        neverBtn = dialog.findViewById(R.id.btn_never);

        String savedReminder = sharedPrefUtils.getString(SharedPrefUtils.KEY_REMINDER, Constants.SETTING_REMINDER_DAILY);
        switch (savedReminder){
            case Constants.SETTING_REMINDER_DAILY:
                dailyBtn.setChecked(true);
                break;
            case Constants.SETTING_REMINDER_NEVER:
                neverBtn.setChecked(true);
                break;
            case Constants.SETTING_REMINDER_WEEKLY:
                weeklyBtn.setChecked(true);
                break;
        }
        dialog.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (rg.getCheckedRadioButtonId()){
                    case R.id.btn_daily:
                        sharedPrefUtils.saveString(SharedPrefUtils.KEY_REMINDER, Constants.SETTING_REMINDER_DAILY);
                        mReminderValueTextView.setText(R.string.btn_daily);
                        break;
                    case R.id.btn_weekly:
                        sharedPrefUtils.saveString(SharedPrefUtils.KEY_REMINDER,Constants.SETTING_REMINDER_WEEKLY);
                        mReminderValueTextView.setText(R.string.btn_weekly);
                        break;
                    case R.id.btn_never:
                        sharedPrefUtils.saveString(SharedPrefUtils.KEY_REMINDER,Constants.SETTING_REMINDER_NEVER);
                        mReminderValueTextView.setText(R.string.btn_never);
                        break;
                }

                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void findViews() {
        mExpandableContainer = findViewById(R.id.container_expand);
        mNotificationsGroupContainer = findViewById(R.id.container_notifications_group);
        mNotificationImageView = findViewById(R.id.image_notification);
        mNotificationsTextView = findViewById(R.id.text_notifications);
        mReminderContainer = findViewById(R.id.container_reminder);
        mMessagingSwitch = findViewById(R.id.switch_messages);
        mMemberSwitch = findViewById(R.id.switch_members);
        mReminderValueTextView = findViewById(R.id.text_subtitle_reminder);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
