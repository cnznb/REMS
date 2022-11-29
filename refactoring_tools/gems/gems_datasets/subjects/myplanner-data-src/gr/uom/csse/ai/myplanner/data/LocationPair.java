package gr.uom.csse.ai.myplanner.data;

import java.io.Serializable;
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
 * @version 1.1
 */

public class LocationPair implements Serializable, Cloneable, CrystalObject {

    static final long serialVersionUID = 5464264298526416246L;

    private Location l1;
    private Location l2;
    private int distance;

    public LocationPair(Location l1, Location l2, int distance) {
        this.l1 = l1;
        this.l2 = l2;
        this.distance = distance;
    }

    public Location getLocation1() {
        return l1;
    }

    public Location getLocation2() {
        return l2;
    }

    public int getDistance() {
        return distance;
    }

    public void changeDistance(int newDis) {
        distance = newDis;
    }

    public boolean concerns(Location loc1, Location loc2) {
        return ((l1.equals(loc1) && l2.equals(loc2)) ||
                (l1.equals(loc2) && l2.equals(loc1)));
    }

    public boolean concerns(Location l) {
        return (l1.equals(l) || l2.equals(l));
    }

    public boolean equals(Object o) {
        LocationPair l;
        try {
            l = (LocationPair) o;
            return equals(l);
        } catch (ClassCastException e) {
            return false;
        }
    }

    public boolean equals(LocationPair l) {
        return ((l1.equals(l.getLocation1()) && l2.equals(l.getLocation2())) &&
                distance == l.getDistance() ||
                (l1.equals(l.getLocation2()) && l2.equals(l.getLocation1())) &&
                distance == l.getDistance());
    }

    public String toString() {
        return "Distance between " + l1.toString() + " and " + l2.toString() +
                " is " + distance;
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public Object[] getInternalState() {
        Object[] data = new Object[3];
        data[0] = l1;
        data[1] = l2;
        data[2] = distance;
        return data;
    }

    public void setInternalState(Object[] data) {
        l1 = (Location) data[0];
        l2 = (Location) data[1];
        distance = (Integer) data[2];
    }
}
