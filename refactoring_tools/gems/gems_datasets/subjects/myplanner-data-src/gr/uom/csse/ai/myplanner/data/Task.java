package gr.uom.csse.ai.myplanner.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TimeZone;

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
 * @version 1.5
 */

public class Task implements Serializable, Cloneable, CrystalObject {

    public static boolean KEEP_SUBTASKS = false;

    static final long serialVersionUID = 8184934154943552858L;

    private int id;
    private String name;
    private int duration;
    private String locationName;
    private boolean interruptible;
    private int minDur;
    private int maxDur;
    private int minDis;
    private Domain domain;
    private DomainPrefs prefs;
    private SubTask[][] subTasks;
    private int partsDone;
    private int totalDur; // task max duration
    private PeriodicPrefs periodic;
    private SubTask[][] subTaskHistory;
    private int taskEditedFromPlanner;
    private boolean omitted;
    private HashMap<Date, Task> periods = null;
    private String calendar = null;
    private String description = null;
    private String group = null;
    private double utility = 1.0;
    private double utilization = 1.0;
    private int totalDurMin = 0; // task min duration
    private int currentTotalDur = 0; // task current duration ( tmind >= tcd >= tmaxd)
    private int maxDis = Integer.MAX_VALUE;
    private double durUtil = 0.0;
    private int prefMinDis = 0;
    private double prefMinDisUtil = 0.0;
    private int prefMaxDis = Integer.MAX_VALUE;
    private double prefMaxDisUtil = 0.0;
    private int durMin = 0;
    private int remainingDur = 0;

    // deprecated constructor
    public Task(int id, String name, int duration, String locName, Domain domain, DomainPrefs prefs, PeriodicPrefs pprefs) {
        this.id = id;
        this.name = name;
        this.duration = duration;
        this.locationName = locName;
        this.domain = domain;
        this.prefs = prefs;
        periodic = pprefs;
        totalDur = duration;
        totalDurMin = duration;
        currentTotalDur = duration;
        remainingDur = duration;
        partsDone = 0;
        interruptible = false;
        taskEditedFromPlanner = 0;
        omitted = false;
        description = "";
        group = "";
    }

     // deprecated constructor
    public Task(int id, String name, int duration, String locName, Domain domain, DomainPrefs prefs, PeriodicPrefs pprefs, int minDur, int maxDur, int minDis) {
        this.id = id;
        this.name = name;
        this.duration = duration;
        this.locationName = locName;
        this.domain = domain;
        this.prefs = prefs;
        periodic = pprefs;
        totalDur = duration;
        totalDurMin = duration;
        currentTotalDur = duration;
        remainingDur = duration;
        partsDone = 0;
        this.minDur = minDur;
        this.maxDur = maxDur;
        this.minDis = minDis;
        maxDis = 0;
        interruptible = true;
        taskEditedFromPlanner = 0;
        omitted = false;
        description = "";
        group = "";
    }

    public Task(int id, String name, int durationMin, int durationMax, double durUtil, String locName, Domain domain, DomainPrefs prefs, PeriodicPrefs pprefs, double utility, double utilization) {
       this.id = id;
       this.name = name;
       this.duration = durationMax;
       durMin = durationMin;
       this.durUtil = durUtil;
       this.locationName = locName;
       this.domain = domain;
       this.prefs = prefs;
       periodic = pprefs;
       totalDur = durationMax;
       totalDurMin = durationMin;
       currentTotalDur = durationMax;
       remainingDur = durationMax;
       partsDone = 0;
       interruptible = false;
       taskEditedFromPlanner = 0;
       omitted = false;
       description = "";
       group = "";
       this.utility = utility;
       this.utilization = utilization;
   }

