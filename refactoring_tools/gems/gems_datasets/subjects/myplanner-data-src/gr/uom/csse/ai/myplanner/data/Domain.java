package gr.uom.csse.ai.myplanner.data;

import java.io.Serializable;
import java.util.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.ArrayList;
import java.util.Iterator;
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
* @version 1.2.16
 */

public class Domain implements Serializable, Cloneable, CrystalObject {

    static final long serialVersionUID = 1770177127919145487L;

    private Date release;
    private Date deadline;
    private HashMap<Integer, DomainAction> actions;
    private HashMap<Integer, Date[]> templateDates;
    private int curInd;
    private String name;
    private transient ArrayList<DomainChangeListener> listeners = null;

    public Domain(Date release, Date deadline) {
        name = "Unnamed Domain";
        this.release = release;
        this.deadline = deadline;
        actions = new HashMap<Integer, DomainAction>();
        templateDates = new HashMap<Integer, Date[]>();
        curInd = 0;
    }

    public String name() {
        return name;
    }

    public void setName(String newName) {
        name = newName;
    }

    public void addDomainChangeListener(DomainChangeListener listener) {
        if (listeners == null)
            listeners = new ArrayList<DomainChangeListener>();
        if (!listeners.contains(listener))
            listeners.add(listener);
    }

    public void removeDomainChangeListener(DomainChangeListener listener) {
        if (listeners != null && listeners.contains(listener))
            listeners.remove(listener);
    }

    public void removeAllListeners() {
        listeners = null;
    }

    private void fireDomainChangeEvent() {
        if (listeners == null)
            return;
        Iterator it = listeners.iterator();
        DomainChangeListener listener;
        while (it.hasNext()) {
            listener = (DomainChangeListener) it.next();
            listener.domainChanged();
        }
    }

    public static int getHalfHourIntervalsSize(Date startDate, Date endDate, TimeZone tz) {
        int hfi = 0;
        int ph;
        Calendar start = Calendar.getInstance(tz, Locale.ENGLISH);
        start.setTime(startDate);
        Calendar end = Calendar.getInstance(tz, Locale.ENGLISH);
        end.setTime(endDate);
        Calendar temp = Calendar.getInstance(tz, Locale.ENGLISH);
        temp.setTime(startDate);
        Calendar checkCal = Calendar.getInstance(tz, Locale.ENGLISH);
        Date prev = temp.getTime();
        while (temp.getTime().compareTo(end.getTime()) < 0) {
            hfi++;
            temp.add(Calendar.MINUTE, 30);
            // daylight savings
            checkCal.setTime(prev);
            ph = checkCal.get(Calendar.HOUR_OF_DAY);
            if (checkCal.get(Calendar.MINUTE) == 30 &&
                temp.get(Calendar.HOUR_OF_DAY) == ph)
                temp.add(Calendar.HOUR, 1);
            else if (checkCal.get(Calendar.MINUTE) == 30 &&
                     temp.get(Calendar.HOUR_OF_DAY) == ph + 2)
                hfi += 2;

            prev = temp.getTime();
        }
        return hfi;
    }

    public Date[] getIntervalTimes(Date fromDate, TimeZone tz, int intBeg, int intEnd) {
        return getIntervalTimes(fromDate, deadline, tz, intBeg, intEnd);
    }

