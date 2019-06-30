package com.elena.listentogether.ui.viewmodel.user;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;

import com.elena.listentogether.data.local.entity.UserEntity;

public interface IUser {
    MutableLiveData<UserEntity> findUser(Context context, UserEntity userEntity);
    MutableLiveData<Integer> getUserRoomsCount(Context context, Long userId);
    MutableLiveData<String> updateAvatar(Context context, Long userId, String avatar);
    void updateCity(Context context, Long userId, String city);
    void updateCountry(Context context, Long userId, String country);
    void updatePhone(Context context, Long userId, String phone);
}
