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
import com.github.bmsantos.core.cola.story.annotations.DependsOn;
import com.github.bmsantos.core.cola.story.annotations.Given;
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
import static org.slf4j.LoggerFactory.getLogger;

public class StoryProcessor {
    private static final Logger log = getLogger(StoryProcessor.class);

    private static final TypeToken<Map<String, String>> projectionType = new TypeToken<Map<String, String>>() {};
    private static final TypeToken<List<ReportDetails>> reportType = new TypeToken<List<ReportDetails>>() {};

    private static final String NEW_LINE = "\n";

    private static final List<String> fillers = asList("And", "But");

    private static final BindingsManager bindingsManager = new BindingsManager();

    public static void ignore(final String feature, final String scenario) {
        log.warn("Feature: " + feature + " - Scenario: " + scenario + " (@ignored)");
    }

    public static void process(final String feature, final String scenario, final String story,
        final String projectionDetails, final String reports, final Object instance) throws Throwable {

        boolean processedDependsOn = false;
        try {
            processedDependsOn = invokeDependsOn(instance, instance.getClass().getAnnotation(DependsOn.class));

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

            final List<MethodDetails> calls = new ArrayList<>();
            MethodDetails found;
            String previousType = null;
            for (final String line : lines) {

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
                log.info("> " + lines[i]);
                final MethodDetails details = calls.get(i);
                processedDependsOn |= invokeDependsOn(instance, details.getMethod().getAnnotation(DependsOn.class));
                details.getMethod().invoke(instance, details.getArguments());
            }
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

    private static MethodDetails findMethodWithAnnotation(final String type, final String step, final Method[] methods, final Map<String, String> projectionValues) {
        for (final Method method : methods) {
            boolean foundMethod = false;
            String annotationValue = null;

            if (isGiven(type, step, method)) {
                foundMethod = true;
                annotationValue = method.getAnnotation(Given.class).value();
            } else if (isWhen(type, step, method)) {
                foundMethod = true;
                annotationValue = method.getAnnotation(When.class).value();
            } else if (isThen(type, step, method)) {
                foundMethod = true;
                annotationValue = method.getAnnotation(Then.class).value();
            }

            if (foundMethod) {
                return MethodDetails.build(method, step, projectionValues, annotationValue);
            }
        }
        return null;
    }

    private static boolean isGiven(final String type, final String step, final Method method) {
        return Given.class.getName().endsWith(type) && method.isAnnotationPresent(Given.class)
            && (method.getAnnotation(Given.class).value().equals(step) ||
                step.matches(method.getAnnotation(Given.class).value()) ||
                step.matches(method.getAnnotation(Given.class).value().replaceAll("<(.+?)>", "(.*)")));
    }

    private static boolean isWhen(final String type, final String step, final Method method) {
        return When.class.getName().endsWith(type) && method.isAnnotationPresent(When.class)
            && (method.getAnnotation(When.class).value().equals(step) ||
                step.matches(method.getAnnotation(When.class).value()) ||
                step.matches(method.getAnnotation(When.class).value().replaceAll("<(.+?)>", "(.*)")));
    }

    private static boolean isThen(final String type, final String step, final Method method) {
        return Then.class.getName().endsWith(type) && method.isAnnotationPresent(Then.class)
            && (method.getAnnotation(Then.class).value().equals(step) ||
                step.matches(method.getAnnotation(Then.class).value()) ||
                step.matches(method.getAnnotation(Then.class).value().replaceAll("<(.+?)>", "(.*)")));
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

    private final static boolean invokeDependsOn(final Object instance, final DependsOn dependsOn) throws Exception {
        if (!isSet(dependsOn)) return false;
        loadInjectables(instance);
        for (final Class test : dependsOn.value()) {
            new JUnitCore().run(test);
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