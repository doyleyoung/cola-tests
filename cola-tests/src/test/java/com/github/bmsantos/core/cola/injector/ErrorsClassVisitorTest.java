package com.github.bmsantos.core.cola.injector;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.TraceClassVisitor;

public class ErrorsClassVisitorTest {

    private static final String ERROR_MESSAGE = "THIS IS AN ERROR MESSAGE";

    private ClassReader cr;
    private ErrorsClassVisitor uut;
    private ByteArrayOutputStream capture;

    @Before
    public void init() {
        capture = new ByteArrayOutputStream();
    }

    @Test
    public void shouldInjectErrorMethod() throws IOException {
        // Given
        final ClassVisitor cw = initClassWriterFor("test.utils.InvalidTest");
        uut = new ErrorsClassVisitor(cw, ERROR_MESSAGE);

        // When
        cr.accept(uut, 0);

        // Then
        assertThat(capture.toString(), containsString(ERROR_MESSAGE));
    }

    private ClassVisitor initClassWriterFor(final String className) throws IOException {
        cr = new ClassReader(className);
        final ClassWriter cw = new ClassWriter(cr, COMPUTE_FRAMES | COMPUTE_MAXS);
        final TraceClassVisitor tcv = new TraceClassVisitor(cw, new PrintWriter(new PrintStream(capture), true));
        return tcv;
    }
}
