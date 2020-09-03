package com.elena.listentogether.ui.viewmodel.message;

import androidx.lifecycle.MutableLiveData;
import android.content.Context;

import com.elena.listentogether.model.local.entity.MessageEntity;

import java.util.List;

public interface IMessage {
    void insertMessage(Context context, MessageEntity messageEntity);
    MutableLiveData<List<MessageEntity>> getMessages(Context context, Long roomId);
}
