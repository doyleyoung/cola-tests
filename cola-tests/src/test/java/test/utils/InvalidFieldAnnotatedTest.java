package test.utils;

import com.github.bmsantos.core.cola.story.annotations.Feature;

public class InvalidFieldAnnotatedTest {
    @Feature
    private final String annotatedField = "This is invalid feature";
}
