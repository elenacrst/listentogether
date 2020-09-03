package com.elena.listentogether.ui.viewmodel.listen;

import androidx.lifecycle.MutableLiveData;
import android.content.Context;
import androidx.annotation.NonNull;
import android.util.Log;

import com.elena.listentogether.model.local.entity.ListenEntity;
import com.elena.listentogether.model.remote.ApiCallInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class IListenImpl implements IListen {
    private ApiCallInterface mRetrofitService;

    public IListenImpl(){
    }

    public IListenImpl(Retrofit retrofit){
        mRetrofitService = retrofit.create(ApiCallInterface.class);//keep this
    }

    @Override
    public void insertListen(Context context, ListenEntity listenEntity) {
        Call<Void> call = mRetrofitService.joinRoom(listenEntity);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call,
                                   @NonNull Response<Void> response) {
                if (response.body() != null){
                    Log.wtf("listen","joined room");
                   // Toast.makeText(context, R.string.msg_joined, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public MutableLiveData<List<ListenEntity>> getListens(Context context, Long roomId) {
        final MutableLiveData<List<ListenEntity>> liveData = new MutableLiveData<>();
        Call<List<ListenEntity>> call = mRetrofitService.getListeningForRoom(roomId);
        call.enqueue(new Callback<List<ListenEntity>>() {
            @Override
            public void onResponse(@NonNull Call<List<ListenEntity>> call,
                                   @NonNull Response<List<ListenEntity>> response) {
                if (response.body() != null){
                    liveData.setValue(response.body());
                    //  UserViewModel.handleResponse(response.body(), context, new MainActivity());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ListenEntity>> call, @NonNull Throwable t) {

                t.printStackTrace();
            }
        });
        return liveData;
    }

    @Override
    public void deleteListen(Context context, ListenEntity listenEntity) {
        Call<String> call = mRetrofitService.deleteListen(listenEntity);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call,
                                   @NonNull Response<String> response) {
                if (response.body() != null){
                   // Log.wtf("listen","joined room");
                    // Toast.makeText(context, R.string.msg_joined, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }
}