package com.elena.listentogether.ui.viewmodel.room;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.elena.listentogether.data.local.entity.RoomEntity;
import com.elena.listentogether.data.local.entity.UserEntity;
import com.elena.listentogether.data.remote.ApiCallInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class IRoomImpl implements IRoom {
    private ApiCallInterface mRetrofitService;

    public IRoomImpl(){
    }

    public IRoomImpl(Retrofit retrofit){
        mRetrofitService = retrofit.create(ApiCallInterface.class);//keep this
    }

    @Override
    public void insertRoom(Context context, RoomEntity roomEntity) {
        Call<String> call = mRetrofitService.insertRoom(roomEntity);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call,
                                   @NonNull Response<String> response) {
                if (response.body() != null){
                    Toast.makeText(context, response.body(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public MutableLiveData<List<RoomEntity>> getRooms(Context context) {
        final MutableLiveData<List<RoomEntity>> liveData = new MutableLiveData<>();
        Call<List<RoomEntity>> call = mRetrofitService.getRooms();
        call.enqueue(new Callback<List<RoomEntity>>() {
            @Override
            public void onResponse(@NonNull Call<List<RoomEntity>> call,
                                   @NonNull Response<List<RoomEntity>> response) {
                if (response.body() != null){
                    liveData.setValue(response.body());
                    //  UserViewModel.handleResponse(response.body(), context, new MainActivity());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<RoomEntity>> call, @NonNull Throwable t) {

                t.printStackTrace();
            }
        });
        return liveData;
    }

    @Override
    public void updateVideo(Long roomId, String videoId, String videoTitle) {
        Call<Void> call = mRetrofitService.updateVideo(roomId, videoId, videoTitle);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call,
                                   @NonNull Response<Void> response) {
            }
            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public MutableLiveData<RoomEntity> findRoom(final Context context, Long id) {
        final MutableLiveData<RoomEntity> liveData = new MutableLiveData<>();
        Call<RoomEntity> call = mRetrofitService.findRoom(id);
        call.enqueue(new Callback<RoomEntity>() {
            @Override
            public void onResponse(@NonNull Call<RoomEntity> call,
                                   @NonNull Response<RoomEntity> response) {
                if (response.body() != null){
                    liveData.setValue(response.body());
                    //  UserViewModel.handleResponse(response.body(), context, new MainActivity());
                    //todo err message when there s no internet when logging in
                }
            }

            @Override
            public void onFailure(@NonNull Call<RoomEntity> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
        return liveData;
    }

    @Override
    public MutableLiveData<List<RoomEntity>> findUserRooms(Context context, Long userId) {
        final MutableLiveData<List<RoomEntity>> liveData = new MutableLiveData<>();
        Call<List<RoomEntity>> call = mRetrofitService.loadUserRooms(userId);
        call.enqueue(new Callback<List<RoomEntity>>() {
            @Override
            public void onResponse(@NonNull Call<List<RoomEntity>> call,
                                   @NonNull Response<List<RoomEntity>> response) {
              //  if (response.body() != null){
                    liveData.setValue(response.body());
                    //  UserViewModel.handleResponse(response.body(), context, new MainActivity());
                    //todo err message when there s no internet when logging in
                //}
            }

            @Override
            public void onFailure(@NonNull Call<List<RoomEntity>> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
        return liveData;
    }
}