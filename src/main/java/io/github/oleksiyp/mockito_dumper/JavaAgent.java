package io.github.oleksiyp.mockito_dumper;

import com.lmax.disruptor.dsl.Disruptor;
import io.github.oleksiyp.mockito_dumper.FieldWriteGateway.StaticRef;
import javassist.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public class JavaAgent {
    public static FieldWriteGateway GATEWAY;
    public static final StaticRef GATEWAY_STATIC_REF = new StaticRef(JavaAgent.class, "GATEWAY");

    public static void premain(String args, Instrumentation inst) {
        if (args == null) {
            System.err.println("'mockito-dumper.jar' java agent requires filename as an argument");
            return;
        }

        Disruptor<FieldWriteEvent> disruptor = new Disruptor<>(
                FieldWriteEvent::new,
                1024,
                runnable -> {
                    Thread thread = new Thread(runnable, "field tracker '" + args + "'");
                    thread.setDaemon(true);
                    return thread;
                });

        try {
            disruptor.handleEventsWith(
                    new FormattingFieldWriteEventHandler(
                            new FileLoggingEventHandler(new File(args))));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        GATEWAY = new PublishingFieldWriteGateway(disruptor.start());

        inst.addTransformer(new ClassFileTransformer() {
            Map<ClassLoader, ClassPool> pools = Collections.synchronizedMap(new WeakHashMap<>());
            FieldWriteInstrumentation instrumentation = new FieldWriteInstrumentation();

            @Override
            public byte[] transform(ClassLoader classLoader,
                                    String className,
                                    Class<?> classBeingRedefined,
                                    ProtectionDomain protectionDomain,
                                    byte[] classfileBuffer) throws IllegalClassFormatException {
                if (className == null
                        || className.startsWith("sun/")
                        || className.startsWith("java/")
                        || className.startsWith("javax/")
                        || className.startsWith("io/github/oleksiyp/mockito_dumper")
                        || className.startsWith("gnu/trove")
                        || className.equals("com/intellij/ide/plugins/PluginClassCache")) {
                    return  classfileBuffer;
                }

                ClassPool pool = pools.computeIfAbsent(classLoader, (loader) -> {
                    ClassPool createdPool = new ClassPool();
                    createdPool.appendSystemPath();
                    createdPool.appendClassPath(new LoaderClassPath(loader));
                    System.out.println("NEW POOL " + loader.toString());
                    return createdPool;
                });

                try {
                    CtClass ctClass = pool.getOrNull(className);
                    if (ctClass != null && ctClass.isModified()) {
                        System.out.println("FROM POOL " + className);
                        return ctClass.toBytecode();
                    }

                    ctClass = pool.makeClass(new ByteArrayInputStream(classfileBuffer));

                    if (ctClass.isEnum() ||
                            ctClass.isFrozen() ||
                            ctClass.isInterface() ||
                            ctClass.isPrimitive() ||
                            ctClass.isArray() ||
                            ctClass.isAnnotation()) {
                        return classfileBuffer;
                    }

                    boolean instrument = instrumentation
                            .instrument(ctClass, GATEWAY_STATIC_REF);

                    if (instrument) {
                        System.out.println(className);
                    }

                    return ctClass.toBytecode();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return classfileBuffer;
            }
        }, false);
    }
}
