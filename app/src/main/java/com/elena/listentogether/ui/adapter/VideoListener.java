package com.elena.listentogether.ui.adapter;

import com.elena.listentogether.data.local.entity.RoomEntity;
import com.elena.listentogether.data.local.entity.VideoItem;

public interface VideoListener {
    void onVideoSelected(VideoItem videoItem);
}
