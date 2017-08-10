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

    List<RepetitionExecution> getExecutions() {
        return executions;
    }

    // TODO we should be able to make "mutable string" slots and just set them for each rep
    // so only 1 traverse to put mut strings in place
    RepetitionConfigPlugin substitute(Map<String, String> vars) throws MojoExecutionException {
        RepetitionConfigPlugin output = new RepetitionConfigPlugin();
        output.version = version;
        output.artifactId = artifactId;
        output.groupId = groupId;
        output.executions = new ArrayList<>(executions.size());
        for (RepetitionExecution exec : executions) {
            output.executions.add(exec.sub(vars));
        }
        return output;
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
