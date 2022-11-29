package gr.uom.csse.ai.myplanner.data;

import java.io.Serializable;
import java.util.Date;

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

public class DomainPrefs implements Serializable, Cloneable, CrystalObject {

    static final long serialVersionUID = 8559789822988091257L;

    public static enum Pref {NO_PREF, LINEAR_ASC, LINEAR_DESC, STEP_ASC, STEP_DESC };

    private Pref pref;
    private double min;
    private double max;
    private Date step;

    public DomainPrefs(Pref pref) {
        this.pref = pref;
    }

    public DomainPrefs(Pref pref, double min, double max) {
        this.pref = pref;
        this.min = min;
        this.max = max;
    }

    public DomainPrefs(Pref pref, double min, double max, Date step) {
        this.pref = pref;
        this.min = min;
        this.max = max;
        this.step = step;
    }

    public Pref pref() {
        return pref;
    }

    public void setPref(Pref newPref) {
        pref = newPref;
    }

    public double minUtility() {
        return min;
    }

    public void setMinUtility(int newMin) {
        min = newMin;
    }

    public double maxUtility() {
        return max;
    }

    public void setMaxUtility(int newMax) {
        max = newMax;
    }

    public Date stepPoint() {
        return step;
    }

    public void setStepPoint(Date newStep) {
        step = newStep;
    }

    public boolean equals(Object o) {
        DomainPrefs p;
        try {
            p = (DomainPrefs) o;
            return equals(p);
        } catch (ClassCastException e) {
            return false;
        }
    }

    public boolean equals(DomainPrefs p) {
        if (!pref.name().equals(p.pref().name()))
            return false;
        switch (pref) {
        case NO_PREF:
            return true;
        case LINEAR_ASC:
            return (min == p.minUtility() && max == p.maxUtility());
        case LINEAR_DESC:
            return (min == p.minUtility() && max == p.maxUtility());
        case STEP_ASC:
            return (min == p.minUtility() && max == p.maxUtility() &&
                    step.equals(p.stepPoint()));
        case STEP_DESC:
            return (min == p.minUtility() && max == p.maxUtility() &&
                    step.equals(p.stepPoint()));
        }
        return false;
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public Object[] getInternalState() {
        Object[] data = new Object[4];
        data[0] = pref;
        data[1] = min;
        data[2] = max;
        data[3] = step;
        return data;
    }

    public void setInternalState(Object[] data) {
        pref = (Pref) data[0];
        min = (Integer) data[1];
        max = (Integer) data[2];
        step = (Date) data[3];
    }
}
