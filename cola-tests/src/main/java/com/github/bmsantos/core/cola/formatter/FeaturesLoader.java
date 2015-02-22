package com.github.bmsantos.core.cola.formatter;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;
import gherkin.lexer.LexingError;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import com.github.bmsantos.core.cola.exceptions.InvalidFeature;
import com.github.bmsantos.core.cola.exceptions.InvalidFeatureUri;
import com.github.bmsantos.core.cola.story.annotations.Feature;
import com.github.bmsantos.core.cola.story.annotations.Features;

public class FeaturesLoader {

    private final ClassLoader classLoader;
    private final List<FeatureDetails> featureList = new ArrayList<>();
    private final String[] fileExtensions = { ".feature", ".stories", ".story", ".gherkin", "" };

    public FeaturesLoader(final ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public List<FeatureDetails> getFeatureList() {
        return featureList;
    }

    public void load(final Class<?> annotatedClass) {
        featureList.addAll(loadClassAnnotations(annotatedClass));
        if (featureList.isEmpty()) {
            featureList.addAll(loadFieldAnnotations(annotatedClass));
        }
    }

    private List<FeatureDetails> loadClassAnnotations(final Class<?> clazz) {
        final List<FeatureDetails> foundFeatureList = new ArrayList<>();

        final Features features = clazz.getAnnotation(Features.class);
        if (features != null) {

            for (final String fileName : features.value()) {

                final String featureUri = clazz.getPackage().getName().replace(".", "/") + "/" + fileName;

                final InputStream in = findResource(featureUri);
                if (in == null) {
                    raiseInvalidFeatureUri(featureUri);
                }

                String contents = null;
                try {
                    contents = readFeatureContents(in).trim();
                    if (contents.isEmpty()) {
                        raiseInvalidFeature(featureUri);
                    }
                } catch (final NoSuchElementException e) {
                    raiseInvalidFeature(featureUri);
                }
                foundFeatureList.add(FeatureFormatter.parse(contents, featureUri));
            }
        }

        return foundFeatureList;
    }

    private List<FeatureDetails> loadFieldAnnotations(final Class<?> clazz) {
        final List<FeatureDetails> foundFeatureList = new ArrayList<>();

        final List<Field> fields = new ArrayList<>();
        fields.addAll(asList(clazz.getDeclaredFields()));
        fields.addAll(asList(clazz.getFields()));

        for (final Field field : fields) {
            if (field.getType().equals(String.class)) {

                final Feature feature = field.getAnnotation(Feature.class);
                if (feature != null) {

                    String contents = null;
                    try {
                        field.setAccessible(true);
                        final Constructor<?> constructor = clazz.getDeclaredConstructor(new Class<?>[] {});
                        constructor.setAccessible(true);
                        contents = (String) field.get(constructor.newInstance());
                        contents = contents.trim();
                    } catch (final Throwable t) {
                        // empty
                    }

                    if (contents.isEmpty()) {
                        raiseInvalidFeature(field.getName() + " - Cause: Empty feature.");
                    }

                    try {
                        foundFeatureList.add(FeatureFormatter.parse(contents, clazz.getName() + "." + field.getName()));
                    } catch (final LexingError e) {
                        raiseInvalidFeature(clazz.getName() + "." + field.getName() + " - Cause: " + e.getMessage());
                    }
                }
            }
        }

        return foundFeatureList;
    }

    private InputStream findResource(final String resource) {
        for (final String ext : fileExtensions) {
            final String fullResource = resource + ext;
            final InputStream in = classLoader.getResourceAsStream(fullResource);
            if (in != null) {
                return in;
            }
        }
        return null;
    }

    private String readFeatureContents(final InputStream input) {
        try (Scanner scanner = new Scanner(input, UTF_8.name())) {
            return scanner.useDelimiter("\\A").next();
        }
    }

    private void raiseInvalidFeature(final String featureUri) {
        throw new InvalidFeature("No features found in: " + featureUri);
    }

    private void raiseInvalidFeatureUri(final String featureUri) {
        throw new InvalidFeatureUri("Unable to find feature file (.feature|.stories|.story|.gherkin): " + featureUri);
    }

    public static List<FeatureDetails> loadFeaturesFrom(final Class<?> annotatedClass) {
        final FeaturesLoader loader = new FeaturesLoader(annotatedClass.getClassLoader());
        loader.load(annotatedClass);
        return loader.getFeatureList();
    }
}
