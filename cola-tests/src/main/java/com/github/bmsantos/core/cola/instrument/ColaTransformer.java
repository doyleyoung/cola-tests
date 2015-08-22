package com.github.bmsantos.core.cola.instrument;

import static com.github.bmsantos.core.cola.utils.ColaUtils.binaryToOS;
import static com.github.bmsantos.core.cola.utils.ColaUtils.isSet;
import static java.lang.System.getProperty;
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

import com.github.bmsantos.core.cola.exceptions.InvalidFeature;
import com.github.bmsantos.core.cola.injector.ErrorsClassVisitor;
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

        if (!isTestClass(className)) {
            return classfileBuffer;
        }

        final byte[] instrumentedBuffer = copyOf(classfileBuffer, classfileBuffer.length);

        try {
            final ClassReader reader = new ClassReader(instrumentedBuffer);
            final ClassWriter writer = new ClassWriter(reader, WRITER_FLAGS);
            final InfoClassVisitor info = new InfoClassVisitor(writer, classLoader);
            final InjectorClassVisitor injector = new InjectorClassVisitor(info);
            reader.accept(injector, 0);

            info.getIdeEnabledMethods().addAll(methodsToRemove);

            if (!info.getIdeEnabledMethods().isEmpty()) {
                return removeMethods(writer.toByteArray(), info);
            }

            if (!info.getFeatures().isEmpty() || !info.getIdeEnabledMethods().isEmpty()) {
                return writer.toByteArray();
            }
        } catch (final InvalidFeature i) {
            return injectErrorNotificationMethod(classfileBuffer, i);
        } catch (final Throwable t) {
            return classfileBuffer;
        }

        return classfileBuffer;
    }

    private byte[] removeMethods(final byte[] bytes, final InfoClassVisitor info) {
        final ClassReader reader = new ClassReader(bytes);
        final ClassWriter writer = new ClassWriter(reader, WRITER_FLAGS);
        final MethodRemoverClassVisitor remover = new MethodRemoverClassVisitor(writer, info.getIdeEnabledMethods());
        reader.accept(remover, 0);
        return writer.toByteArray();
    }

    private byte[] injectErrorNotificationMethod(final byte[] bytes, final Throwable t) {
        final ClassReader reader = new ClassReader(bytes);
        final ClassWriter writer = new ClassWriter(reader, WRITER_FLAGS);
        final ErrorsClassVisitor injector = new ErrorsClassVisitor(writer, t.getMessage());
        reader.accept(injector, 0);
        return writer.toByteArray();
    }

    private boolean isTestClass(final String className) {
        final String test = convertToRegexPath(getProperty("test", ""));
        final String itTest = convertToRegexPath(getProperty("it.test", ""));

        final String filters = isSet(test) ?
          (isSet(itTest) ? test + "," + itTest : test) :
          (isSet(itTest) ? itTest : ".*Test");

        for (final String filter : filters.split(",")) {
            if (className.matches(filter)) {
                return true;
            }
        }
        return false;
    }

    private String convertToRegexPath(final String binaryPath) {
        return binaryToOS(binaryPath).replace("*", ".*");
    }
}