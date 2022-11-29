package gr.uom.csse.ai.myplanner.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

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

public class BinaryManager implements Serializable, Cloneable, CrystalObject {

    static final long serialVersionUID = -1418831134361367761L;

    private String user;
    private ArrayList<BinaryConstraint> constraints;
    private ArrayList<BinaryPreference> preferences;
    private TaskManager tm;

    public BinaryManager(String user, TaskManager tm) {
        this.user = user;
        constraints = new ArrayList<BinaryConstraint>();
        preferences = new ArrayList<BinaryPreference>();
        this.tm = tm;
    }

    protected void setConstraints(ArrayList<BinaryConstraint> constraints) {
        this.constraints = constraints;
    }

    protected void setPreferences(ArrayList<BinaryPreference> preferences) {
        this.preferences = preferences;
    }

    protected TaskManager getTaskManager() {
        return tm;
    }

    public void setTaskManager(TaskManager newTaskManager) {
        tm = newTaskManager;
    }

    public boolean addConstraint(BinaryConstraint c) {
        if (!constraints.contains(c) &&
            constraintThatConcernsTasks(c.getFirstTaskID(), c.getSecondTaskID()) == null) {
            c.setBinaryManager(this);
            constraints.add(c);
            return true;
        } else
            return false;
    }

    public boolean addPreference(BinaryPreference p) {
        if (!preferences.contains(p) &&
            preferenceThatConcernsTasks(p.getFirstTaskID(), p.getSecondTaskID()) == null) {
            p.setBinaryManager(this);
            preferences.add(p);
            return true;
        } else
            return false;
    }

    public boolean removeContraint(BinaryConstraint c) {
        if (!constraints.contains(c))
            return false;
        constraints.remove(c);
        return true;
    }

    public boolean removePreference(BinaryPreference p) {
        if (!preferences.contains(p))
            return false;
        preferences.remove(p);
        return true;
    }

    public boolean containtsConstraint(BinaryConstraint c) {
        return (constraints.contains(c));
    }

    public boolean containtsPrefrence(BinaryPreference p) {
        return (preferences.contains(p));
    }

    public BinaryConstraint constraintThatConcernsTasks(Task a, Task b) {
        return constraintThatConcernsTasks(a.id(), b.id());
    }

    public BinaryConstraint constraintThatConcernsTasks(int taskIDA, int taskIDB) {
        Iterator it = constraints.iterator();
        BinaryConstraint c;
        while (it.hasNext()) {
            c = (BinaryConstraint) it.next();
            if (c.concerns(taskIDA, taskIDB))
                return c;
        }
        return null;
    }

    public BinaryPreference preferenceThatConcernsTasks(Task a, Task b) {
        return preferenceThatConcernsTasks(a.id(), b.id());
    }

    public BinaryPreference preferenceThatConcernsTasks(int taskIDA, int taskIDB) {
        Iterator it = preferences.iterator();
        BinaryPreference p;
        while (it.hasNext()) {
            p = (BinaryPreference) it.next();
            if (p.concerns(taskIDA, taskIDB))
                return p;
        }
        return null;
    }

    public BinaryConstraint[] constraintsThatConcernsTask(Task t) {
        return constraintsThatConcernsTask(t.id());
    }

    public BinaryConstraint[] constraintsThatConcernsTask(int taskID) {
        Iterator it = constraints.iterator();
        BinaryConstraint c;
        ArrayList<BinaryConstraint> concList = new ArrayList<BinaryConstraint>();
        while (it.hasNext()) {
            c = (BinaryConstraint) it.next();
            if (c.concerns(taskID))
                concList.add(c);
        }
        Object[] o = concList.toArray();
        BinaryConstraint[] cs = new BinaryConstraint[o.length];
        for (int i = 0; i < o.length; i++) {
            cs[i] = (BinaryConstraint) o[i];
        }
        return cs;
    }

    public BinaryPreference[] preferencesThatConcernsTask(Task t) {
        return preferencesThatConcernsTask(t.id());
    }

