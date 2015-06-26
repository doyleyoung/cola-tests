package com.github.bmsantos.core.cola.injector;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;

import java.io.IOException;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import com.github.bmsantos.core.cola.exceptions.InvalidFeature;
import com.github.bmsantos.core.cola.exceptions.InvalidFeatureUri;
import com.github.bmsantos.core.cola.story.annotations.Feature;
import com.github.bmsantos.core.cola.story.annotations.Features;

public class InfoClassVisitorTest {

    private ClassReader cr;
    private InfoClassVisitor uut;

    @Test
    public void shouldRetrieveDefaultField() throws IOException {
        // Given
        final ClassWriter cw = initClassWriterFor("com.github.bmsantos.core.cola.injector.InfoClassVisitorTest$StoriesFieldClass");
        uut = new InfoClassVisitor(cw, getClass().getClassLoader());

        // When
        cr.accept(uut, 0);

        // Then
        assertThat(uut.getFeatures().isEmpty(), is(false));
    }

    @Test
    public void shouldRetrieveAnnotatedField() throws IOException {
        // Given
        final ClassWriter cw = initClassWriterFor("com.github.bmsantos.core.cola.injector.InfoClassVisitorTest$FieldAnnotatedClass");
        uut = new InfoClassVisitor(cw, getClass().getClassLoader());

        // When
        cr.accept(uut, 0);

        // Then
        assertThat(uut.getFeatures().isEmpty(), is(false));
    }

    @Test
    public void shouldRetrieveMultipleAnnotatedField() throws IOException {
        // Given
        final ClassWriter cw = initClassWriterFor("com.github.bmsantos.core.cola.injector.InfoClassVisitorTest$MultipleFieldAnnotatedClass");
        uut = new InfoClassVisitor(cw, getClass().getClassLoader());

        // When
        cr.accept(uut, 0);

        // Then
        assertThat(uut.getFeatures().size(), is(2));
    }

    @Test
    public void shouldRetrieveAnnotatedClass() throws IOException {
        // Given
        final ClassWriter cw = initClassWriterFor("com.github.bmsantos.core.cola.injector.InfoClassVisitorTest$ClassAnnotatedClass");
        uut = new InfoClassVisitor(cw, getClass().getClassLoader());

        // When
        cr.accept(uut, 0);

        // Then
        assertThat(uut.getFeatures().isEmpty(), is(false));
    }

    @Test
    public void shouldRetrieveMultipleAnnotatedClass() throws IOException {
        // Given
        final ClassWriter cw = initClassWriterFor("com.github.bmsantos.core.cola.injector.InfoClassVisitorTest$MultipleFilesClassAnnotatedClass");
        uut = new InfoClassVisitor(cw, getClass().getClassLoader());

        // When
        cr.accept(uut, 0);

        // Then
        assertThat(uut.getFeatures().size(), is(2));
    }
    
    @Test
    public void shouldRetrieveComplexClass() throws IOException {
        // Given
        final ClassWriter cw = initClassWriterFor("com.github.bmsantos.core.cola.injector.InfoClassVisitorTest$ComplexClass");
        uut = new InfoClassVisitor(cw, getClass().getClassLoader());

        // When
        cr.accept(uut, 0);

        // Then
        assertThat(uut.getFeatures().size(), is(5));
    }

    @Test(expected = InvalidFeature.class)
    public void shouldFailToLoadInvalidFieldAnnotatedClass() throws IOException {
        // Given
        final ClassWriter cw = initClassWriterFor("com.github.bmsantos.core.cola.injector.InfoClassVisitorTest$InvalidFieldAnnotatedClass");
        uut = new InfoClassVisitor(cw, getClass().getClassLoader());

        // When
        cr.accept(uut, 0);
    }

    @Test(expected = InvalidFeature.class)
    public void shouldFailToLoadEmptyFieldAnnotatedClass() throws IOException {
        // Given
        final ClassWriter cw = initClassWriterFor("com.github.bmsantos.core.cola.injector.InfoClassVisitorTest$EmptyFieldAnnotatedClass");
        uut = new InfoClassVisitor(cw, getClass().getClassLoader());

        // When
        cr.accept(uut, 0);
    }

    @Test(expected = InvalidFeatureUri.class)
    public void shouldFailToLoadInvalidAnnotatedClass() throws IOException {
        // Given
        final ClassWriter cw = initClassWriterFor("com.github.bmsantos.core.cola.injector.InfoClassVisitorTest$InvalidFeatureAnnotatedClass");
        uut = new InfoClassVisitor(cw, getClass().getClassLoader());

        // When
        cr.accept(uut, 0);
    }

    @Test(expected = InvalidFeatureUri.class)
    public void shouldFailToLoadEmptyFeatureAnnotatedClass() throws IOException {
        // Given
        final ClassWriter cw = initClassWriterFor("com.github.bmsantos.core.cola.injector.InfoClassVisitorTest$EmptyFeatureAnnotatedClass");
        uut = new InfoClassVisitor(cw, getClass().getClassLoader());

        // When
        cr.accept(uut, 0);
    }
    
    private ClassWriter initClassWriterFor(final String className) throws IOException {
        cr = new ClassReader(className);
        final ClassWriter cw = new ClassWriter(cr, COMPUTE_FRAMES | COMPUTE_MAXS);
        return cw;
    }

    private class StoriesFieldClass {
        private final String stories =
            "Feature: Load feature from default field\n"
                + "Scenario: Should have scenario steps\n"
                + "Given A\n"
                + "When B\n"
                + "Then C\n";
    }

    private class FieldAnnotatedClass {
        @Feature
        private final String annotatedField =
        "Feature: Load feature from annotated field\n"
            + "Scenario: Should have scenario steps\n"
            + "Given A\n"
            + "When B\n"
            + "Then C\n";
    }

    private class MultipleFieldAnnotatedClass {
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

    @Features({ "gherkin-example", "other-gherkin-example" })
    private class ComplexClass {
        private final String stories =
            "Feature: Load feature from default field\n"
                + "Scenario: Should have scenario steps\n"
                + "Given A\n"
                + "When B\n"
                + "Then C\n";
        
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
    
    @Features("gherkin-example")
    private class ClassAnnotatedClass {
    }

    @Features({ "gherkin-example", "other-gherkin-example" })
    private class MultipleFilesClassAnnotatedClass {
    }
    
    private class InvalidFieldAnnotatedClass {
        @Feature
        private final String annotatedField = "This is invalid feature";
    }

    private class EmptyFieldAnnotatedClass {
        @Feature
        private final String annotatedField = "";
    }

    @Features("invalid_feature")
    private class InvalidFeatureAnnotatedClass {
    }

    @Features("empty_feature")
    private class EmptyFeatureAnnotatedClass {
    }
}

