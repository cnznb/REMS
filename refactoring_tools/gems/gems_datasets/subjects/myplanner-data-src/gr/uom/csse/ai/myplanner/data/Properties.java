package gr.uom.csse.ai.myplanner.data;

import java.io.Serializable;
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
 * @version 1.5.2
 */

public class Properties implements Serializable, Cloneable, CrystalObject {

    static final long serialVersionUID = -4553434599969504389L;

    private String defaultCalendar;
    private String defaultCalendarID;
    private ArrayList<String> googleCalendars;
    private ArrayList<String> googleCalendarIDs = null;

    public Properties(String defaultCalendar, ArrayList<String> googleCalendars) {
        this.defaultCalendar = defaultCalendar;
        this.googleCalendars = googleCalendars;
    }

    public Properties(String defaultCalendar, String defaultCalendarID, ArrayList<String> googleCalendars,
                      ArrayList<String> googleCalendarIDs) {
        this.defaultCalendar = defaultCalendar;
        this.defaultCalendarID = defaultCalendar;
        this.googleCalendars = googleCalendars;
        this.googleCalendarIDs = googleCalendarIDs;
    }


    public String[] googleCalendars() {
        Object[] o = googleCalendars.toArray();
        String[] cals = new String[o.length];
        for (int i = 0; i < o.length; i++) {
            cals[i] = (String) o[i];
        }
        return cals;
    }

    public String[] googleCalendarsIDs() {
        Object[] o = googleCalendarIDs.toArray();
        String[] cals = new String[o.length];
        for (int i = 0; i < o.length; i++) {
            cals[i] = (String) o[i];
        }
        return cals;
    }


    public String defaultCalendar() {
        return defaultCalendar;
    }

    public String defaultCalendarID() {
        return defaultCalendarID;
    }

    public Object clone() {
        ArrayList<String> newCal = new ArrayList<String>();
        String[] cals = googleCalendars();
        for (int i = 0; i < cals.length; i++) {
            newCal.add(cals[i]);
        }
        ArrayList<String> newCalIDs = new ArrayList<String>();
        if (googleCalendarIDs != null) {
            String[] calIDs = googleCalendarsIDs();
            for (int i = 0; i < calIDs.length; i++) {
                newCalIDs.add(calIDs[i]);
            }
        }
        return new Properties(defaultCalendar, defaultCalendarID, newCal, newCalIDs);
    }

    public boolean equals(Object o) {
        Properties p;
        try {
            p = (Properties) o;
            return equals(p);
        } catch (ClassCastException e) {
            return false;
        }
    }

    public boolean equals(Properties p) {
        return (googleCalendars.equals(p.googleCalendars) &&
                defaultCalendar.equals(p.defaultCalendar));
    }

    public Object[] getInternalState() {
        Object[] data = new Object[4];
        data[0] = defaultCalendar;
        data[1] = googleCalendars;
        data[2] = googleCalendarIDs;
        data[3] = defaultCalendarID;
        return data;
    }

    public void setInternalState(Object[] data) {
        defaultCalendar = (String) data[0];
        googleCalendars = (ArrayList<String>) data[1];
        googleCalendarIDs = (ArrayList<String>) data[2];
        defaultCalendarID = (String) data[3];
    }
}
