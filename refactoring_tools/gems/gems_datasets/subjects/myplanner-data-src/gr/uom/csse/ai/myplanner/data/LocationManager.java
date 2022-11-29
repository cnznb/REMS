package gr.uom.csse.ai.myplanner.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

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

public class LocationManager implements Serializable, Cloneable, CrystalObject {

    static final long serialVersionUID = 4296857891387776074L;

    private ArrayList<Location> locations;
    private ArrayList<LocationPair> distances;
    private LocationAnywhere la;
    private String user;

    public LocationManager(String user) {
        this.user = user;
        la = new LocationAnywhere();
        locations = new ArrayList<Location>();
        locations.add(la);
        distances = new ArrayList<LocationPair>();
    }

    protected void setLocations(ArrayList<Location> locations) {
        this.locations = locations;
    }

    protected void setLocationPairs(ArrayList<LocationPair> locPairs) {
        this.distances = locPairs;
    }

    public String user() {
        return user;
    }

    public LocationAnywhere getLocationAnywhereInstance() {
        return la;
    }

    public Location[] getLocations() {
        Object[] o = locations.toArray();
        Location[] locs = new Location[o.length];
        for (int i = 0; i < locs.length; i++) {
            locs[i] = (Location) o[i];
        }
        return locs;
    }

    public LocationPair[] getLocationPairs() {
        Object[] o = distances.toArray();
        LocationPair[] locs = new LocationPair[o.length];
        for (int i = 0; i < locs.length; i++) {
            locs[i] = (LocationPair) o[i];
        }
        return locs;
    }

    public boolean addLocation(Location l) {
        if (!locations.contains(l) && getLocation(l.name()) == null) {
            locations.add(l);
            return true;
        }
        return false;
    }

    public boolean containsLocation(Location l) {
        return locations.contains(l);
    }

    public boolean containsLocationPair(LocationPair lp) {
        return distances.contains(lp);
    }

    public boolean containsDistance(Location l1, Location l2) {
        Iterator it = distances.iterator();
        LocationPair lp;
        while (it.hasNext()) {
            lp = (LocationPair) it.next();
            if (lp.concerns(l1, l2))
                return true;
        }
        return false;
    }

    protected boolean removeLocation(Location l) {
        if (!locations.contains(l))
            return false;
        locations.remove(l);
        LocationPair[] lps = getDistancePairs(l);
        for (int i = 0; i < lps.length; i++) {
            distances.remove(lps[i]);
        }
        return true;
    }

    public Location getLocation(String name) {
        Iterator it = locations.iterator();
        Location l;
        while (it.hasNext()) {
            l = (Location) it.next();
            if (l.name().equals(name))
                return l;
        }
        return null;
    }

    public Location getLocation(int locID) {
        Iterator it = locations.iterator();
        Location l;
        while (it.hasNext()) {
            l = (Location) it.next();
            if (l.id() == locID)
                return l;
        }
        return null;
    }

    public boolean addDistance(Location l1, Location l2, int distance) {
        Iterator it = distances.iterator();
        LocationPair p;
        while (it.hasNext()) {
            p = (LocationPair) it.next();
            if (p.concerns(l1, l2)) {
                p.changeDistance(distance);
                return true;
            }
        }
        distances.add(new LocationPair(l1, l2, distance));
        return true;
    }

    public int getDistance(Location l1, Location l2) {
        Iterator it = distances.iterator();
        LocationPair lp;
        // What is the Distance from Anywhere to a Location?
        // From a location to anywhere is zero
        if (l1.id() == -1 || l2.id() == -1)
            return 0;
        if (l1.equals(l2))
            return 0;
        while (it.hasNext()) {
            lp = (LocationPair) it.next();
            if (lp.concerns(l1, l2))
                return lp.getDistance();
        }
        return 0;
    }

    public void changeDistance(Location l1, Location l2, int newDist) {
        Iterator it = distances.iterator();
        LocationPair lp;
        if (l1.id() == -1 || l2.id() == -1)
            return;
        if (l1.equals(l2))
            return;
        while (it.hasNext()) {
            lp = (LocationPair) it.next();
            if (lp.concerns(l1, l2))
                lp.changeDistance(newDist);
        }
    }

    public LocationPair[] getDistancePairs(Location l) {
        ArrayList<LocationPair> dp = new ArrayList<LocationPair>();
        Iterator it = distances.iterator();
        LocationPair lp;
        while (it.hasNext()) {
            lp = (LocationPair) it.next();
            if (lp.concerns(l))
                dp.add(lp);
        }
        Object[] o = dp.toArray();
        LocationPair[] dpa = new LocationPair[o.length];
        for (int i = 0; i < o.length; i++) {
            dpa[i] = (LocationPair) o[i];
        }
        return dpa;
    }

    public int getMaxId() {
        Iterator it = locations.iterator();
        int max = 0;
        Location l;
        while (it.hasNext()) {
            l = (Location) it.next();
            if (l.id() > max)
                max = l.id();
        }
        return max;
    }

    public int size() {
        return locations.size();
    }

    public Object clone() {
        LocationManager clone;
        try {
            clone = (LocationManager) super.clone();
            ArrayList<Location> locs = new ArrayList<Location>();
            ArrayList<LocationPair> pairs = new ArrayList<LocationPair>();
            Location[] orLocs = getLocations();
            LocationPair[] orPairs = getLocationPairs();
            for (int i = 0; i < orLocs.length; i++) {
                locs.add(orLocs[i]);
            }
            for (int i = 0; i < orPairs.length; i++) {
                pairs.add(orPairs[i]);
            }
            clone.setLocations(locs);
            clone.setLocationPairs(pairs);
            return clone;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public Object[] getInternalState() {
        Object[] data = new Object[4];
        data[0] = locations;
        data[1] = distances;
        data[2] = la;
        data[3] = user;
        return data;
    }

    public void setInternalState(Object[] data) {
        locations = (ArrayList<Location>) data[0];
        distances = (ArrayList<LocationPair>) data[1];
        la = (LocationAnywhere) data[2];
        user = (String) data[3];
    }
}
