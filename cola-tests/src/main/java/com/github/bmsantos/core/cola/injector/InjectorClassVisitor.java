package com.github.bmsantos.core.cola.injector;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.RETURN;
import gherkin.formatter.model.Step;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import com.github.bmsantos.core.cola.formatter.FeatureDetails;
import com.github.bmsantos.core.cola.formatter.FeatureFormatter;
import com.github.bmsantos.core.cola.formatter.ProjectionValues;
import com.github.bmsantos.core.cola.formatter.ScenarioDetails;

public class InjectorClassVisitor extends ClassVisitor {

    private final ClassWriter cw;
    private String stories;
    private List<FeatureDetails> features;

    public InjectorClassVisitor(final int api, final ClassWriter cw, final List<FeatureDetails> features) {
        super(api, cw);
        this.cw = cw;
        this.features = features;
    }

    @Override
    public FieldVisitor visitField(final int access, final String name, final String desc, final String signature,
        final Object value) {
        if ((features == null || features.isEmpty()) && name.equals("stories")) {
            stories = (String) value;
            features = new ArrayList<>();
            features.add(FeatureFormatter.parse(stories, "/from/junit/stories/field"));
        }
        return super.visitField(access, name, desc, signature, value);
    }

    @Override
    public void visitEnd() {
        for (final FeatureDetails feature : features) {
            injectTestMethod(feature);
        }
        super.visitEnd();
    }

    private void injectTestMethod(final FeatureDetails featureDetails) {

        // process scenarios
        for (final ScenarioDetails scenarioDetails : featureDetails.getScenarios()) {

            // project background into scenario story
            final List<Step> steps = new ArrayList<>(featureDetails.getBackgroundSteps());
            steps.addAll(scenarioDetails.getSteps());
            if (steps.isEmpty()) {
                continue;
            }

            final String story = buildStory(steps);
            final String featureName = featureDetails.getFeature().getName();
            final String scenarioName = scenarioDetails.getScenario().getName();
            String values = "";

            if (scenarioDetails.hasProjectionValues()) {
                final ProjectionValues projectionValues = scenarioDetails.getProjectionValues();
                for (int i = 0; i < projectionValues.size(); i++) {
                    values = projectionValues.doRowProjection(i);
                    injectTestMethod(featureName, scenarioName + " - projection " + i, story, values);
                }
            } else {
                injectTestMethod(featureName, scenarioName, story, values);
            }
        }
    }

    private void injectTestMethod(final String feature, final String scenario, final String story,
        final String projectionValues) {

        final MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, feature + " : " + scenario, "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitLdcInsn(feature);
        mv.visitLdcInsn(scenario);
        mv.visitLdcInsn(story);
        mv.visitLdcInsn(projectionValues);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESTATIC, "com/github/bmsantos/core/cola/story/processor/StoryProcessor", "process",
            "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)V", false);
        mv.visitInsn(RETURN);
        mv.visitAnnotation("Lorg/junit/Test;", true);
        mv.visitEnd();
        mv.visitMaxs(0, 0);
    }

    private String buildStory(final List<Step> steps) {
        String story = "";
        for (final Step step : steps) {
            story += step.getKeyword() + step.getName() + "\n";
        }
        return story;
    }
}
