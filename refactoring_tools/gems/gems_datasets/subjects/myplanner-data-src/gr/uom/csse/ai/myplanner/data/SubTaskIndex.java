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

public class SubTaskIndex implements Serializable, Cloneable {

    static final long serialVersionUID = 3027692747100867750L;

    private int taskID;
    private int subTaskID;
    private int periodID;

    public SubTaskIndex(int taskID, int periodID, int subTaskID) {
        this.taskID = taskID;
        this.periodID = periodID;
        this.subTaskID = subTaskID;
    }

    public int taskID() {
        return taskID;
    }

    public int subTaskID() {
        return subTaskID;
    }

    public int periodID() {
        return periodID;
    }

    public boolean equals(Object o) {
        SubTaskIndex s;
        try {
            s = (SubTaskIndex) o;
            return equals(s);
        } catch (ClassCastException e) {
            return false;
        }
    }

    public boolean equals(SubTaskIndex s) {
        return (taskID == s.taskID() && subTaskID == s.subTaskID() &&
                periodID == s.periodID());
    }

    public String toString() {
        return "Task ID = " + taskID + ", Period ID = " + periodID +
                ", SubTask ID = " + subTaskID;
    }

    public Object clone() {
        return new SubTaskIndex(taskID, periodID, subTaskID);
    }
}
