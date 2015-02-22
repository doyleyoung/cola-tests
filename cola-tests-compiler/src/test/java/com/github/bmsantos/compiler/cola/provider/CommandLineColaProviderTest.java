package com.github.bmsantos.compiler.cola.provider;

import static com.github.bmsantos.core.cola.utils.ColaUtils.CLASS_EXT;
import static com.github.bmsantos.core.cola.utils.ColaUtils.binaryToOsClass;
import static com.github.bmsantos.core.cola.utils.ColaUtils.toOSPath;
import static java.lang.System.getProperties;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.github.bmsantos.compiler.cola.provider.CommandLineColaProvider;

public class CommandLineColaProviderTest {

    private final String targetDirectory = toOSPath("target/test-classes/");

    private CommandLineColaProvider uut;

    @Before
    public void setUp() {
        uut = new CommandLineColaProvider(targetDirectory);

        getProperties().remove("test");
        getProperties().remove("it.test");
    }

    @Test
    public void shouldReturnTargetDirectory() {
        // Then
        assertThat(uut.getTargetDirectory(), is(targetDirectory));
    }

    @Test
    public void shouldReturnNormalizedTargetDirectory() {
        // When
        uut = new CommandLineColaProvider(toOSPath("target/test-classes"));

        // Then
        assertThat(uut.getTargetDirectory(), is(targetDirectory));
    }

    @Test
    public void shoudGetTargetCloassLoader() throws Exception {
        // When
        final ClassLoader loader = uut.getTargetClassLoader();

        // Then
        assertThat(loader, notNullValue());
    }

    @Test
    public void shouldLoadCurrentTest() throws Exception {
        // Given
        final String binaryName = getClass().getCanonicalName();
        final ClassLoader loader = uut.getTargetClassLoader();

        // When
        final Class<?> clazz = loader.loadClass(binaryName);

        // Then
        assertThat(clazz, notNullValue());
    }

    @Test
    public void shouldGetTargetClasses() {
        // When
        final List<String> classes = uut.getTargetClasses();

        // Then
        assertThat(classes.isEmpty(), is(false));
        assertThat(classes, hasItem(binaryToOsClass(getClass().getCanonicalName())));
    }

    @Test
    public void shouldFilterByTestSystemProperty() {
        // Given
        getProperties().setProperty("test", "**/*" + getClass().getSimpleName() + CLASS_EXT);

        // When
        final List<String> classes = uut.getTargetClasses();

        // Then
        assertThat(classes.isEmpty(), is(false));
        assertThat(classes, contains(binaryToOsClass(getClass().getCanonicalName())));
    }

    @Test
    public void shouldFilterByIntegrationTestSystemProperty() {
        // Given
        getProperties().setProperty("it.test", "**/*" + getClass().getSimpleName() + CLASS_EXT);

        // When
        final List<String> classes = uut.getTargetClasses();

        // Then
        assertThat(classes.isEmpty(), is(false));
        assertThat(classes, contains(binaryToOsClass(getClass().getCanonicalName())));
    }

}
