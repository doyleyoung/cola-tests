package com.github.bmsantos.compiler.cola.provider;

import static com.github.bmsantos.core.cola.utils.ColaUtils.CLASS_EXT;
import static java.io.File.separator;
import static java.lang.System.getProperties;
import static java.util.Arrays.asList;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.util.DirectoryScanner;

import com.github.bmsantos.compiler.cola.Application;
import com.github.bmsantos.core.cola.provider.IColaProvider;

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
public class CommandLineColaProvider implements IColaProvider {

    private final String targetDirectory;

    public CommandLineColaProvider(final String targetDirectory) {
        this.targetDirectory = targetDirectory.endsWith(separator) ? targetDirectory : targetDirectory + separator;
    }

    @Override
    public String getTargetDirectory() {
        return targetDirectory;
    }

    @Override
    public URLClassLoader getTargetClassLoader() throws Exception {
        final List<URL> urls = new ArrayList<>();

        urls.add(new File(targetDirectory).toURI().toURL());

        return new URLClassLoader(urls.toArray(new URL[urls.size()]), Application.class.getClassLoader());
    }

    @Override
    public List<String> getTargetClasses() {
        final DirectoryScanner scanner = new DirectoryScanner();
        scanner.setIncludes(resolveIncludes());
        scanner.setBasedir(targetDirectory);
        scanner.setCaseSensitive(true);
        scanner.scan();

        return new ArrayList<String>(asList(scanner.getIncludedFiles()));
    }

    private String[] resolveIncludes() {
        final List<String> list = new ArrayList<>();
        final String test = getProperties().getProperty("test");
        final String itTest = getProperties().getProperty("it.test");

        if (test != null) {
            list.add(test.endsWith(CLASS_EXT) ? test : test + CLASS_EXT);
        }
        if (itTest != null) {
            list.add(itTest.endsWith(CLASS_EXT) ? itTest : itTest + CLASS_EXT);
        }

        if (list.isEmpty()) {
            list.add("**/*.class");
        }

        return list.toArray(new String[list.size()]);
    }

}
