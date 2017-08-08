package com.nlocketz.plugins;

import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class FilteringTest {
    private Map<String, String> varMap = map(
            "string-one", "1",
            "string-two", "2",
            "nameThree", "three",
            "4", "name-four");

    private List<String> varNames = new ArrayList<>(varMap.keySet());

    // Seeded so the order is always the same, I just don't want to be bothered with picking one.
    private Random r = new Random(1337);

    private String randVarName() {
        return varNames.get(r.nextInt(varNames.size()));
    }

    private Map<String, String> map(String ... entries) {
        if ((entries.length & 1) != 0) {
            throw new IllegalArgumentException("Entries must be key1,value1,key2,value2 ... etc. Given odd number!");
        }

        Map<String, String> result = new HashMap<>();

        // Always aligned properly due to check above.
        for (int i = 0; i < entries.length; i+=2) {
            result.put(entries[i], entries[i+1]);
        }

        return Collections.unmodifiableMap(result);
    }

    private Xpp3Dom domOf(String contents) {
        Xpp3Dom d = new Xpp3Dom("tag");
        d.setValue(contents);
        return d;
    }

    private Xpp3Dom domOf(Xpp3Dom ... children) {

        if (children.length == 0) {
            throw new IllegalArgumentException("Must have at least one child ... ");
        }

        Xpp3Dom result = new Xpp3Dom("compose-tag");

        for (int i = 0; i < children.length; i++) {
            result.addChild(children[i]);
        }

        return result;
    }

    private String subsOf(String s) {
        return String.format("@%s@", s);
    }

    private PlexusConfiguration conf(Xpp3Dom d) {
        return new XmlPlexusConfiguration(d);
    }

    private void assertSameConf(PlexusConfiguration expected, PlexusConfiguration actual) {
        assertEquals(expected.getChildCount(), actual.getChildCount());
        assertEquals(expected.getValue(), actual.getValue());
        assertEquals(expected.getName(), actual.getName());
        assertArrayEquals(expected.getAttributeNames(), actual.getAttributeNames());
        for (String name : expected.getAttributeNames()) {
            assertEquals(expected.getAttribute(name), actual.getAttribute(name));
        }
        assertEquals(expected.getChildCount(), actual.getChildCount());
        for (int i = 0; i < expected.getChildCount(); i++) {
            assertSameConf(expected.getChild(i), actual.getChild(i));
        }
    }

    @Test
    public void testFilterSimple() {
        Xpp3Dom simpleDom = domOf("Shouldn't be replaced");
        assertSameConf(conf(simpleDom), Util.filter(varMap, conf(simpleDom)));
        assertEquals(simpleDom.getValue(), Util.filter(varMap, simpleDom.getValue()));
    }

    @Test
    public void testFilterNoAtSigns() {
        String varName = randVarName();
        // None of these should be replaced since there were no @'s around the variable ....
        Xpp3Dom simpleDom = domOf(varName);
        assertSameConf(conf(simpleDom), Util.filter(varMap, conf(simpleDom)));
        assertEquals(simpleDom.getValue(), Util.filter(varMap, simpleDom.getValue()));
    }

    @Test
    public void testFilterJustVars() {
        String varName = randVarName();

        Xpp3Dom simpleDom = domOf(subsOf(varName));
        assertSameConf(conf(domOf(varMap.get(varName))), Util.filter(varMap, conf(simpleDom)));
        assertEquals(varMap.get(varName), Util.filter(varMap, simpleDom.getValue()));
    }

    @Test
    public void testFilterMixed() {
        String varName = randVarName();

        Xpp3Dom simpleDom = domOf(subsOf(varName));
        Xpp3Dom simpleDom2 = domOf("not replaced .... ");
        Xpp3Dom composed = domOf(simpleDom, simpleDom2);

        Xpp3Dom expected = domOf(varMap.get(varName));
        Xpp3Dom expectedComposed = domOf(expected, simpleDom2);

        assertSameConf(conf(expectedComposed), Util.filter(varMap, conf(composed)));
    }

    @Test
    public void testFilterDoubleNested() {
        String varName = randVarName();

        Xpp3Dom simpleDom = domOf(subsOf(varName));
        Xpp3Dom simpleDom2 = domOf(simpleDom);
        Xpp3Dom composed = domOf(simpleDom2, simpleDom2);


        Xpp3Dom expected = domOf(varMap.get(varName));
        Xpp3Dom expectedOne = domOf(expected);
        Xpp3Dom expectedComposed = domOf(expectedOne, expectedOne);

        assertSameConf(conf(expectedComposed), Util.filter(varMap, conf(composed)));
    }

    @Test
    public void testDoubleReplace() {
        String varOne = randVarName();
        String varTwo = randVarName();
        String vars1 = subsOf(varOne) + subsOf(varTwo);
        String vars2 = subsOf(varTwo) + subsOf(varOne);
        String expectOne = varMap.get(varOne) + varMap.get(varTwo);
        String expectTwo = varMap.get(varTwo) + varMap.get(varOne);

        assertEquals(expectOne, Util.filter(varMap, vars1));
        assertEquals(expectTwo, Util.filter(varMap, vars2));

    }

}
