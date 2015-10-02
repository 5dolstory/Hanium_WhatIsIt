package org.core_a.dev.wii.db;

public class Row {
    private String Project;
    private String Title;
    private String Content;
    private String Author;
    private String No;
    private String Url;
    private Morpheme Morpheme;
    private String WriteAt;

    public Row(String Title, String Author, String Url) {
        this.Title = Title;
        this.Author = Author;
        this.Url = Url;
    }

    public String getProject() { return Project; }
    public void setProject(String Project) { this.Project = Project; }
    public String getTitle() { return Title; }
    public void setTitle(String Title) { this.Title = Title; }
    public String getContent() { return Content; }
    public void setContent(String Content) { this.Content = Content; }
    public String getAuthor() { return Author; }
    public void setAuthor(String Author) { this.Author = Author; }
    public String getNo() { return No; }
    public void setNo(String No) { this.No = No; }
    public String getUrl() { return Url; }
    public void setUrl(String Url) { this.Url = Url; }
    public Morpheme getMorpheme() { return Morpheme; }
    public void setMorpheme(Morpheme Morpheme) { this.Morpheme = Morpheme; }
    public String getWriteAt() { return WriteAt; }
    public void setWriteAt(String WriteAt) { this.WriteAt = WriteAt; }

    @Override
    public String toString() {
        return Title + "\t" + Author + "\t" + Url + "\t" + Morpheme.toString();
    }
}
