package gr.uom.csse.ai.myplanner.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Date;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

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

public class TaskManager implements Serializable, Cloneable, CrystalObject  {

    static final long serialVersionUID = -7155141509521521466L;

    public enum Filter {NOT_COMPLETED, COMPLETED, EXPIRED, BOTH};

    private ArrayList<Task> tasks;
    private String user;
    private int maxID;
    private ArrayList<Solution> pastSolutions = null;

    public TaskManager(String user) {
        this.user = user;
        tasks = new ArrayList<Task>();
        maxID = 0;
        initPastSolutionsList();
    }

    protected void initPastSolutionsList() {
        pastSolutions = new ArrayList<Solution>();
    }

    protected ArrayList<Solution> pastSolutions() {
        return pastSolutions;
    }

    protected void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    protected void setPastSolutions(ArrayList<Solution> pastSolutions) {
        this.pastSolutions = pastSolutions;
    }

    public boolean addTask(Task task) {
        if (getTask(task.id()) != null || getTask(task.name()) != null)
            return false;
        if (!tasks.contains(task)) {
            tasks.add(task);
            maxID++;
            return true;
        }
        return false;
    }

    public boolean contains(Task task) {
        return tasks.contains(task);
    }

    public boolean replaceTask(Task task) {
        Task t = getTask(task.id());
        if (t != null)
            removeTask(t);
        return addTask(task);
    }

