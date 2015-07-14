package test.utils;

import com.github.bmsantos.core.cola.story.annotations.Feature;

public class MultipleFieldAnnotatedTest {
    @Feature
    private final String annotatedField1 =
    "Feature: Load feature from annotated field1\n"
        + "Scenario: Should have scenario steps\n"
        + "Given A\n"
        + "When B\n"
        + "Then C\n";

    @Feature
    private final String annotatedField2 =
    "Feature: Load feature from annotated field2\n"
        + "Scenario: Should have scenario steps\n"
        + "Given A\n"
        + "When B\n"
        + "Then C\n";
}
