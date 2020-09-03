package com.elena.listentogether.ui.viewmodel.user;

import androidx.lifecycle.MutableLiveData;
import android.content.Context;
import androidx.annotation.NonNull;

import com.elena.listentogether.model.local.entity.UserEntity;
import com.elena.listentogether.model.remote.ApiCallInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class IUserImpl implements IUser {
    private ApiCallInterface mRetrofitService;

    public IUserImpl(){
    }

    public IUserImpl(Retrofit retrofit){
        mRetrofitService = retrofit.create(ApiCallInterface.class);//keep this
    }

    @Override
    public MutableLiveData<UserEntity> findUser(final Context context,
                                                UserEntity userEntity) {
        final MutableLiveData<UserEntity> liveData = new MutableLiveData<>();
        Call<UserEntity> call = mRetrofitService.findUser(userEntity.getUsername(), userEntity.getPassword());
        call.enqueue(new Callback<UserEntity>() {
            @Override
            public void onResponse(@NonNull Call<UserEntity> call,
                                   @NonNull Response<UserEntity> response) {
                if (response.body() != null){
                    liveData.setValue(response.body());
                  //  UserViewModel.handleResponse(response.body(), context, new MainActivity());
                    //fixme err message when there s no internet when logging in
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserEntity> call, @NonNull Throwable t) {

                t.printStackTrace();
            }
        });
        return liveData;
    }

    @Override
    public MutableLiveData<Integer> getUserRoomsCount(Context context, Long userId) {
        final MutableLiveData<Integer> liveData = new MutableLiveData<>();
        Call<Integer> call = mRetrofitService.getUserRoomsCount(userId);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(@NonNull Call<Integer> call,
                                   @NonNull Response<Integer> response) {
              //  if (response.body() != null){
                    liveData.setValue(response.body());
                    //  UserViewModel.handleResponse(response.body(), context, new MainActivity());
                    //fixme err message when there s no internet when logging in
               // }
            }

            @Override
            public void onFailure(@NonNull Call<Integer> call, @NonNull Throwable t) {

                t.printStackTrace();
            }
        });
        return liveData;
    }

    @Override
    public MutableLiveData<String> updateAvatar(Context context, Long userId, String avatar) {
        final MutableLiveData<String> result = new MutableLiveData<>();
        Call<String> call = mRetrofitService.updateUserAvatar(userId, avatar);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call,
                                   @NonNull Response<String> response) {
                if (response.body() != null){
                    //  UserViewModel.handleResponse(response.body(), context, new MainActivity());
                    //fixme err message when there s no internet
                    result.setValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {

                t.printStackTrace();
            }
        });
        return result;
    }

    @Override
    public void updateCity(Context context, Long userId, String city) {
        Call<Void> call = mRetrofitService.updateUserCity(userId, city);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call,
                                   @NonNull Response<Void> response) {
                if (response.body() != null){
                    //  UserViewModel.handleResponse(response.body(), context, new MainActivity());
                    //fixme err message when there s no internet
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {

                t.printStackTrace();
            }
        });
    }

    @Override
    public void updateCountry(Context context, Long userId, String country) {
        Call<Void> call = mRetrofitService.updateUserCountry(userId, country);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call,
                                   @NonNull Response<Void> response) {
                if (response.body() != null){
                    //  UserViewModel.handleResponse(response.body(), context, new MainActivity());
                    //fixme err message when there s no internet
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {

                t.printStackTrace();
            }
        });
    }

    @Override
    public void updatePhone(Context context, Long userId, String phone) {
        Call<Void> call = mRetrofitService.updateUserPhone(userId, phone);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call,
                                   @NonNull Response<Void> response) {
                if (response.body() != null){
                    //  UserViewModel.handleResponse(response.body(), context, new MainActivity());
                    //fixme err message when there s no internet
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {

                t.printStackTrace();
            }
        });
    }

    @Override
    public MutableLiveData<UserEntity> insertUser(Context context, UserEntity userEntity) {
        MutableLiveData<UserEntity> liveData = new MutableLiveData<>();
        Call<UserEntity> call = mRetrofitService.insertUser(userEntity);
        call.enqueue(new Callback<UserEntity>() {
            @Override
            public void onResponse(@NonNull Call<UserEntity> call,
                                   @NonNull Response<UserEntity> response) {
                if (response.body() != null){
                    liveData.setValue(response.body());
                  //  Toast.makeText(context, response.body(), Toast.LENGTH_SHORT).show();
                }else{
                    liveData.setValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserEntity> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
        return liveData;
    }
}