package io.github.oleksiyp.mockito_dumper;

import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

public class SetFieldInstrumentation {
    public boolean instrument(CtClass classForInstrumentation,
                              InstrumentationGateway.StaticRef ref) throws NotFoundException {
        try {
            classForInstrumentation.instrument(new ExprEditor(){
                @Override
                public void edit(FieldAccess f) throws CannotCompileException {
                    if (f.isStatic() || f.isReader()) {
                        return;
                    }
                    StringBuilder src = new StringBuilder();

                    src.append("$proceed($$);");

                    src.append(ref.getSetIntFieldMethod())
                            .append("($0, \"")
                            .append(f.getFieldName())
                            .append("\",  $$);");

                    f.replace(src.toString());
                }
            });
            return true;
        } catch (CannotCompileException e) {
            throw new RuntimeException("failed to instrument via javassist", e);
        }
    }
}
