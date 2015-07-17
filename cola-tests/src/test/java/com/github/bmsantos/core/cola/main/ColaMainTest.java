package com.github.bmsantos.core.cola.main;

import static com.github.bmsantos.core.cola.config.ConfigurationManager.config;
import static com.github.bmsantos.core.cola.utils.ColaUtils.toOSPath;
import static java.io.File.separator;
import static java.lang.String.format;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static uk.org.lidalia.slf4jtest.LoggingEvent.error;

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
import com.github.bmsantos.core.cola.exceptions.ColaExecutionException;
import com.github.bmsantos.core.cola.provider.IColaProvider;

public class ColaMainTest {

    @Rule
    public ConditionalIgnoreRule rule = new ConditionalIgnoreRule();

    private static final String TARGET_DIR = toOSPath("target/test-classes");

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

        uut = new ColaMain();
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
        classes.add(toOSPath("this/path/takes/to/nowhere/NotFount1Test.class"));
        classes.add(toOSPath("this/path/takes/to/nowhere/NotFount2Test.class"));
        classes.add(toOSPath("this/path/takes/to/nowhere/NotFount3Test.class"));

        // When
        try {
            uut.execute(provider);
        } catch (final Exception e) {
        }

        // Then
        assertThat(uut.getFailures().size(), is(3));
        assertThat(logger.getLoggingEvents(), hasItem(error(format(config.error("failed.tests"), 3, 4))));
    }

    // This test requires a clean build
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
