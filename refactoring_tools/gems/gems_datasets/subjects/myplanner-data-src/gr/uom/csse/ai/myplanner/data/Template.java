package gr.uom.csse.ai.myplanner.data;

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
* @version 1.2.13
 */

public class Template extends DomainAction {

    static final long serialVersionUID = 1375960723996639627L;

    private ActionAffects affects;
    private Action[] actions;
    private int taskIND;
    private String name;
    private boolean complement = false;
    private int indexInDomain = 0;
    private Domain d;
    private Action act = null;

    public Template(ActionAffects affects, String name, int taskIND) {
        super();
        this.taskIND = taskIND;
        this.name = name;
        this.affects = affects;
        switch (affects) {
        case DAY:
            actions = new Action[48];
            break;
        case WEEK:
            actions = new Action[336];
            break;
        case MONTH:
            actions = new Action[30];
            break;
        case YEAR:
            actions = new Action[365];
            break;
        }
        for (int i = 0; i < actions.length; i++) {
            actions[i] = Action.UNDEF;
        }
    }

    public void setComplement(boolean enable) {
        if (complement != enable) {
            for (int i = 0; i < actions.length; i++) {
                if (actions[i] == Action.INC)
                    actions[i] = Action.NOT_INC;
                else if (actions[i] == Action.NOT_INC)
                    actions[i] = Action.INC;
            }
            this.complement = enable;
        }
    }

    public void setActions(Action[] actions) {
        if (this.actions.length != actions.length)
            throw new IllegalArgumentException("Array sizes do not match");
        this.actions = actions;
    }

    public boolean isComplementSet() {
        return complement;
    }

    public ActionAffects affects() {
        return affects;
    }

    public void setMultipleActions(int startIndex, Action action,
                                   int population) {
        //int pop = population;
        for (int i = startIndex; i < (startIndex + population); i++) {
            //            if (i == actions.length)
            //                break;
            //            if (actions[i] == Action.ILLEGAL)
            //                pop++;
            //            else
            actions[i] = action;
        }
    }

    public void setSingleAction(int index, Action action) {
        actions[index] = action;
    }

    public String name() {
        return name;
    }

    public boolean isBound() {
        return (taskIND > -1);
    }

    public int getTaskIndex() {
        return taskIND;
    }

    public Action[] actions() {
        return actions;
    }

    public Action getAction(int index) {
        return actions[index];
    }

    public Action getAction(Date date, TimeZone tz, ActionAffects view) {
        Calendar cal = Calendar.getInstance(tz, Locale.ENGLISH);
        cal.setTime(date);
        Action action;
        switch (affects) {
        case DAY:
            switch (view) {
            case DAY:
                action = actions[calcHHInd(cal, false)];
                if (act == action)
                    return action;
                else
                    return Action.UNDEF;
            case WEEK:
                action = actions[calcHHInd(cal, false)];
                if (act == action)
                    return action;
                else
                    return Action.UNDEF;
            case MONTH:
                return Action.PARDEF;
            case YEAR:
                return Action.PARDEF;
            case INTERVALS:
                action = actions[calcHHInd(cal, false)];
                if (act == action)
                    return action;
                else
                    return Action.UNDEF;
            }
            break;
        case WEEK:
            switch (view) {
            case DAY:
                action = actions[calcHHInd(cal, true)];
                if (act == action)
                    return action;
                else
                    return Action.UNDEF;
            case WEEK:
                action = actions[calcHHInd(cal, true)];
                if (act == action)
                    return action;
                else
                    return Action.UNDEF;
            case MONTH:
                return Action.PARDEF;
            case YEAR:
                return Action.PARDEF;
            case INTERVALS:
                action = actions[calcHHInd(cal, true)];
                if (act == action)
                    return action;
                else
                    return Action.UNDEF;
            }
            break;
        case MONTH:
            if (cal.get(Calendar.DATE) < 31)
                action = actions[cal.get(Calendar.DATE) - 1];
            else
                action = actions[29];
            if (act == action)
                return action;
            else
                return Action.UNDEF;
        case YEAR:
            action = actions[calcDMInd(cal)];
            if (act == action)
                return action;
            else
                return Action.UNDEF;
        }
        return null;
    }

