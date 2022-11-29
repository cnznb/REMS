package gr.uom.csse.ai.myplanner.data;

import java.util.Date;
import java.util.TimeZone;
import java.util.Calendar;
import java.util.Locale;

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
* @version 1.2.1
 */

public class ManualAction extends DomainAction {

    static final long serialVersionUID = 8346147696136831037L;

    private Date startAction;
    private Date endAction;
    private Action action;

    public ManualAction(Date startAction, Date endAction, Action action) {
        super();
        this.startAction = startAction;
        this.endAction = endAction;
        this.action = action;
    }

    public Date getStartDate() {
        return startAction;
    }

    public Date getEndDate() {
        return endAction;
    }

    public Action getAction() {
        return action;
    }

    public Action getAction(Date date, TimeZone tz, ActionAffects view) {
        Calendar start = Calendar.getInstance(tz, Locale.ENGLISH);
        Calendar end = Calendar.getInstance(tz, Locale.ENGLISH);
        Calendar curDate = Calendar.getInstance(tz, Locale.ENGLISH);
        start.setTime(startAction);
        end.setTime(endAction);
        curDate.setTime(date);
        if (start.getTime().compareTo(curDate.getTime()) <= 0 &&
            curDate.getTime().compareTo(end.getTime()) <= 0)
            return action;
        else
            return Action.UNDEF;
    }

    public boolean equals(Object o) {
        ManualAction m;
        try {
            m = (ManualAction) o;
            return equals(m);
        } catch (ClassCastException e) {
            return false;
        }
    }

    public boolean equals(ManualAction m) {
        return (startAction.equals(m.getStartDate()) &&
                endAction.equals(m.getEndDate()) &&
                action.name().equals(m.getAction().name()));
    }

    public String toString() {
        String act = (action == Action.INC)? "ADD " : "REMOVE ";
        return act + "FROM " + startAction.toString() + " TO " + endAction.toString();
    }

    public Object clone() {
        return new ManualAction(new Date(startAction.getTime()),
                                new Date(endAction.getTime()), action);
    }

    public Object[] getInternalState() {
        Object[] data = new Object[3];
        data[0] = startAction;
        data[1] = endAction;
        data[2] = action;
        return data;
    }

    public void setInternalState(Object[] data) {
        startAction = (Date) data[0];
        endAction = (Date) data[1];
        action = (Action) data[2];
    }
}
