package gr.uom.csse.ai.myplanner.data;

import java.io.Serializable;
import java.util.Arrays;

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

public class TaskSolved implements Serializable, Cloneable, CrystalObject {

    static final long serialVersionUID = 396347915335917457L;

    public enum Status { SOLVED, PARTIAL, NOT};

    private Task t;
    private Task period;
    private SubTask[][] solution;
    private int scheduled;
    private int total;

    public TaskSolved(Task t, Task period, SubTask[][]  solution, int scheduled, int total) {
        this.t = t;
        this.period = period;
        this.solution = solution;
        this.scheduled = scheduled;
        this.total = total;
    }

    public Task task() {
        return t;
    }

    public Task period() {
        return period;
    }

    public Status status() {
        if (scheduled == 0)
            return Status.NOT;
        else if (scheduled < total)
            return Status.PARTIAL;
        else
            return Status.SOLVED;
    }

    public SubTask[][] solution() {
        return solution;
    }

    public int scheduledDuration() {
        return scheduled;
    }

    public int totalDuration() {
        return total;
    }

    public Object clone() {
        return new TaskSolved(t, period, solution, scheduled, total);
    }

    public boolean equals(Object o) {
        TaskSolved s;
        try {
            s = (TaskSolved) o;
            return equals(s);
        } catch (ClassCastException e) {
            return false;
        }
    }

    public boolean equals(TaskSolved s) {
        return (t.equals(s.task()) && period.equals(s.period()) &&
                Arrays.deepEquals(solution, s.solution()) &&
                scheduled == s.scheduledDuration() &&
                total == s.totalDuration());
    }

    public String toString() {
        return "Solution for task period of  " + t.name();
    }

    public Object[] getInternalState() {
        Object[] data = new Object[5];
        data[0] = t;
        data[1] = period;
        data[2] = solution;
        data[3] = scheduled;
        data[4] = total;
        return data;
    }

    public void setInternalState(Object[] data) {
        t = (Task) data[0];
        period = (Task) data[1];
        solution = (SubTask[][]) data[2];
        scheduled = (Integer) data[3];
        total = (Integer) data[4];
    }

}