   public Task(int id, String name, int durationMin, int durationMax, double durUtil, String locName, Domain domain, DomainPrefs prefs, PeriodicPrefs pprefs, double utility, double utilization, int minDur, int maxDur, int minDis, int maxDis) {
       this.id = id;
       this.name = name;
       this.duration = durationMax;
       durMin = durationMin;
       this.durUtil = durUtil;
       this.locationName = locName;
       this.domain = domain;
       this.prefs = prefs;
       periodic = pprefs;
       totalDur = durationMax;
       totalDurMin = durationMin;
       currentTotalDur = durationMax;
       remainingDur = durationMax;
       partsDone = 0;
       this.minDur = minDur;
       this.maxDur = maxDur;
       this.minDis = minDis;
       this.maxDis = maxDis;
       interruptible = true;
       taskEditedFromPlanner = 0;
       omitted = false;
       description = "";
       group = "";
       this.utility = utility;
       this.utilization = utilization;
   }


    protected void setOmitted(boolean omitted) {
        this.omitted = omitted;
    }

    public boolean isOmitted() {
        return omitted;
    }

    public void setPartsDone(int num) {
        partsDone = num;
    }

    public int partsDone() {
        return partsDone;
    }

    public boolean isCompleted() {
        Task[] allPer;
        if (isPeriodic()) {
            allPer = periods();
            if (allPer == null)
                return false;
            for (int i = 0; i < allPer.length; i++) {
                if (!allPer[i].isCompleted())
                    return false;
            }
            return true;
        } else return (currentTotalDur == 0) ? (duration == 0) :
                ((totalDur - currentTotalDur) == duration);
    }

    public int timesTaskEditedFromPlanner() {
        return taskEditedFromPlanner;
    }

    public void setCalendar(String calendar) {
        this.calendar = calendar;
    }

    public String calendar() {
        return calendar;
    }

    public void setUtility(double newValue) {
        utility = newValue;
    }

    public void setUtilization(double newValue) {
        utilization = newValue;
    }

    public double utility() {
        return utility;
    }

    public double utilization() {
        return utilization;
    }

    public void setDescription(String description) {
        Task[] allPer;
        this.description = description;
        if (isPeriodic()) {
            allPer = allPeriods();
            if (allPer == null)
                return;
            for (int i = 0; i < allPer.length; i++) {
                allPer[i].description = description;
            }
        }
    }

    public void setGroup(String group) {
        Task[] allPer;
        this.group = group;
        if (isPeriodic()) {
            allPer = allPeriods();
            if (allPer == null)
                return;
            for (int i = 0; i < allPer.length; i++) {
                allPer[i].group = group;
            }
        }
    }

    public String description() {
        return description;
    }

    public String group() {
        return group;
    }

    public int[][] getDomainIntervals(Date fromDate, TimeZone tz) {
        int[][] intervals;
        boolean lookForEnd;
        int i;
        int dur = interruptible ? minDur / 30 : duration / 30;
        ArrayList<Integer> beg = new ArrayList<Integer>();
        ArrayList<Integer> fin = new ArrayList<Integer>();
        DomainAction.Action[] domainActs = domain.actionsAt(fromDate, tz,
                DomainAction.ActionAffects.INTERVALS);
        lookForEnd = false;
        for (i = 0; i < domainActs.length; i++) {
            if (!lookForEnd &&
                (domainActs[i] == DomainAction.Action.INC ||
                 domainActs[i] == DomainAction.Action.UNDEF)) {
                lookForEnd = true;
                beg.add(i);
            } else if (lookForEnd &&
                       (domainActs[i] == DomainAction.Action.NOT_INC ||
                        domainActs[i] == DomainAction.Action.OUT_OF_DOMAIN)) {
                lookForEnd = false;
                if (((i - 1) - beg.get(beg.size() - 1)) >= (dur - 1))
                    fin.add(i);
                else
                    beg.remove(beg.size() - 1);
            }
        }
        if (beg.size() == 0) return null;
        if (((i - 1) - beg.get(beg.size() - 1)) >= (dur - 1))
            fin.add(i);
        else
            beg.remove(beg.size() - 1);
        intervals = new int[beg.size()][2];
        for (i = 0; i < beg.size(); i++) {
            intervals[i][0] = beg.get(i);
            intervals[i][1] = fin.get(i);
        }
        return intervals;
    }

    public int id() {
        return id;
    }

