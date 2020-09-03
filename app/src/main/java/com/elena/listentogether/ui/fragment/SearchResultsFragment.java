package com.elena.listentogether.ui.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elena.listentogether.R;
import com.elena.listentogether.model.local.entity.VideoItem;
import com.elena.listentogether.ui.activity.RoomDetailActivity;
import com.elena.listentogether.ui.adapter.VideosAdapter;
import com.elena.listentogether.youtube.YoutubeSearchListener;

import java.util.List;

public class SearchResultsFragment extends Fragment implements YoutubeSearchListener {

    private RecyclerView mResultsRecyclerView;
    private VideosAdapter mAdapter;

    public SearchResultsFragment() {
        // Required empty public constructor
    }

    public static SearchResultsFragment newInstance() {
        SearchResultsFragment fragment = new SearchResultsFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_results, container,
                false);
        mResultsRecyclerView = (RecyclerView) view;
        return view;
    }

    @Override
    public void onSearchCompleted(List<VideoItem> videoItems) {
        mAdapter = new VideosAdapter(videoItems, (RoomDetailActivity)getActivity(), getContext());
        RecyclerView.LayoutManager mLayoutManager =
                new LinearLayoutManager(getContext());
        mResultsRecyclerView.setLayoutManager(mLayoutManager);
        mResultsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mResultsRecyclerView.setAdapter(mAdapter);
    }

}
