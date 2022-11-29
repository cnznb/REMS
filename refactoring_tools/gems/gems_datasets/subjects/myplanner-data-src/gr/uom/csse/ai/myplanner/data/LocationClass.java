package gr.uom.csse.ai.myplanner.data;

import java.util.ArrayList;

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

public class LocationClass extends Location {

    static final long serialVersionUID = -1137194901298510056L;

    private ArrayList<String> locationsNames;

    public LocationClass(int id, String name) {
        super(id, name);
        locationsNames = new ArrayList<String>();
    }

    public boolean addLocation(String locName) {
        if (!locationsNames.contains(locName)) {
            locationsNames.add(locName);
            return true;
        } else
            return false;
    }

    public boolean removeLocation(String locName) {
        if (locationsNames.contains(locName)) {
            locationsNames.remove(locName);
            return true;
        } else
            return false;
    }

    public boolean containsLocation(Location loc) {
        return containsLocation(loc.name());
    }

    public boolean containsLocation(String locName) {
        String[] locs = locations();
        for (int i = 0; i < locs.length; i++) {
            if (locs[i].equals(locName));
            return true;
        }
        return false;
    }

    public String[] locations() {
        Object[] o = locationsNames.toArray();
        String[] locs = new String[o.length];
        for (int i = 0; i < o.length; i++) {
            locs[i] = (String) o[i];
        }
        return locs;
    }

    public boolean equals(Object o) {
        LocationClass l;
        try {
            l = (LocationClass) o;
            return equals(l);
        } catch (ClassCastException e) {
            return false;
        }
    }

    public boolean equals(LocationClass l) {
        return name().equals(l.name());
    }

    public String name() {
        return "Class: " + name;
    }

    public Object[] getInternalState() {
        Object[] data = new Object[3];
        data[0] = name;
        data[1] = id;
        data[2] = locationsNames;
        return data;
    }

    public void setInternalState(Object[] data) {
        name = (String) data[0];
        id = (Integer) data[1];
        locationsNames = (ArrayList<String>) data[2];
    }
}
