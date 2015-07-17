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
package com.github.bmsantos.core.cola.main;

import static com.github.bmsantos.core.cola.config.ConfigurationManager.config;
import static com.github.bmsantos.core.cola.utils.ColaUtils.isSet;
import static com.github.bmsantos.core.cola.utils.ColaUtils.resourceClassToResource;
import static java.lang.String.format;
import static org.codehaus.plexus.util.IOUtil.toByteArray;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.bmsantos.core.cola.exceptions.ColaExecutionException;
import com.github.bmsantos.core.cola.instrument.ColaTransformer;
import com.github.bmsantos.core.cola.provider.IColaProvider;

public class ColaMain {

    private static Logger log = LoggerFactory.getLogger(ColaMain.class);

    private IColaProvider provider;

    private List<String> failures;

    public List<String> getFailures() {
        return failures;
    }

    public void execute(final IColaProvider provider) throws ColaExecutionException {
        this.provider = provider;

        failures = new ArrayList<>();

        if (!isSet(provider)) {
            return;
        }

        final List<String> targetClasses = provider.getTargetClasses();
        if (!isSet(targetClasses)) {
            return;
        }

        for (final String className : targetClasses) {
            try {
                processClass(className, null);
            } catch (final Throwable t) {
                log.error(format(config.error("failed.process.file"), className), t);
                failures.add(format(config.error("failed.processing"), className, t.getMessage()));
            }
        }

        if (!failures.isEmpty()) {
            log.error(format(config.error("failed.tests"), failures.size(), targetClasses.size()));
            for (final String failure : failures) {
                log.error(failure);
            }

            throw new ColaExecutionException(config.error("processing"));
        }
    }

    private void processClass(final String className, final String methodToRemove)
        throws Exception {

        final String filePath = provider.getTargetDirectory() + className;
        log.info(config.info("processing") + filePath);

        final InputStream in = provider.getTargetClassLoader().getResourceAsStream(className);

        final ColaTransformer transformer = new ColaTransformer();
        transformer.removeMethod(methodToRemove);
        final byte[] instrumented = transformer.transform(provider.getTargetClassLoader(), resourceClassToResource(className), null, null, toByteArray(in));

        final File file = new File(filePath);
        try (final DataOutputStream dout = new DataOutputStream(new FileOutputStream(file))) {
            dout.write(instrumented);
        }
    }
}
