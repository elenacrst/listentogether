package com.elena.listentogether.ui.viewmodel.listen;

import androidx.lifecycle.MutableLiveData;
import android.content.Context;

import com.elena.listentogether.model.local.entity.ListenEntity;

import java.util.List;

public interface IListen {
    void insertListen(Context context, ListenEntity listenEntity);
    MutableLiveData<List<ListenEntity>> getListens(Context context, Long roomId);
    void deleteListen(Context context, ListenEntity listenEntity);
}