    public BinaryPreference[] preferencesThatConcernsTask(int taskID) {
        Iterator it = preferences.iterator();
        BinaryPreference p;
        ArrayList<BinaryPreference> prefList = new ArrayList<BinaryPreference>();
        while (it.hasNext()) {
            p = (BinaryPreference) it.next();
            if (p.concerns(taskID))
                prefList.add(p);
        }
        Object[] o = prefList.toArray();
        BinaryPreference[] ps = new BinaryPreference[o.length];
        for (int i = 0; i < o.length; i++) {
            ps[i] = (BinaryPreference) o[i];
        }
        return ps;
    }

    public BinaryConstraint[] constraints() {
        Object[] o = constraints.toArray();
        BinaryConstraint[] cs = new BinaryConstraint[o.length];
        for (int i = 0; i < cs.length; i++) {
            cs[i] = (BinaryConstraint) o[i];
        }
        return cs;
    }

    public BinaryPreference[] preferences() {
        Object[] o = preferences.toArray();
        BinaryPreference[] ps = new BinaryPreference[o.length];
        for (int i = 0; i < ps.length; i++) {
            ps[i] = (BinaryPreference) o[i];
        }
        return ps;
    }

    public BinaryConstraint[] activeConstraints() {
        BinaryConstraint[] constraints = constraints();
        BinaryConstraint[] activeConstraints;
        int active = constraints.length;
        int cnt = 0;
        for (int i = 0; i < constraints.length; i++) {
            if (tm.getTask(constraints[i].getFirstTaskID()).isCompleted() ||
                tm.getTask(constraints[i].getSecondTaskID()).isCompleted())
                active--;
        }
        activeConstraints = new BinaryConstraint[active];
        for (int i = 0; i < constraints.length; i++) {
            if (!tm.getTask(constraints[i].getFirstTaskID()).isCompleted() &&
                !tm.getTask(constraints[i].getSecondTaskID()).isCompleted()) {
                activeConstraints[cnt] = constraints[i];
                cnt++;
            }
        }
        return activeConstraints;
    }

    public BinaryPreference[] activePreferences() {
        BinaryPreference[] preferences = preferences();
        BinaryPreference[] activePreferences; ;
        int active = preferences.length;
        int cnt = 0;
        for (int i = 0; i < preferences.length; i++) {
            if (tm.getTask(preferences[i].getFirstTaskID()).isCompleted() ||
                tm.getTask(preferences[i].getSecondTaskID()).isCompleted())
                active--;
        }
        activePreferences = new BinaryPreference[active];
        for (int i = 0; i < preferences.length; i++) {
            if (!tm.getTask(preferences[i].getFirstTaskID()).isCompleted() &&
                !tm.getTask(preferences[i].getSecondTaskID()).isCompleted()) {
                activePreferences[cnt] = preferences[i];
                cnt++;
            }
        }
        return activePreferences;
    }

    public int constraintsSize() {
        return constraints.size();
    }

    public int preferencesSize() {
        return preferences.size();
    }

    public String user() {
        return user;
    }

    public Object clone() {
        BinaryManager clone;
        try {
            clone = (BinaryManager) super.clone();
            clone.setTaskManager(null);
            ArrayList<BinaryConstraint> consts = new ArrayList<BinaryConstraint>();
            ArrayList<BinaryPreference> prefs = new ArrayList<BinaryPreference>();
            BinaryConstraint[] orConsts = constraints();
            BinaryPreference[] orPrefs = preferences();
            for (int i = 0; i < orConsts.length; i++) {
                consts.add(orConsts[i]);
            }
            for (int i = 0; i < orPrefs.length; i++) {
                prefs.add(orPrefs[i]);
            }
            clone.setConstraints(consts);
            clone.setPreferences(prefs);
            return clone;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public Object[] getInternalState() {
        Object[] data = new Object[3];
        data[0] = user;
        data[1] = constraints;
        data[2] = preferences;
        return data;
    }

    public void setInternalState(Object[] data) {
        user = (String) data[0];
        constraints = (ArrayList<BinaryConstraint>) data[1];
        preferences = (ArrayList<BinaryPreference>) data[2];
    }
}
