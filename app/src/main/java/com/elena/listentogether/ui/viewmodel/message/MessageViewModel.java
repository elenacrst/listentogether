package com.elena.listentogether.ui.viewmodel.message;

import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.support.annotation.Nullable;

import com.elena.listentogether.data.local.entity.MessageEntity;

import java.util.List;

import retrofit2.Retrofit;

public class MessageViewModel extends ViewModel {
    //private MediatorLiveData<UserEntity> mUserData;
    private IMessage mIMessage;
    private MediatorLiveData<List<MessageEntity>> mMessages;

    // No argument constructor
    public MessageViewModel() {//passed from activity where it's injected
      //  mUserData = new MediatorLiveData<>();
        mIMessage = new IMessageImpl();
        mMessages = new MediatorLiveData<>();
    }

    public void setRetrofit(Retrofit retrofit){
        mIMessage = new IMessageImpl(retrofit);
    }

    public void insertMessage(final Context context, MessageEntity messageEntity) {
        mIMessage.insertMessage(context, messageEntity);
    }

    public void loadMessages(final Context context, Long roomId) {
        mMessages.addSource(
                mIMessage.getMessages(context, roomId), new Observer<List<MessageEntity>>() {
                    @Override
                    public void onChanged(@Nullable List<MessageEntity> data) {
                        /*
                        if (userEntity == null){
                            Log.wtf("user","null");
                            handleError();
                        }else {*/
                        mMessages.setValue(data);
                        //  handleResponse(data, context, new MainActivity());
                        // }
                    }
                }
        );
    }

    public MediatorLiveData<List<MessageEntity>> getmMessages() {
        return mMessages;
    }

}
