package gr.uom.csse.ai.myplanner.data;

import java.io.Serializable;
import java.util.ArrayList;
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
 * @version 1.5.2
 */

public class MyPlannerData implements Serializable, Cloneable, CrystalObject {

    static final long serialVersionUID = -540258669493753001L;

    public static final int MAX_SOLUTIONS = 10; // you can only increase this constant, never decrease it

    private String user;
    private TaskManager tasks;
    private LocationManager locations;
    private BinaryManager constraints;
    private TemplateManager templates;
    private DomainManager domains;
    private LocationClassManager locsc;
    private Properties properties = null;
    private BinaryManager dminconstraints = null;
    private BinaryManager dmaxconstraints = null;
    private BinaryManager iconstraints = null;
    private boolean accessGoogleAccount = true;

    public MyPlannerData(String user, TaskManager tasks,
                         LocationManager locations, BinaryManager constraints,
                         TemplateManager templates, DomainManager domains,
                         LocationClassManager locsc) {
        this.user = user;
        this.tasks = tasks;
        this.locations = locations;
        this.constraints = constraints;
        this.templates = templates;
        this.domains = domains;
        this.locsc = locsc;
    }

    public MyPlannerData(String user, TaskManager tasks,
                         LocationManager locations, BinaryManager constraints,
                         TemplateManager templates, DomainManager domains,
                         LocationClassManager locsc, BinaryManager dminconstraints,
                         BinaryManager dmaxconstraints, BinaryManager iconstraints) {
        this.user = user;
        this.tasks = tasks;
        this.locations = locations;
        this.constraints = constraints;
        this.templates = templates;
        this.domains = domains;
        this.locsc = locsc;
        this.dminconstraints = dminconstraints;
        this.dmaxconstraints = dmaxconstraints;
        this.iconstraints = iconstraints;
    }

    public void setHaveAccessToGoogleAccount(boolean enabled) {
        accessGoogleAccount = enabled;
    }

    public boolean haveAccessToGoogleAccount() {
        return accessGoogleAccount;
    }

    public void addSolution(Solution sol) {
        if (tasks.pastSolutions() == null)
            tasks.initPastSolutionsList();
        if (tasks.pastSolutions().size() < MAX_SOLUTIONS)
            tasks.pastSolutions().add(sol);
        else {
            tasks.pastSolutions().remove(0);
            // shifting to the left is done by the ArrayList (doh!)
//            for (int i = 0; i < MAX_SOLUTIONS - 1; i++) {
//                tasks.pastSolutions().add(i, tasks.pastSolutions().get(i + 1));
//                tasks.pastSolutions().remove(i + 1);
//            }
//            tasks.pastSolutions().add(MAX_SOLUTIONS -1, sol);
            tasks.pastSolutions().add(sol);
        }
    }

    public void clearPastSolutions() {
        tasks.initPastSolutionsList();
    }

    public Solution[] pastSolutions() {
        if (tasks.pastSolutions() == null)
            tasks.initPastSolutionsList();
        Object[] o = tasks.pastSolutions().toArray();
        Solution[] s = new Solution[o.length];
        for (int i = 0; i < s.length; i++) {
            s[i] = (Solution) o[i];
        }
        return s;
    }

    public Solution lastSolution() {
        return tasks.pastSolutions().get(tasks.pastSolutions().size() - 1);
    }

    public int solutionsStored() {
        if (tasks.pastSolutions() == null)
            return 0;
        else
            return tasks.pastSolutions().size();
    }

    public String user() {
        return user;
    }

    public Properties properties() {
        return properties;
    }

    public TaskManager getTaskManager() {
        return tasks;
    }

    public LocationManager getLocationManager() {
        return locations;
    }

    public BinaryManager getBinaryManager() {
        return constraints;
    }

    public BinaryManager getDistMinBinaryManager() {
        if (dminconstraints == null)
            dminconstraints = new BinaryManager(user, tasks);
        return dminconstraints;
    }

