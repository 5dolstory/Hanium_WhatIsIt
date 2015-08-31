package org.core_a.dev.wii.db;

import java.util.List;
import java.util.ArrayList;

public class Article {
    private String article_no;
    private String author;
    private String title;
    private String context;
    private List<String> morpheme_title;
    private List<String> morpheme_context;
    private String create_at;

    @Override
    public String toString() {
        return "";
    }
}
