package com.elena.listentogether.ui.viewmodel.listen;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;

import com.elena.listentogether.data.local.entity.ListenEntity;
import com.elena.listentogether.data.local.entity.RoomEntity;

import java.util.List;

public interface IListen {
    void insertListen(Context context, ListenEntity listenEntity);
    MutableLiveData<List<ListenEntity>> getListens(Context context, Long roomId);
    void deleteListen(Context context, ListenEntity listenEntity);
}