    public String name() {
        return name;
    }

    public void setName(String newName) {
        Task[] allPer;
        name = newName;
        if (isPeriodic()) {
            allPer = allPeriods();
            if (allPer == null)
                return;
            for (int i = 0; i < allPer.length; i++) {
                allPer[i].name = newName;
            }
        }
    }

    public void setProxPreference(int min, double minUtil, int max, double maxUtil) {
        prefMinDis = min;
        prefMinDisUtil = minUtil;
        prefMaxDis = max;
        prefMaxDisUtil = maxUtil;
    }

    public int getProxPreferenceMinDis() {
        return prefMinDis;
    }

    public double getProxPreferenceMinDisUtil() {
        return prefMinDisUtil;
    }

    public int getProxPreferenceMaxDis() {
        return prefMaxDis;
    }

    public double getProxPreferenceMaxDisUtil() {
        return prefMaxDisUtil;
    }

    public int duration() {
        return duration;
    }

    public int durationMin() {
        return durMin;
    }

    public void setDurationUtil(double util) {
        durUtil = util;
    }

    public double durationUtil() {
        return durUtil;
    }

    public int totalDuration() {
        return (currentTotalDur == 0)? totalDur : currentTotalDur;
    }

    public int remainingDuration() {
        return remainingDur;
    }

    public int totalDurationMin() {
        return totalDurMin;
    }

    public int totalDurationMax() {
        return totalDur;
    }

    // set per period
    public void setCurrentTotalDuration(int newCurDur) {
//        Task[] allPer;
        if (durMin + (newCurDur - currentTotalDur) < 0) {
            if (durMin == duration)
                duration = 0;
            else
                duration += (newCurDur - currentTotalDur);
            durMin = 0;
        }
        else {
            durMin += (newCurDur - currentTotalDur);
            duration += (newCurDur - currentTotalDur);
        }
        currentTotalDur = newCurDur;
        remainingDur = newCurDur;
//        if (isPeriodic()) {
//            allPer = allPeriods();
//            if (allPer == null)
//                return;
//            for (int i = 0; i < allPer.length; i++) {
//                allPer[i].duration += (newCurDur- allPer[i].currentTotalDur);
//                allPer[i].currentTotalDur = newCurDur;
//            }
//        }
    }

    public void setDuration(int newDuration) {
        throw new UnsupportedOperationException("Deprecated, use setDuration(min,max)");
    }

    public void setInitialDuration(int newDurationMin, int newDurationMax) {
        Task[] allPer;
        if (newDurationMax < newDurationMin)
            throw new IllegalArgumentException();
        //        if (duration + (newDurationMax - totalDur) < 0)
        //            throw new IllegalArgumentException();
        totalDur = newDurationMax;
        totalDurMin = newDurationMin;
        duration = newDurationMax;
        durMin = newDurationMin;
        currentTotalDur = newDurationMax;
        remainingDur = newDurationMax;
        if (isPeriodic()) {
            allPer = allPeriods();
            if (allPer == null)
                return;
            for (int i = 0; i < allPer.length; i++) {
                allPer[i].totalDur = newDurationMax;
                allPer[i].totalDurMin = newDurationMin;
                allPer[i].duration = newDurationMax;
                allPer[i].durMin = newDurationMin;
                allPer[i].currentTotalDur = newDurationMax;
                allPer[i].remainingDur = newDurationMax;
            }
        }
    }

    public void setDuration(int newDurationMin, int newDurationMax) {
        Task[] allPer;
        if (newDurationMax < newDurationMin)
            throw new IllegalArgumentException();
//        if (duration + (newDurationMax - totalDur) < 0)
//            throw new IllegalArgumentException();
        totalDur = newDurationMax;
        totalDurMin = newDurationMin;
        if (isPeriodic()) {
            allPer = allPeriods();
            if (allPer == null)
                return;
            for (int i = 0; i < allPer.length; i++) {
                allPer[i].totalDur = newDurationMax;
                allPer[i].totalDurMin = newDurationMin;
            }
        }
    }

