package io.github.oleksiyp.mockito_dumper;

import io.github.oleksiyp.mockito_dumper.FieldWriteGateway.StaticRef;
import javassist.*;
import javassist.expr.ConstructorCall;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FieldWriteInstrumentation {
    public boolean instrument(CtClass classForInstrumentation,
                              StaticRef gatewayRef) throws NotFoundException {
        try {
            classForInstrumentation.instrument(new ExprEditor(){
                Set<CtBehavior> calledConstructors = new HashSet<>();

                @Override
                public void edit(ConstructorCall c) throws CannotCompileException {
                    calledConstructors.add(c.where());
                }

                @Override
                public void edit(FieldAccess f) throws CannotCompileException {
                    if (f.isStatic() || f.isReader()) {
                        return;
                    }
                    if (f.where() instanceof CtConstructor) {
                        if (!calledConstructors.contains(f.where())) {
                            return;
                        }
                    }
                    StringBuilder src = new StringBuilder();

                    src.append("$proceed($$);");

                    CtClass type;
                    try {
                        type = f.getField().getType();
                    } catch (NotFoundException e) {
                        throw new CannotCompileException(e);
                    }

                    String method;
                    String typeDesc;
                    if (type.isPrimitive()) {
                        method = gatewayRef.writePrimitiveFieldMethod();
                        typeDesc = " '" + ((CtPrimitiveType)type).getDescriptor() + "', ";
                    } else {
                        method = gatewayRef.writeObjectFieldMethod();
                        typeDesc = "";
                    }

                    String valueExpr = "$1";
                    if (type == CtPrimitiveType.booleanType) {
                        valueExpr = "(long)($1 ? 1 : 0)";
                    } else if (type == CtPrimitiveType.floatType) {
                        valueExpr = "(long)Float.floatToRawIntBits($1)";
                    } else if (type == CtPrimitiveType.doubleType) {
                        valueExpr = "Double.doubleToRawLongBits($1)";
                    } else if (type.isPrimitive()) {
                        valueExpr = "(long)$1";
                    }

                    src.append(method)
                            .append("($0, ")
                            .append("\"").append(f.getFieldName()).append("\", ")
                            .append(typeDesc)
                            .append(valueExpr)
                            .append(");");

                    f.replace(src.toString());
                }
            });

            return true;
        } catch (CannotCompileException e) {
            throw new RuntimeException("failed to instrument via javassist", e);
        }
    }
}
