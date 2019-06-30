package com.elena.listentogether.ui.viewmodel.user;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.elena.listentogether.data.local.entity.UserEntity;
import com.elena.listentogether.ui.activity.MainActivity;

import javax.inject.Inject;

import retrofit2.Retrofit;

public class UserViewModel extends ViewModel {
    private MediatorLiveData<UserEntity> mUserData;
    private IUser mIUser;
    private MediatorLiveData<Integer> mUserRoomsCount;
    private MediatorLiveData<String> mUpdateResult;

    // No argument constructor
    public UserViewModel() {//passed from activity where it's injected
        mUserData = new MediatorLiveData<>();
        mIUser = new IUserImpl();
        mUserRoomsCount = new MediatorLiveData<>();
        mUpdateResult = new MediatorLiveData<>();
    }

    public void setRetrofit(Retrofit retrofit){
        mIUser = new IUserImpl(retrofit);
    }

    @NonNull
    public LiveData<UserEntity> getUserData() {
        return mUserData;
    }

    public void loadUser(final Context context, UserEntity userEntity) {
        mUserData.addSource(
                mIUser.findUser(context, userEntity), new Observer<UserEntity>() {
                    @Override
                    public void onChanged(@Nullable UserEntity data) {
                        /*
                        if (userEntity == null){
                            Log.wtf("user","null");
                            handleError();
                        }else {*/
                            mUserData.setValue(data);
                          //  handleResponse(data, context, new MainActivity());
                       // }

                    }
                }
        );
    }

    /*static void handleResponse(final UserEntity data, final Context context, Activity activity) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.wtf("user result","user "+ data.getEmail());
              //  MainActivity.setupData(data);
            }
        });

    }*/

    /*private static void handleError() {
        Log.wtf("user result","error");
    }

    public IUser getmIUser() {
        return mIUser;
    }*/

    public void loadUserRoomsCount(final Context context, Long userId) {
        mUserData.addSource(
                mIUser.getUserRoomsCount(context, userId), new Observer<Integer>() {
                    @Override
                    public void onChanged(@Nullable Integer data) {
                        /*
                        if (userEntity == null){
                            Log.wtf("user","null");
                            handleError();
                        }else {*/
                        mUserRoomsCount.setValue(data);
                        //  handleResponse(data, context, new MainActivity());
                        // }

                    }
                }
        );
    }

    public MediatorLiveData<Integer> getmUserRoomsCount() {
        return mUserRoomsCount;
    }

    public void updateAvatar(Context context, Long userId, String avatar){
        mUpdateResult.addSource(mIUser.updateAvatar(context, userId, avatar), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                mUpdateResult.setValue(s);
            }
        });

    }

    public void updateCountry(Context context, Long userId, String country){
        mIUser.updateCountry(context, userId, country);
    }

    public void updateCity(Context context, Long userId, String city){
        mIUser.updateCity(context, userId, city);
    }

    public void updatePhone(Context context, Long userId, String phone){
        mIUser.updatePhone(context, userId, phone);
    }

    public MediatorLiveData<String> getmUpdateResult() {
        return mUpdateResult;
    }
}
