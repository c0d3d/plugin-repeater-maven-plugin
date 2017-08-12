package com.nlocketz.plugins;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.MojoExecutionException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RepetitionConfigPlugin {

    private String groupId;
    private String artifactId;
    private String version;
    private List<RepetitionExecution> executions;

    /**
     * Copy constructor.
     * @param other The other plugin to copy.
     */
    public RepetitionConfigPlugin(RepetitionConfigPlugin other) {
        this.groupId = other.groupId;
        this.artifactId = other.artifactId;
        this.version = other.version;
        this.executions = new ArrayList<>();
        for (RepetitionExecution execution : other.getExecutions()) {
            this.executions.add(new RepetitionExecution(execution));
        }
    }

    public RepetitionConfigPlugin() {
        // For SISU
    }


    List<RepetitionExecution> getExecutions() {
        return executions;
    }

    void substitute(Map<String, String> vars) throws MojoExecutionException {
        for (RepetitionExecution exec : executions) {
            exec.sub(vars);
        }
    }

    Plugin asPlugin() {
        Plugin newPlugin = new Plugin();
        newPlugin.setGroupId(groupId);
        newPlugin.setArtifactId(artifactId);
        newPlugin.setVersion(version);
        // We currently don't support inline dependencies, although this is easy enough to add in the future.
        newPlugin.setDependencies(Collections.<Dependency>emptyList());
        return newPlugin;
    }

    String toGav() {
        return String.format("%s:%s:%s", groupId, artifactId, version);
    }
}