    public void alterRemainingDuration(int alterValue) {
        if (durMin + alterValue >= 0) {
            durMin += alterValue;
            duration += alterValue;
            remainingDur += alterValue;
        } else if (duration == durMin) {
            durMin = 0;
            duration = 0;
            remainingDur = 0;
        } else {
            durMin = 0;
            duration += alterValue;
            remainingDur += alterValue;
        }
    }

    public boolean isPeriodic() {
        return (periodic != null);
    }

    public PeriodicPrefs periodicPrefs() {
        return periodic;
    }

    public void setPeriodicPrefs(PeriodicPrefs pprefs) {
        periodic = pprefs;
        if (pprefs == null || !pprefs.period().name().equals(periodic.period().name())) {
            subTasks = null;
            periods = null;
        }
    }

    public String locationName() {
        return locationName;
    }

    public void setLocationName(String newLocName) {
        Task[] allPer;
        locationName = newLocName;
        if (isPeriodic()) {
            allPer = allPeriods();
            if (allPer == null)
                return;
            for (int i = 0; i < allPer.length; i++) {
                allPer[i].locationName = newLocName;
            }
        }

    }

    public boolean isInterruptible() {
        return interruptible;
    }

    public void setInterruptible(boolean interruptible) {
        Task[] allPer;
        this.interruptible = interruptible;
        if (isPeriodic()) {
            allPer = allPeriods();
            if (allPer == null)
                return;
            for (int i = 0; i < allPer.length; i++) {
                allPer[i].interruptible = interruptible;
            }
        }

    }

    public int minDuration() {
        return minDur;
    }

    public void setMinDuration(int dur) {
        Task[] allPer;
        minDur = dur;
        if (isPeriodic()) {
            allPer = allPeriods();
            if (allPer == null)
                return;
            for (int i = 0; i < allPer.length; i++) {
                allPer[i].minDur = dur;
            }
        }

    }

    public int maxDuration() {
        return maxDur;
    }

    public void setMaxDuration(int dur) {
        Task[] allPer;
        maxDur = dur;
        if (isPeriodic()) {
            allPer = allPeriods();
            if (allPer == null)
                return;
            for (int i = 0; i < allPer.length; i++) {
                allPer[i].maxDur = dur;
            }
        }

    }

    public int minDistance() {
        return minDis;
    }

    public int maxDistance() {
        return (maxDis == 0)? Integer.MAX_VALUE : maxDis;
    }

    public void setMinDistance(int dis) {
        Task[] allPer;
        minDis = dis;
        if (isPeriodic()) {
            allPer = allPeriods();
            if (allPer == null)
                return;
            for (int i = 0; i < allPer.length; i++) {
                allPer[i].minDis = dis;
            }
        }
    }

    public void setMaxDistance(int dis) {
        Task[] allPer;
        maxDis = dis;
        if (isPeriodic()) {
            allPer = allPeriods();
            if (allPer == null)
                return;
            for (int i = 0; i < allPer.length; i++) {
                allPer[i].maxDis = dis;
            }
        }
    }
    public void setDomain(Domain domain) {
        Task[] allPer;
        Date release, deadline;
        this.domain = domain;
        if (isPeriodic()) {
            allPer = allPeriods();
            if (allPer == null)
                return;
            for (int i = 0; i < allPer.length; i++) {
                release = allPer[i].domain().releaseDate();
                deadline = allPer[i].domain().deadlineDate();
                allPer[i].domain = (Domain) domain.clone();
                allPer[i].domain().setReleaseDate(release);
                allPer[i].domain().setDeadlineDate(deadline);
            }
        }
    }

    public void setDomainPrefs(DomainPrefs prefs) {
        Task[] allPer;
        this.prefs = prefs;
        if (isPeriodic()) {
            allPer = allPeriods();
            if (allPer == null)
                return;
            for (int i = 0; i < allPer.length; i++) {
                allPer[i].prefs = (DomainPrefs) prefs.clone();
            }
        }

    }

