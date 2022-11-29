package gr.uom.csse.ai.myplanner.data;

/**
 * <p>Title: MyPlanner</p>
 *
 * <p>Description: My Planner</p>
 *
 * <p>Copyright: Copyright (c) 2007 Anastasios Alexiadis, I. Refanidis</p>
 *
 * <p>Company: University of Macedonia</p>
 *
 * @author Tasos Alexiadis
 * @version 1.2
 */

public class LocationAnywhere extends Location implements Cloneable {

    static final long serialVersionUID = -5775487387076872469L;

    public LocationAnywhere() {
        super(-1, "ANYWHERE");
        latlng = "ANYWHERE";
    }

    public boolean equals(Location l) {
        return (l != null && l.id() == -1);
    }
}
