import java.util.ArrayList;

public class commit {
    private String sha1;
    private ArrayList<refactoring> refactorings;
    private String author;
    private String id;
    private String time;
    private String repository;

    public commit() {
    }

    public commit(String sha1, ArrayList<refactoring> refactorings, String author, String id, String time, String repository) {
        this.sha1 = sha1;
        this.refactorings = refactorings;
        this.author = author;
        this.id = id;
        this.time = time;
        this.repository = repository;
    }

    public String getSha1() {
        return sha1;
    }

    public void setSha1(String sha1) {
        this.sha1 = sha1;
    }

    public ArrayList<refactoring> getRefactorings() {
        return refactorings;
    }

    public void setRefactorings(ArrayList<refactoring> refactorings) {
        this.refactorings = refactorings;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    @Override
    public String toString() {
        return "commit{" +
                "sha1='" + sha1 + '\'' +
                ", refactorings=" + refactorings +
                ", author='" + author + '\'' +
                ", id='" + id + '\'' +
                ", time='" + time + '\'' +
                ", repository='" + repository + '\'' +
                '}';
    }
}