    public BinaryManager getDistMaxBinaryManager() {
        if (dmaxconstraints == null)
            dmaxconstraints = new BinaryManager(user, tasks);
        return dmaxconstraints;
    }

    public BinaryManager getImpBinaryManager() {
        if (iconstraints == null)
            iconstraints = new BinaryManager(user, tasks);
        return iconstraints;
    }

    public TemplateManager getTemplateManager() {
        return templates;
    }

    public DomainManager getDomainManager() {
        return domains;
    }

    public LocationClassManager getLocationClassManager() {
        return locsc;
    }

    public void replaceTaskManager(TaskManager newManager) {
        tasks = newManager;
        constraints.setTaskManager(tasks);
        dminconstraints.setTaskManager(tasks);
        dmaxconstraints.setTaskManager(tasks);
        iconstraints.setTaskManager(tasks);
    }

    public void replaceLocationManager(LocationManager newManager) {
        locations = newManager;
    }

    public void replaceBinaryManager(BinaryManager newManager) {
        constraints = newManager;
    }

    public void replaceDistMinBinaryManager(BinaryManager newManager) {
        dminconstraints = newManager;
    }

    public void replaceDistMaxBinaryManager(BinaryManager newManager) {
        dmaxconstraints = newManager;
    }

    public void replaceImpBinaryManager(BinaryManager newManager) {
        iconstraints = newManager;
    }

    public void replaceTemplateManager(TemplateManager newManager) {
        templates = newManager;
    }

    public void replaceDomainManager(DomainManager newManager) {
        domains = newManager;
    }

    public void replaceLocationClassManager(LocationClassManager newManager) {
        locsc = newManager;
    }

    public void setProperties(Properties newProperties) {
        properties = newProperties;
    }

    public boolean removeTask(Task t) {
        boolean suc = false;
        suc = tasks.removeTask(t);
        if (!suc)
            return false;
        removeConstraints(constraints, t);
        removeConstraints(dminconstraints, t);
        removeConstraints(dmaxconstraints, t);
        removeConstraints(iconstraints, t);
        return true;
    }

    private static void removeConstraints(BinaryManager manager, Task t) {
        BinaryConstraint[] cs = manager.constraintsThatConcernsTask(t);
        BinaryPreference[] ps = manager.preferencesThatConcernsTask(t);
        for (int i = 0; i < cs.length; i++) {
            manager.removeContraint(cs[i]);
        }
        for (int i = 0; i < ps.length; i++) {
            manager.removePreference(ps[i]);
        }
    }

    public boolean deleteGroup(String groupName) {
        boolean b;
        Task[] toDelete = tasks.getGroup(groupName);
        for (int i = 0; i < toDelete.length; i++) {
            b = removeTask(toDelete[i]);
            if (!b)
                return false;
        }
        return true;
    }

    public boolean removeLocation(Location loc) {
        Task[] ts = tasks.tasks();
        for (int i = 0; i < ts.length; i++) {
            if (ts[i].locationName().equals(loc.name()))
                ts[i].setLocationName(locations.getLocationAnywhereInstance().name());
        }
        return locations.removeLocation(loc);
    }

    public boolean removeLocationClass(LocationClass locClass) {
        Task[] ts = tasks.tasks();
        for (int i = 0; i < ts.length; i++) {
            if (ts[i].locationName().equals(locClass.name()))
                ts[i].setLocationName(locations.getLocationAnywhereInstance().name());
        }
        return locsc.removeLocationClass(locClass);
    }

    public Location[] locationsOfAClass(LocationClass locClass) {
        String[] names = locClass.locations();
        Location[] locs = new Location[names.length];
        for (int i = 0; i < names.length; i++) {
            locs[i] = locations.getLocation(names[i]);
        }
        return locs;
    }

