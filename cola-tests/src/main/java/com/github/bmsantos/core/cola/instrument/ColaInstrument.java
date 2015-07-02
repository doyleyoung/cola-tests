package com.github.bmsantos.core.cola.instrument;

import java.lang.instrument.Instrumentation;

public class ColaInstrument {
    public static void premain(final String agentArgs, final Instrumentation instrumentation) {
        instrumentation.addTransformer(new ColaTransformer());
    }
}
