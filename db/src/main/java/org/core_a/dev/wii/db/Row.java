package org.core_a.dev.wii.db;

import java.util.List;
import java.util.ArrayList;

public class Article {
    private String article_no;
    private String author;
    private String title;
    private String context;
    private String morpheme_title; //json?
    private String morpheme_context; //json?
    private String morpheme_context; //json?
    //private List<String> url = new ArrayList<String>();
    // use hive timestamp

    @Override
    public String toString() {
        return task_no + " - " + String.join(", ", url);
    }
}
