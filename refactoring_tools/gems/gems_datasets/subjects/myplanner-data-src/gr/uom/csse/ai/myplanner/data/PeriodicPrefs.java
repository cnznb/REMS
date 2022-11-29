package gr.uom.csse.ai.myplanner.data;

import java.io.Serializable;
import java.util.Date;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * <p>Title: MyPlanner</p>
 *
 * <p>Description: My Planner</p>
 *
 * <p>Copyright: Copyright (c) 2007-2008 Anastasios Alexiadis, I. Refanidis</p>
 *
 * <p>Company: University of Macedonia</p>
 *
 * @author Tasos Alexiadis
 * @version 1.2
 */

public class PeriodicPrefs implements Serializable, Cloneable, CrystalObject {

    static final long serialVersionUID = -1080115928717305192L;

    public static enum Period {DAILY, WEEKLY, MONTHLY};
    public static enum Range {END_AFTER, END_BY_DEADLINE};

    private Period period;
    private Range range;
    private int recurrences;
    private int totalRec;
    private boolean includeFirst;
    private boolean includeLast;
    private boolean modified;
    private ArrayList<Date> excluded;

    public PeriodicPrefs(Period period, Range range, int recurrences, boolean includeFirst) {
        this.period = period;
        this.range = range;
        this.totalRec = recurrences;
        this.recurrences = recurrences;
        this.includeFirst = includeFirst;
        this.includeLast = false;
        this.modified = true;
        excluded = new ArrayList<Date>();
    }

    public PeriodicPrefs(Period period, Range range, boolean includeFirst, boolean includeLast) {
        this.period = period;
        this.range = range;
        this.totalRec = 0;
        this.recurrences = 0;
        this.includeFirst = includeFirst;
        this.includeLast = includeLast;
        this.modified = true;
        excluded = new ArrayList<Date>();
    }

    public void copyExcludedListTo(PeriodicPrefs p) {
        p.excluded = excluded;
    }

    public void copyExcludedListFrom(PeriodicPrefs p) {
        excluded = p.excluded;
    }

    public void excludedPeriod(Date periodStart) {
        if (!excluded.contains(periodStart))
            excluded.add(periodStart);
    }

    public void includedPeriod(Date periodStart) {
        if (excluded.contains(periodStart))
            excluded.remove(periodStart);
    }

    public boolean isExcluded(Date periodStart) {
        return excluded.contains(periodStart);
    }

    public Period period() {
        return period;
    }

    public Range range() {
        return range;
    }

    public int recurrences() {
        return recurrences;
    }

    public int totalRecurrences() {
        return totalRec;
    }

    public void setReccurences(int rec) {
        recurrences = rec;
    }

    public boolean includesFirst() {
        return includeFirst;
    }

    public boolean includesLast() {
        return includeLast;
    }

    public boolean isModified() {
        return modified;
    }

    protected void setModified(boolean modified) {
        this.modified = modified;
    }

    public boolean equals(Object o) {
        PeriodicPrefs p;
        try {
            p = (PeriodicPrefs) o;
            return equals(p);
        } catch (ClassCastException e) {
            return false;
        }
    }

    public boolean equals(PeriodicPrefs p) {
        boolean b = (period.equals(p.period()) && range.equals(p.range()) &&
                     recurrences == p.recurrences() &&
                     includeFirst == p.includesFirst() &&
                     includeLast == p.includesLast());
        if (!b)
            return false;
        Object[] o1 = excluded.toArray();
        Object[] o2 = p.excluded.toArray();
        if (o1.length != o2.length)
            return false;
        for (int i = 0; i < o1.length; i++) {
            if (!o1[i].equals(o2[i]))
                return false;
        }
        return true;
    }

    public Object clone() {
        PeriodicPrefs clone;
        try {
            clone = (PeriodicPrefs) super.clone();
            clone.excluded = new ArrayList<Date>();
            Iterator it = excluded.iterator();
            while (it.hasNext()) {
                clone.excluded.add((Date) (((Date) it.next()).clone()));
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public Object[] getInternalState() {
        Object[] data = new Object[7];
        data[0] = period;
        data[1] = range;
        data[2] = recurrences;
        data[3] = includeFirst;
        data[4] = includeLast;
        data[5] = modified;
        data[6] = excluded;
        return data;
    }

    public void setInternalState(Object[] data) {
        period = (Period) data[0];
        range = (Range) data[1];
        recurrences = (Integer) data[2];
        includeFirst = (Boolean) data[3];
        includeLast = (Boolean) data[4];
        modified = (Boolean) data[5];
        excluded = (ArrayList<Date>) data[6];
    }
}
