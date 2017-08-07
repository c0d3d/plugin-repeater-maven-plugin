package com.nlocketz.plugins;

import org.codehaus.plexus.configuration.PlexusConfiguration;

import java.util.Map;

import static com.nlocketz.plugins.Util.filter;

public class RepetitionExecution {

    private String id;
    private PlexusConfiguration configuration;

    public String getId() {
        return id;
    }

    PlexusConfiguration getConfiguration() {
        return configuration;
    }

    RepetitionExecution sub(Map<String, String> vars) {
        RepetitionExecution execution = new RepetitionExecution();
        execution.id = filter(vars, id);
        execution.configuration = filter(vars, configuration);
        return execution;
    }

}
