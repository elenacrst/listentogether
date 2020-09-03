package com.elena.listentogether.youtube;

import com.elena.listentogether.model.local.entity.VideoItem;

import java.util.List;

public interface YoutubeSearchListener {
    void onSearchCompleted(List<VideoItem> videoItems);
}
