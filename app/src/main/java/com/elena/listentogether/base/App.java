package com.elena.listentogether.base;

import android.app.Application;

import com.elena.listentogether.di.component.DaggerNetComponent;
import com.elena.listentogether.di.component.NetComponent;
import com.elena.listentogether.di.module.AppModule;
import com.elena.listentogether.di.module.NetModule;
import com.google.firebase.messaging.FirebaseMessaging;

public class App extends Application {//pass it in the manifest android:name=".App"
    //it's used for initialization of global state before the first activity is displayed

    private NetComponent mNetComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        //initialization logic here
        mNetComponent = DaggerNetComponent.builder()//if it cant be resolved, try build project. the project needs to be compiled so that this file would be generated
                .appModule(new AppModule(this))//method not resolved if it's not defined as module in netcomponent
                .netModule(new NetModule())
                .build();

        //FirebaseMessaging.getInstance().subscribeToTopic("news");
    }

    public NetComponent getNetComponent() {
        return mNetComponent;
    }
}