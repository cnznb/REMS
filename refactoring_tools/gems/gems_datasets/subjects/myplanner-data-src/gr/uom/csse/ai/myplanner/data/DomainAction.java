package gr.uom.csse.ai.myplanner.data;

import java.io.Serializable;
import java.util.Date;
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
* @version 1.2.1
 */

public abstract class DomainAction implements Serializable, Cloneable, CrystalObject {

    static final long serialVersionUID = 6289203751535237759L;

    public static enum Action {INC, NOT_INC, PARDEF, UNDEF, OUT_OF_DOMAIN};
    public static enum ActionAffects {DAY, WEEK, MONTH, YEAR, INTERVALS};

    public DomainAction() {

    }

    public abstract Action getAction(Date date, TimeZone tz, ActionAffects view);

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public String toString() {
        return "Undefined Domain Action";
    }

    public abstract Object[] getInternalState();

    public abstract void setInternalState(Object[] data);

}