    public int getStepPoint(Date fromDate, TimeZone tz) {
        if (prefs.pref() == DomainPrefs.Pref.STEP_ASC ||
            prefs.pref() == DomainPrefs.Pref.STEP_DESC) {
            if (prefs.stepPoint().compareTo(fromDate) < 0)
                return 0;
            else if (domain.deadlineDate().compareTo(prefs.stepPoint()) < 0)
                return domain.getHalfHourIntervalsSize(fromDate,
                        domain.deadlineDate(), tz);
            return domain.getHalfHourIntervalsSize(fromDate,
                                                   prefs.stepPoint(), tz);
        } else
            return -1;
    }

    public Domain domain() {
        return domain;
    }

    public DomainPrefs domainPrefs() {
        return prefs;
    }

    public void setSubTasks(SubTask[][] subTasks) {
        if (KEEP_SUBTASKS) {
            if ((taskEditedFromPlanner > 0 && subTaskHistory == null) ||
                (subTaskHistory.length != subTasks.length)) {
                this.subTaskHistory = this.subTasks;
            } else if (taskEditedFromPlanner > 0) {
                SubTask[][] newSubTasks = new SubTask[subTaskHistory.length][];
                for (int i = 0; i < subTaskHistory.length; i++) {
                    newSubTasks[i] = new SubTask[subTaskHistory[i].length +
                                     subTasks[i].length];
                    System.arraycopy(subTaskHistory[i], 0, newSubTasks[i], 0,
                                     subTaskHistory[i].length);
                    System.arraycopy(subTasks[i], 0, newSubTasks[i],
                                     subTaskHistory[i].length,
                                     subTasks[i].length);
                }
                this.subTaskHistory = newSubTasks;
            }
        }
        this.subTasks = subTasks;
        taskEditedFromPlanner++;
    }

    public Task[] periods() {
        if (!isPeriodic())
            return null;
        if (periods == null)
            return null;
        Object[] o = periods.values().toArray();
        ArrayList<Task> activePeriods = new ArrayList<Task>();
        int i;
        Task period;
        for (i = 0; i < o.length; i++) {
            period = (Task) o[i];
            if ((period.domain().releaseDate().compareTo(domain.releaseDate()) > 0
                 &&
                 period.domain().deadlineDate().compareTo(domain.deadlineDate()) <
                 0) ||
                (period.domain().releaseDate().compareTo(domain.releaseDate()) == 0 &&
                 periodic.includesFirst())
                ||
                (period.domain().deadlineDate().compareTo(domain.deadlineDate()) ==
                 0 &&
                 periodic.includesLast())) {
                activePeriods.add(period);
            }
        }
        if (activePeriods.size() == 0)
            return null;
        Task[] t = new Task[activePeriods.size()];
        Iterator it = activePeriods.iterator();
        i = 0;
        while (it.hasNext()) {
            t[i] = (Task) it.next();
            i++;
        }
        return t;
    }

    protected Task[] allPeriods() {
        if (!isPeriodic())
            return null;
        if (periods == null)
            return null;
        Object[] o = periods.values().toArray();
        Task[] t = new Task[o.length];
        for (int i = 0; i < o.length; i++) {
            t[i] = (Task) o[i];
        }
        return t;
    }

    public Task getPeriod(Date startDate) {
        if (!isPeriodic())
            return this;
        if (periods == null)
            return null;
        return periods.get(startDate);
    }

    public Task getPeriod(Date startDate, Date endDate) {
        if (!isPeriodic())
            return this;
        Task per = getPeriod(startDate);
        if (per != null && per.domain().deadlineDate().equals(endDate))
            return per;
        else
            return null;
//        Task period;
//        for (int i = 0; i < subTasks.length; i++) {
//            period = subTasks[i][0].partOf();
//            if (period.domain().releaseDate().equals(startDate) &&
//                period.domain().deadlineDate().equals(endDate))
//                return period;
//        }
//        return null;
    }

