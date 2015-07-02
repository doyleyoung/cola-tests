package com.github.bmsantos.core.cola.instrument;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static test.utils.TestUtils.loadClassBytes;

import org.junit.Before;
import org.junit.Test;

import test.utils.StoriesFieldClass;

public class ColaTransformerTest {

    private ColaTransformer uut;

    @Before
    public void setUp() {
        uut = new ColaTransformer();
    }

    @Test
    public void shouldTransformClass() throws Exception {
        // Given
        final Class<?> clazz = StoriesFieldClass.class;
        final byte[] original = loadClassBytes(clazz);

        // When
        final byte[] result = uut.transform(ColaTransformer.class.getClassLoader(), clazz.getName(), clazz, null,
            original);

        // Then
        assertThat(result, not(equalTo(original)));
    }

    @Test
    public void shouldNotTransformClass() throws Exception {
        // Given
        final Class<?> clazz = ColaTransformerTest.class;
        final byte[] original = loadClassBytes(clazz);

        // When
        final byte[] result = uut.transform(ColaTransformer.class.getClassLoader(), clazz.getName(), clazz, null,
            original);

        // Then
        assertThat(result, equalTo(original));
    }
}