package com.github.bmsantos.core.cola.injector;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

public class MethodRemoverClassVisitor extends ClassVisitor {

    private final String methodToRemove;

    public MethodRemoverClassVisitor(final int api, final ClassWriter cw, final String methodToRemove) {
        super(api, cw);
        this.methodToRemove = methodToRemove;
    }

    @Override
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        if (name.equals(methodToRemove)) {
            return null;
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
    }
}