    public int getPeriodID(Task t) {
        if (periods == null || periods.size() == 0)
            return -1;
//        if (!isPeriodic())
//            return -1;
        if (!name.equals(t.name()))
            return -1;
        if (id == t.id())
            return -1;
        Task period;
        int i = 0;
        Iterator it = periods.values().iterator();
        while (it.hasNext()) {
            period = (Task) it.next();
            if (period.equals(t))
                return i;
            i++;
        }
        return -1;
    }

    public Task getPeriod(int periodID) {
        if (periods == null || periods.size() == 0)
            return null;
//        if (periodID >= subTasks.length)
//            return null;
        return (Task) periods.values().toArray()[periodID];
    }

    public SubTask[][] subTasks() {
        return subTasks;
    }

    public SubTask[][] subTaskHistory() {
        return subTaskHistory;
    }

    public int getSubTaskIndex(SubTask s) {
        for (int i = 0; i < subTasks.length; i++) {
            for (int k = 0; k < subTasks[i].length; k++) {
                if (subTasks[i][k] != null && subTasks[i][k].equals(s))
                    return k;
            }
        }
        return -1;
    }

    public boolean equals(Object o) {
        Task t;
        try {
            t = (Task) o;
            return equals(t);
        } catch (ClassCastException e) {
            return false;
        }
    }

    public boolean equals(Task t) {
        if ((interruptible && !t.isInterruptible()) ||
            (!interruptible && t.isInterruptible()))
            return false;
        boolean b, c;
        if (interruptible)
            b = (id == t.id() && name == t.name() &&
                 locationName.equals(t.locationName()) &&
                 domain.equals(t.domain()) && prefs.equals(t.domainPrefs()) &&
                 minDur == t.minDuration() && maxDur == t.maxDuration() &&
                 minDis == t.minDistance() && maxDis == t.maxDistance());
        else
            b = (id == t.id() && name == t.name() &&
                 locationName.equals(t.locationName()) &&
                 domain.equals(t.domain()) && prefs.equals(t.domainPrefs()));
        c = (utility == t.utility() && utilization == t.utilization() &&
             totalDur == t.totalDurationMax() && totalDurMin == t.totalDurationMin() &&
             currentTotalDur == t.totalDuration() && durUtil == t.durationUtil() &&
             prefMinDis == t.getProxPreferenceMinDis() &&
             prefMinDisUtil == t.getProxPreferenceMinDisUtil() &&
             prefMaxDis == t.getProxPreferenceMaxDis() &&
             prefMaxDisUtil == t.getProxPreferenceMaxDisUtil() &&
             duration == t.duration() &&
             durMin == t.durationMin() && remainingDur == t.remainingDuration());
        if (isPeriodic() && t.isPeriodic())
            return (b && c && periodic.equals(t.periodicPrefs()));
        else if (!isPeriodic() && !t.isPeriodic())
            return b && c;
        else
            return false;
    }

    public String toString() {
        String per = (periodic != null)? "Periodic " : "";
        return per + "Task: " + name;
    }

