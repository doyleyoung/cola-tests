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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.bmsantos.core.cola.story.annotations.Group;
import com.github.bmsantos.core.cola.story.annotations.Projection;

public class MethodDetails {

	private final Method method;
	private final List<String> projections;
	private final Object[] arguments;

	private MethodDetails(final Method method, final List<String> projections, final Object[] arguments) {
		this.method = method;
		this.projections = projections;
		this.arguments = arguments;
	}

	public Method getMethod() {
		return method;
	}

	public boolean hasProjections() {
		return projections != null && !projections.isEmpty();
	}

	public List<String> getProjections() {
		return projections;
	}

	public Object[] getArguments() {
		return arguments;
	}

	private static List<String> prepareProjections(final String step) {

		final List<String> results = new ArrayList<>();

		if (step != null) {
			final Pattern pattern = Pattern.compile("<(.+?)>");
			final Matcher matcher = pattern.matcher(step);
			while (matcher.find()) {
				results.add(matcher.group(1));
			}
		}

		return results;
	}

	private static List<String> prepareGroups(final String step, final Method method, final String annotationValue) {

		final List<String> results = new ArrayList<>();

		if (step != null && annotationValue != null) {
			final Pattern pattern = Pattern.compile(annotationValue);
			final Matcher matcher = pattern.matcher(step);
			if (matcher.matches()) {
				for( int i = 0; i <= matcher.groupCount(); i++ ) {
					results.add(matcher.group(i));
				}
			}
		}

		return results;
	}

	private static Object[] prepareArguments(final Method method, final List<String> projections,
			final Map<String, String> projectionValues, final List<String> groups) {

		final Annotation[][] params = method.getParameterAnnotations();
		final Object[] args = new Object[params.length];

		if ((projectionValues == null || projectionValues.isEmpty()) && (groups == null || groups.isEmpty())) {
			return args;
		}

		final Class<?>[] types = method.getParameterTypes();
		for (int i = 0; i < params.length; i++) {
			args[i] = null;
			for (final Annotation annotation : params[i]) {
				if (annotation.annotationType().equals(Projection.class)) {
					final Projection projection = (Projection) annotation;
					if (projections.contains(projection.value())) {
						args[i] = generateValue(types[i], projectionValues.get(projection.value()));
					}
				} else if (annotation.annotationType().equals(Group.class)) {
					final Group group = (Group) annotation;
					if (groups.size() > group.value()) {
						args[i] = generateValue(types[i], groups.get(group.value()));
					}
				}
			}
		}

		return args;
	}

	private static Object generateValue(final Class<?> type, final String value) {
		if (type.isAssignableFrom(String.class)) {
			return value;
		} else if (type.isAssignableFrom(Boolean.class)) {
			return Boolean.valueOf(value);
		} else if (type.isAssignableFrom(Byte.class)) {
			return Byte.valueOf(value);
		} else if (type.isAssignableFrom(Short.class)) {
			return Short.valueOf(value);
		} else if (type.isAssignableFrom(Integer.class)) {
			return Integer.valueOf(value);
		} else if (type.isAssignableFrom(Long.class)) {
			return Long.valueOf(value);
		} else if (type.isAssignableFrom(Float.class)) {
			return Float.valueOf(value);
		} else if (type.isAssignableFrom(Double.class)) {
			return Double.valueOf(value);
		}
		return null;
	}

	public static MethodDetails build(final Method method, final String step,
			final Map<String, String> projectionValues, final String annotationValue) {
		final List<String> projections = prepareProjections(step);
		final List<String> groups = prepareGroups(step, method, annotationValue);
		final Object[] arguments = prepareArguments(method, projections, projectionValues, groups);

		return new MethodDetails(method, projections, arguments);
	}
}
