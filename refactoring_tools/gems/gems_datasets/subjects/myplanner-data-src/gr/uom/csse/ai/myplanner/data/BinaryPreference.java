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
 * @version 1.5
 */

public class BinaryPreference implements Serializable, Cloneable, CrystalObject {

    static final long serialVersionUID = -430001374808913930L;

    public static enum Type {ORD, DMIN, DMAX, IMP};

    private int taskIDA;
    private int taskIDB;
    private boolean away = false; // deprecated variable
    private double utility = 0.00;
    private int distance = 0;
    private Type type;
    private BinaryManager bm;

    // deprecated constructor
    public BinaryPreference(int taskIDA, int taskIDB, boolean away) {
        this.taskIDA = taskIDA;
        this.taskIDB = taskIDB;
        this.away = away;
    }

    public BinaryPreference(int taskIDA, int taskIDB, double utility, Type type) {
        this.taskIDA = taskIDA;
        this.taskIDB = taskIDB;
        this.utility = utility;
        this.type = type;
    }

    public BinaryPreference(int taskIDA, int taskIDB, double utility, int distance, Type type) {
        this.taskIDA = taskIDA;
        this.taskIDB = taskIDB;
        this.utility = utility;
        this.distance = distance;
        this.type = type;
    }


    protected void setBinaryManager(BinaryManager mgr) {
        bm = mgr;
    }

    public int getFirstTaskID() {
        return taskIDA;
    }

    public int getSecondTaskID() {
        return taskIDB;
    }

    public int getDistance() {
        return distance;
    }

    public boolean areAway() {
        return away;
    }

    public boolean areClose() {
        return !away;
    }

    public double utility() {
        return utility;
    }

    public Type type() {
        return type;
    }

    public boolean concerns(int tIDA, int tIDB) {
        return ((tIDA == taskIDA && tIDB == taskIDB) ||
                (tIDA == taskIDB && tIDB == taskIDA));
    }

    public boolean concerns(int tID) {
        return (taskIDA == tID || taskIDB == tID);
    }

    public boolean equals(Object o) {
        BinaryPreference p;
        try {
            p = (BinaryPreference) o;
            return equals(p);
        } catch (ClassCastException e) {
            return false;
        }
    }

   public boolean equals(BinaryPreference p) {
       return (taskIDA == p.getFirstTaskID() && taskIDB == p.getSecondTaskID() &&
               distance == p.getDistance() && utility == p.utility() &&
               type.equals(p.type()));
   }

   public String toString() {
       switch (type) {
       case ORD:
           return bm.getTaskManager().getTask(taskIDA).name() + " < " +
                   bm.getTaskManager().getTask(taskIDB).name() + " utility: " +
                   utility;
       case DMIN:
           return bm.getTaskManager().getTask(taskIDA).name() + " dmin " +
                   bm.getTaskManager().getTask(taskIDB).name() + " utility: " +
                   utility + " distance: " +
                   distance;
       case DMAX:
           return bm.getTaskManager().getTask(taskIDA).name() + " dmax " +
                   bm.getTaskManager().getTask(taskIDB).name() + " utility: " +
                   utility + " distance: " +
                   distance;
       case IMP:
           return bm.getTaskManager().getTask(taskIDA).name() + " -> " +
                   bm.getTaskManager().getTask(taskIDB).name() + " utility: " +
                   utility;
       }
       return "INVALID PREFERENCE";
   }

   public Object clone() {
       try {
           return super.clone();
       } catch (CloneNotSupportedException e) {
           return null;
       }
   }

   public Object[] getInternalState() {
       Object[] data = new Object[6];
       data[0] = taskIDA;
       data[1] = taskIDB;
       data[2] = away;
       data[3] = utility;
       data[4] = distance;
       data[5] = type;
       return data;
   }

   public void setInternalState(Object[] data) {
       taskIDA = (Integer) data[0];
       taskIDB = (Integer) data[1];
       away = (Boolean) data[2];
       utility = (Double) data[3];
       distance = (Integer) data[4];
       type = (Type) data[5];
   }
}
