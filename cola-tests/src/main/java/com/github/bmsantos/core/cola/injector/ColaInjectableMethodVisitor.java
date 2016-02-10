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

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ColaInjectableMethodVisitor extends MethodVisitor implements Opcodes {

    public static final String PROCESSOR = "com/github/bmsantos/core/cola/story/processor/StoryProcessor";
    public static final String PROCESSOR_METHOD = "initColaInjector";
    public static final String PROCESSOR_SIG_TYPE = "(Ljava/lang/Object;)Lcom/google/inject/Injector;";

    private final InfoClassVisitor infoClassVisitor;
    private boolean initialized = false;

    public ColaInjectableMethodVisitor(final MethodVisitor mv, final InfoClassVisitor infoClassVisitor) {
        super(ASM4, mv);
        this.infoClassVisitor = infoClassVisitor;
    }

    @Override
    public void visitInsn(final int opcode) {
        if (!initialized) {
            if (opcode == RETURN || opcode == DUP) {
                initialized = true;
                for (final String field : infoClassVisitor.getColaInjectorFields()) {
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitMethodInsn(INVOKESTATIC, PROCESSOR, PROCESSOR_METHOD, PROCESSOR_SIG_TYPE, false);
                    mv.visitFieldInsn(PUTFIELD, infoClassVisitor.getClassName(), field, "Lcom/google/inject/Injector;");
                }
                if (infoClassVisitor.isColaInjected()) {
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitMethodInsn(INVOKESTATIC, PROCESSOR, PROCESSOR_METHOD, PROCESSOR_SIG_TYPE, false);
                    mv.visitInsn(POP);
                }
            }
            if (opcode == POP && infoClassVisitor.isColaInjected()) {
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKESTATIC, PROCESSOR, PROCESSOR_METHOD, PROCESSOR_SIG_TYPE, false);
            }
        }
        super.visitInsn(opcode);
    }
}