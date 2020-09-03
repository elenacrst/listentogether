package com.elena.listentogether.di.component;

import com.elena.listentogether.di.module.AppModule;
import com.elena.listentogether.di.module.NetModule;
import com.elena.listentogether.ui.activity.MainActivity;
import com.elena.listentogether.ui.activity.ProfileActivity;
import com.elena.listentogether.ui.activity.RegisterActivity;
import com.elena.listentogether.ui.activity.RoomDetailActivity;
import com.elena.listentogether.ui.activity.RoomsActivity;
import com.elena.listentogether.ui.fragment.ChatFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, NetModule.class})
public interface NetComponent {
        void injectMain(MainActivity activity);
        void injectRooms(RoomsActivity roomsActivity);
        void injectRoomDetails(RoomDetailActivity roomDetailActivity);
        void injectChatFragment(ChatFragment chatFragment);
        void injectProfile(ProfileActivity profileActivity);
        void injectRegister(RegisterActivity registerActivity);
}