    protected boolean removeTask(Task task) {
        //        if (!tasks.contains(task))
        //            return false;
        //        tasks.remove(task);
        boolean b = false;
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).name().equals(task.name())) {
                tasks.remove(i);
                b = true;
            }
        }
        return b;
    }

    public Task getTask(String taskName) {
        Iterator it = tasks.iterator();
        Task t;
        while (it.hasNext()) {
            t = (Task) it.next();
            if (t.name().equals(taskName))
                return t;
        }
        return null;
    }

    public String[] groups() {
        ArrayList<String> groupNameList = new ArrayList<String>();
        Iterator it = tasks.iterator();
        Task t;
        while (it.hasNext()) {
            t = (Task) it.next();
            if (t.group() != null && t.group().length() > 1 &&
                !groupNameList.contains(t.group()))
                groupNameList.add(t.group());
        }
        Object[] o = groupNameList.toArray();
        String[] group = new String[o.length];
        for (int i = 0; i < group.length; i++) {
            group[i] = (String) o[i];
        }
        return group;
    }

    public Task[] getGroup(String groupName) {
        ArrayList<Task> groupList = new ArrayList<Task>();
        Iterator it = tasks.iterator();
        Task t;
        while (it.hasNext()) {
            t = (Task) it.next();
            if (t.group() != null && t.group().equals(groupName))
                groupList.add(t);
        }
        Object[] o = groupList.toArray();
        Task[] group = new Task[o.length];
        for (int i = 0; i < group.length; i++) {
            group[i] = (Task) o[i];
        }
        return group;
    }

    public Task[] getUngrouped() {
        ArrayList<Task> groupList = new ArrayList<Task>();
        Iterator it = tasks.iterator();
        Task t;
        while (it.hasNext()) {
            t = (Task) it.next();
            if (t.group() == null || t.group().length() < 1)
                groupList.add(t);
        }
        Object[] o = groupList.toArray();
        Task[] group = new Task[o.length];
        for (int i = 0; i < group.length; i++) {
            group[i] = (Task) o[i];
        }
        return group;
    }

    public Task getTask(int taskID) {
        Iterator it = tasks.iterator();
        Task t;
        while (it.hasNext()) {
            t = (Task) it.next();
            if (t.id() == taskID)
                return t;
        }
        return null;
    }

    public Task[] tasks() {
        Object[] o = tasks.toArray();
        Task[] t = new Task[o.length];
        for (int i = 0; i < t.length; i++) {
            t[i] = (Task) o[i];
        }
        return t;
    }

    public String user() {
        return user;
    }

    public int getMaxId() {
        return maxID;
//        Iterator it = tasks.iterator();
//        int max = 0;
//        Task t;
//        while (it.hasNext()) {
//            t = (Task) it.next();
//            if (t.id() > max)
//                max = t.id();
//        }
//        return max;
    }

    public SubTask[] subTasks() {
        int subL = 0;
        //Task[] t = expandedTasks();
        Task[] t = tasks();
        SubTask[] subTasks;
        SubTask[][] s;
        for (int i = 0; i < t.length; i++) {
            s = t[i].subTasks();
            if (s != null) {
                for (int k = 0; k < s.length; k++) {
                    subL += s[k].length;
                }
            }
        }
        if (subL == 0)
            return null;
        subTasks = new SubTask[subL];
        subL = 0;
        for (int i = 0; i < t.length; i++) {
            s = t[i].subTasks();
            if (s != null) {
                for (int k = 0; k < s.length; k++) {
                    System.arraycopy(s[k], 0, subTasks, subL, s[k].length);
                    subL += s[k].length;
                }
            }
        }
        return subTasks;
    }

    public int size() {
        return tasks.size();
    }

    public Object clone() {
        TaskManager clone;
        try {
            clone = (TaskManager)super.clone();
            ArrayList<Task> t = new ArrayList<Task>();
            Task[] orTasks = tasks();
            for (int i = 0; i < orTasks.length; i++) {
                t.add(orTasks[i]);
            }
            clone.setTasks(t);
            ArrayList<Solution> s = new ArrayList<Solution>();
            if (pastSolutions != null) {
                for (int i = 0; i < pastSolutions.size(); i++) {
                    s.add(pastSolutions.get(i));
                }
            }
            clone.setPastSolutions(s);
            return clone;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public Task[] expandTasks() {
        return expandedTasks(TimeZone.getDefault());
    }

    public Task[] expandedTasks(TimeZone tz) {
        Task[] tasks = tasks();
        ArrayList<Task> expandedTasks = new ArrayList<Task>();
        Task[] expanded;
        for (int i = 0; i < tasks.length; i++) {
            expanded = expandTask(tasks[i], tz);
            if (expanded != null) {
                for (int k = 0; k < expanded.length; k++) {
                    expandedTasks.add(expanded[k]);
                }
            }
        }
        if (expandedTasks.size() == 0)
            return null;
        Object[] o = expandedTasks.toArray();
        expanded = new Task[o.length];
        for (int i = 0; i < o.length; i++) {
            expanded[i] = (Task) o[i];
        }
        return sortTasks(expanded);
    }

    public Task[] expandTask(Task task) {
        return expandTask(task, TimeZone.getDefault());
    }

    public Task[] expandTask(Task task, TimeZone tz) {
        Task[] periods;
        ArrayList<Task> expandedList = new ArrayList<Task>();
        Date release, deadline;
        Calendar start = Calendar.getInstance(tz, Locale.ENGLISH);
        Calendar end = Calendar.getInstance(tz, Locale.ENGLISH);
        Object[] o;
        Task[] expandedTasks;
        int period = 0;
        int periodValue = 0;
        int s;
        Task t;
        int freeID = getMaxId() + 1;
        boolean so = true;
        //int oi;
        if (task.isPeriodic()) {
            if (task.periodicPrefs().isModified()) {
                start.setTime(task.domain().releaseDate());
                switch (task.periodicPrefs().period()) {
                case DAILY:
                    period = Calendar.DATE;
                    periodValue = 1;
                    break;
                case WEEKLY:
                    start.set(Calendar.DAY_OF_WEEK, 1);
                    period = Calendar.DATE;
                    periodValue = 7;
                    break;
                case MONTHLY:
                    start.set(Calendar.DAY_OF_MONTH, 1);
                    period = Calendar.MONTH;
                    periodValue = 1;
                    break;
                }
                start.set(Calendar.HOUR_OF_DAY, 0);
                start.set(Calendar.MINUTE, 0);
                start.set(Calendar.SECOND, 0);
                start.set(Calendar.MILLISECOND, 0);
                release = start.getTime();
                end.setTime(release);
                end.add(period, periodValue);
                if (Domain.getHalfHourIntervalsSize(release,
                                                    task.domain().releaseDate(), tz) >=
                    0 && task.periodicPrefs().includesFirst()) {
                    deadline = end.getTime();
                    t = task.getPeriod(task.domain().releaseDate(),
                                       deadline, freeID, so);
                    expandedList.add(t);
                    start.setTime(deadline);
                    end.add(period, periodValue);
                    s = 1;
                    freeID++;
                } else {
                    deadline = end.getTime();
                    start.setTime(deadline);
                    end.add(period, periodValue);
                    s = 0;
                }
                switch (task.periodicPrefs().range()) {
                case END_AFTER:
                    for (int k = s;
                                 k < task.periodicPrefs().recurrences();
                                 k++) {
                        release = start.getTime();
                        deadline = end.getTime();
                        t = task.getPeriod(release, deadline, freeID, so);
                        expandedList.add(t);
                        start.setTime(deadline);
                        end.add(period, periodValue);
                        s++;
                        freeID++;
                    }
                    break;
                case END_BY_DEADLINE:
                    while (end.getTime().compareTo(task.domain().
                                                   deadlineDate()) <
                           0) {
                        release = start.getTime();
                        deadline = end.getTime();
                        t = task.getPeriod(release, deadline, freeID, so);
                        expandedList.add(t);
                        start.setTime(deadline);
                        end.add(period, periodValue);
                        s++;
                        freeID++;
                    }
                    release = start.getTime();
                    if (Domain.getHalfHourIntervalsSize(release,
                            task.domain().deadlineDate(), tz) > 0 &&
                        task.periodicPrefs().includesLast()) {
                        t = task.getPeriod(release,
                                           task.domain().deadlineDate(),
                                           freeID, so);
                        expandedList.add(t);
                        s++;
                        freeID++;
                    }
                    break;
                }
                if (!task.name().equals("NEW TASK"))
                    task.periodicPrefs().setModified(false);
            } else {
                periods = task.periods();
                if (periods != null) {
                    for (int k = 0; k < periods.length; k++) {
                        expandedList.add(periods[k]);
                    }
                }
                //                    for (int k = 0; k < task.subTasks().length; k++) {
                //                        expandedList.add(task.subTasks()[k][0].partOf());
                //                    }
            }
        } else
            expandedList.add(task);
        if (expandedList.size() == 0)
            return null;
        o = expandedList.toArray();
        expandedTasks = new Task[o.length];
        for (int i = 0; i < o.length; i++) {
            expandedTasks[i] = (Task) o[i];
        }
        if (!task.name().equals("NEW TASK"))
            maxID = freeID - 1;
        return expandedTasks;
    }

    public static Task[] getPeriodicPartsOf(Task t, Task[] expandedTasks) {
        int periods = 0;
        for (int i = 0; i < expandedTasks.length; i++) {
            if (expandedTasks[i].name().equals(t.name()))
                periods++;
        }
        Task[] instances = new Task[periods];
        int k = 0;
        for (int i = 0; i < expandedTasks.length; i++) {
            if (expandedTasks[i].name().equals(t.name())) {
                instances[k] = expandedTasks[i];
                k++;
            }
        }
        return sortTasks(instances);
    }

    public Task[] filterTasks(Filter filter) {
        return filterTasks(tasks(), filter);
    }

    public static Task[] filterTasks(Task[] tasks, Filter filter) {
        Task[] filtered;
        Task[] periods;
        boolean pcomp;
        ArrayList<Integer> included = new ArrayList<Integer>();
        Date now = new Date();
        for (int i = 0; i < tasks.length; i++) {
            if ((!tasks[i].isPeriodic() && tasks[i].isCompleted() &&
                 filter == Filter.COMPLETED) ||
                (!tasks[i].isPeriodic() && !tasks[i].isCompleted() &&
                 filter == Filter.NOT_COMPLETED) ||
                (!tasks[i].isPeriodic() && tasks[i].isCompleted() &&
                 filter == Filter.BOTH) ||
                (!tasks[i].isPeriodic() &&
                 tasks[i].domain().deadlineDate().compareTo(now) < 0 &&
                 filter == Filter.EXPIRED) ||
                (!tasks[i].isPeriodic() &&
                 tasks[i].domain().deadlineDate().compareTo(now) < 0 &&
                 filter == Filter.BOTH))
                included.add(i);

            if (tasks[i].isPeriodic()) {
                periods = tasks[i].periods();
                if ((filter == Filter.NOT_COMPLETED && periods == null) ||
                    (tasks[i].domain().deadlineDate().compareTo(now) < 0 &&
                     (filter == Filter.EXPIRED || filter == Filter.BOTH)))
                    included.add(i);
                else {
                    pcomp = true;
                    for (int k = 0; k < periods.length; k++) {
                        if (!periods[k].isCompleted()) {
                            if (filter == Filter.NOT_COMPLETED)
                                included.add(i);
                            pcomp = false;
                            break;
                        }
                    }
                    if (pcomp &&
                        (filter == Filter.COMPLETED || filter == Filter.BOTH))
                        included.add(i);
                }
            }
        }
        filtered = new Task[included.size()];
        for (int i = 0; i < filtered.length; i++) {
            filtered[i] = tasks[included.get(i)];
        }
        return filtered;
    }

    public static Task[] sortTasks(Task[] tasks) {
        Task[] sorted = new Task[tasks.length];
        boolean[] ord = new boolean[tasks.length];
        int num = 0;
        int min = 0;
        for (int i = 0; i < ord.length; i++) {
            ord[i] = false;
        }
        while (num < tasks.length) {
            min = Integer.MAX_VALUE;
            for (int i = 0; i < tasks.length; i++) {
                if ((!ord[i] && min == Integer.MAX_VALUE) ||
                    (!ord[i] &&
                     tasks[i].domain().releaseDate().compareTo(tasks[min].domain().
                        releaseDate()) < 0)) {
                    min = i;
                }
            }
            ord[min] = true;
            sorted[num] = tasks[min];
            num++;
        }
        return sorted;
    }

    public Object[] getInternalState() {
        Object[] data = new Object[5];
        data[0] = tasks;
        data[1] = user;
        data[3] = maxID;
        data[4] = pastSolutions;
        return data;
    }

    public void setInternalState(Object[] data) {
        tasks = (ArrayList<Task>) data[0];
        user = (String) data[1];
        maxID = (Integer) data[3];
        pastSolutions = (ArrayList<Solution>) data[4];
    }
}
