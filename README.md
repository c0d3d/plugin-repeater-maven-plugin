# Plugin repeater maven plugin [![Build Status](https://travis-ci.org/c0d3d/plugin-repeater-maven-plugin.svg?branch=master)](https://travis-ci.org/c0d3d/plugin-repeater-maven-plugin) [![Maven Central](https://img.shields.io/maven-central/v/com.nlocketz.plugins/plugin-repeater-maven-plugin.svg?label=Maven%20Central)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.nlocketz.plugins%22%20a%3A%22plugin-repeater-maven-plugin%22)

Table of contents
=================

  * [Summary](#summary)
  * [Simple Example](#simple-example)
  * [Default Variable Groups](#default-variable-groups)
  * [Conditional Inclusion](#conditional-xml-inclusion)

## Summary
The plugin repeater maven plugin allows you to follow the [DRY principal](https://en.wikipedia.org/wiki/Don%27t_repeat_yourself) when writing your POM files.

If you've ever had to write a very similar plugin execution dozens of times then this is the plugin for you.

The plugin repeater plugin lets you repeat a plugin execution with different variables substituted.

All the examples in this README assume that we have a fictional plugin, `adder-maven-plugin`, that adds numbers. Anywhere you see `<!-- Boilerplate omitted -->`
means that we've left out the plugin GAV as well as any tags that aren't necessary. This may mean it is in the `contentPlugin` section of a repeater plugin
configuration or a plugin inside a pom's `plugins` section. The context should provide that information for you.

## Simple Example

Say we wanted to add 5 sets of two numbers. Normally you'd have to write something like this:

    <plugin>
        <groupId>fake.plugin.group</groupId>
        <artifactId>adder-maven-plugin</artifactId>
        <version>0.0.0</version>
        <executions>
            <execution>
                <id>first-number-add</id>
                <goals>
                    <goal>add</goal>
                </goals>
                <configuration>
                    <numberOne>1</numberOne>
                    <numberTwo>2</numberTwo>
                </configuration>
            </execution>
            <execution>
                <id>second-number-add</id>
                <goals>
                    <goal>add</goal>
                </goals>
                <configuration>
                    <numberOne>3</numberOne>
                    <numberTwo>4</numberTwo>
                </configuration>
            </execution>
            <execution>
                <id>third-number-add</id>
                <goals>
                    <goal>add</goal>
                </goals>
                <configuration>
                    <numberOne>5</numberOne>
                    <numberTwo>6</numberTwo>
                </configuration>
            </execution>
            <execution>
                <id>four-number-add</id>
                <goals>
                    <goal>add</goal>
                </goals>
                <configuration>
                    <numberOne>7</numberOne>
                    <numberTwo>8</numberTwo>
                </configuration>
            </execution>
            <execution>
                <id>fifth-number-add</id>
                <goals>
                    <goal>add</goal>
                </goals>
                <configuration>
                    <numberOne>9</numberOne>
                    <numberTwo>10</numberTwo>
                </configuration>
            </execution>
        </executions>
    </plugin>

With the plugin repeater maven plugin you can just write:

    <plugin>
        <groupId>com.nlocketz.plugins</groupId>
        <artifactId>plugin-repeater-maven-plugin</artifactId>
        <!-- Check maven central for latest version -->
        <version>LATEST</version>
        <executions>
            <execution>
                <id>run-addings</id>
                <goals>
                    <goal>repeat</goal>
                </goals>
                <configuration>
                    <repetitions>
                        <!-- Each repetition corresponds to a single run of contentPlugin -->
                        <!-- The values mapped in the repetition are subbed in @ surrounded tags -->
                        <repetition>
                            <nOne>1</nOne>
                            <nTwo>2</nTwo>
                        </repetition>
                        <repetition>
                            <nOne>3</nOne>
                            <nTwo>4</nTwo>
                        </repetition>
                        <repetition>
                            <nOne>5</nOne>
                            <nTwo>6</nTwo>
                        </repetition>
                        <repetition>
                            <nOne>7</nOne>
                            <nTwo>8</nTwo>
                        </repetition>
                        <repetition>
                            <nOne>9</nOne>
                            <nTwo>10</nTwo>
                        </repetition>
                    </repetitions>
                    <contentPlugin>
                        <groupId>fake.plugin.group</groupId>
                        <artifactId>adder-maven-plugin</artifactId>
                        <version>0.0.0</version>
                        <executions>
                            <execution>
                                <id>repeat-add</id>
                                <goals>
                                    <!-- Every goal will be run. -->
                                    <!-- the adder-maven-plugin only has one goal -->
                                    <goal>add</goal>
                                </goals>
                                <!-- NOTE: Due to a limitation of the maven plugin API -->
                                <!-- this doesn't support the phase tag. All the executions -->
                                <!-- are run in the same phase as your plugin-repeator-maven-plugin -->
                                <configuration>
                                    <!-- These @ surrounded variables are replaced with their -->
                                    <!-- corresponding values during each repetition. -->
                                    <numberOne>@nOne@</numberOne>
                                    <numberTwo>@nTwo@</numberTwo>
                                </configuration>
                            </execution>
                        </executions>
                    </contentPlugin>
                </configuration>
            </execution>
        </executions>
    </plugin>

## Default Variable Groups

If you have some repetitions that all use a certain variable with a certain value, except 1, you'd have to repeat that variable in the repetitions (violating the DRY principal).
Fear not! We have thought of this, and added a feature called `repetitionGroup`s that allow you to set defaults for certain repetitions, and override it for others.

Here is an example:

    <!-- Boilerplate omitted -->
    <execution>
        <id>run-addings</id>
        <goals>
            <goal>repeat</goal>
        </goals>
        <configuration>
            <repetitions>
                <repetitionGroup>
                    <!-- Here we set a default for 'nOne'. -->
                    <!-- If any repetitions nested inside this repetitionGroup -->
                    <!-- *don't* declare 'nOne', it will be supplied by the most recent default. -->
                    <!-- In this case that is the top level repetition group, and 2. -->
                    <nOne>2</nOne>
                    <repetition>
                        <nTwo>2</nTwo>
                    </repetition>
                    <repetition>
                        <nTwo>3</nTwo>
                    </repetition>
                    <repetition>
                        <nTwo>4</nTwo>
                        <nOne>4</nOne>
                    </repetition>
                </repetitionGroup>
            </repetitions>
            ...
            <!-- Same as before ... -->
            ...
        </configuration>
    </execution>

which is equivalent to:

    <!-- Boilerplate omitted -->
    <executions>
        <execution>
            <!-- NOTE: even though these have the same execution ID -->
            <!-- they will all be run inside the plugin-repeater-maven-plugin -->
            <id>repeat-add</id>
            <goals>
                <goal>add</goal>
            </goals>
            <configuration>
                <numberOne>2</numberOne>
                <numberTwo>2</numberTwo>
            </configuration>
        </execution>
        <execution>
            <id>repeat-add</id>
            <goals>
                <goal>add</goal>
            </goals>
            <configuration>
                <numberOne>2</numberOne>
                <numberTwo>3</numberTwo>
            </configuration>
        </execution>
        <execution>
            <id>repeat-add</id>
            <goals>
                <goal>add</goal>
            </goals>
            <configuration>
                <numberOne>4</numberOne>
                <numberTwo>4</numberTwo>
            </configuration>
        </execution>
    </executions>

You can even nest these as deeply as you want!

    <!-- Boilerplate omitted -->
    <execution>
        <id>run-addings</id>
        <goals>
            <goal>repeat</goal>
        </goals>
        <configuration>
            <repetitions>
                <repetitionGroup>
                    <nOne>2</nOne>
                    <repetitionGroup>
                        <nTwo>2</nTwo>
                        <repetition>
                            <nOne>10</nOne>
                        </repetition>
                    </repetitionGroup>
                    <repetition>
                        <nTwo>-1</nTwo>
                    </repetition>
                </repetitionGroup>
            </repetitions>
            ...
            <!-- Same as before ... -->
            ...
        </configuration>
    </execution>

is equivalent to:

    <!-- Boilerplate omitted -->
    <executions>
        <execution>
            <id>repeat-add</id>
            <goals>
                <goal>add</goal>
            </goals>
            <configuration>
                <numberOne>10</numberOne>
                <numberTwo>2</numberTwo>
            </configuration>
        </execution>
        <execution>
            <id>repeat-add</id>
            <goals>
                <goal>add</goal>
            </goals>
            <configuration>
                <numberOne>2</numberOne>
                <numberTwo>-1</numberTwo>
            </configuration>
        </execution>
    <executions>

##### Small gotcha

Due to limitations on maven's XML, you cannot currently do something like this:

    <!-- Boilerplate omitted -->
    <execution>
        <id>run-addings</id>
        <goals>
            <goal>repeat</goal>
        </goals>
        <configuration>
            <repetitions>
                <repetitionGroup>
                    <nOne>2</nOne>
                    <repetitionGroup>
                        <nTwo>2</nTwo>
                        <!-- neither of the following tags will be recognized! -->
                        <!-- Even though they are unambiguous in terms of their intention -->
                        <repetition/>
                        <repetition>
                        </repetition>
                    </repetitionGroup>
                </repetitionGroup>
            </repetitions>
            ...
            <!-- Same as before ... -->
            ...
        </configuration>
    </execution>

## Conditional XML Inclusion

The plugin-repeater-maven-plugin also supports conditional inclusion of XML for content plugins.
Currently you can only make the decision based on whether a filter variable is defined for the current repetition or not.

In order to conditionally include a tag, `myTag` inside the configuration of your content plugin you can do the following:

    <configuration>
        <myTag if="myVar">@myVar@</myTag>
    </configuration>

This includes `<myTag>myVarValue</myTag>` if `myVar` has a definition for the current repetition.
An important note for this is that a failed condition excludes *everything* within the failing tag.
Any nested XML tags will also be excluded as well as any attributes or string contents.

The plugin-repeater-maven-plugin also supports the `unless` attribute.

Here is a toy usage:

    <!-- Boilerplate omitted -->
    <execution>
        <id>run-addings</id>
        <goals>
            <goal>repeat</goal>
        </goals>
        <configuration>
            <repetitions>
                <repetition>
                    <nOne>1</nOne>
                    <nTwo>2</nTwo>
                </repetition>
                <repetition>
                    <nTwo>5</nTwo>
                </repetition>
            </repetitions>
            <contentPlugin>
                <groupId>fake.plugin.group</groupId>
                <artifactId>adder-maven-plugin</artifactId>
                <version>0.0.0</version>
                <executions>
                    <execution>
                        <id>repeat-add</id>
                        <goals>
                            <goal>add</goal>
                        </goals>
                        <configuration>
                            <numberOne if="nOne">@nOne@</numberOne>
                            <numberOne unless="nOne">0</numberOne>
                            <numberTwo>@nTwo@</numberTwo>
                        </configuration>
                    </execution>
                </executions>
            </contentPlugin>
        </execution>
	</executions>

which equivalent to:

    <!-- Boilerplate omitted -->
    <executions>
        <execution>
            <id>repeat-add</id>
            <goals>
                <goal>add</goal>
            </goals>
            <configuration>
                <numberOne>1</numberOne>
                <numberTwo>2</numberTwo>
            </configuration>
        </execution>
        <execution>
            <id>repeat-add</id>
            <goals>
                <goal>add</goal>
            </goals>
            <configuration>
                <numberOne>0</numberOne>
                <numberTwo>5</numberTwo>
            </configuration>
        </execution>
    <executions>
