package com.nlocketz.plugins;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;

@Mojo(name = "echo", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class EchoMojo extends AbstractMojo {

    @Parameter(property = "echo", required = true)
    private String toEcho;

    @Parameter(property = "file")
    private File toFile;

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (toFile == null) {
            getLog().info(toEcho);
        } else {
            try {
                getLog().info("Writing to file " + toFile.toString());
                Path outputPath = toFile.toPath();
                Files.createDirectories(outputPath.getParent());
                Files.deleteIfExists(outputPath);
                Files.createFile(outputPath);
                try (BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {
                    writer.write(toEcho);
                }
            } catch (IOException e) {
                throw new MojoExecutionException("Couldn't write", e);
            }
        }
    }
}
