package com.github.bmsantos.core.cola.injector;

import java.io.IOException;

import com.github.bmsantos.core.cola.exceptions.InvalidFeature;
import com.github.bmsantos.core.cola.exceptions.InvalidFeatureUri;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;

public class InfoClassVisitorTest {

    private ClassReader cr;
    private InfoClassVisitor uut;

    @Test
    public void shouldRetrieveDefaultField() throws IOException {
        // Given
        final ClassWriter cw = initClassWriterFor("test.utils.StoriesFieldTest");
        uut = new InfoClassVisitor(cw, getClass().getClassLoader());

        // When
        cr.accept(uut, 0);

        // Then
        assertThat(uut.getFeatures().isEmpty(), is(false));
    }

    @Test
    public void shouldRetrieveAnnotatedField() throws IOException {
        // Given
        final ClassWriter cw = initClassWriterFor("test.utils.FieldAnnotatedTest");
        uut = new InfoClassVisitor(cw, getClass().getClassLoader());

        // When
        cr.accept(uut, 0);

        // Then
        assertThat(uut.getFeatures().isEmpty(), is(false));
    }

    @Test
    public void shouldRetrieveMultipleAnnotatedField() throws IOException {
        // Given
        final ClassWriter cw = initClassWriterFor("test.utils.MultipleFieldAnnotatedTest");
        uut = new InfoClassVisitor(cw, getClass().getClassLoader());

        // When
        cr.accept(uut, 0);

        // Then
        assertThat(uut.getFeatures().size(), is(2));
    }

    @Test
    public void shouldRetrieveAnnotatedClass() throws IOException {
        // Given
        final ClassWriter cw = initClassWriterFor("test.utils.ClassAnnotatedTest");
        uut = new InfoClassVisitor(cw, getClass().getClassLoader());

        // When
        cr.accept(uut, 0);

        // Then
        assertThat(uut.getFeatures().isEmpty(), is(false));
    }

    @Test
    public void shouldRetrieveMultipleAnnotatedClass() throws IOException {
        // Given
        final ClassWriter cw = initClassWriterFor("test.utils.MultipleFilesClassAnnotatedTest");
        uut = new InfoClassVisitor(cw, getClass().getClassLoader());

        // When
        cr.accept(uut, 0);

        // Then
        assertThat(uut.getFeatures().size(), is(2));
    }

    @Test
    public void shouldRetrieveComplexClass() throws IOException {
        // Given
        final ClassWriter cw = initClassWriterFor("test.utils.ComplexTest");
        uut = new InfoClassVisitor(cw, getClass().getClassLoader());

        // When
        cr.accept(uut, 0);

        // Then
        assertThat(uut.getFeatures().size(), is(5));
    }

    @Test(expected = InvalidFeature.class)
    public void shouldFailToLoadInvalidFieldAnnotatedClass() throws IOException {
        // Given
        final ClassWriter cw = initClassWriterFor("test.utils.InvalidFieldAnnotatedTest");
        uut = new InfoClassVisitor(cw, getClass().getClassLoader());

        // When
        cr.accept(uut, 0);
    }

    @Test(expected = InvalidFeature.class)
    public void shouldFailToLoadEmptyFieldAnnotatedClass() throws IOException {
        // Given
        final ClassWriter cw = initClassWriterFor("test.utils.EmptyFieldAnnotatedTest");
        uut = new InfoClassVisitor(cw, getClass().getClassLoader());

        // When
        cr.accept(uut, 0);
    }

    @Test(expected = InvalidFeature.class)
    public void shouldFailToLoadInFeatureWithMalformedProjections() throws IOException {
        // Given
        final ClassWriter cw = initClassWriterFor("test.utils.MalformedProjectionTest");
        uut = new InfoClassVisitor(cw, getClass().getClassLoader());

        // When
        cr.accept(uut, 0);
    }

    @Test(expected = InvalidFeatureUri.class)
    public void shouldFailToLoadInvalidAnnotatedClass() throws IOException {
        // Given
        final ClassWriter cw = initClassWriterFor("test.utils.InvalidFeatureAnnotatedTest");
        uut = new InfoClassVisitor(cw, getClass().getClassLoader());

        // When
        cr.accept(uut, 0);
    }

    @Test(expected = InvalidFeatureUri.class)
    public void shouldFailToLoadEmptyFeatureAnnotatedClass() throws IOException {
        // Given
        final ClassWriter cw = initClassWriterFor("test.utils.EmptyFeatureAnnotatedTest");
        uut = new InfoClassVisitor(cw, getClass().getClassLoader());

        // When
        cr.accept(uut, 0);
    }

    @Test
    public void shouldListIdeEnabledMethod() throws IOException {
        // Given
        final ClassWriter cw = initClassWriterFor("test.utils.IdeEnablerTest");
        uut = new InfoClassVisitor(cw, getClass().getClassLoader());

        // When
        cr.accept(uut, 0);

        // Then
        assertThat(uut.getIdeEnabledMethods(), hasItem("shouldBeRemoved"));
    }

    @Test
    public void shouldListAllIdeEnabledMethods() throws IOException {
        // Given
        final ClassWriter cw = initClassWriterFor("test.utils.MultipleIdeEnablerTest");
        uut = new InfoClassVisitor(cw, getClass().getClassLoader());

        // When
        cr.accept(uut, 0);

        // Then
        assertThat(uut.getIdeEnabledMethods(), contains("shouldBeRemoved1", "shouldBeRemoved2"));
    }

    @Test
    public void shouldNotHaveIdeEnabledMethods() throws IOException {
        // Given
        final ClassWriter cw = initClassWriterFor("test.utils.StoriesFieldTest");
        uut = new InfoClassVisitor(cw, getClass().getClassLoader());

        // When
        cr.accept(uut, 0);

        // Then
        assertThat(uut.getIdeEnabledMethods().size(), is(0));
    }

    @Test
    public void shouldBeColaInjected() throws IOException {
        // Given
        final ClassWriter cw = initClassWriterFor("test.utils.ColaInjectedTest");
        uut = new InfoClassVisitor(cw, getClass().getClassLoader());

        // When
        cr.accept(uut, 0);

        // Then
        assertThat(uut.isColaInjected(), is(true));
    }

    @Test
    public void shouldListColaInjectorFields() throws IOException {
        // Given
        final ClassWriter cw = initClassWriterFor("test.utils.ColaInjectorTest");
        uut = new InfoClassVisitor(cw, getClass().getClassLoader());

        // When
        cr.accept(uut, 0);

        // Then
        assertThat(uut.getColaInjectorFields(), hasItems("i1", "i2"));
    }

    private ClassWriter initClassWriterFor(final String className) throws IOException {
        cr = new ClassReader(className);
        final ClassWriter cw = new ClassWriter(cr, COMPUTE_FRAMES | COMPUTE_MAXS);
        return cw;
    }
}

