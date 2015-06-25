package com.github.bmsantos.core.cola.injector;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.objectweb.asm.Opcodes.ASM4;
import gherkin.lexer.LexingError;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;

import com.github.bmsantos.core.cola.exceptions.InvalidFeature;
import com.github.bmsantos.core.cola.exceptions.InvalidFeatureUri;
import com.github.bmsantos.core.cola.formatter.FeatureDetails;
import com.github.bmsantos.core.cola.formatter.FeatureFormatter;

public class InfoClassVisitor extends ClassVisitor {

    private static final String DEFAULT_FIELD_NAME = "stories";
    private static final String FEATURE_DESCRIPTOR = "Lcom/github/bmsantos/core/cola/story/annotations/Feature;";
    private static final String FEATURES_DESCRIPTOR = "Lcom/github/bmsantos/core/cola/story/annotations/Features;";

    private final String[] fileExtensions = { ".feature", ".stories", ".story", ".gherkin", "" };

    private final ClassLoader classLoader;
    private String className;
    private String fieldName;
    private Object fieldValue;

    private final List<FeatureDetails> features = new ArrayList<>();

    public InfoClassVisitor(final ClassVisitor cv, final ClassLoader classLoader) {
        super(ASM4, cv);
        this.classLoader = classLoader;
    }

    public List<FeatureDetails> getFeatures() {
        return features;
    }

    @Override
    public void visit(final int version, final int access, final String name, final String signature,
        final String superName, final String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        className = name;
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String descriptor, final boolean runtimeVisible) {
        if (descriptor.equals(FEATURES_DESCRIPTOR)) {
            return new AnnotationVisitor(ASM4, super.visitAnnotation(descriptor, runtimeVisible)) {
                @Override
                public AnnotationVisitor visitArray(final String name) {
                    if (name.equals("value")) {
                        return new AnnotationVisitor(ASM4, super.visitArray(name)) {
                            @Override
                            public void visit(final String name, final Object value) {
                                super.visit(name, value);

                                final String featureUri = className.substring(0, className.lastIndexOf("/") + 1) + value.toString();

                                System.err.println(className);
                                System.err.println(featureUri);
                                System.err.println(value.toString());

                                final InputStream in = findResource(featureUri);
                                if (in == null) {
                                    raiseInvalidFeatureUri(featureUri, "Unable to find feature file (.feature|.stories|.story|.gherkin)");
                                }

                                String contents = null;
                                try {
                                    contents = readFeatureContents(in).trim();
                                    if (contents.isEmpty()) {
                                        raiseInvalidFeatureUri(featureUri, "Empty feature.");
                                    }
                                } catch (final NoSuchElementException e) {
                                    raiseInvalidFeatureUri(featureUri, e.getMessage());
                                }
                                features.add(FeatureFormatter.parse(contents, featureUri));
                            }
                        };
                    }
                    return super.visitArray(name);
                }
            };
        }
        return super.visitAnnotation(descriptor, runtimeVisible);
    }

    @Override
    public FieldVisitor visitField(final int access, final String name, final String desc, final String signature, final Object value) {
        fieldName = name;
        fieldValue = value;

        if (name.equals(DEFAULT_FIELD_NAME)) {
            features.add(parseFeature(DEFAULT_FIELD_NAME, value));
        }

        final FieldVisitor fv = super.visitField(access, name, desc, signature, value);

        return new FieldVisitor(ASM4, fv) {
            @Override
            public AnnotationVisitor visitAnnotation(final String descriptor, final boolean runtimeVisible) {
                if (descriptor.equals(FEATURE_DESCRIPTOR)) {
                    features.add(parseFeature(fieldName, fieldValue));
                }
                return super.visitAnnotation(descriptor, runtimeVisible);
            }

        };
    }

    private void raiseInvalidFeature(final String fieldName, final String cause) {
        throw new InvalidFeature("No features found in: " + className + "." + fieldName + " - Cause: " + cause);
    }

    private void raiseInvalidFeatureUri(final String featureUri, final String cause) {
        throw new InvalidFeatureUri(featureUri + " - Cause: " + cause);
    }

    private FeatureDetails parseFeature(final String fieldName, final Object fieldValue) {
        try {
            final String featureValue = String.class.cast(fieldValue).trim();

            if (featureValue.isEmpty()) {
                raiseInvalidFeature(fieldName, "Empty feature.");
            }

            return FeatureFormatter.parse(featureValue, className + "." + fieldName);
        } catch (final LexingError e) {
            raiseInvalidFeature(fieldName, e.getMessage());
        }
        raiseInvalidFeature(fieldName, "Invalid feature.");
        return null;
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
}
