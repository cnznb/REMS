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

public class BinaryConstraint implements Serializable, Cloneable, CrystalObject {

    static final long serialVersionUID = -1746955856203134278L;

    public static enum Type {ORD, DMIN, DMAX, IMP};

    private int taskIDA;
    private int taskIDB;
    private int distance = 0;
    private Type type;
    private BinaryManager bm;

    public BinaryConstraint(int taskIDA, int taskIDB, Type type) {
        this.taskIDA = taskIDA;
        this.taskIDB = taskIDB;
        this.type = type;
    }

    public BinaryConstraint(int taskIDA, int taskIDB, int distance, Type type) {
        this.taskIDA = taskIDA;
        this.taskIDB = taskIDB;
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
        BinaryConstraint p;
        try {
            p = (BinaryConstraint) o;
            return equals(p);
        } catch (ClassCastException e) {
            return false;
        }
    }

   public boolean equals(BinaryConstraint c) {
       return (taskIDA == c.getFirstTaskID() && taskIDB == c.getSecondTaskID() &&
               distance == c.getDistance() && type.equals(c.type()));
   }

   public String toString() {
       switch (type) {
       case ORD:
           return bm.getTaskManager().getTask(taskIDA).name() + " < " +
                   bm.getTaskManager().getTask(taskIDB).name();
       case DMIN:
           return bm.getTaskManager().getTask(taskIDA).name() + " dmin " +
                   bm.getTaskManager().getTask(taskIDB).name() + " distance: " +
                   distance;
       case DMAX:
           return bm.getTaskManager().getTask(taskIDA).name() + " dmax " +
                   bm.getTaskManager().getTask(taskIDB).name() + " distance: " +
                   distance;
       case IMP:
           return bm.getTaskManager().getTask(taskIDA).name() + " -> " +
                   bm.getTaskManager().getTask(taskIDB).name();

       }
       return "INVALID CONSTRAINT";
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
       data[0] = taskIDA;
       data[1] = taskIDB;
       data[2] = distance;
       data[3] = type;
       return data;
   }

   public void setInternalState(Object[] data) {
       taskIDA = (Integer) data[0];
       taskIDB = (Integer) data[1];
       distance = (Integer) data[2];
       type = (Type) data[3];
   }
}
