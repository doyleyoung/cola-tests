package com.github.bmsantos.core.cola.story.processor;

import java.lang.reflect.Field;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class BindingsManagerTest {

    private final NamedInstance ni = new NamedInstance();
    private Field field;
    private BindingsManager uut;

    @Before
    public void setUp() throws Exception {
        uut = new BindingsManager();

        field = BindingsManagerTest.class.getDeclaredField("field");
        ni.name = field.getName();
        ni.instance = field;
    }

    @Test
    public void shouldAddBinding() {
        // When
        uut.addBinding(Field.class, ni);

        // Then
        assertTrue(uut.getBindings().containsKey(Field.class));
        assertThat(uut.getBindingsForType(Field.class), notNullValue());
        assertTrue(uut.getBindingsForType(Field.class).containsKey(ni.name));
    }

    @Test
    public void shouldHaveBindings() {
        // Given
        uut.addBinding(Field.class, ni);

        // When
        final boolean result = uut.hasBindings();

        // Then
        assertTrue(result);
    }

    @Test
    public void shouldResetBindings() {
        // Given
        uut.addBinding(Field.class, ni);

        // When
        uut.reset();

        // Then
        assertFalse(uut.hasBindings());
    }
}
