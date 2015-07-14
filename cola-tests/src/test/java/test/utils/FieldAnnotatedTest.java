package test.utils;

import com.github.bmsantos.core.cola.story.annotations.Feature;

public class FieldAnnotatedTest {
    @Feature
    private final String annotatedField =
    "Feature: Load feature from annotated field\n"
        + "Scenario: Should have scenario steps\n"
        + "Given A\n"
        + "When B\n"
        + "Then C\n";
}
