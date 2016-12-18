package io.github.oleksiyp.mockito_dumper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class BasicFormatter implements Formatter {
    @Override
    public void outputFormatted(Object object, StringBuilder buf) {
        buf.append(object.getClass().getName());
        buf.append(" ");
        buf.append("obj");
        buf.append(Long.toHexString(System.identityHashCode(object)));
        buf.append("\n");

        for (Field field : object.getClass().getFields()) {
            field.setAccessible(true);
            try {
                String fieldName = field.getName();
                Object fieldValue = field.get(object);
                buf.append(fieldName);
                buf.append("=");
                buf.append(fieldValue);
                buf.append("\n");
            } catch (IllegalAccessException e) {
                // skip
            }
        }
    }

    static boolean checkHasToString(Object object) {
        for (Method method : object.getClass().getDeclaredMethods()) {
            String name = method.getName();
            Class<?> declaringClass = method.getDeclaringClass();
            int paramCount = method.getParameterCount();

            if ("toString".equals(name) &&
                    declaringClass != Object.class &&
                    paramCount == 0) {
                return true;
            }
        }
        return false;
    }
}
