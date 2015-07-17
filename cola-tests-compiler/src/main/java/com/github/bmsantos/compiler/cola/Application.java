package com.github.bmsantos.compiler.cola;

import static com.github.bmsantos.core.cola.config.ConfigurationManager.config;
import static java.lang.System.out;

import java.net.URLClassLoader;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.github.bmsantos.compiler.cola.provider.CommandLineColaProvider;
import com.github.bmsantos.core.cola.exceptions.ColaExecutionException;
import com.github.bmsantos.core.cola.main.ColaMain;

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
public class Application {

    private static final String ERR_MSG_FAILED_COMPILATION = "Failed to compile COLA Tests: ";

    @Parameter(names = { "-v", "--version" }, description = "Print out version information")
    private boolean version;

    @Parameter(names = { "-h", "--help" }, description = "Print this guide")
    private boolean help;

    @Parameter(names = { "-t", "--target" }, required = true, help = true,
        description = "Base directory containing compiled java packages and classes (required)")
    private String targetDirectory;

    public static void main(final String[] args) {

        final Application app = new Application();
        final JCommander jc = new JCommander(app);
        jc.setProgramName("java -jar /path/to/cola-tests.jar");

        ColaMain main = null;
        try {
            jc.parse(args);

            final CommandLineColaProvider provider = new CommandLineColaProvider(app.targetDirectory);

            try (final URLClassLoader loader = provider.getTargetClassLoader()) {
                main = new ColaMain();
                main.execute(provider);
            }

            return;
        } catch (final ColaExecutionException e) {
            if (main != null) {
                for (final String f : main.getFailures()) {
                    out.println(f);
                }
            }
        } catch (final ParameterException e) {
            if (app.version) {
                app.printVersion(jc);
                return;
            } else if (!app.help) {
                out.println(ERR_MSG_FAILED_COMPILATION + e.getMessage());
            }
        } catch (final Throwable t) {
            if (!app.help) {
                out.println(ERR_MSG_FAILED_COMPILATION);
                t.printStackTrace();
            }
        }
        jc.usage();
    }

    private void printVersion(final JCommander jCommander) {
        final StringBuilder builder = new StringBuilder(config.getProperty("app.name"));
        builder.append(" ").append(config.getProperty("app.version"));
        System.out.println(builder);
    }

}
