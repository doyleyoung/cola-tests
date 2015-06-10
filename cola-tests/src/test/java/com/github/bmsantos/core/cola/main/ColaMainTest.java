package com.github.bmsantos.core.cola.main;

import static com.github.bmsantos.core.cola.config.ConfigurationManager.config;
import static java.io.File.separator;
import static java.lang.String.format;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static uk.org.lidalia.slf4jtest.LoggingEvent.error;
import static uk.org.lidalia.slf4jtest.LoggingEvent.info;
import static uk.org.lidalia.slf4jtest.LoggingEvent.warn;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

import com.codeaffine.test.ConditionalIgnoreRule;
import com.codeaffine.test.ConditionalIgnoreRule.ConditionalIgnore;
import com.github.bmsantos.core.cola.exceptions.ColaExecutionException;
import com.github.bmsantos.core.cola.provider.IColaProvider;
import com.github.bmsantos.core.cola.utils.RunningOnWindows;

public class ColaMainTest {

    @Rule
    public ConditionalIgnoreRule rule = new ConditionalIgnoreRule();

    private static final String TARGET_DIR = toOSPath("target/test-classes");
    private static final String NO_IDE_CLASS = null;
    private static final String NO_IDE_CLASS_METHOD = null;

    private final TestLogger logger = TestLoggerFactory.getTestLogger(ColaMain.class);

    private ColaMain uut;

    private StubProvider provider;
    private List<String> classes;

    private final String testClass = toOSPath("cola/ide/AnotherColaTest.class");

    @Before
    public void setUp() {
        classes = new ArrayList<>();
        classes.add(testClass);

        provider = new StubProvider();
        provider.setTargetDirectory(TARGET_DIR);
        provider.setTargetClasses(classes);
        provider.setTargetClassLoader(getClass().getClassLoader());

        uut = new ColaMain(NO_IDE_CLASS, NO_IDE_CLASS_METHOD);
    }

    @Test
    public void shouldNotProcessOnNullList() throws ColaExecutionException {
        // When
        uut.execute(null);

        // Then
        assertTrue(true);
    }

    @Test
    public void shouldNotProcessOnEmptyList() throws ColaExecutionException {
        // When
        provider.setTargetClasses(Collections.<String> emptyList());
        uut.execute(provider);

        // Then
        assertTrue(true);
    }

    @Test
    public void shouldNotProcessMissingIdeBaseClassTest() throws ColaExecutionException {
        // When
        uut.execute(provider);

        // Then
        assertThat(logger.getLoggingEvents(), hasItem(warn(config.warn("missing.ide.test"))));
    }

    @Test
    public void shouldNotProcessMissingIdeBaseClass() throws ColaExecutionException {
        // When
        uut.execute(provider);

        // Then
        assertThat(logger.getAllLoggingEvents(), hasItem(info(config.info("missing.ide.class"))));
    }

    @Test
    @ConditionalIgnore(condition = RunningOnWindows.class)
    public void shouldNotProcessDefaultIdeBaseClass() throws ColaExecutionException {
        // Given
        final File ideClass = new File(TARGET_DIR + separator + toOSPath(config.getProperty("default.ide.class"))
            + ".class");
        final File renamedIdeClass = new File(TARGET_DIR + separator
            + toOSPath(config.getProperty("default.ide.class")) + "_renamed");
        ideClass.renameTo(renamedIdeClass);

        // When
        uut.execute(provider);
        renamedIdeClass.renameTo(ideClass);

        // Then
        assertThat(logger.getLoggingEvents(), hasItem(info(config.info("missing.default.ide.class"))));
    }

    @Test
    public void shouldProcessDefaultIdeBaseClass() throws ColaExecutionException {
        // When
        uut.execute(provider);

        // Then
        assertThat(logger.getLoggingEvents(), hasItem(info(config.info("found.default.ide.class"))));
    }

    @Test
    public void shouldFindProvidedIdeBaseClass() throws ColaExecutionException {
        // Given
        provider.setTargetClassLoader(getClass().getClassLoader());

        final String ideClass = toOSPath(config.getProperty("default.ide.class"));

        uut = new ColaMain(ideClass, NO_IDE_CLASS_METHOD);

        // When
        uut.execute(provider);

        // Then
        assertThat(logger.getLoggingEvents(), hasItem(info(config.info("processing") + TARGET_DIR + separator
            + ideClass + ".class")));
    }

    // In order to have the following test pass the class has to be recompiled.
    @Test
    public void shouldFindProvidedIdeBaseClassTest() throws ColaExecutionException {
        // Given
        provider.setTargetClassLoader(getClass().getClassLoader());

        final String ideClass = toOSPath(config.getProperty("default.ide.class"));
        final File testClassFile = new File(TARGET_DIR + separator + ideClass + ".class");
        final long initialSize = testClassFile.length();

        uut = new ColaMain(ideClass, "toBeRemoved");

        // When
        uut.execute(provider);

        // Then
        final long finalSize = testClassFile.length();
        assertThat(initialSize > finalSize, is(true));
    }

    @Test
    public void shouldProcessTestClasses() throws ColaExecutionException {
        // Given
        final File testClassFile = new File(TARGET_DIR + separator + testClass);
        final long initialSize = testClassFile.length();

        // When
        uut.execute(provider);
        final long finalSize = testClassFile.length();

        // Then
        assertThat(initialSize < finalSize, is(true));
    }

    @Test(expected = ColaExecutionException.class)
    public void shouldThrowMojExecutionExceptionOnInvalidClasses() throws ColaExecutionException {
        // Given
        classes.add(toOSPath("this/path/takes/to/nowhere/NotFountTest.class"));

        // When
        uut.execute(provider);

        // Then
        fail("Should have thrown an exception");
    }

    @Test
    public void shouldCollectFailureHistory() {
        // Given
        classes.add(toOSPath("this/path/takes/to/nowhere/NotFountTest1.class"));
        classes.add(toOSPath("this/path/takes/to/nowhere/NotFountTest2.class"));
        classes.add(toOSPath("this/path/takes/to/nowhere/NotFountTest3.class"));

        // When
        try {
            uut.execute(provider);
        } catch (final Exception e) {
        }

        // Then
        assertThat(uut.getFailures().size(), is(3));
        assertThat(logger.getLoggingEvents(), hasItem(error(format(config.error("failed.tests"), 3, 4))));
    }

    @Test
    public void shouldHandlePreviouslyProcessedTestClasses() throws ColaExecutionException {
        // Process the class once
        uut.execute(provider);

        // Given
        final File testClassFile = new File(TARGET_DIR + separator + testClass);
        final long initialSize = testClassFile.length();

        // When
        uut.execute(provider);
        final long finalSize = testClassFile.length();

        // Then
        assertThat(finalSize, equalTo(initialSize));
    }

    private static String toOSPath(final String value) {
        return value.replace("/", separator);
    }

    private class StubProvider implements IColaProvider {

        private String targetDirectory;
        private URLClassLoader loader;
        private List<String> classes;

        @Override
        public String getTargetDirectory() {
            return targetDirectory;
        }

        public void setTargetDirectory(final String targetDirectory) {
            this.targetDirectory = targetDirectory.endsWith(separator) ? targetDirectory : targetDirectory + separator;
        }

        @Override
        public URLClassLoader getTargetClassLoader() throws Exception {
            return loader;
        }

        public void setTargetClassLoader(final ClassLoader loader) {
            this.loader = new URLClassLoader(new URL[] {}, loader);
        }

        @Override
        public List<String> getTargetClasses() {
            return classes;
        }

        public void setTargetClasses(final List<String> classes) {
            this.classes = classes;
        }
    }
}
