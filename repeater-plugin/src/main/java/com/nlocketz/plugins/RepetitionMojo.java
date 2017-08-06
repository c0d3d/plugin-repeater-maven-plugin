package com.nlocketz.plugins;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.*;
import org.apache.maven.plugin.descriptor.MojoDescriptor;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3DomUtils;
import org.eclipse.aether.RepositorySystemSession;

import java.util.List;
import java.util.Map;

import static com.nlocketz.plugins.Util.plexusToDom;

/**
 * Mojo which will run your repetitions
 */
@Mojo(name = "repeat", defaultPhase = LifecyclePhase.PROCESS_SOURCES, configurator = "custom-basic")
public class RepetitionMojo
        extends AbstractMojo {
    /**
     * The goal to call in the repeated plugin.
     */
    @Parameter(property = "goal", required = true)
    private String goal;

    /**
     * Substitutions we will need to make.
     */
    @Parameter(property = "repetitions", required = true)
    private List<Map<String, String>> repetitions;

    /**
     * The plugin that will need to be repeated.
     */
    @Parameter(required = true, property = "content")
    private RepetitionConfigPlugin contentPlugin;

    /**
     * Handle to the current plugin manager.
     */
    @Component
    private BuildPluginManager pluginBuildManager;

    /**
     * Handle to the current maven project.
     */
    @Component
    private MavenProject mavenProject;

    /**
     * Handle to the current maven session.
     */
    @Component
    private MavenSession mavenSession;

    public void execute()
            throws MojoExecutionException {
        for (Map<String, String> sub : repetitions) {
            executeSubstitution(substituteIn(sub, contentPlugin));
        }
    }

    private RepetitionConfigPlugin substituteIn(Map<String, String> subs, RepetitionConfigPlugin original) {
        return original.substitute(subs);
    }

    private void executeSubstitution(RepetitionConfigPlugin subbed) throws MojoExecutionException {
        for (RepetitionExecution execution : subbed.getExecutions()) {
            try {
                MojoExecution mojoDescriptor = loadMojoDescriptor(subbed, execution);
                pluginBuildManager.executeMojo(mavenSession, mojoDescriptor);
            } catch (MojoFailureException
                    | PluginConfigurationException
                    | PluginManagerException
                    | InvalidPluginDescriptorException
                    | PluginResolutionException
                    | PluginNotFoundException
                    | PluginDescriptorParsingException e) {

                throw new MojoExecutionException("Failed to repeat execution!", e);
            }
        }
    }

    private MojoExecution loadMojoDescriptor(RepetitionConfigPlugin subbed, RepetitionExecution execution) throws
            InvalidPluginDescriptorException,
            PluginResolutionException,
            PluginDescriptorParsingException,
            PluginNotFoundException,
            MojoExecutionException {

        RepositorySystemSession repositorySession = mavenSession.getRepositorySession();

        PluginDescriptor pDesc =
                pluginBuildManager.loadPlugin(
                        subbed.asPlugin(),
                        mavenProject.getRemotePluginRepositories(),
                        repositorySession);

        MojoDescriptor mojoDescriptor = pDesc.getMojo(goal);

        if (mojoDescriptor == null) {
            throw new MojoExecutionException("Goal "+goal+" doesn't exist for plugin " + subbed.toGav());
        }

        return new MojoExecution(
                mojoDescriptor,
                Xpp3DomUtils.mergeXpp3Dom(
                        plexusToDom(execution.getConfiguration()),
                        plexusToDom(mojoDescriptor.getMojoConfiguration())));
    }


}
