package io.github.oleksiyp.mockito_dumper;

import javassist.*;

import java.lang.reflect.Modifier;

public class DumpInFinalizeInstrumentation {
    public static final String FINALIZE_METHOD = "finalize";
    public static final String DUMP_METHOD = "dump";

    public boolean instrument(CtClass classForInstrumentation,
                              String dumperInstanceClassName,
                              String dumperInstanceFieldName) throws NotFoundException {
        try {
            ClassPool pool = classForInstrumentation.getClassPool();

            StringBuilder src = new StringBuilder();

            src.append("{");

            src.append("super")
                    .append(".")
                    .append(FINALIZE_METHOD)
                    .append("();");

            src.append(dumperInstanceClassName)
                    .append(".")
                    .append(dumperInstanceFieldName)
                    .append("." + DUMP_METHOD + "(this);");

            src.append("}");

            String finalizeDumpCallCode = src.toString();

            CtMethod existingMethod = null;
            for (CtMethod method : classForInstrumentation.getMethods()) {
                boolean finalizeName = FINALIZE_METHOD.equals(method.getName());
                boolean sameClass = classForInstrumentation.equals(method.getDeclaringClass());
                boolean finalFlag = Modifier.isFinal(method.getModifiers());

                if (finalizeName && sameClass) {
                    existingMethod = method;
                }
                if (finalizeName && !sameClass && finalFlag) {
                    return false;
                }
            }

            if (existingMethod != null) {
                existingMethod.insertAfter(finalizeDumpCallCode);
            } else {
                CtMethod newFinalizeMethod = CtNewMethod.make(
                        pool.get(void.class.getName()),
                        FINALIZE_METHOD,
                        new CtClass[0],
                        new CtClass[] {
                                pool.get(Throwable.class.getName())
                        },
                        finalizeDumpCallCode,
                        classForInstrumentation
                );

                classForInstrumentation.addMethod(newFinalizeMethod);
            }
            return true;
        } catch (CannotCompileException e) {
            throw new RuntimeException("failed to instrument via javassist", e);
        }
    }
}
