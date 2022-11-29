import java.util.List;

public class ref_json {
    private String type;
    private String description;
    private List<SideLocation> leftSideLocations;
    private List<SideLocation> rightSideLocations;

    public ref_json() {
    }

    public ref_json(String type, String description, List<SideLocation> leftSideLocations, List<SideLocation> rightSideLocations) {
        this.type = type;
        this.description = description;
        this.leftSideLocations = leftSideLocations;
        this.rightSideLocations = rightSideLocations;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<SideLocation> getLeftSideLocations() {
        return leftSideLocations;
    }

    public void setLeftSideLocations(List<SideLocation> leftSideLocations) {
        this.leftSideLocations = leftSideLocations;
    }

    public List<SideLocation> getRightSideLocations() {
        return rightSideLocations;
    }

    public void setRightSideLocations(List<SideLocation> rightSideLocations) {
        this.rightSideLocations = rightSideLocations;
    }

    @Override
    public String toString() {
        return "ref_json{" +
                "type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", leftSideLocations=" + leftSideLocations +
                ", rightSideLocations=" + rightSideLocations +
                '}';
    }
}
