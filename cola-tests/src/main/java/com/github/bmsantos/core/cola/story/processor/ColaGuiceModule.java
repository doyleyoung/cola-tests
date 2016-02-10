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

import java.util.Collection;
import java.util.Map;

import com.google.inject.AbstractModule;

import static com.google.inject.name.Names.named;

public class ColaGuiceModule extends AbstractModule {

    private Map<Class, Map<String, NamedInstance>> bindings;

    public ColaGuiceModule(final Map<Class, Map<String, NamedInstance>> bindings) {
        this.bindings = bindings;
    }

    @Override
    protected void configure() {
        for (final Class clazz : bindings.keySet()) {
            final Collection<NamedInstance> instances = bindings.get(clazz).values();
            bind(clazz).toInstance(clazz.cast(instances.iterator().next().instance));
            for (final NamedInstance namedInstance : instances) {
                bind(clazz).annotatedWith(named(namedInstance.name)).toInstance(clazz.cast(namedInstance.instance));
            }
        }
    }
}
