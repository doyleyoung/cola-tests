/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.bmsantos.core.cola.injector;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASM5;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.RETURN;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public class ErrorsClassVisitor extends ClassVisitor {

    private static final String METHOD_NAME = "Cola Tests : Compilation Errors";

    private final String errors;
    private final ClassVisitor classVisitor;

    public ErrorsClassVisitor(final ClassVisitor classVisitor, final String errors) {
        super(ASM5, classVisitor);
        this.errors = errors;
        this.classVisitor = classVisitor;
    }

    @Override
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature,
        final String[] exceptions) {
        if (name.equals(METHOD_NAME)) {
            return null;
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
    }

    @Override
    public void visitEnd() {
        injectTestMethod();
        super.visitEnd();
    }

    private void injectTestMethod() {

        final MethodVisitor mv = classVisitor.visitMethod(ACC_PUBLIC, METHOD_NAME, "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitLdcInsn(errors);
        mv.visitMethodInsn(INVOKESTATIC, "org/junit/Assert", "fail", "(Ljava/lang/String;)V", false);
        mv.visitInsn(RETURN);
        mv.visitAnnotation("Lorg/junit/Test;", true);
        mv.visitEnd();
        mv.visitMaxs(0, 0);
    }
}
