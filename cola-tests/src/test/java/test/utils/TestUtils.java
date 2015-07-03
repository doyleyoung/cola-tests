package test.utils;

import static com.github.bmsantos.core.cola.utils.ColaUtils.binaryToOsClass;
import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.List;

import org.codehaus.plexus.util.IOUtil;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.TraceClassVisitor;

import com.github.bmsantos.core.cola.formatter.FeatureDetails;
import com.github.bmsantos.core.cola.injector.InfoClassVisitor;

public class TestUtils {

    private static final int WRITER_FLAGS = COMPUTE_FRAMES | COMPUTE_MAXS;

    public static List<FeatureDetails> loadFeatures(final String className) throws IOException {
        final ClassReader cr = new ClassReader(className);
        final ClassWriter cw = new ClassWriter(cr, WRITER_FLAGS);
        final InfoClassVisitor classVisitor = new InfoClassVisitor(cw, Thread.currentThread().getContextClassLoader());

        // When
        cr.accept(classVisitor, 0);

        return classVisitor.getFeatures();
    }

    public static byte[] loadClassBytes(final Class<?> clazz) throws IOException {
        final InputStream is = clazz.getClassLoader().getResourceAsStream(binaryToOsClass(clazz.getName()));
        return IOUtil.toByteArray(is);
    }

    public static String traceBytecode(final byte[] bytecode) {
        final ClassReader cr = new ClassReader(bytecode);
        final ClassWriter cw = new ClassWriter(cr, WRITER_FLAGS);
        final ByteArrayOutputStream capture = new ByteArrayOutputStream();
        final TraceClassVisitor tcv = new TraceClassVisitor(cw, new PrintWriter(new PrintStream(capture), true));
        cr.accept(tcv, 0);
        return capture.toString();
    }
}
