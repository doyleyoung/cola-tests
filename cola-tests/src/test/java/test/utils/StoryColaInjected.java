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
package test.utils;

import javax.inject.Inject;
import javax.inject.Named;

import com.github.bmsantos.core.cola.story.annotations.ColaInjected;
import com.github.bmsantos.core.cola.story.annotations.ColaInjector;
import com.google.inject.Injector;

@ColaInjected
public class StoryColaInjected extends Story {

    @Inject
    @Named("number2")
    public Number numberB;

    @Inject
    @Named("Zoltan")
    public Number numberA;

    @Inject
    public String string;

    @ColaInjector
    public Injector colaInjector;
}
