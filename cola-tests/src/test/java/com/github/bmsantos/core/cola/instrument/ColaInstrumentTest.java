package com.github.bmsantos.core.cola.instrument;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.lang.instrument.Instrumentation;

import org.junit.Test;

public class ColaInstrumentTest {

    private static final String NO_ARGS = null;

    @Test
    public void shouldAddTransformer() {
        // Given
        final Instrumentation instrumentation = mock(Instrumentation.class);

        // When
        ColaInstrument.premain(NO_ARGS, instrumentation);

        // Then
        verify(instrumentation).addTransformer(any(ColaTransformer.class));
    }

}
