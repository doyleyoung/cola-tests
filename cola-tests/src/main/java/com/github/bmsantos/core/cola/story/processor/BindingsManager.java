package com.github.bmsantos.core.cola.story.processor;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Thread.currentThread;

public class BindingsManager {

    private final Map<Long, Map<Class, Map<String, NamedInstance>>> threadBindings = new HashMap<>();

    public  Map<Class, Map<String, NamedInstance>> getBindings() {
        if (!threadBindings.containsKey(currentThread().getId())) {
            threadBindings.put(currentThread().getId(), new HashMap<Class, Map<String, NamedInstance>>());
        }
        return threadBindings.get(currentThread().getId());
    }

    public Map<String, NamedInstance> getBindingsForType(final Class type) {
        final Map<Class, Map<String, NamedInstance>> bindings = getBindings();
        if (!bindings.containsKey(type)) {
            bindings.put(type, new HashMap<String, NamedInstance>());
        }
        return bindings.get(type);
    }

    public void addBinding(final Class type, final NamedInstance ni) {
        Map<String, NamedInstance> bindings = getBindingsForType(type);
        bindings.put(ni.name, ni);
    }

    public boolean hasBindings() {
        return !getBindings().isEmpty();
    }

    public void reset() {
        threadBindings.remove(currentThread().getId());
    }
}