    public static Date[] getIntervalTimes(Date fromDate, Date endDate, TimeZone tz, int intBeg, int intEnd) {
        Date[] dates = new Date[2];
        int ph;
        Calendar start = Calendar.getInstance(tz, Locale.ENGLISH);
        Calendar end = Calendar.getInstance(tz, Locale.ENGLISH);
        Calendar temp = Calendar.getInstance(tz, Locale.ENGLISH);
        Calendar checkCal = Calendar.getInstance(tz, Locale.ENGLISH);
        start.setTime(fromDate);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);
        if (start.get(Calendar.MINUTE) > 30)
            start.add(Calendar.MINUTE, 60 - start.get(Calendar.MINUTE));
        else if (start.get(Calendar.MINUTE) > 0 && start.get(Calendar.MINUTE) < 30)
            start.add(Calendar.MINUTE, 30 - start.get(Calendar.MINUTE));
        end.setTime(endDate);
        end.set(Calendar.SECOND, 0);
        end.set(Calendar.MILLISECOND, 0);
        if (end.get(Calendar.MINUTE) > 30)
            end.add(Calendar.MINUTE, 60 - end.get(Calendar.MINUTE));
        else if (end.get(Calendar.MINUTE) > 0 && end.get(Calendar.MINUTE) < 30)
            end.add(Calendar.MINUTE, 30 - end.get(Calendar.MINUTE));
        temp.setTime(start.getTime());
        Date prev = temp.getTime();
        int i = 0;
        while (temp.getTime().compareTo(end.getTime()) <= 0) {
            if (i == intBeg)
                dates[0] = temp.getTime();
            if (i == intEnd) {
                dates[1] = temp.getTime();
                return dates;
            }
            i++;
            temp.add(Calendar.MINUTE, 30);
            // daylight savings
            checkCal.setTime(prev);
            ph = checkCal.get(Calendar.HOUR_OF_DAY);
            if (checkCal.get(Calendar.MINUTE) == 30 &&
                temp.get(Calendar.HOUR_OF_DAY) == ph)
                temp.add(Calendar.HOUR, 1);
            else if (checkCal.get(Calendar.MINUTE) == 30 &&
                     temp.get(Calendar.HOUR_OF_DAY) == ph + 2)
                i += 2;

            prev = temp.getTime();
        }
        return null;
    }

    public static int[] getIntervalsIndeces(Date fromDate, Date startDate, Date endDate, TimeZone tz) {
        int[] interval = new int[2];
        int ph;
        Date startD;
        Calendar start = Calendar.getInstance(tz, Locale.ENGLISH);
        Calendar end = Calendar.getInstance(tz, Locale.ENGLISH);
        Calendar temp = Calendar.getInstance(tz, Locale.ENGLISH);
        Calendar checkCal = Calendar.getInstance(tz, Locale.ENGLISH);
        start.setTime(startDate);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);
        if (start.get(Calendar.MINUTE) > 30)
            start.add(Calendar.MINUTE, 60 - start.get(Calendar.MINUTE));
        else if (start.get(Calendar.MINUTE) > 0 && start.get(Calendar.MINUTE) < 30)
            start.add(Calendar.MINUTE, 30 - start.get(Calendar.MINUTE));
        startD = start.getTime();
        start.setTime(fromDate);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);
        if (start.get(Calendar.MINUTE) > 30)
            start.add(Calendar.MINUTE, 60 - start.get(Calendar.MINUTE));
        else if (start.get(Calendar.MINUTE) > 0 && start.get(Calendar.MINUTE) < 30)
            start.add(Calendar.MINUTE, 30 - start.get(Calendar.MINUTE));
        end.setTime(endDate);
        end.set(Calendar.SECOND, 0);
        end.set(Calendar.MILLISECOND, 0);
        if (end.get(Calendar.MINUTE) > 30)
            end.add(Calendar.MINUTE, 60 - end.get(Calendar.MINUTE));
        else if (end.get(Calendar.MINUTE) > 0 && end.get(Calendar.MINUTE) < 30)
            end.add(Calendar.MINUTE, 30 - end.get(Calendar.MINUTE));
        temp.setTime(start.getTime());
        Date prev = temp.getTime();
        int i = 0;
        interval[0] = -1;
        interval[1] = -1;
        while (temp.getTime().compareTo(end.getTime()) < 0) {
            if (temp.getTime().compareTo(startD) == 0)
                interval[0] = i;
            i++;
            temp.add(Calendar.MINUTE, 30);
            // daylight savings
            checkCal.setTime(prev);
            ph = checkCal.get(Calendar.HOUR_OF_DAY);
            if (checkCal.get(Calendar.MINUTE) == 30 &&
                temp.get(Calendar.HOUR_OF_DAY) == ph)
                temp.add(Calendar.HOUR, 1);
            else if (checkCal.get(Calendar.MINUTE) == 30 &&
                     temp.get(Calendar.HOUR_OF_DAY) == ph + 2)
                i += 2;

            prev = temp.getTime();
        }
        if (interval[0] == -1 && startD.compareTo(start.getTime()) <= 0 &&
            start.getTime().compareTo(end.getTime()) <= 0) {
            interval[0] = 0;
            interval[1] = i;
            return interval;
        } else if (interval[0] == -1)
            return null;
        else {
            interval[1] = i;
            return interval;
        }
    }

    public DomainAction.Action[] actionsAt(Date date, TimeZone tz,
                                           DomainAction.ActionAffects view) {
        return actionsAt(date, tz, view, curInd - 1);
    }

    public DomainAction.Action[] actionsAt(Date date, TimeZone tz,
                                           DomainAction.ActionAffects view,
                                           int maxActionIndex) {
        DomainAction d;
        ManualAction m;
        Date[] dates;
        DomainAction.Action[] acts = null;
        DomainAction.Action[] dailyActs = null;
        int tmp;
        int ph;
        Calendar start = Calendar.getInstance(tz, Locale.ENGLISH);
        Calendar end = Calendar.getInstance(tz, Locale.ENGLISH);
        Calendar temp = Calendar.getInstance(tz, Locale.ENGLISH);
        Calendar startD = Calendar.getInstance(tz, Locale.ENGLISH);
        Calendar endD = Calendar.getInstance(tz, Locale.ENGLISH);
        Calendar checkCal = Calendar.getInstance(tz, Locale.ENGLISH);
        //        Calendar deadLine = Calendar.getInstance(tz, Locale.ENGLISH);
        Calendar minEnd;
        Date prev = null;
        start.setTime(date);
        //        deadLine.setTime(deadline);
        //        deadLine.add(Calendar.MINUTE, -30);
        int addfield = 0;
        int addvalue = 0;
        int index = 0;
        int subIndex = 0;
        int s = checkConsistency(maxActionIndex);
        int maxIndex = (maxActionIndex < curInd) ? maxActionIndex : curInd - 1;
        maxIndex = (s < maxIndex) ? maxIndex - s : maxIndex;
        switch (view) {
        case DAY:
            acts = new DomainAction.Action[48];
            start.set(Calendar.HOUR_OF_DAY, 0);
            start.set(Calendar.MINUTE, 0);
            start.set(Calendar.SECOND, 0);
            start.set(Calendar.MILLISECOND, 0);
            end.setTime(start.getTime());
            end.add(Calendar.DATE, 1);
            addfield = Calendar.MINUTE;
            addvalue = 30;
            break;
        case WEEK:
            acts = new DomainAction.Action[336];
            start.set(Calendar.DAY_OF_WEEK, 1);
            start.set(Calendar.HOUR_OF_DAY, 0);
            start.set(Calendar.MINUTE, 0);
            start.set(Calendar.SECOND, 0);
            start.set(Calendar.MILLISECOND, 0);
            end.setTime(start.getTime());
            end.add(Calendar.DATE, 7);
            addfield = Calendar.MINUTE;
            addvalue = 30;
            break;
        case MONTH:
            acts = new DomainAction.Action[start.getActualMaximum(Calendar.
                    DAY_OF_MONTH)];
            start.set(Calendar.DAY_OF_MONTH, 1);
            start.set(Calendar.HOUR_OF_DAY, 0);
            start.set(Calendar.MINUTE, 0);
            start.set(Calendar.SECOND, 0);
            start.set(Calendar.MILLISECOND, 0);
            end.setTime(start.getTime());
            end.add(Calendar.MONTH, 1);
            addfield = Calendar.DATE;
            addvalue = 1;
            break;
        case YEAR:
            acts = new DomainAction.Action[start.getActualMaximum(Calendar.
                    DAY_OF_YEAR)];
            start.set(Calendar.DAY_OF_YEAR, 1);
            start.set(Calendar.HOUR_OF_DAY, 0);
            start.set(Calendar.MINUTE, 0);
            start.set(Calendar.SECOND, 0);
            start.set(Calendar.MILLISECOND, 0);
            end.setTime(start.getTime());
            end.add(Calendar.YEAR, 1);
            addfield = Calendar.DATE;
            addvalue = 1;
            break;
        case INTERVALS:
            start.set(Calendar.SECOND, 0);
            start.set(Calendar.MILLISECOND, 0);
            if (start.get(Calendar.MINUTE) > 30)
                start.add(Calendar.MINUTE, 60 - start.get(Calendar.MINUTE));
            else if (start.get(Calendar.MINUTE) > 0 &&
                     start.get(Calendar.MINUTE) < 30)
                start.add(Calendar.MINUTE, 30 - start.get(Calendar.MINUTE));
            end.setTime(deadline);
            end.set(Calendar.SECOND, 0);
            end.set(Calendar.MILLISECOND, 0);
            if (end.get(Calendar.MINUTE) > 30)
                end.add(Calendar.MINUTE, 60 - end.get(Calendar.MINUTE));
            else if (end.get(Calendar.MINUTE) > 0 && end.get(Calendar.MINUTE) < 30)
                end.add(Calendar.MINUTE, 30 - end.get(Calendar.MINUTE));
            acts = new DomainAction.Action[getHalfHourIntervalsSize(start.getTime(),
                    end.getTime(), tz)];
            addfield = Calendar.MINUTE;
            addvalue = 30;
            break;
        }

        for (int i = 0; i < acts.length; i++) {
            acts[i] = DomainAction.Action.UNDEF;
        }

        for (int i = maxIndex; i >= 0; i--) {
            d = actions.get(i);

            if (templateDates.containsKey(i)) {
                dates = templateDates.get(i);
//                if (dates == null) {
//                    dates = new Date[2];
//                    templateDates.put(i, dates);
//                }
                if (dates[0] != null)
                    startD.setTime(dates[0]);
                else
                    startD.setTime(release);
                if (dates[1] != null)
                    endD.setTime(dates[1]);
                else
                    endD.setTime(deadline);
            } else {
                m = (ManualAction) d;
                startD.setTime(m.getStartDate());
                endD.setTime(m.getEndDate());
            }
            minEnd = (endD.getTime().compareTo(end.getTime()) < 0) ? endD : end;
            temp.setTime(start.getTime());
            index = 0;
            subIndex = 0;
            if (view == DomainAction.ActionAffects.DAY ||
                view == DomainAction.ActionAffects.WEEK ||
                view == DomainAction.ActionAffects.INTERVALS)
                prev = temp.getTime();

            while (temp.getTime().compareTo(minEnd.getTime()) < 0) {
                if (startD.getTime().compareTo(temp.getTime()) <= 0 &&
                    temp.getTime().compareTo(endD.getTime()) <= 0 &&
                    acts[index] == DomainAction.Action.UNDEF)

                    if (view == DomainAction.ActionAffects.MONTH ||
                        view == DomainAction.ActionAffects.YEAR) {

                        dailyActs = actionsAt(temp.getTime(), tz,
                                              DomainAction.ActionAffects.DAY,
                                              maxIndex);
                        tmp = calcDayActs(dailyActs);
                        if (tmp == 1)
                            acts[index] = DomainAction.Action.INC;
                        else if (tmp == -1)
                            acts[index] = DomainAction.Action.NOT_INC;
                        else if (tmp == 2)
                            acts[index] = DomainAction.Action.UNDEF;
                        else
                            acts[index] = DomainAction.Action.PARDEF;
                    } else
                        acts[index] = d.getAction(temp.getTime(), tz, view);

                if (view == DomainAction.ActionAffects.WEEK) {
                    if (index < (subIndex + 47 * 7))
                        // shift index down
                        index += 7;
                    else {
                        // shift index top-right
                        subIndex++;
                        index = subIndex;
                    }
                } else
                    index++;
                temp.add(addfield, addvalue);

                // check for daylight savings
                if (view == DomainAction.ActionAffects.DAY ||
                    view == DomainAction.ActionAffects.WEEK ||
                    view == DomainAction.ActionAffects.INTERVALS) {
                    checkCal.setTime(prev);
                    ph = checkCal.get(Calendar.HOUR_OF_DAY);
                    if (checkCal.get(Calendar.MINUTE) == 30 &&
                        temp.get(Calendar.HOUR_OF_DAY) == ph) {
                        temp.add(Calendar.HOUR, 1);
                    } else if (checkCal.get(Calendar.MINUTE) == 30 &&
                               temp.get(Calendar.HOUR_OF_DAY) == ph + 2) {
                        if (view == DomainAction.ActionAffects.WEEK) {
                            for (int k = 0; k < 2; k++) {
                                if (index < (subIndex + 47 * 7))
                                    // shift index down
                                    index += 7;
                                else {
                                    // shift index top-right
                                    subIndex++;
                                    index = subIndex;
                                }
                            }
                        } else
                            index += 2;
                    }
                    prev = temp.getTime();
                }
            }
        }

        temp.setTime(start.getTime());
        index = 0;
        subIndex = 0;
        if (view == DomainAction.ActionAffects.DAY ||
            view == DomainAction.ActionAffects.WEEK ||
            view == DomainAction.ActionAffects.INTERVALS)
            prev = temp.getTime();

        while (temp.getTime().compareTo(end.getTime()) < 0) {
            if (temp.getTime().compareTo(release) < 0 ||
                deadline.compareTo(temp.getTime()) <= 0)

                acts[index] = DomainAction.Action.OUT_OF_DOMAIN;

            if (view == DomainAction.ActionAffects.WEEK) {
                if (index < (subIndex + 47 * 7))
                    // shift index down
                    index += 7;
                else {
                    // shift index top-right
                    subIndex++;
                    index = subIndex;
                }
            } else
                index++;

            temp.add(addfield, addvalue);
            if (view == DomainAction.ActionAffects.DAY ||
                view == DomainAction.ActionAffects.WEEK ||
                view == DomainAction.ActionAffects.INTERVALS) {
                checkCal.setTime(prev);
                ph = checkCal.get(Calendar.HOUR_OF_DAY);
                if (checkCal.get(Calendar.MINUTE) == 30 &&
                    temp.get(Calendar.HOUR_OF_DAY) == ph) {
                    temp.add(Calendar.HOUR, 1);
                } else if (checkCal.get(Calendar.MINUTE) == 30 &&
                           temp.get(Calendar.HOUR_OF_DAY) == ph + 2) {
                    switch (view) {
                    case DAY:
                        acts[index] = DomainAction.Action.OUT_OF_DOMAIN;
                        if ((index + 1) < acts.length) {
                            acts[index + 1] = DomainAction.Action.OUT_OF_DOMAIN;
                            index += 2;
                        } else
                            index++;
                        break;
                    case WEEK:
                        for (int k = 0; k < 2; k++) {
                            acts[index] = DomainAction.Action.OUT_OF_DOMAIN;

                            if (index < (subIndex + 47 * 7))
                                // shift index down
                                index += 7;
                            else {
                                // shift index top-right
                                subIndex++;
                                index = subIndex;
                            }
                            if (index >= acts.length)
                                break;
                        }
                        break;
                    case INTERVALS:
                        acts[index] = DomainAction.Action.OUT_OF_DOMAIN;
                        if ((index + 1) < acts.length) {
                            acts[index + 1] = DomainAction.Action.OUT_OF_DOMAIN;
                            index += 2;
                        } else
                            index++;
                        break;
                    }
                }
                prev = temp.getTime();
            }
        }
        return acts;
    }

    private int calcDayActs(DomainAction.Action[] acts) {
        int incs = 0;
        int nincs = 0;
        int undefs = 0;
        for (int i = 0; i < acts.length; i++) {
            switch (acts[i]) {
            case INC:
                incs++;
                break;
            case NOT_INC:
                nincs++;
                break;
            case UNDEF:
                undefs++;
                break;
            }
        }
        if (incs == acts.length)
            return 1;
        else if (nincs == acts.length)
            return -1;
        else if (undefs == acts.length)
            return 2;
        else
            return 0;
    }

    private int checkConsistency(int index) {
        DomainAction d;
        int s = 0;
        for (int i = 0; i < curInd; i++) {
            d = actions.get(i);
            if (d == null) {
                decrementIndecesFrom(i);
                if (i < index)
                    s++;
            }
        }
        return (s > 0) ? s : curInd + 1;
    }

    public HashMap<Integer, DomainAction> actions() {
        return actions;
    }

    public HashMap<Integer, Date[]> templateDates() {
        return templateDates;
    }

    public int actsSize() {
        return curInd;
    }

    public Date releaseDate() {
        return release;
    }

    public Date deadlineDate() {
        return deadline;
    }

    public void setReleaseDate(Date d) {
        release = d;
    }

    public void setDeadlineDate(Date d) {
        deadline = d;
    }

    public void removeAction(int actionIndex) {
        actions.remove(actionIndex);
        if (templateDates.containsKey(actionIndex))
            templateDates.remove(actionIndex);
        decrementIndecesFrom(actionIndex);
        fireDomainChangeEvent();
    }

    public int addManualAction(ManualAction m) {
        if (m == null)
            throw new IllegalArgumentException("Cannot Add a null manual action");
        actions.put(curInd, m);
        curInd++;
        fireDomainChangeEvent();
        return curInd - 1;
    }

    public int addTemplate(Template t, Date start, Date end,
                            DomainAction.Action action) {
        if (t == null)
            throw new IllegalArgumentException("Cannot Apply a null template");
        Date[] d = new Date[2];
        d[0] = start;
        d[1] = end;
        Template appTemp = (Template) t.clone();
        appTemp.setAction(action);
        appTemp.setDomain(this);
        appTemp.setIndexInDomain(curInd);
        actions.put(curInd, appTemp);
        templateDates.put(curInd, d);
        curInd++;
        fireDomainChangeEvent();
        return curInd - 1;
    }

    public int addMnualActionAtPosition(ManualAction m, int position) {
        if (m == null)
            throw new IllegalArgumentException("Cannot Add a null manual action");
        incrementIndecesFrom(position);
        actions.put(position, m);
        fireDomainChangeEvent();
        return position;
    }

    public int addTemplateActionAtPosition(Template t, Date start, Date end,
                                            DomainAction.Action action,
                                            int position) {
        if (t == null)
            throw new IllegalArgumentException("Cannot Apply a null template");
        incrementIndecesFrom(position);
        Date[] d = new Date[2];
        d[0] = start;
        d[1] = end;
        Template appTemp = (Template) t.clone();
        appTemp.setAction(action);
        appTemp.setDomain(this);
        appTemp.setIndexInDomain(position);
        actions.put(position, appTemp);
        templateDates.put(position, d);
        fireDomainChangeEvent();
        return position;
    }

    public void exchangeActions(int act1POS, int act2POS) {
        Date[] d1 = null;
        Date[] d2 = null;
        DomainAction da1 = actions.get(act1POS);
        DomainAction da2 = actions.get(act2POS);
        actions.remove(act1POS);
        actions.remove(act2POS);
        actions.put(act2POS, da1);
        actions.put(act1POS, da2);
        if (templateDates.containsKey(act1POS)) {
            d1 = templateDates.get(act1POS);
            templateDates.remove(act1POS);
            ((Template) da1).setIndexInDomain(act2POS);
        }
        if (templateDates.containsKey(act2POS)) {
            d2 = templateDates.get(act2POS);
            templateDates.remove(act2POS);
            templateDates.put(act1POS, d2);
            ((Template) da2).setIndexInDomain(act1POS);
        }
        if (d1 != null)
            templateDates.put(act2POS, d1);
        fireDomainChangeEvent();
    }

    private void incrementIndecesFrom(int index) {
        DomainAction da;
        Date[] d;
        for (int i = curInd - 1; i >= index; i--) {
            da = actions.get(i);
            actions.remove(i);
            actions.put(i + 1, da);
            if (templateDates.containsKey(i)) {
                d = templateDates.get(i);
                templateDates.remove(i);
                templateDates.put(i + 1, d);
                ((Template) da).setIndexInDomain(i + 1);
            }
        }
        curInd++;
    }

    private void decrementIndecesFrom(int index) {
        DomainAction da;
        Date[] d;
        for (int i = index + 1; i < curInd; i++) {
            da = actions.get(i);
            actions.remove(i);
            actions.put(i - 1, da);
            if (templateDates.containsKey(i)) {
                d = templateDates.get(i);
                templateDates.remove(i);
                templateDates.put(i - 1, d);
                ((Template) da).setIndexInDomain(i - 1);
            }
        }
        curInd--;
    }

    public void reloadTemplates(TemplateManager tmgr) {
        Template t, appTemp, newAppTemp;
        for (int i = 0; i < curInd; i++) {
            if (templateDates.containsKey(i)) {
                appTemp = (Template) actions.get(i);
                t = tmgr.getTemplate(appTemp.name());
                if (t != null) {
                    newAppTemp = (Template) t.clone();
                    newAppTemp.setAction(appTemp.getAppliedAction());
                    newAppTemp.setDomain(this);
                    newAppTemp.setIndexInDomain(i);
                    actions.remove(i);
                    actions.put(i, newAppTemp);
                }
            }
        }
        fireDomainChangeEvent();
    }

    public boolean equals(Object o) {
        Domain d;
        try {
            d = (Domain) o;
            return equals(d);
        } catch (ClassCastException e) {
            return false;
        }
    }

    public boolean equals(Domain d) {
        if (curInd != d.actsSize())
            return false;
        DomainAction da1, da2;
        Date[] dates1, dates2;
        HashMap<Integer, DomainAction> dActs = d.actions();
        HashMap<Integer, Date[]> dTempDates = d.templateDates();
        if (!release.equals(d.releaseDate()) || !deadline.equals(d.deadlineDate()))
            return false;
        for (int i = 0; i < curInd; i++) {
            da1 = actions.get(i);
            da2 = dActs.get(i);
            if ((da1 == null && da2 == null) || (da1 == null && da2 != null) ||
                (da1 != null && da2 == null) ||
                !da1.equals(da2))
                return false;
            if (templateDates.containsKey(i)) {
                dates1 = templateDates.get(i);
                dates2 = dTempDates.get(i);
                if ((dates1[0] == null && dates2[0] != null) ||
                    (dates1[1] == null && dates2[1] != null) ||
                    (dates1[0] != null && dates2[0] == null) ||
                    (dates1[1] != null && dates2[1] == null))
                    return false;
                if ((dates1[0] == null && dates2[0] == null) ||
                    (dates1[1] == null && dates2[1] == null))
                    continue;
                if (!dates1[0].equals(dates2[0]) || !dates1[1].equals(dates2[1]))
                    return false;
            }
        }
        return true;
    }

    public Object clone() {
        Domain clone;
        checkConsistency(curInd);
        clone = new Domain(new Date(release.getTime()), new Date(deadline.getTime()));
        DomainAction d;
        ManualAction m;
        Template t;
        Date[] dates;
        for (int i = 0; i < curInd; i++) {
            d = actions.get(i);
            if (templateDates.containsKey(i)) {
                t = (Template) d;
                dates = templateDates.get(i);
//                if (dates == null)
//                    clone.addTemplate(t, null, null, t.getAppliedAction());
                if (dates[0] != null && dates[1] != null)
                    clone.addTemplate(t, new Date(dates[0].getTime()),
                                      new Date(dates[1].getTime()),
                                      t.getAppliedAction());
                else if (dates[0] == null && dates[1] != null)
                    clone.addTemplate(t, null,
                                      new Date(dates[1].getTime()),
                                      t.getAppliedAction());
                else if (dates[0] != null && dates[1] == null)
                    clone.addTemplate(t, new Date(dates[0].getTime()),
                                      null,
                                      t.getAppliedAction());
                else
                    clone.addTemplate(t, null, null, t.getAppliedAction());
            } else {
                m = (ManualAction) d;
                clone.addManualAction((ManualAction) m.clone());
            }
        }
        clone.listeners = null;
        return clone;
    }

    public String toString() {
        return "Domain: " + name;
    }

    public Object[] getInternalState() {
        Object[] data = new Object[6];
        data[0] = release;
        data[1] = deadline;
        data[2] = actions;
        data[3] = templateDates;
        data[4] = curInd;
        data[5] = name;
        return data;
    }

    public void setInternalState(Object[] data) {
        release = (Date) data[0];
        deadline = (Date) data[1];
        actions = (HashMap<Integer, DomainAction>) data[2];
        templateDates = (HashMap<Integer, Date[]>) data[3];
        curInd = (Integer) data[4];
        name = (String) data[5];
    }
}
