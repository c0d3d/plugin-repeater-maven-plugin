package com.nlocketz.plugins;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.nlocketz.plugins.Util.filter;
import static com.nlocketz.plugins.Util.plexusToDom;

public class RepetitionExecution {

    private String id;
    private List<String> goals;
    private PlexusConfiguration configuration;
    private String phase;

    public RepetitionExecution(RepetitionExecution other) {
        this.id = other.id;
        this.goals = new ArrayList<>(other.goals);
        this.configuration = new XmlPlexusConfiguration(plexusToDom(other.configuration));
        this.phase = other.phase;
    }

    public RepetitionExecution() {
        // For SISU
    }

    public String getId() {
        return id;
    }

    PlexusConfiguration getConfiguration() {
        return configuration;
    }

    private static final String IF_ATTR = "if";
    private static final String UNLESS_ATTR = "unless";

    private Xpp3Dom evalAttributes(Map<String, String> vars,
                                   Xpp3Dom toTransform) {
        if (toTransform == null) {
            return null;
        }

        String ifVal = toTransform.getAttribute(IF_ATTR);
        if (ifVal != null && !vars.containsKey(ifVal)) {
            return null;
        } else {
            toTransform = removeAttr(toTransform, IF_ATTR);
        }


        String unlessVal = toTransform.getAttribute(UNLESS_ATTR);
        if (unlessVal != null && vars.containsKey(unlessVal)) {
            return null;
        } else {
            toTransform = removeAttr(toTransform, UNLESS_ATTR);
        }

        toTransform = processChildren(vars, toTransform);

        return toTransform;
    }

    private Xpp3Dom processChildren(Map<String, String> vars, Xpp3Dom toTransform) {
        Xpp3Dom[] children = toTransform.getChildren();
        Xpp3Dom result = new Xpp3Dom(toTransform.getName());
        if (children.length != 0) {
            for (Xpp3Dom child : children) {
                Xpp3Dom pc = evalAttributes(vars, child);
                if (pc != null) {
                    result.addChild(pc);
                }
            }
        } else {
            result.setValue(toTransform.getValue());
        }

        for (String attr : toTransform.getAttributeNames()) {
            result.setAttribute(attr, toTransform.getAttribute(attr));
        }

        return result;
    }

    private Xpp3Dom removeAttr(Xpp3Dom toTransform, String toRemove) {
        Xpp3Dom newEle = new Xpp3Dom(toTransform.getName());

        for (String attr : toTransform.getAttributeNames()) {
            if (!attr.equals(toRemove)) {
                newEle.setAttribute(attr, toTransform.getAttribute(attr));
            }
        }

        Xpp3Dom[] children = toTransform.getChildren();
        if (children.length > 0) {
            for (Xpp3Dom child : toTransform.getChildren()) {
                newEle.addChild(child);
            }
        } else {
            newEle.setValue(toTransform.getValue());
        }

        return newEle;
    }

    private PlexusConfiguration transformXml(Map<String, String> vars,
                                     PlexusConfiguration toTransform) {

        Xpp3Dom d = evalAttributes(vars, plexusToDom(toTransform));

        return d != null ? new XmlPlexusConfiguration(d) : null;

    }

    void sub(Map<String, String> vars) throws MojoExecutionException {

        if (goals == null) {
            throw new MojoExecutionException("Goals not provided for execution: " + id);
        }

        List<String> filteredGoals = new ArrayList<>();
        for (String goal : goals) {
            filteredGoals.add(filter(vars, goal));
        }
        goals = filteredGoals;

        id = filter(vars, id);

       configuration = transformXml(vars, configuration);

        if (configuration != null) {
            configuration = filter(vars, configuration);
        }

    }

    public List<String> getGoals() {
        return goals;
    }

    public String getPhase() {
        return phase;
    }
}
