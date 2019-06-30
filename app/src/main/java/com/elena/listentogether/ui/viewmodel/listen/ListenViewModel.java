package com.elena.listentogether.ui.viewmodel.listen;

import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.support.annotation.Nullable;

import com.elena.listentogether.data.local.entity.ListenEntity;
import com.elena.listentogether.data.local.entity.RoomEntity;

import java.util.List;

import retrofit2.Retrofit;

public class ListenViewModel extends ViewModel {
    private IListen mIListen;
    private MediatorLiveData<List<ListenEntity>> mListens;

    // No argument constructor
    public ListenViewModel() {//passed from activity where it's injected
        mIListen = new IListenImpl();
        mListens = new MediatorLiveData<>();
    }

    public void setRetrofit(Retrofit retrofit){
        mIListen = new IListenImpl(retrofit);
    }

    public void insertListen(final Context context, ListenEntity listenEntity) {
        mIListen.insertListen(context, listenEntity);
    }

    public void loadListens(final Context context, Long roomId) {
        mListens.addSource(
                mIListen.getListens(context, roomId), new Observer<List<ListenEntity>>() {
                    @Override
                    public void onChanged(@Nullable List<ListenEntity> data) {
                        /*
                        if (userEntity == null){
                            Log.wtf("user","null");
                            handleError();
                        }else {*/

                        mListens.setValue(data);
                        //  handleResponse(data, context, new MainActivity());
                        // }

                    }
                }
        );
    }

    public MediatorLiveData<List<ListenEntity>> getmListens() {
        return mListens;
    }

    public void deleteListen(final Context context, ListenEntity listenEntity) {
        mIListen.deleteListen(context, listenEntity);
    }
}

