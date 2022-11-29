package gr.uom.csse.ai.myplanner.data;

import java.io.Serializable;
import java.util.Iterator;
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
 * @version 1.1
 */

public class DomainManager implements Serializable, Cloneable, CrystalObject {

    static final long serialVersionUID = -2613314468146663880L;

    private ArrayList<Domain> domains;
    private String user;

    public DomainManager(String user) {
        this.user = user;
        domains = new ArrayList<Domain>();
    }

    protected void setDomains(ArrayList<Domain> domains) {
        this.domains = domains;
    }

    public boolean addDomain(Domain domain) {
        if (!domains.contains(domain)) {
            Domain d = getDomain(domain.name());
            if (d != null)
                return false;
            domains.add(domain);
            return true;
        }
        return false;
    }

    public boolean contains(Domain domain) {
        return domains.contains(domain);
    }

    public boolean removeDomain(Domain domain) {
        if (!domains.contains(domain))
            return false;
        domains.remove(domain);
        return true;
    }

    public Domain getDomain(String domainName) {
        Iterator it = domains.iterator();
        Domain d;
        while (it.hasNext()) {
            d = (Domain) it.next();
            if (d.name().equals(domainName))
                return d;
        }
        return null;
    }

    public Domain[] domains() {
        Object[] o = domains.toArray();
        Domain[] d = new Domain[o.length];
        for (int i = 0; i < d.length; i++) {
            d[i] = (Domain) o[i];
        }
        return d;
    }

    public String user() {
        return user;
    }

    public int size() {
        return domains.size();
    }

    public Object clone() {
        DomainManager clone;
        try {
            clone = (DomainManager) super.clone();
            ArrayList<Domain> d = new ArrayList<Domain>();
            Domain[] orDomains = domains();
            for (int i = 0; i < orDomains.length; i++) {
                d.add(orDomains[i]);
            }
            clone.setDomains(d);
            return clone;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public Object[] getInternalState() {
        Object[] data = new Object[2];
        data[0] = domains;
        data[1] = user;
        return data;
    }

    public void setInternalState(Object[] data) {
        domains = (ArrayList<Domain>) data[0];
        user = (String) data[1];
    }

}
