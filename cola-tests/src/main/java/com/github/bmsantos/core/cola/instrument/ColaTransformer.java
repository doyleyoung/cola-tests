package com.github.bmsantos.core.cola.instrument;

import static java.util.Arrays.copyOf;
import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import com.github.bmsantos.core.cola.injector.InfoClassVisitor;
import com.github.bmsantos.core.cola.injector.InjectorClassVisitor;
import com.github.bmsantos.core.cola.injector.MethodRemoverClassVisitor;

public class ColaTransformer implements ClassFileTransformer {

    private static final int WRITER_FLAGS = COMPUTE_FRAMES | COMPUTE_MAXS;

    private final List<String> methodsToRemove = new ArrayList<>();

    public void removeMethod(final String methodName) {
        methodsToRemove.add(methodName);
    }

    @Override
    public byte[] transform(final ClassLoader classLoader, final String className, final Class<?> classBeingRedefined,
        final ProtectionDomain protectionDomain, final byte[] classfileBuffer) throws IllegalClassFormatException {

        final byte[] instrumentedBuffer = copyOf(classfileBuffer, classfileBuffer.length);

        try {

            final ClassReader reader = new ClassReader(instrumentedBuffer);
            final ClassWriter writer = new ClassWriter(reader, WRITER_FLAGS);
            final InfoClassVisitor info = new InfoClassVisitor(writer, classLoader);
            final InjectorClassVisitor injector = new InjectorClassVisitor(info);
            reader.accept(injector, 0);

            info.getIdeEnabledMethods().addAll(methodsToRemove);

            if (!info.getIdeEnabledMethods().isEmpty()) {
                return removeMethods(writer, info);
            }

            if (!info.getFeatures().isEmpty() || !info.getIdeEnabledMethods().isEmpty()) {
                return writer.toByteArray();
            }
        } catch (final Throwable t) {
            // empty
        }

        return classfileBuffer;
    }

    private byte[] removeMethods(final ClassWriter writer, final InfoClassVisitor info) {
        final ClassReader rreader = new ClassReader(writer.toByteArray());
        final ClassWriter rwriter = new ClassWriter(rreader, WRITER_FLAGS);
        final MethodRemoverClassVisitor remover = new MethodRemoverClassVisitor(rwriter, info.getIdeEnabledMethods());
        rreader.accept(remover, 0);
        return rwriter.toByteArray();
    }
}
