package com.nlocketz.plugins;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.configurator.BasicComponentConfigurator;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.ConfigurationListener;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;

/**
 * Specialized component configurator so we can get the ability to build nested map parameters.
 * Credit to https://stackoverflow.com/a/38633590/2018455
 */
public class RepetitionComponentConfigurator extends BasicComponentConfigurator {

        @Override
        public void configureComponent(final Object component, final PlexusConfiguration configuration,
                                       final ExpressionEvaluator evaluator, final ClassRealm realm, final ConfigurationListener listener)
                throws ComponentConfigurationException {
            converterLookup.registerConverter(new RepetitionCollectionConverter());
            super.configureComponent(component, configuration, evaluator, realm, listener);
        }

}
