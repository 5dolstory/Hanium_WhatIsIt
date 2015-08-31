package org.core_a.dev.wii.api;

import java.util.List;
import java.util.ArrayList;

public class Morpheme {
    private List<String> Title = new ArrayList<String>();
    private List<String> Content = new ArrayList<String>();

    public Morpheme() {
    }
    public Morpheme(List<String> Title, List<String> Content) {
        this.Title = Title;
        this.Content = Content;
    }

    public List<String> getTitle() { return Title; }
    public void setTitle(List<String> Title) { this.Title = Title; }
    public List<String> getContent() { return Content; }
    public void setContent(List<String> Content) { this.Content = Content; }

    @Override
    public String toString() {
        return "[Morpheme]" + "\t" + Title.toString() + "\t" + Content.toString();
    }
}
