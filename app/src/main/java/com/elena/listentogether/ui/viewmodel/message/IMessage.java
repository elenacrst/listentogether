package com.elena.listentogether.ui.viewmodel.message;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;

import com.elena.listentogether.data.local.entity.MessageEntity;
import com.elena.listentogether.data.local.entity.RoomEntity;

import java.util.List;

public interface IMessage {
    void insertMessage(Context context, MessageEntity messageEntity);
    MutableLiveData<List<MessageEntity>> getMessages(Context context, Long roomId);
}
