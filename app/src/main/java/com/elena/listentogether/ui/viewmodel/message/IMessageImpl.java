package com.elena.listentogether.ui.viewmodel.message;

import androidx.lifecycle.MutableLiveData;
import android.content.Context;
import androidx.annotation.NonNull;

import com.elena.listentogether.model.local.entity.MessageEntity;
import com.elena.listentogether.model.remote.ApiCallInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class IMessageImpl implements IMessage {
    private ApiCallInterface mRetrofitService;

    public IMessageImpl(){
    }

    public IMessageImpl(Retrofit retrofit){
        mRetrofitService = retrofit.create(ApiCallInterface.class);//keep this
    }

    @Override
    public void insertMessage(Context context, MessageEntity messageEntity) {
        Call<Void> call = mRetrofitService.insertMessage(messageEntity);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call,
                                   @NonNull Response<Void> response) {
                /*if (response.body() != null){
                    Toast.makeText(context, response.body(), Toast.LENGTH_SHORT).show();
                }*/
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public MutableLiveData<List<MessageEntity>> getMessages(Context context, Long roomId) {
        final MutableLiveData<List<MessageEntity>> liveData = new MutableLiveData<>();
        Call<List<MessageEntity>> call = mRetrofitService.getMessages(roomId);
        call.enqueue(new Callback<List<MessageEntity>>() {
            @Override
            public void onResponse(@NonNull Call<List<MessageEntity>> call,
                                   @NonNull Response<List<MessageEntity>> response) {
                if (response.body() != null){
                    liveData.setValue(response.body());
                    //  UserViewModel.handleResponse(response.body(), context, new MainActivity());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<MessageEntity>> call, @NonNull Throwable t) {

                t.printStackTrace();
            }
        });
        return liveData;
    }
}