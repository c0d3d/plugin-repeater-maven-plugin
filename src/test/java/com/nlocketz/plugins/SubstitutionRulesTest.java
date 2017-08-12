package com.nlocketz.plugins;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.collect.ImmutableMap.of;
import static org.junit.Assert.assertEquals;

public class SubstitutionRulesTest {
    private RepetitionConfigPlugin dummyPlugin;


    private SubstitutionRules sr(Map<String, String> d) {
        return new SubstitutionRules(d);
    }

    private Map<String, String> mmOf(String ... strs) {
        Map<String, String> m = new HashMap<>();
        if ((strs.length & 1) != 0) {
            throw new IllegalArgumentException("Need even number of elements: " + strs.length);
        }

        // We know we have proper alignment
        for (int i = 0; i < strs.length; i += 2) {
            m.put(strs[i], strs[i+1]);
        }

        return m;
    }

    private void assertSubRulesTo(Map<String, String> rules,
                                  Map<String, String> in,
                                  Map<String, String> out) {

        SubstitutionRules s = sr(rules);

        // Some tests pass in maps such that in == out, which will always be the same map
        // since the rule sub mutates.
        // so we make a copy
        Map<String, String> expect = new HashMap<>(out);

        s.apply(in, dummyPlugin);

        assertEquals(expect, in);
    }

    @Before
    public void init() {
        dummyPlugin = new RepetitionConfigPlugin();
    }

    @Test
    public void testNoDefaults() {
        Map<String, String> subs = mmOf("one", "two");
        // User doesn't have to provide rules
        assertSubRulesTo(null, subs, subs);
    }

    @Test
    public void testEmptyDefaults() {
        Map<String, String> subs = mmOf("one", "two");
        // User might give us no defaults, but still use the element.
        // Why? who knows ...
        assertSubRulesTo(ImmutableMap.<String, String>of(), subs, subs);
    }

    @Test
    public void testSubNoOverride() {
        // Provide no override, default should fill in
        assertSubRulesTo(
                of("r1", "default"),
                mmOf("r2", "myval"),
                of("r1", "default", "r2", "myval"));
    }

    @Test
    public void testSubWithOverride() {
        // Provide an override for a default rule.
        assertSubRulesTo(
                of("r1", "rule"),
                mmOf("r1", "override"),
                of("r1", "override"));
    }
}
