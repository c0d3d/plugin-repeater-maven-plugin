package com.nlocketz.plugins;

import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.ConfigurationListener;
import org.codehaus.plexus.component.configurator.converters.ParameterizedConfigurationConverter;
import org.codehaus.plexus.component.configurator.converters.composite.AbstractCollectionConverter;
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.eclipse.sisu.inject.Logs;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
/**
 * A Collection converter allowing us to convert a {@code List<Map<String,String>>}
 * Most of the code is copied from
 * {@link org.codehaus.plexus.component.configurator.converters.composite.CollectionConverter}.
 */
public class RepetitionCollectionConverter
        extends AbstractCollectionConverter
        implements ParameterizedConfigurationConverter {

    public Object fromConfiguration(final ConverterLookup lookup,
                                    final PlexusConfiguration configuration,
                                    final Class<?> type,
                                    final Type[] typeArguments,
                                    final Class<?> enclosingType,
                                    final ClassLoader loader,
                                    final ExpressionEvaluator evaluator,
                                    final ConfigurationListener listener)
            throws ComponentConfigurationException {

        final Object value = fromExpression(configuration, evaluator);
        if (type.isInstance(value)) {
            return value;
        }
        try {
            final Collection<Object> elements;
            final Class<?> elementType = findElementType(typeArguments);
            if (null == value) {
                elements =
                        fromChildren(lookup, configuration, type, enclosingType, loader, evaluator, listener, elementType);
            } else if (value instanceof String && ("".equals(value) || !value.equals(configuration.getValue()))) {
                final PlexusConfiguration xml = csvToXml(configuration, (String) value);
                elements = fromChildren(lookup, xml, type, enclosingType, loader, evaluator, listener, elementType);
            } else if (value instanceof Object[]) {
                elements = instantiateCollection(configuration, type, loader);
                Collections.addAll(elements, (Object[]) value);
            } else {
                failIfNotTypeCompatible(value, type, configuration);
                elements = Collections.emptyList(); // unreachable
            }
            return elements;
        } catch (final ComponentConfigurationException e) {
            if (null == e.getFailedConfiguration()) {
                e.setFailedConfiguration(configuration);
            }
            throw e;
        } catch (final IllegalArgumentException e) {
            throw new ComponentConfigurationException(configuration, "Cannot store value into collection", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected final Collection<Object> instantiateCollection(final PlexusConfiguration configuration,
                                                             final Class<?> type, final ClassLoader loader)
            throws ComponentConfigurationException {
        final Class<?> implType = getClassForImplementationHint(type, configuration, loader);
        if (null == implType || Modifier.isAbstract(implType.getModifiers())) {
            if (Set.class.isAssignableFrom(type)) {
                if (SortedSet.class.isAssignableFrom(type)) {
                    return new TreeSet<Object>();
                }
                return new HashSet<Object>();
            }
            return new ArrayList<Object>();
        }

        final Object impl = instantiateObject(implType);
        failIfNotTypeCompatible(impl, type, configuration);
        return (Collection<Object>) impl;
    }

    private static Class<?> findElementType(final Type[] typeArguments) {
        if (null == typeArguments || typeArguments.length == 0) {
            return Object.class;
        } else if (typeArguments[0] instanceof Class<?>) {
            return (Class<?>) typeArguments[0];
        } else if (typeArguments[0] instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) typeArguments[0]).getRawType();
        }
        return Object.class;
    }

    public boolean canConvert(Class type) {
        return Collection.class.isAssignableFrom( type ) && !Map.class.isAssignableFrom( type );
    }

    public Object fromConfiguration(ConverterLookup lookup,
                                    PlexusConfiguration configuration,
                                    Class type,
                                    Class baseType,
                                    ClassLoader loader,
                                    ExpressionEvaluator evaluator,
                                    ConfigurationListener listener)
            throws ComponentConfigurationException {
        return fromConfiguration( lookup, configuration, type, null, baseType, loader, evaluator, listener );
    }
}
