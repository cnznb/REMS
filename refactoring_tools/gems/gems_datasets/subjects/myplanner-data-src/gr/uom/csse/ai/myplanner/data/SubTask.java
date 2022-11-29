package gr.uom.csse.ai.myplanner.data;

import java.util.Date;
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
 * @version 1.3
 */

public class SubTask implements Serializable, Cloneable, CrystalObject {

    static final long serialVersionUID = -7123488954070101474L;

    private Date startSubTaskTime;
    private Date endSubTaskTime;
    private int interSize;
    private Task t;
    private boolean completed;
    public String locationName;

    public SubTask(Task t, Date startTime, Date endTime, int interSize, String locationName) {
        this.t = t;
        startSubTaskTime = startTime;
        endSubTaskTime = endTime;
        this.interSize = interSize;
        completed = false;
        this.locationName = locationName;
    }

    public Date getStartTime() {
        return startSubTaskTime;
    }

    public Date getEndTime() {
        return endSubTaskTime;
    }

    public int intervalSize() {
        return interSize;
    }

    public Task partOf() {
        return t;
    }

    public boolean isCompleted() {
        return completed;
    }

    public String locationName() {
        return locationName;
    }

    public void setCompleted(boolean completed) {
        if ((this.completed && completed) || (!this.completed && !completed))
            return;
        this.completed = completed;
        if (completed) {
            t.alterRemainingDuration( -(30 * interSize));
            t.setPartsDone(t.partsDone() + 1);
        } else {
            t.alterRemainingDuration(30 * interSize);
            t.setPartsDone(t.partsDone() - 1);
        }
    }

    public boolean equals(Object o) {
        SubTask s;
        try {
            s = (SubTask) o;
            return equals(s);
        } catch (ClassCastException e) {
            return false;
        }
    }

    public boolean equals(SubTask s) {
        if (s == null)
            return false;
        return (t.equals(s.partOf()) && startSubTaskTime.equals(s.getStartTime()) &&
                endSubTaskTime.equals(s.getEndTime()) &&
                interSize == s.intervalSize());
    }

    public String toString() {
        return "Sub task of " + t.name() + ": " + startSubTaskTime + " to " +
                endSubTaskTime;
    }

    public Object clone() {
        return new SubTask(t, startSubTaskTime, endSubTaskTime, interSize, locationName);
    }

    public Object[] getInternalState() {
        Object[] data = new Object[6];
        data[0] = startSubTaskTime;
        data[1] = endSubTaskTime;
        data[2] = interSize;
        data[3] = t;
        data[4] = completed;
        data[5] = locationName;
        return data;
    }

    public void setInternalState(Object[] data) {
        startSubTaskTime = (Date) data[0];
        endSubTaskTime = (Date) data[1];
        interSize = (Integer) data[2];
        t = (Task) data[3];
        completed = (Boolean) data[4];
        locationName = (String) data[5];
    }
}
