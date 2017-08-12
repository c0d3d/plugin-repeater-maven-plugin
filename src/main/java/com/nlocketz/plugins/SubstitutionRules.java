package com.nlocketz.plugins;

import java.util.Map;

public class SubstitutionRules {

    /**
     * The default rules.<br>
     * If a repetition doesn't define a filter rule with any key in this map a rule will be added.
     */
    private Map<String, String> defaults;

    public SubstitutionRules(Map<String, String> defaults) {
        this.defaults = defaults;
    }

    public SubstitutionRules() {
        // For SISU
    }

    /**
     * EFFECTS: Modifies {@code sub} and {@code plugin} according to the rules configured in this component.
     * Make sure to pass ones that can get modified without any adverse effects.
     * Apply the rules with the given substitution map, and plugin.
     * @param sub The substitutions for the current run.
     * @param plugin The configured plugin for the current run.
     */
    @SuppressWarnings("unused") // Plugin to be used in the future.
    public void apply(Map<String, String> sub, RepetitionConfigPlugin plugin) {
        if (defaults != null) {
            addDefaults(sub);
        }
    }

    /**
     * Apply the default rules.
     * @param sub the map to modify
     */
    private void addDefaults(Map<String, String> sub) {
        for (Map.Entry<String, String> defaultEntry : defaults.entrySet()) {
            if (!sub.containsKey(defaultEntry.getKey())) {
                sub.put(defaultEntry.getKey(), defaultEntry.getValue());
            }
        }
    }
}
