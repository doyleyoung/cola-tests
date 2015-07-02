package com.github.bmsantos.core.cola.instrument;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import com.github.bmsantos.core.cola.injector.InfoClassVisitor;
import com.github.bmsantos.core.cola.injector.InjectorClassVisitor;

public class ColaTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(final ClassLoader classLoader, final String className, final Class<?> classBeingRedefined,
        final ProtectionDomain protectionDomain, final byte[] classfileBuffer) throws IllegalClassFormatException {

        try {

            final ClassReader reader = new ClassReader(classfileBuffer);
            final ClassWriter writer = new ClassWriter(reader, COMPUTE_FRAMES | COMPUTE_MAXS);
            final InfoClassVisitor info = new InfoClassVisitor(writer, classLoader);
            final InjectorClassVisitor injector = new InjectorClassVisitor(info);
            reader.accept(injector, 0);

            return writer.toByteArray();
        } catch (final Throwable t) {
            // empty
        }

        return classfileBuffer;
    }
}
