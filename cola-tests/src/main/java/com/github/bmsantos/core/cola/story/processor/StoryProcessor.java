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
package com.github.bmsantos.core.cola.story.processor;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.bmsantos.core.cola.formatter.ReportDetails;
import com.github.bmsantos.core.cola.report.Report;
import com.github.bmsantos.core.cola.story.annotations.ColaInjectable;
import com.github.bmsantos.core.cola.story.annotations.Dependencies;
import com.github.bmsantos.core.cola.story.annotations.DependsOn;
import com.github.bmsantos.core.cola.story.annotations.Given;
import com.github.bmsantos.core.cola.story.annotations.PostSteps;
import com.github.bmsantos.core.cola.story.annotations.PreSteps;
import com.github.bmsantos.core.cola.story.annotations.Then;
import com.github.bmsantos.core.cola.story.annotations.When;
import com.google.inject.Injector;
import gherkin.deps.com.google.gson.Gson;
import gherkin.deps.com.google.gson.reflect.TypeToken;
import org.junit.runner.JUnitCore;
import org.slf4j.Logger;

import static com.github.bmsantos.core.cola.report.ReportLoader.reportLoader;
import static com.github.bmsantos.core.cola.utils.ColaUtils.isSet;
import static com.google.inject.Guice.createInjector;
import static java.util.Arrays.asList;
import static org.junit.runner.Request.method;
import static org.slf4j.LoggerFactory.getLogger;

public class StoryProcessor {
    private static final Logger log = getLogger(StoryProcessor.class);

    private static final TypeToken<Map<String, String>> projectionType = new TypeToken<Map<String, String>>() {};
    private static final TypeToken<List<ReportDetails>> reportType = new TypeToken<List<ReportDetails>>() {};

    private static final String NEW_LINE = "\n";

    private static final List<String> fillers = asList("And", "But");

    private static final BindingsManager bindingsManager = new BindingsManager();

    private StoryProcessor() throws InstantiationException {
        throw new InstantiationException("This class is not for instantiation");
    }

    public static void ignore(final String feature, final String scenario) {
        log.warn("Feature: " + feature + " - Scenario: " + scenario + " (@ignored)");
    }

    public static void process(final String feature, final String scenario, final String story,
        final String projectionDetails, final String reports, final Object instance) throws Throwable {

        boolean processedDependsOn = false;
        try {
            processedDependsOn = invokeDependsOn(instance, getTestDependencies(instance.getClass()));

            log.info("Feature: " + feature + " - Scenario: " + scenario);
            if (projectionDetails != null && !projectionDetails.isEmpty()) {
                log.info(projectionDetails);
            }

            Map<String, String> projectionValues = new HashMap<>();
            if (projectionDetails != null && !projectionDetails.isEmpty()) {
                projectionValues = new Gson().fromJson(projectionDetails, projectionType.getType());
            }

            final Method[] methods = instance.getClass().getMethods();
            final String[] lines = story.split(NEW_LINE);

            processedDependsOn |= invokeSteps(lines, instance, methods, projectionValues);

        } catch (final InvocationTargetException ex) {
            processReports(reports, ex.getCause());
            throw ex.getCause();
        } catch (final Throwable t) {
            processReports(reports, t);
            throw t;
        } finally {
            if (processedDependsOn) {
                bindingsManager.reset();
            }
        }

        processReports(reports, null);
    }

    final static boolean invokeSteps(final String[] steps, final Object instance, final Method[] methods,
                                     final  Map<String, String> projectionValues) throws Throwable {

        boolean processedDependsOn = false;
        final List<MethodDetails> calls = new ArrayList<>();
        MethodDetails found;
        String previousType = null;

        for (final String line : steps) {
            final int firstSpace = line.indexOf(" ");
            String type = line.substring(0, firstSpace);
            if (fillers.contains(type)) {
                if (previousType != null) {
                    type = previousType;
                } else {
                    logAndThrow("Invalid step: '" + line + "' - '" + type
                      + "' step must be preceded with a Given, When or Then step: ");
                }
            } else {
                previousType = type;
            }

            final String step = line.substring(firstSpace + 1);
            found = findMethodWithAnnotation(type, step, methods, projectionValues);
            if (found != null) {
                calls.add(found);
            } else {
                logAndThrow("Failed to find step: " + line);
            }
        }

        for (int i = 0; i < calls.size(); i++) {
            log.info("> " + steps[i]);
            final MethodDetails details = calls.get(i);
            processedDependsOn |= invokeDependsOn(instance, getTestDependencies(details.getMethod()));
            processedDependsOn |= invokePreSteps(details.getMethod(), instance, methods, projectionValues);
            details.getMethod().invoke(instance, details.getArguments());
            processedDependsOn |= invokePostSteps(details.getMethod(), instance, methods, projectionValues);
        }

        return processedDependsOn;
    }

