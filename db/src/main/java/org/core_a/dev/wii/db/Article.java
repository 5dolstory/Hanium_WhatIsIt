package org.core_a.dev.wii.db;

import java.util.List;
import java.util.ArrayList;

public class Article {
    private final String Title;

    public Article(String Title) {
        System.out.println("Response -----------------\n\n" +Title +"\n--------------\n\n");
        this.Title = Title;
    }

    public String getTitle() {
        return Title;
    }
}