    private int calcHHInd(Calendar cal, boolean weekTemplate) {
        Calendar cal2 = Calendar.getInstance(cal.getTimeZone(), Locale.ENGLISH);
        Calendar checkCal = Calendar.getInstance(cal.getTimeZone(), Locale.ENGLISH);
        cal2.setTime(cal.getTime());
        if (weekTemplate)
            cal2.set(Calendar.DAY_OF_WEEK, 1);
        cal2.set(Calendar.HOUR_OF_DAY, 0);
        cal2.set(Calendar.MINUTE, 0);
        cal2.set(Calendar.SECOND, 0);
        cal2.set(Calendar.MILLISECOND, 0);
        int i = 0;
        int subInd = 0;
        int ph;
        Date prev = cal2.getTime();
        while (i < actions.length) {
            if (cal2.getTime().compareTo(cal.getTime()) == 0)
                return i;
            cal2.add(Calendar.MINUTE, 30);
            if (weekTemplate) {
                if (i < (subInd + 47 * 7))
                    // shift index down
                    i += 7;
                else {
                    // shift index top-right
                    subInd++;
                    i = subInd;
                }
            } else
                i++;
            // daylight savings
            checkCal.setTime(prev);
            ph = checkCal.get(Calendar.HOUR_OF_DAY);
            if (checkCal.get(Calendar.MINUTE) == 30 &&
                cal2.get(Calendar.HOUR_OF_DAY) == ph)
                cal2.add(Calendar.HOUR, 1);
            else if (checkCal.get(Calendar.MINUTE) == 30 &&
                     cal2.get(Calendar.HOUR_OF_DAY) == ph + 2) {
                if (weekTemplate) {
                    for (int k = 0; k < 2; k++) {
                        if (i < (subInd + 47 * 7))
                            // shift index down
                            i += 7;
                        else {
                            // shift index top-right
                            subInd++;
                            i = subInd;
                        }
                    }
                } else
                    i += 2;
            }
            prev = cal2.getTime();
        }
        return -1;
    }

    private int calcDMInd(Calendar cal) {
        Calendar cal2 = Calendar.getInstance(cal.getTimeZone(), Locale.ENGLISH);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal2.setTime(cal.getTime());
        cal2.set(Calendar.DAY_OF_YEAR, 1);
        for (int i = 0; i < actions.length; i++) {
            if (cal2.getTime().compareTo(cal.getTime()) == 0)
                return i;
            cal2.add(Calendar.DATE, 1);
        }
        return -1;
    }

    public boolean equals(Object o) {
        Template t;
        try {
            t = (Template) o;
            return equals(t);
        } catch (ClassCastException e) {
            return false;
        }
    }

    public boolean equals(Template t) {
        if (t == null)
            return false;
        if (actions.length != t.actions().length)
            return false;
        for (int i = 0; i < actions.length; i++) {
            if (!actions[i].name().equals(t.actions()[i].name()))
                return false;
        }
        return true;
    }

    public boolean isApplied() {
        return (d != null && act != null);
    }

    protected void setDomain(Domain d) {
        this.d = d;
    }

    protected void setAction(Action act) {
        this.act = act;
    }

    protected Action getAppliedAction() {
        return act;
    }

    protected void setIndexInDomain(int index) {
        indexInDomain = index;
    }

    public String toString() {
        if (!isApplied())
            return "Template " + name;
        String compl = (complement) ? " complement" : "";
        Date[] dates = d.templateDates().get(indexInDomain);
        String actMsg = (act == Action.INC) ? "ADD" : "REMOVE";
//        if (dates == null)
//            dates = new Date[2];
        String start = (dates[0] != null) ? dates[0].toString() :
                       "Beginning of Domain";
        String end = (dates[1] != null) ? dates[1].toString() : "End of Domain";
        return "Apply: " + actMsg + " " + name + " FROM " + start +
                " TO " + end + compl;
    }

    public Object clone() {
        Template tcl = new Template(affects, name, taskIND);
        for (int i = 0; i < actions.length; i++) {
            tcl.setSingleAction(i, actions[i]);
        }
        tcl.complement = complement;
        return tcl;
    }

    public Object[] getInternalState() {
        Object[] data = new Object[8];
        data[0] = affects;
        data[1] = actions;
        data[2] = taskIND;
        data[3] = name;
        data[4] = complement;
        data[5] = indexInDomain;
        data[6] = d;
        data[7] = act;
        return data;
    }

    public void setInternalState(Object[] data) {
        affects = (ActionAffects) data[0];
        actions = (Action[]) data[1];
        taskIND = (Integer) data[2];
        name = (String) data[3];
        complement = (Boolean) data[4];
        indexInDomain = (Integer) data[5];
        d = (Domain) data[6];
        act = (Action) data[7];
    }
}
