package com.elena.listentogether.di.component;

import com.elena.listentogether.di.module.AppModule;
import com.elena.listentogether.di.module.NetModule;
import com.elena.listentogether.ui.activity.MainActivity;
import com.elena.listentogether.ui.activity.ProfileActivity;
import com.elena.listentogether.ui.activity.RoomDetailActivity;
import com.elena.listentogether.ui.activity.RoomsActivity;
import com.elena.listentogether.ui.fragment.ChatFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton //a single instance is needed from this class
@Component(modules = {AppModule.class, NetModule.class})//specify all module classes of this project
public interface NetComponent {
        void injectMain(MainActivity activity);//pass all activities/fragments that are using this
        // module[using retrofit]
    // send retrofit as parameter for mvvm int impl classes
        void injectRooms(RoomsActivity roomsActivity);
        void injectRoomDetails(RoomDetailActivity roomDetailActivity);
        void injectChatFragment(ChatFragment chatFragment);
        void injectProfile(ProfileActivity profileActivity);
}
