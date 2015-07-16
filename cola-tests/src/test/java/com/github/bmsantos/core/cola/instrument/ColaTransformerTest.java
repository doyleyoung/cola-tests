package com.github.bmsantos.core.cola.instrument;

import static java.lang.System.setProperty;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static test.utils.TestUtils.loadClassBytes;
import static test.utils.TestUtils.traceBytecode;

import org.junit.Before;
import org.junit.Test;

import test.utils.InvalidTest;
import test.utils.MultipleIdeEnablerTest;
import test.utils.StoriesFieldTest;

public class ColaTransformerTest {

    private ColaTransformer uut;

    @Before
    public void setUp() {
        setProperty("colaTests", ".*Test");
        uut = new ColaTransformer();
    }

    @Test
    public void shouldTransformClass() throws Exception {
        // Given
        final Class<?> clazz = StoriesFieldTest.class;
        final byte[] original = loadClassBytes(clazz);

        // When
        final byte[] result = uut.transform(ColaTransformer.class.getClassLoader(), clazz.getName(), clazz, null, original);

        // Then
        assertThat(result, not(equalTo(original)));
        final String trace = traceBytecode(result);
        assertThat(trace, containsString("@Lorg/junit/Test;()"));
    }

    @Test
    public void shouldNotTransformClass() throws Exception {
        // Given
        final Class<?> clazz = ColaTransformerTest.class;
        final byte[] original = loadClassBytes(clazz);

        // When
        final byte[] result = uut.transform(ColaTransformer.class.getClassLoader(), clazz.getName(), clazz, null, original);

        // Then
        assertThat(result, equalTo(original));
    }

    @Test
    public void shouldRemoveTransformClass() throws Exception {
        // Given
        final Class<?> clazz = MultipleIdeEnablerTest.class;
        final byte[] original = loadClassBytes(clazz);

        // When
        final byte[] result = uut.transform(ColaTransformer.class.getClassLoader(), clazz.getName(), clazz, null, original);

        // Then
        assertThat(result, not(equalTo(original)));
        final String trace = traceBytecode(result);
        assertThat(trace, not(containsString("shouldBeRemoved1")));
        assertThat(trace, not(containsString("shouldBeRemoved2")));
    }

    @Test
    public void shouldInjectErrorMethod() throws Exception {
        // Given
        final Class<?> clazz = InvalidTest.class;
        final byte[] original = loadClassBytes(clazz);

        // When
        final byte[] result = uut.transform(ColaTransformer.class.getClassLoader(), clazz.getName(), clazz, null, original);

        // Then
        assertThat(result, not(equalTo(original)));
        final String trace = traceBytecode(result);
        assertThat(trace, containsString("Cola Tests : Compilation Errors"));
    }

    @Test
    public void shouldSetColaTestsFilterPropertyAndSkipNonmatchingClass() throws Exception {
        // Given
        setProperty("colaTests", ".*Foo");
        final Class<?> clazz = StoriesFieldTest.class;
        final byte[] original = loadClassBytes(clazz);

        // When
        final byte[] result = uut.transform(ColaTransformer.class.getClassLoader(), clazz.getName(), clazz, null, original);

        // Then
        assertThat(result, equalTo(original));
    }
    
    @Test
    public void shouldSetColaTestsFilterPropertyAndFilterMatchingClass() throws Exception {
        // Given
        setProperty("colaTests", ".*FieldTest");
        final Class<?> clazz = StoriesFieldTest.class;
        final byte[] original = loadClassBytes(clazz);

        // When
        final byte[] result = uut.transform(ColaTransformer.class.getClassLoader(), clazz.getName(), clazz, null, original);

        // Then
        assertThat(result, not(equalTo(original)));
    }

    @Test
    public void shouldAllowMultipleFilters() throws Exception {
        // Given
        setProperty("colaTests", ".*FooTest,.*FieldTest");
        final Class<?> clazz = StoriesFieldTest.class;
        final byte[] original = loadClassBytes(clazz);

        // When
        final byte[] result = uut.transform(ColaTransformer.class.getClassLoader(), clazz.getName(), clazz, null, original);

        // Then
        assertThat(result, not(equalTo(original)));
    }
}