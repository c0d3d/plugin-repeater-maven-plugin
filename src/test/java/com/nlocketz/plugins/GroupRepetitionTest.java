package com.nlocketz.plugins;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;

public class GroupRepetitionTest {

    private static PlexusConfiguration base() {
        return p("repetitions");
    }

    private static PlexusConfiguration base(PlexusConfiguration ... others) {
        return p("repetitions", others);
    }

    private static PlexusConfiguration rep(PlexusConfiguration ... others) {
        return p("repetition", others);
    }

    private static PlexusConfiguration grp(PlexusConfiguration ... others) {
        return p("repetitionGroup", others);
    }


    private static PlexusConfiguration p(String name, PlexusConfiguration ... others) {
        PlexusConfiguration p = new XmlPlexusConfiguration(name);
        for (PlexusConfiguration other : others) {
            p.addChild(other);
        }
        return p;
    }

    private static PlexusConfiguration p(String name, String value) {
        PlexusConfiguration conf = new XmlPlexusConfiguration(name);
        conf.setValue(value);
        return conf;
    }


    @Test
    public void testNoGroup() {
        assertParseTo(ImmutableList.<Map<String, String>>of(), base());
    }

    @Test
    public void testLeaf() {
        assertParseTo(
                ImmutableList.<Map<String, String>>of(ImmutableMap.of("one", "two")),
                base(rep(p("one", "two"))));
    }

    @Test
    public void test2Leaf() {
        assertParseTo(
                ImmutableList.<Map<String, String>>of(ImmutableMap.of("one", "two", "three", "four")),
                base(rep(p("one", "two"), p("three", "four"))));
    }

    @Test
    public void testLeaf2() {
        assertParseTo(
                ImmutableList.<Map<String, String>>of(
                        ImmutableMap.of("one", "two"),
                        ImmutableMap.of("three", "four")),
                base(rep(p("one", "two")), rep(p("three", "four"))));
    }

    @Test
    public void test2Leaf2() {
        assertParseTo(
                ImmutableList.<Map<String, String>>of(
                        ImmutableMap.of("one", "two", "three", "four"),
                        ImmutableMap.of("five", "six", "seven", "eight")),
                base(
                        rep(
                                p("one", "two"),
                                p("three", "four")),
                        rep(
                                p("five", "six"),
                                p("seven", "eight"))));
    }

    @Test
    public void testGroupLeaf() {
        assertParseTo(ImmutableList.<Map<String,String>>of(
                ImmutableMap.of("one", "two", "a", "b")),
                base(grp(p("one", "two"), rep(p("a", "b")))));
    }

    @Test
    public void test2GroupLeaf() {
        assertParseTo(ImmutableList.<Map<String,String>>of(
                ImmutableMap.of("one", "two", "a", "b"),
                ImmutableMap.of("one", "two", "d", "e")),
                base(grp(p("one", "two"),
                        rep(p("a", "b")),
                        rep(p("d", "e")))));
    }

    private void assertParseTo(List<Map<String, String>>  expectedVars, PlexusConfiguration p) {
        assertEquals(expectedVars, RepetitionParser.parseRepetitions(p));
    }
}
