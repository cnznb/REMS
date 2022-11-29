package gr.uom.csse.ai.myplanner.data;

import java.io.Serializable;
import java.util.Iterator;
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

public class LocationClassManager implements Serializable, Cloneable, CrystalObject  {

    static final long serialVersionUID = -5087917367254362684L;

    private ArrayList<LocationClass> locationsc;
    private String user;

    public LocationClassManager(String user) {
        this.user = user;
        locationsc = new ArrayList<LocationClass>();
    }

    protected void setlocationsc(ArrayList<LocationClass> locationsc) {
        this.locationsc = locationsc;
    }

    public boolean addLocationClass(LocationClass locationClass) {
        if (!locationsc.contains(locationClass) &&
            getLocationClass(locationClass.name()) == null) {
            locationsc.add(locationClass);
            return true;
        }
        return false;
    }

    public boolean replaceLocationClass(LocationClass locationClass) {
        LocationClass l = getLocationClass(locationClass.name());
        if (l != null)
            locationsc.remove(l);
        return addLocationClass(locationClass);
    }

    public boolean contains(LocationClass locationClass) {
        return locationsc.contains(locationClass);
    }

    protected boolean removeLocationClass(LocationClass locationClass) {
        if (!locationsc.contains(locationClass))
            return false;
        locationsc.remove(locationClass);
        return true;
    }

    public LocationClass getLocationClass(String locationClassName) {
        if (!locationClassName.startsWith("Class:"))
            locationClassName = "Class: " + locationClassName;
        Iterator it = locationsc.iterator();
        LocationClass l;
        while (it.hasNext()) {
            l = (LocationClass) it.next();
            if (l.name().equals(locationClassName))
                return l;
        }
        return null;
    }

    public LocationClass[] locationClasses() {
        Object[] o = locationsc.toArray();
        LocationClass[] l = new LocationClass[o.length];
        for (int i = 0; i < l.length; i++) {
            l[i] = (LocationClass) o[i];
        }
        return l;
    }

    public String user() {
        return user;
    }

    public int size() {
        return locationsc.size();
    }

    public Object clone() {
        LocationClassManager clone;
        try {
            clone = (LocationClassManager) super.clone();
            ArrayList<LocationClass> l = new ArrayList<LocationClass>();
            LocationClass[] locationsc = locationClasses();
            for (int i = 0; i < locationsc.length; i++) {
                l.add(locationsc[i]);
            }
            clone.setlocationsc(l);
            return clone;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public Object[] getInternalState() {
        Object[] data = new Object[2];
        data[0] = locationsc;
        data[1] = user;
        return data;
    }

    public void setInternalState(Object[] data) {
        locationsc = (ArrayList<LocationClass>) data[0];
        user = (String) data[1];
    }
}
