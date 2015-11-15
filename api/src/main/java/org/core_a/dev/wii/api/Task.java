package org.core_a.dev.wii.api;

import java.util.List;
import java.util.ArrayList;

public class Task {
    private String ProjectName;
    private List<String> Url = new ArrayList<String>();

    public String getProjectName() { return ProjectName; }
    public void setProjectName(String ProjectName) { this.ProjectName = ProjectName; }
    public List<String> getUrls() {
        return Url;
    }

    @Override
    public String toString() {
        return ProjectName + " - " + String.join(", ", Url);
    }
}
