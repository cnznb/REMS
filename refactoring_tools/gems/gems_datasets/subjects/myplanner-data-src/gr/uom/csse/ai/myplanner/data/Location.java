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
 * @version 1.2
 */

public class Location implements Serializable, Cloneable, CrystalObject {

    static final long serialVersionUID = 782622135410412757L;

    protected String name;
    protected int id;
    protected String latlng;

    public Location(int id, String name) {
        this.id = id;
        this.name = name;
        latlng = "";
        if (name.toLowerCase().startsWith("class:"))
            throw new IllegalArgumentException("Reserved location name");
        else if (name.toUpperCase().equals("ANYWHERE") && id != -1)
            throw new IllegalArgumentException("Illegal location definition");
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLatLng(String latlng) {
        this.latlng = latlng;
    }

    public String name() {
        return name;
    }

    public int id() {
        return id;
    }

    public String latLng() {
        return latlng;
    }

    public boolean equals(Object o) {
        Location l;
        try {
            l = (Location) o;
            return equals(l);
        } catch (ClassCastException e) {
            return false;
        }
    }

    public boolean equals(Location l) {
        return (l != null && name.equals(l.name()) && latlng.equals(l.latLng()));
    }

    public String toString() {
        return name();
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
        data[0] = name;
        data[1] = id;
        data[2] = latlng;
        return data;
    }

    public void setInternalState(Object[] data) {
        name = (String) data[0];
        id = (Integer) data[1];
        latlng = (String) data[2];
    }

}
