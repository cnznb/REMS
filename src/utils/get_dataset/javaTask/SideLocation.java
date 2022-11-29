public class SideLocation {
    private String filePath;
    private String startLine;
    private String endLine;

    public SideLocation() {
    }

    public SideLocation(String filePath, String startLine, String endLine) {
        this.filePath = filePath;
        this.startLine = startLine;
        this.endLine = endLine;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getStartLine() {
        return startLine;
    }

    public void setStartLine(String startLine) {
        this.startLine = startLine;
    }

    public String getEndLine() {
        return endLine;
    }

    public void setEndLine(String endLine) {
        this.endLine = endLine;
    }

    @Override
    public String toString() {
        return "SideLocation{" +
                "filePath='" + filePath + '\'' +
                ", startLine='" + startLine + '\'' +
                ", endLine='" + endLine + '\'' +
                '}';
    }
}