    public Location getLocation(String locName) {
        if (locName.startsWith("Class:"))
            return locsc.getLocationClass(locName);
        else
            return locations.getLocation(locName);
    }

    public Location[] locations() {
        Location[] locc = locsc.locationClasses();
        Location[] loc = locations.getLocations();
        Location[] allLocs = new Location[locc.length + loc.length];
        int i = 0;
        for (i = 0; i < locc.length; i++) {
            allLocs[i] = locc[i];
        }
        for (int k = 0; k < loc.length; k++) {
            allLocs[i + k] = loc[k];
        }
        return allLocs;
    }

    public void reloadTemplates() {
        Task[] ts = tasks.tasks();
        for (int i = 0; i < ts.length; i++) {
            ts[i].domain().reloadTemplates(templates);
        }
    }

    public Object clone() {
        MyPlannerData clone;
        try {
            clone = (MyPlannerData) super.clone();
            TaskManager tm = (TaskManager) tasks.clone();
            LocationManager lm = (LocationManager) locations.clone();
            BinaryManager bm = (BinaryManager) constraints.clone();
            BinaryManager dminm = (BinaryManager) dminconstraints.clone();
            BinaryManager dmaxm = (BinaryManager) dmaxconstraints.clone();
            BinaryManager im = (BinaryManager) iconstraints.clone();
            bm.setTaskManager(tm);
            dminm.setTaskManager(tm);
            dmaxm.setTaskManager(tm);
            im.setTaskManager(tm);
            clone.replaceTaskManager(tm);
            clone.replaceLocationManager(lm);
            clone.replaceBinaryManager(bm);
            clone.replaceDistMinBinaryManager(dminm);
            clone.replaceDistMaxBinaryManager(dmaxm);
            clone.replaceImpBinaryManager(im);
            return clone;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public Task[] expandTasks() {
        return expandTasks(TimeZone.getDefault());
    }

    public Task[] expandTasks(TimeZone tz) {
        return tasks.expandedTasks(tz);
    }

    public BinaryConstraint[] expandConstraints(Task[] expandedTasks) {
        return expandConstraints(expandedTasks, constraints.constraints());
    }

    public BinaryPreference[] expandPreferences(Task[] expandedTasks) {
        return expandPreferences(expandedTasks, constraints.preferences());
    }

    public BinaryConstraint[] expandDminConstraints(Task[] expandedTasks) {
        return expandConstraints(expandedTasks, dminconstraints.constraints());
    }

    public BinaryPreference[] expandDminPreferences(Task[] expandedTasks) {
        return expandPreferences(expandedTasks, dminconstraints.preferences());
    }

    public BinaryConstraint[] expandDmaxConstraints(Task[] expandedTasks) {
        return expandConstraints(expandedTasks, dmaxconstraints.constraints());
    }

    public BinaryPreference[] expandDmaxPreferences(Task[] expandedTasks) {
        return expandPreferences(expandedTasks, dmaxconstraints.preferences());
    }

    public BinaryConstraint[] expandImpConstraints(Task[] expandedTasks) {
        return expandConstraints(expandedTasks, iconstraints.constraints());
    }

    public BinaryPreference[] expandImpPreferences(Task[] expandedTasks) {
        return expandPreferences(expandedTasks, iconstraints.preferences());
    }

    public BinaryConstraint[] expandConstraints(Task[] expandedTasks, BinaryConstraint[] consts) {
        ArrayList<BinaryConstraint> expandedList = new ArrayList<BinaryConstraint>();
        Task t1, t2;
        Task[] periodicPartsOfT1, periodicPartsOfT2;
        Object[] o;
        BinaryConstraint[] expandedC;
        int[] com;
        int t;
        for (int i = 0; i < consts.length; i++) {
            t1 = tasks.getTask(consts[i].getFirstTaskID());
            t2 = tasks.getTask(consts[i].getSecondTaskID());
            if (t1.isPeriodic() && !t2.isPeriodic()) {
                periodicPartsOfT1 = tasks.getPeriodicPartsOf(t1, expandedTasks);
                expandedList.add(new BinaryConstraint(periodicPartsOfT1[
                                                      periodicPartsOfT1.length -
                                                      1].id(),
                                                      t2.id(),
                                                      consts[i].getDistance(),
                                                      consts[i].type()));
            } else if (t2.isPeriodic() && !t1.isPeriodic()) {
                periodicPartsOfT2 = tasks.getPeriodicPartsOf(t2, expandedTasks);
                expandedList.add(new BinaryConstraint(t1.id(),
                                                      periodicPartsOfT2[0].id(),
                                                      consts[i].getDistance(),
                                                      consts[i].type()));

            } else if (t1.isPeriodic() && t2.isPeriodic()) {
                periodicPartsOfT1 = tasks.getPeriodicPartsOf(t1, expandedTasks);
                periodicPartsOfT2 = tasks.getPeriodicPartsOf(t2, expandedTasks);
                if (!t1.periodicPrefs().period().equals(t2.periodicPrefs().
                        period()))
                    return null;
                com = firstCommonPeriod(periodicPartsOfT1, periodicPartsOfT2);
                if ((periodicPartsOfT1.length - com[0]) >=
                    (periodicPartsOfT2.length - com[1])) {
                    t = com[0];
                    for (int k = com[1]; k < periodicPartsOfT2.length; k++) {
                        expandedList.add(new BinaryConstraint(periodicPartsOfT1[
                                t].id(),
                                periodicPartsOfT2[k].id(), consts[i].getDistance(),
                                consts[i].type()));
                        t++;
                    }
                } else if ((periodicPartsOfT1.length - com[0]) <
                           (periodicPartsOfT2.length - com[1])) {
                    t = com[1];
                    for (int k = com[0]; k < periodicPartsOfT1.length; k++) {
                        expandedList.add(new BinaryConstraint(periodicPartsOfT1[
                                k].id(),
                                periodicPartsOfT2[t].id(), consts[i].getDistance(),
                                consts[i].type()));
                        t++;
                    }
                }
                if (com[0] > 0 && com[1] > 0)
                    expandedList.add(new BinaryConstraint(periodicPartsOfT1[
                            com[0] - 1].id(),
                            periodicPartsOfT2[com[1] - 1].id(),
                            consts[i].getDistance(), consts[i].type()));

            } else
                expandedList.add(consts[i]);
        }
        o = expandedList.toArray();
        expandedC = new BinaryConstraint[o.length];
        for (int i = 0; i < o.length; i++) {
            expandedC[i] = (BinaryConstraint) o[i];
        }
        return expandedC;
    }

    public BinaryPreference[] expandPreferences(Task[] expandedTasks, BinaryPreference[] prefs) {
        ArrayList<BinaryPreference> expandedList = new ArrayList<BinaryPreference>();
        Task t1, t2;
        Task[] periodicPartsOfT1, periodicPartsOfT2;
        Object[] o;
        BinaryPreference[] expandedC;
        int[] com;
        int t;
        for (int i = 0; i < prefs.length; i++) {
            t1 = tasks.getTask(prefs[i].getFirstTaskID());
            t2 = tasks.getTask(prefs[i].getSecondTaskID());
            if (t1.isPeriodic() && !t2.isPeriodic()) {
                periodicPartsOfT1 = tasks.getPeriodicPartsOf(t1, expandedTasks);
                expandedList.add(new BinaryPreference(periodicPartsOfT1[
                                                      periodicPartsOfT1.length -
                                                      1].id(),
                                                      t2.id(), prefs[i].utility(),
                                                      prefs[i].getDistance(),
                                                      prefs[i].type()));
            } else if (t2.isPeriodic() && !t1.isPeriodic()) {
                periodicPartsOfT2 = tasks.getPeriodicPartsOf(t2, expandedTasks);
                expandedList.add(new BinaryPreference(t1.id(),
                                                      periodicPartsOfT2[0].id(),
                                                      prefs[i].utility(),
                                                      prefs[i].getDistance(),
                                                      prefs[i].type()));

            } else if (t1.isPeriodic() && t2.isPeriodic()) {
                periodicPartsOfT1 = tasks.getPeriodicPartsOf(t1, expandedTasks);
                periodicPartsOfT2 = tasks.getPeriodicPartsOf(t2, expandedTasks);
                if (!t1.periodicPrefs().period().equals(t2.periodicPrefs().
                        period()))
                    return null;
                com = firstCommonPeriod(periodicPartsOfT1, periodicPartsOfT2);
                if ((periodicPartsOfT1.length - com[0]) >=
                    (periodicPartsOfT2.length - com[1])) {
                    t = com[0];
                    for (int k = com[1]; k < periodicPartsOfT2.length; k++) {
                        expandedList.add(new BinaryPreference(periodicPartsOfT1[
                                t].id(),
                                periodicPartsOfT2[k].id(), prefs[i].utility(),
                                prefs[i].getDistance(), prefs[i].type()));
                        t++;
                    }
                } else if ((periodicPartsOfT1.length - com[0]) <
                           (periodicPartsOfT2.length - com[1])) {
                    t = com[1];
                    for (int k = com[0]; k < periodicPartsOfT1.length; k++) {
                        expandedList.add(new BinaryPreference(periodicPartsOfT1[
                                k].id(),
                                periodicPartsOfT2[t].id(), prefs[i].utility(),
                                prefs[i].getDistance(), prefs[i].type()));
                        t++;
                    }
                }
                if (com[0] > 0 && com[1] > 0)
                    expandedList.add(new BinaryPreference(periodicPartsOfT1[
                            com[0] - 1].id(),
                            periodicPartsOfT2[com[1] - 1].id(), prefs[i].utility(),
                            prefs[i].getDistance(), prefs[i].type()));

            } else
                expandedList.add(prefs[i]);
        }
        o = expandedList.toArray();
        expandedC = new BinaryPreference[o.length];
        for (int i = 0; i < o.length; i++) {
            expandedC[i] = (BinaryPreference) o[i];
        }
        return expandedC;
    }

    private static int[] firstCommonPeriod(Task[] periodic1, Task[] periodic2) {
        int[] out = new int[2];
        for (int i = 0; i < periodic1.length; i++) {
            for (int k = 0; k < periodic2.length; k++) {
                if (periodic1[i].domain().releaseDate().equals(periodic2[k].domain().
                        releaseDate()) &&
                    periodic1[i].domain().deadlineDate().equals(periodic2[k].domain().
                        deadlineDate())) {
                    out[0] = i;
                    out[1] = k;
                    return out;
                }
            }
        }
        return null;
    }

    public Object[] getInternalState() {
        Object[] data = new Object[12];
        data[0] = user;
        data[1] = tasks;
        data[2] = locations;
        data[3] = constraints;
        data[4] = templates;
        data[5] = domains;
        data[6] = locsc;
        data[7] = properties;
        data[8] = dminconstraints;
        data[9] = dmaxconstraints;
        data[10] = iconstraints;
        data[11] = accessGoogleAccount;
        return data;
    }

    public void setInternalState(Object[] data) {
        user = (String) data[0];
        tasks = (TaskManager) data[1];
        locations = (LocationManager) data[2];
        constraints = (BinaryManager) data[3];
        templates = (TemplateManager) data[4];
        domains = (DomainManager) data[5];
        locsc = (LocationClassManager) data[6];
        properties = (Properties) data[7];
        dminconstraints = (BinaryManager) data[8];
        dmaxconstraints = (BinaryManager) data[9];
        iconstraints = (BinaryManager) data[10];
        accessGoogleAccount = (Boolean) data[11];
    }
}
