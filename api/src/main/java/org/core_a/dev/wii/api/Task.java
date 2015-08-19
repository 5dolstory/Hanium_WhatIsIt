package org.core_a.dev.wii.api;

import java.util.List;
import java.util.ArrayList;

public class Task {
    private String task_no;
    private List<String> url = new ArrayList<String>();

    public List<String> getUrls() {
        return url;
    }

    @Override
    public String toString() {
        return task_no + " - " + String.join(", ", url);
    }
}
