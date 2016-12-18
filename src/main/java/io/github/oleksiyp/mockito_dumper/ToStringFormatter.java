package io.github.oleksiyp.mockito_dumper;

import java.lang.reflect.Method;

public class ToStringFormatter implements Formatter {
    @Override
    public void outputFormatted(Object object, StringBuilder buf) {
        if (checkHasToString(object)) {
            buf.append(object.toString());
            buf.append("\n");
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
