package com.nlocketz.plugins;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;

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
        configuration = filter(vars, configuration);

    }

    public List<String> getGoals() {
        return goals;
    }

    public String getPhase() {
        return phase;
    }
}
