package com.nlocketz.plugins;

import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import java.util.Map;

final class Util {
    private Util () {
        //
    }

    public static String filter(Map<String, String> subs, String toFilter) {

        if (toFilter == null) {
            // Null doesn't filter ...
            return null;
        }

        String output = toFilter;

        // TODO precompiled pattern (or pattern cache)
        for (String varName : subs.keySet()) {
            String varExpr = String.format("@%s@", varName);
            if (output.contains(varExpr)) {
                output = output.replaceAll(varExpr, subs.get(varName));
            }
        }

        return output;
    }

    private static void filterInternal(Map<String, String> subs, PlexusConfiguration toFilter) {

        // If its a leaf, just do it.
        if (toFilter.getChildCount() == 0) {
            toFilter.setValue(filter(subs, toFilter.getValue()));
            return;
        }

        // Filter children
        for (int i = 0; i < toFilter.getChildCount(); i++) {
            PlexusConfiguration child = toFilter.getChild(i);
            if (child.getChildCount() == 0) {
                child.setValue(filter(subs, child.getValue()));
            } else {
                filterInternal(subs, child);
            }
        }

        // Filter attributes
        for (String attrName : toFilter.getAttributeNames()) {
            toFilter.setAttribute(attrName, filter(subs, toFilter.getAttribute(attrName)));
        }
    }

    static PlexusConfiguration filter(Map<String, String> subs, PlexusConfiguration config) {
        PlexusConfiguration output = new XmlPlexusConfiguration(plexusToDom(config));

        // This one mutates the given config
        filterInternal(subs, output);

        return output;
    }

    static Xpp3Dom plexusToDom(PlexusConfiguration config) {

        if (config == null) {
            return null;
        }

        Xpp3Dom output = new Xpp3Dom(config.getName());
        output.setValue(config.getValue(null));
        for (String attrName : config.getAttributeNames()) {
            output.setAttribute(attrName, config.getAttribute(attrName));
        }
        for (PlexusConfiguration child : config.getChildren()) {
            // Convert children.
            output.addChild(plexusToDom(child));
        }
        return output;
    }
}
