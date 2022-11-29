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

public class TemplateManager implements Serializable, Cloneable, CrystalObject  {

    static final long serialVersionUID = 3575551748206728523L;

    private ArrayList<Template> templates;
    private String user;

    public TemplateManager(String user) {
        this.user = user;
        templates = new ArrayList<Template>();
    }

    protected void setTemplates(ArrayList<Template> templates) {
        this.templates = templates;
    }

    public boolean addTemplate(Template template) {
        if (template.isApplied())
            return false;
        if (!templates.contains(template)) {
            templates.add(template);
            return true;
        }
        return false;
    }

    public boolean contains(Template template) {
        return templates.contains(template);
    }

    public boolean removeTemplate(Template template) {
        if (!templates.contains(template))
            return false;
        templates.remove(template);
        return true;
    }

    public Template getTemplate(String templateName) {
        Iterator it = templates.iterator();
        Template t;
        while (it.hasNext()) {
            t = (Template) it.next();
            if (t.name().equals(templateName))
                return t;
        }
        return null;
    }

    public Template[] templates() {
        Object[] o = templates.toArray();
        Template[] t = new Template[o.length];
        for (int i = 0; i < t.length; i++) {
            t[i] = (Template) o[i];
        }
        return t;
    }

    public void resetTemplates() {
        Iterator it = templates.iterator();
        Template t;
        while (it.hasNext()) {
            t = (Template) it.next();
            t.setComplement(false);
        }
    }

    public String user() {
        return user;
    }

    public int size() {
        return templates.size();
    }

    public Object clone() {
        TemplateManager clone;
        try {
            clone = (TemplateManager) super.clone();
            ArrayList<Template> t = new ArrayList<Template>();
            Template[] orTemplates = templates();
            for (int i = 0; i < orTemplates.length; i++) {
                t.add(orTemplates[i]);
            }
            clone.setTemplates(t);
            return clone;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public Object[] getInternalState() {
        Object[] data = new Object[2];
        data[0] = templates;
        data[1] = user;
        return data;
    }

    public void setInternalState(Object[] data) {
        templates = (ArrayList<Template>) data[0];
        user = (String) data[1];
    }
}
