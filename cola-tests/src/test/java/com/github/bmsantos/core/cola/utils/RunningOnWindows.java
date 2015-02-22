package com.github.bmsantos.core.cola.utils;

import static java.lang.System.getProperty;

import com.codeaffine.test.ConditionalIgnoreRule.IgnoreCondition;

public class RunningOnWindows implements IgnoreCondition {
    @Override
    public boolean isSatisfied() {
        return getProperty("os.name").startsWith("Windows");
    }
}
