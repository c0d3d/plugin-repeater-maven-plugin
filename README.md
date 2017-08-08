# Plugin repeater maven plugin [![Build Status](https://travis-ci.org/c0d3d/plugin-repeater-maven-plugin.svg?branch=master)](https://travis-ci.org/c0d3d/plugin-repeater-maven-plugin)

The plugin repeater maven plugin allows you to follow the [DRY principal](https://en.wikipedia.org/wiki/Don%27t_repeat_yourself) when writing your POM files.

If you've ever had to write a very similar plugin execution dozens of times then this is the plugin for you.

The plugin repeater plugin lets you repeat a plugin execution with different variables substituted.

#### Example

The following example assume that we have a fictional plugin, `adder-maven-plugin`, that adds numbers.

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
                    <!-- The goal to run in the sub-plugin -->
                    <goal>add</goal>
                    <contentPlugin>
                        <groupId>fake.plugin.group</groupId>
                        <artifactId>adder-maven-plugin</artifactId>
                        <version>0.0.0</version>
                        <executions>
                            <execution>
                                <id>repeat-add</id>
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
