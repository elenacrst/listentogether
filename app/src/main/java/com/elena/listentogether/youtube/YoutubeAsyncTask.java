package com.elena.listentogether.youtube;

import android.os.AsyncTask;

import com.elena.listentogether.model.local.entity.VideoItem;

import java.util.List;

public class YoutubeAsyncTask extends AsyncTask<String, Void, List<VideoItem>> {
    private YoutubeSearchListener mListener;

    public YoutubeAsyncTask(YoutubeSearchListener mListener) {
        this.mListener = mListener;
    }

    @Override
    protected List<VideoItem> doInBackground(String... strings) {
        if (strings != null && strings.length > 0){
            YoutubeConnector youtubeConnector = new YoutubeConnector();
            return youtubeConnector.search(strings[0]);
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<VideoItem> videoItems) {
        super.onPostExecute(videoItems);
        mListener.onSearchCompleted(videoItems);
    }
}
