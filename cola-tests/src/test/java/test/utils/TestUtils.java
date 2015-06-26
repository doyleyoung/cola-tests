package test.utils;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;

import java.io.IOException;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import com.github.bmsantos.core.cola.formatter.FeatureDetails;
import com.github.bmsantos.core.cola.injector.InfoClassVisitor;

public class TestUtils {

    public static List<FeatureDetails> loadFeatures(final String className) throws IOException {
        final ClassReader cr = new ClassReader(className);
        final ClassWriter cw = new ClassWriter(cr, COMPUTE_FRAMES | COMPUTE_MAXS);
        final InfoClassVisitor classVisitor = new InfoClassVisitor(cw, Thread.currentThread().getContextClassLoader());

        // When
        cr.accept(classVisitor, 0);

        return classVisitor.getFeatures();
    }

}
