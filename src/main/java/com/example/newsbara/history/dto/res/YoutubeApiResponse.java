package com.example.newsbara.history.dto.res;

import lombok.Data;

import java.util.List;

@Data
public class YoutubeApiResponse {
    private List<Item> items;

    @Data
    public static class Item {
        private Snippet snippet;
        private ContentDetails contentDetails;
    }

    @Data
    public static class Snippet {
        private String title;
        private Thumbnails thumbnails;
        private String channelTitle;
        private String categoryId;
    }

    @Data
    public static class Thumbnails {
        private ThumbnailInfo defaultThumbnail;

        // "default"는 Java 예약어라서 이렇게 이름 매핑
        public ThumbnailInfo getDefault() {
            return defaultThumbnail;
        }

        public void setDefault(ThumbnailInfo defaultThumbnail) {
            this.defaultThumbnail = defaultThumbnail;
        }
    }

    @Data
    public static class ThumbnailInfo {
        private String url;
    }

    @Data
    public static class ContentDetails {
        private String duration; // 예: PT5M33S
    }
}
