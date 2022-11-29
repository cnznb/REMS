package gr.uom.csse.ai.myplanner.data;

import java.io.Serializable;
import java.util.Date;
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
 * @version 1.5
 */

public class Solution implements Serializable, Cloneable, CrystalObject {

    static final long serialVersionUID = 4954149547962909671L;

    private ArrayList<TaskSolved> table;
    private Date solDate;
    private double quality = 0.0;
    private double maxQual = 0.0;
    private double percentage = 0.0;

    public Solution(TaskSolved[] solution, Date solDate) {
        table = new ArrayList<TaskSolved>();
        for (int i = 0; i < solution.length; i++) {
            table.add(solution[i]);
        }
        this.solDate = solDate;
    }

    public Solution(TaskSolved[] solution, Date solDate, double quality, double maxQual, double percentage) {
        table = new ArrayList<TaskSolved>();
        for (int i = 0; i < solution.length; i++) {
            table.add(solution[i]);
        }
        this.solDate = solDate;
        this.quality = quality;
        this.maxQual = maxQual;
        this.percentage = percentage;
    }

    public Date solutionDate() {
        return solDate;
    }

    public double quality() {
        return quality;
    }

    public double maxQuality() {
        return maxQual;
    }

    public double percentage() {
        return percentage;
    }

    public TaskSolved[] getTaskSolution(String taskName) {
        TaskSolved[] t = tasks();
        ArrayList<TaskSolved> list = new ArrayList<TaskSolved>();
        for (int i = 0; i < t.length; i++) {
            if (t[i].task().name().equals(taskName))
                list.add(t[i]);
        }
        return convertListToArray(list);
    }

    public TaskSolved[] tasks() {
        return convertListToArray(table);
    }

    public TaskSolved[] unsolvedTasks() {
        TaskSolved[] t = tasks();
        ArrayList<TaskSolved> list = new ArrayList<TaskSolved>();
        for (int i = 0; i < t.length; i++) {
            if (t[i].scheduledDuration() == 0)
                list.add(t[i]);
        }
        return convertListToArray(list);
    }

    public TaskSolved[] partialSolvedTasks() {
        TaskSolved[] t = tasks();
        ArrayList<TaskSolved> list = new ArrayList<TaskSolved>();
        for (int i = 0; i < t.length; i++) {
            if (t[i].scheduledDuration() > 0 && t[i].scheduledDuration() < t[i].totalDuration())
                list.add(t[i]);
        }
        return convertListToArray(list);
    }

    public TaskSolved[] solvedTasks() {
        TaskSolved[] t = tasks();
        ArrayList<TaskSolved> list = new ArrayList<TaskSolved>();
        for (int i = 0; i < t.length; i++) {
            if (t[i].scheduledDuration() == t[i].totalDuration())
                list.add(t[i]);
        }
        return convertListToArray(list);
    }

    private static TaskSolved[] convertListToArray(ArrayList<TaskSolved> list) {
        TaskSolved[] t = new TaskSolved[list.size()];
        for (int i = 0; i < t.length; i++) {
            t[i] = list.get(i);
        }
        return t;
    }

    public Object clone() {
        try {
            return (Solution) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public boolean equals(Object o) {
        Solution s;
        try {
            s = (Solution) o;
            return equals(s);
        } catch (ClassCastException e) {
            return false;
        }
    }

    public boolean equals(Solution s) {
        return (table.equals(s.table) && solDate.equals(s.solutionDate()) &&
                quality == s.quality() && maxQual == s.maxQuality() &&
                percentage == s.percentage());
    }

    public String toString() {
        return "Solution done in " + solDate;
    }


    public Object[] getInternalState() {
        Object[] data = new Object[5];
        data[0] = table;
        data[1] = solDate;
        data[2] = quality;
        data[3] = maxQual;
        data[4] = percentage;
        return data;
    }

    public void setInternalState(Object[] data) {
        table = (ArrayList<TaskSolved>) data[0];
        solDate = (Date) data[1];
        quality = (Double) data[2];
        maxQual = (Double) data[3];
        percentage = (Double) data[4];
    }

}
