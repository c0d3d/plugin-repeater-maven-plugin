package com.nlocketz.plugins;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.configuration.PlexusConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.nlocketz.plugins.Util.filter;

public class RepetitionExecution {

    private String id;
    private List<String> goals;
    private PlexusConfiguration configuration;

    public String getId() {
        return id;
    }

    PlexusConfiguration getConfiguration() {
        return configuration;
    }

    RepetitionExecution sub(Map<String, String> vars) throws MojoExecutionException {
        RepetitionExecution execution = new RepetitionExecution();

        execution.goals = new ArrayList<>();
        if (goals == null) {
            throw new MojoExecutionException("Goals not provided for execution: " + id);
        }
        for (String goal : goals) {
            execution.goals.add(filter(vars, goal));
        }

        execution.id = filter(vars, id);
        execution.configuration = filter(vars, configuration);
        return execution;
    }

    public List<String> getGoals() {
        return goals;
    }
}
