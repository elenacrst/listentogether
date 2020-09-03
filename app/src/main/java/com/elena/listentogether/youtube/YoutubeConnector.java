	package com.elena.listentogether.youtube;

    import android.util.Log;

    import com.elena.listentogether.model.local.entity.VideoItem;
    import com.google.api.client.http.HttpRequest;
    import com.google.api.client.http.HttpRequestInitializer;
    import com.google.api.client.http.javanet.NetHttpTransport;
    import com.google.api.client.json.jackson2.JacksonFactory;
    import com.google.api.services.youtube.YouTube;
    import com.google.api.services.youtube.model.ResourceId;
    import com.google.api.services.youtube.model.SearchListResponse;
    import com.google.api.services.youtube.model.SearchResult;
    import com.google.api.services.youtube.model.Thumbnail;

    import java.io.IOException;
    import java.util.ArrayList;
    import java.util.Iterator;
    import java.util.List;

    public class YoutubeConnector {
        private YouTube youtube;
        private YouTube.Search.List query;
        public static final String KEY = "AIzaSyBwwhY8b3psfmGsmSKodl4sMY-CHsvnqDg";
        public static final String PACKAGENAME = "com.elena.listentogether";
        public static final String SHA1 = "B4:29:35:33:CD:1B:7C:4C:C6:11:90:7F:8A:63:76:45:64:8A:3C:9A";
        private static final long MAXRESULTS = 25;
        public YoutubeConnector() {
            youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
                @Override
                public void initialize(HttpRequest request) throws IOException {
                    request.getHeaders().set("X-Android-Package", PACKAGENAME);
                    request.getHeaders().set("X-Android-Cert",SHA1);
                }
            }).setApplicationName("SearchYoutube").build();
            try {
                query = youtube.search().list("id,snippet");
                query.setKey(KEY);
                query.setType("video");
                query.setFields("items(id/kind,id/videoId,snippet/title,snippet/description,snippet/thumbnails/high/url)");

            } catch (IOException e) {
                Log.d("YC", "Could not initialize: " + e);
            }
        }
        public List<VideoItem> search(String keywords) {
            query.setQ(keywords);
            query.setMaxResults(MAXRESULTS);
            try {
                SearchListResponse response = query.execute();
                List<SearchResult> results = response.getItems();
                List<VideoItem> items = new ArrayList<>();
                if (results != null) {
                    items = setItemsList(results.iterator());
                }
                return items;
            } catch (IOException e) {
                Log.d("YC", "Could not search: " + e);
                return null;
            }
        }
        private static List<VideoItem> setItemsList(Iterator<SearchResult> iteratorSearchResults) {
            List<VideoItem> tempSetItems = new ArrayList<>();
            if (!iteratorSearchResults.hasNext()) {
                System.out.println(" There aren't any results for your query.");
            }
            while (iteratorSearchResults.hasNext()) {
                SearchResult singleVideo = iteratorSearchResults.next();
                ResourceId rId = singleVideo.getId();
                if (rId.getKind().equals("youtube#video")) {
                    VideoItem item = new VideoItem();
                    Thumbnail thumbnail = singleVideo.getSnippet().getThumbnails().getHigh();
                    item.setVideoId(singleVideo.getId().getVideoId());
                    item.setTitle(singleVideo.getSnippet().getTitle());
                    item.setThumbnailUrl(thumbnail.getUrl());
                    tempSetItems.add(item);
                }
            }
            return tempSetItems;
        }
    }