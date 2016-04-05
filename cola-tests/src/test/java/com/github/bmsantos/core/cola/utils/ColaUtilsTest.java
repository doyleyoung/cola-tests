package com.github.bmsantos.core.cola.utils;

import static com.github.bmsantos.core.cola.utils.ColaUtils.*;
import static java.io.File.separator;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ColaUtilsTest {

    public static final String STRING_SEPARATOR = ",";

    @Test
    public void shoulBeSet() {
        // Given
        final String value = "something";

        // When / Then
        assertThat(isSet(value), is(true));
    }

    @Test
    public void shouldNotBeSetOnNull() {
        // Given
        final String value = null;

        // When / Then
        assertThat(isSet(value), is(false));
    }

    @Test
    public void shouldNotBeSetOnEmpty() {
        // Given
        final String value = "";

        // When / Then
        assertThat(isSet(value), is(false));
    }

    @Test
    public void shoulBeSetByObject() {
        // Given
        final Object value = new Object();

        // When / Then
        assertThat(isSet(value), is(true));
    }

    @Test
    public void shouldNotBeSetOnNullByObject() {
        // Given
        final Object value = null;

        // When / Then
        assertThat(isSet(value), is(false));
    }

    @Test
    public void shoulBeSetByList() {
        // Given
        final List<Object> value = new ArrayList<>();
        value.add(new Object());

        // When / Then
        assertThat(isSet(value), is(true));
    }

    @Test
    public void shouldNotBeSetOnNullByList() {
        // Given
        final List<Object> value = null;

        // When / Then
        assertThat(isSet(value), is(false));
    }

    @Test
    public void shouldNotBeSetOnEmptyByList() {
        // Given
        final List<Object> value = new ArrayList<>();

        // When / Then
        assertThat(isSet(value), is(false));
    }

    @Test
    public void shoulBeSetByArray() {
        // Given
        final String[] value = new String[] { "something" };

        // When / Then
        assertThat(isSet(value), is(true));
    }

    @Test
    public void shouldNotBeSetOnNullByArray() {
        // Given
        final String[] value = null;

        // When / Then
        assertThat(isSet(value), is(false));
    }

    @Test
    public void shouldNotBeSetOnEmptyByArray() {
        // Given
        final String[] value = new String[] {};

        // When / Then
        assertThat(isSet(value), is(false));
    }

    @Test
    public void shouldBeClassFile() {
        // Given
        final String className = "MyClass.class";

        // When / Then
        assertThat(ColaUtils.isClassFile(className), is(true));
    }

    @Test
    public void shouldNotBeClassFile() {
        // Given
        final String className = "MyClass";

        // When / Then
        assertThat(ColaUtils.isClassFile(className), is(false));
    }

    @Test
    public void shouldConvertToOSPathToBinaryFormat() {
        // Given
        final String path = toOSPath("com", "github", "bmsantos", "MyClass");
        final String expected = path.replace(separator, ".");

        // When
        final String result = osToBinary(path);

        // Then
        assertThat(result, is(expected));
    }

    @Test
    public void shouldConvertToClassPathToBinaryFormat() {
        // Given
        final String path = toOSPath("com", "github", "bmsantos", "MyClass.class");
        final String expected = path.replace(separator, ".").replace(".class", "");

        // When
        final String result = classToBinary(path);

        // Then
        assertThat(result, is(expected));
    }

    @Test
    public void shouldConvertToOSPath() {
        // Given
        final String path = toBinary("com", "github", "bmsantos", "MyClass");
        final String expected = path.replace(".", separator);

        // When
        final String result = binaryToOS(path);

        // Then
        assertThat(result, is(expected));
    }

    @Test
    public void shouldConvertToResource() {
        // Given
        final String path = toBinary("com", "github", "bmsantos", "MyClass");
        final String expected = path.replace(".", RESOURCE_SEPARATOR);

        // When
        final String result = binaryToResource(path);

        // Then
        assertThat(result, is(expected));
    }

    @Test
    public void shouldConvertToOSClass() {
        // Given
        final String path = toBinary("com", "github", "bmsantos", "MyClass");
        final String expected = path.replace(".", separator) + ".class";

        // When
        final String result = binaryToOsClass(path);

        // Then
        assertThat(result, is(expected));
    }

    @Test
    public void shouldConvertToResourceClass() {
        // Given
        final String path = toBinary("com", "github", "bmsantos", "MyClass");
        final String expected = path.replace(".", RESOURCE_SEPARATOR) + ".class";

        // When
        final String result = binaryToResourceClass(path);

        // Then
        assertThat(result, is(expected));
    }

    @Test
    public void shouldFindFile() {
        // Given
        final String targetDir = toOSPath("target", "test-classes");
        final String className = getClass().getName();

        // When - Then
        assertThat(binaryFileExists(targetDir, className), is(true));
    }

    @Test
    public void shouldNotFindFile() {
        // Given
        final String targetDir = toOSPath("target", "test-classes");
        final String className = "invalid";

        // When - Then
        assertThat(binaryFileExists(targetDir, className), is(false));
    }

    @Test
    public void shouldJoinStringList() {
        // Given
        final List<String> list = asList("ONE", "TWO", "THREE");

        // When
        final String result = join(STRING_SEPARATOR, list);

        // Then
        assertThat(result, is("ONE,TWO,THREE"));
    }

    @Test
    public void shouldReturnEmptyStringOnEmplyList() {
        // Given
        final List<String> list = emptyList();

        // When
        final String result = join(STRING_SEPARATOR, list);

        // Then
        assertThat(result.isEmpty(), is(true));
    }

    @Test
    public void shouldJoinStrings() {
        // Given
        // When
        final String result = joinStrings(STRING_SEPARATOR, "", "ONE", "TWO");

        // Then
        assertThat(result, is("ONE,TWO"));
    }

    private String toOSPath(final String... parts) {
        StringBuilder result = new StringBuilder(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            result.append(separator).append(parts[i]);
        }
        return result.toString();
    }

    private String toBinary(final String... parts) {
        StringBuilder result = new StringBuilder(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            result.append(".").append(parts[i]);
        }
        return result.toString();
    }
}
