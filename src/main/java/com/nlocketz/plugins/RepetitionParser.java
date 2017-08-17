package com.nlocketz.plugins;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.codehaus.plexus.configuration.PlexusConfiguration;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class RepetitionParser {

    static List<Map<String, String>> parseRepetitions(PlexusConfiguration xml) {

        ensureName(xml, "repetitions");

        List<Map<String, String>> results = Lists.newArrayList();
        for (PlexusConfiguration conf : xml.getChildren()) {
            results.addAll(parse(conf, new HashMap<String, String>()));
        }

        return results;
    }

    private static List<Map<String,String>> parse(PlexusConfiguration conf, Map<String, String> bound) {

        List<PlexusConfiguration> children = Lists.newArrayList(conf.getChildren());
        Map<String, String> leafChildren = partitionOnLeaves(children);

        if (children.isEmpty()) {

            ensureName(conf, "repetition");

            // Here we have reached an ultimate leaf, meaning
            // this counts as one instance of a repetition.
            return ImmutableList.of(instance(bound, leafChildren));

        } else {

            ensureName(conf, "repetitionGroup");

            List<Map<String, String>> childInstances = Lists.newArrayList();
            for (PlexusConfiguration child : children) {
                childInstances.addAll(parse(child, instance(bound, leafChildren)));
            }

            return childInstances;

        }
    }

    private static void ensureName(PlexusConfiguration conf, String name) {
        if (!conf.getName().equals(name)) {
            throw new IllegalArgumentException(
                    String.format("%s element must have name %s, was %s", name, name, conf.getName()));
        }
    }

    private static Map<String, String> instance(Map<String, String> bound,
                                         Map<String, String> base) {
        Map<String, String> varInstance = Maps.newHashMap(bound);
        varInstance.putAll(base);
        return Util.filterVars(bound, varInstance);
    }

    private static Map<String, String> partitionOnLeaves(List<PlexusConfiguration> children) {

        Map<String, String> leafMappings = Maps.newHashMap();
        Iterator<PlexusConfiguration> itr = children.iterator();

        while (itr.hasNext()) {
            PlexusConfiguration next = itr.next();
            if (next.getChildCount() == 0) {
                leafMappings.put(next.getName(), next.getValue());
                itr.remove();
            }
        }

        return leafMappings;
    }
}
