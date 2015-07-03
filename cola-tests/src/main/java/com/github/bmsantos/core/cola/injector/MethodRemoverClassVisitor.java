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

import static java.util.Objects.requireNonNull;
import static org.objectweb.asm.Opcodes.ASM4;

import java.util.List;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public class MethodRemoverClassVisitor extends ClassVisitor {

    private final List<String> methodsToRemove;

    public MethodRemoverClassVisitor(final ClassVisitor cv, final List<String> methodsToRemove) {
        super(ASM4, cv);
        this.methodsToRemove = requireNonNull(methodsToRemove);
    }

    @Override
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        if (methodsToRemove.contains(name)) {
            return null;
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
    }
}