    public Object clone() {
        Task t;
        try {
            t = (Task) super.clone();
            if (domain != null)
                t.setDomain((Domain) domain.clone());
            if (prefs != null)
                t.setDomainPrefs((DomainPrefs) prefs.clone());
            if (isPeriodic())
                t.setPeriodicPrefs((PeriodicPrefs) periodic.clone());
            if (periods != null)
                t.periods = (HashMap<Date, Task>) periods.clone();
            return t;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public void copyPeriodicListFrom(Task t) {
        periods = t.periods;
    }

    public void copyPeriodicListTo(Task t) {
        t.periods = periods;
    }

    protected Task getPeriod(Date release, Date deadline,int periodID, boolean searchForOld) {
        if (!isPeriodic())
            throw new UnsupportedOperationException();
        Task period;
        if (searchForOld) {
            if (release.equals(domain.releaseDate())) { // request the first period
                period = getPeriodByDeadline(deadline);
                if (period != null)
                    period.domain().setReleaseDate(release);
            } else if (deadline.equals(domain.deadlineDate())) { // request the last period
                period = getPeriod(release);
                if (period != null)
                    period.domain().setDeadlineDate(deadline);
            } else {
                period = getPeriod(release); // request a middle period
                if (period != null && !deadline.equals(period.domain().deadlineDate()))
                    period.domain().setDeadlineDate(deadline);
            }
            if (period == null) {
                period = getPeriodByDeadline(deadline);
                if (period != null)
                    period.domain().setReleaseDate(release);
            }
            if (period != null) {
                if (periodic.isExcluded(release))
                    period.setOmitted(true);
                else
                    period.setOmitted(false);
                return period;
            }
        }
        return clonePeriod(release, deadline, periodID);
    }

    private Task clonePeriod(Date release, Date deadline, int periodID) {
        Task period = (Task) clone();
        period.setPeriodicPrefs(null);
        period.domain().setReleaseDate(release);
        period.domain().setDeadlineDate(deadline);
        period.id = periodID;
        if (periodic.isExcluded(release))
            period.setOmitted(true);
        else
            period.setOmitted(false);
        if (!name.equals("NEW TASK")) {
            if (periods == null)
                periods = new HashMap<Date, Task>();
            periods.put(release, period);
        }
        return period;
    }

    protected Task getPeriodByDeadline(Date deadline) {
        Task[] allPer = allPeriods();
        if (allPer == null)
            return null;
        for (int i = 0; i < allPer.length; i++) {
            if (allPer[i].domain().deadlineDate().equals(deadline))
                return allPer[i];
        }
        return null;
    }

    public Object[] getInternalState() {
        Object[] data = new Object[33];
        data[0] = id;
        data[1] = name;
        data[2] = duration;
        data[3] = locationName;
        data[4] = interruptible;
        data[5] = minDur;
        data[6] = maxDur;
        data[7] = minDis;
        data[8] = domain;
        data[9] = prefs;
        data[10] = subTasks;
        data[11] = partsDone;
        data[12] = totalDur;
        data[13] = periodic;
        data[14] = subTaskHistory;
        data[15] = taskEditedFromPlanner;
        data[16] = omitted;
        data[17] = periods;
        data[18] = calendar;
        data[19] = description;
        data[20] = group;
        data[21] = utility;
        data[22] = utilization;
        data[23] = totalDurMin;
        data[24] = currentTotalDur;
        data[25] = maxDis;
        data[26] = durUtil;
        data[27] = prefMinDis;
        data[28] = prefMinDisUtil;
        data[29] = prefMaxDis;
        data[30] = prefMaxDisUtil;
        data[31] = durMin;
        data[32] = remainingDur;
        return data;
    }

    public void setInternalState(Object[] data) {
        id = (Integer) data[0];
        name = (String) data[1];
        duration = (Integer) data[2];
        locationName = (String) data[3];
        interruptible = (Boolean) data[4];
        minDur = (Integer) data[5];
        maxDur = (Integer) data[6];
        minDis = (Integer) data[7];
        domain = (Domain) data[8];
        prefs = (DomainPrefs) data[9];
        subTasks = (SubTask[][]) data[10];
        partsDone = (Integer) data[11];
        totalDur = (Integer) data[12];
        if (data[13] != null)
            periodic = (PeriodicPrefs) data[13];
        subTaskHistory = (SubTask[][]) data[14];
        taskEditedFromPlanner = (Integer) data[15];
        omitted = (Boolean) data[16];
        periods = (HashMap<Date, Task>) data[17];
        if (data[18] != null)
            calendar = (String) data[18];
        description = (String) data[19];
        group = (String) data[20];
        utility = (Double) data[21];
        utilization = (Double) data[22];
        totalDurMin = (Integer) data[23];
        currentTotalDur = (Integer) data[24];
        maxDis = (Integer) data[25];
        durUtil = (Double) data[26];
        prefMinDis = (Integer) data[27];
        prefMinDisUtil = (Double) data[28];
        prefMaxDis = (Integer) data[29];
        prefMaxDisUtil = (Double) data[30];
        durMin = (Integer) data[31];
        remainingDur = (Integer) data[32];
    }
}
