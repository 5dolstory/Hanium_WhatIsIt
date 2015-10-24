package org.core_a.dev.wii.db;

public class Order {
    private String Project;
    private String StartAt;
    private String EndAt;

    public Order() {
    }
    public Order(String Project, String StartAt, String EndAt) {
        this.Project = Project;
        this.StartAt = StartAt;
        this.EndAt = EndAt;
    }

    public String getProject() { return Project; }
    public void setProject(String Project) { this.Project = Project; }
    public String getStartAt() { return StartAt; }
    public void setStartAt(String StartAt) { this.StartAt = StartAt; }
    public String getEndAt() { return EndAt; }
    public void setEndAt(String EndAt) { this.EndAt = EndAt; }
}
