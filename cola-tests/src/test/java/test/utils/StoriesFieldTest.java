package test.utils;

import com.github.bmsantos.core.cola.story.annotations.ColaInjector;
import com.google.inject.Injector;

public class StoriesFieldTest {
    private final String stories =
        "Feature: Load feature from default field\n"
            + "Scenario: Should have scenario steps\n"
            + "Given A\n"
            + "When B\n"
            + "Then C\n";

    @ColaInjector
    public Injector colaInjector;
}