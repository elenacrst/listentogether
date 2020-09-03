package com.elena.listentogether.ui.viewmodel.user;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.elena.listentogether.model.local.entity.UserEntity;

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

    public void loadUserRoomsCount(final Context context, Long userId) {
        mUserRoomsCount.addSource(
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

    public void insertUser(Context context,UserEntity userEntity){
        mUserData.addSource(mIUser.insertUser(context, userEntity), new Observer<UserEntity>() {
            @Override
            public void onChanged(@Nullable UserEntity userEntity) {
                mUserData.setValue(userEntity);
            }
        });
    }
}
