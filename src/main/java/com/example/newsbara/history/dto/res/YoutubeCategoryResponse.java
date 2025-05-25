package com.example.newsbara.history.dto.res;

import lombok.Data;
import java.util.List;

@Data
public class YoutubeCategoryResponse {
    private List<Item> items;

    @Data
    public static class Item {
        private Snippet snippet;
    }

    @Data
    public static class Snippet {
        private String title;
    }
}