    private static boolean invokePreSteps(final Method method, final Object instance, final Method[] methods,
                                       final  Map<String, String> projectionValues) throws Throwable {
        if (method.isAnnotationPresent(PreSteps.class)) {
            final String[] lines = method.getAnnotation(PreSteps.class).value();
            return invokeSteps(lines, instance, methods, projectionValues);
        }
        return false;
    }

    private static boolean invokePostSteps(final Method method, final Object instance, final Method[] methods,
                                       final  Map<String, String> projectionValues) throws Throwable {
        if (method.isAnnotationPresent(PostSteps.class)) {
            final String[] lines = method.getAnnotation(PostSteps.class).value();
            return invokeSteps(lines, instance, methods, projectionValues);
        }
        return false;
    }

    private static MethodDetails findMethodWithAnnotation(final String type, final String step, final Method[] methods, final Map<String, String> projectionValues) {
        for (final Method method : methods) {
            String annotationValue;

            if (isSet(annotationValue = isGiven(type, step, method))) {
            } else if (isSet(annotationValue = isWhen(type, step, method))) {
            } else if (isSet(annotationValue = isThen(type, step, method))) {
            }

            if (isSet(annotationValue)) {
                return MethodDetails.build(method, step, projectionValues, annotationValue);
            }
        }
        return null;
    }

    private static String isGiven(final String type, final String step, final Method method) {
        if (Given.class.getName().endsWith(type) && method.isAnnotationPresent(Given.class)) {
            return isSameStep(method.getAnnotation(Given.class).value(), step, method);
        }
        return null;
    }

    private static String isWhen(final String type, final String step, final Method method) {
        if (When.class.getName().endsWith(type) && method.isAnnotationPresent(When.class)) {
            return isSameStep(method.getAnnotation(When.class).value(), step, method);
        }

        return null;
    }

    private static String isThen(final String type, final String step, final Method method) {
        if (Then.class.getName().endsWith(type) && method.isAnnotationPresent(Then.class)) {
            return isSameStep(method.getAnnotation(Then.class).value(), step, method);
        }
        return null;
    }

    private static String isSameStep(final String stepValues[], final String step, final Method method) {
        for (final String stepValue : stepValues) {
            if (stepValue.equals(step) ||
              step.matches(stepValue) ||
              step.matches(stepValue.replaceAll("<(.+?)>", "(.*)"))) {
                return stepValue;
            }
        }
        return null;
    }

    private static void logAndThrow(final String message) {
        log.error(message);
        throw new RuntimeException(message);
    }

    private static void processReports(final String reports, final Throwable error) {
        if (!isSet(reports)) {
            return;
        }

        final List<ReportDetails> reportDetails = new Gson().fromJson(reports, reportType.getType());
        for (final ReportDetails detail : reportDetails) {
            final Report report = reportLoader.get(detail.getReport());
            if (isSet(report)) {
                report.report(detail.getArguments(), error);
            }
        }
    }

    private final static List<DependsOn> getTestDependencies(final AnnotatedElement element) {
        final List<DependsOn> dependencies = new ArrayList<>();
        final DependsOn single = DependsOn.class.cast(element.getAnnotation(DependsOn.class));
        if (isSet(single)) {
            dependencies.add(single);
        } else {
            final Dependencies multiple = Dependencies.class.cast(element.getAnnotation(Dependencies.class));
            if (isSet(multiple)) {
                dependencies.addAll(asList(multiple.value()));
            }
        }
        return dependencies;
    }

    private final static boolean invokeDependsOn(final Object instance, final List<DependsOn> dependencies) throws Exception {
        if (!isSet(dependencies)) return false;
        loadInjectables(instance);
        for (final DependsOn dependsOn : dependencies) {
            final Class test = dependsOn.value();
            if (dependsOn.methods().length == 0) {
                new JUnitCore().run(test);
            } else {
                for (final String method : dependsOn.methods()) {
                    new JUnitCore().run(method(test, method));
                }
            }
        }
        return true;
    }

    private final static void loadInjectables(final Object instance) throws Exception {
        if (!bindingsManager.hasBindings()) {
            for (final Field field : instance.getClass().getDeclaredFields()) {
                final ColaInjectable injectable = field.getAnnotation(ColaInjectable.class);
                if (injectable != null) {
                    field.setAccessible(true);

                    final NamedInstance ni = new NamedInstance();
                    ni.name = injectable.value().isEmpty() ? field.getName() : injectable.value();
                    ni.instance = field.get(instance);

                    bindingsManager.addBinding(field.getType(), ni);
                }
            }
        }
    }

    public final static Injector initColaInjector(final Object instance) {
        if (bindingsManager.hasBindings()) {
            try {
                final Injector injector = createInjector(new ColaGuiceModule(bindingsManager.getBindings()));
                injector.injectMembers(instance);
                return injector;
            } catch (final Exception e) {
                log.error("@ColaInjected class has injection errors: " + e.getMessage());
            }
        }
        return null;
    }
}