package io.github.oleksiyp.mockito_dumper;

import java.lang.reflect.Field;

public class MockitoFormatter implements Formatter {
    public static final String MOCKITO_CLASSNAME = "org.mockito.Mockito";

    @Override
    public void outputFormatted(Object object, StringBuilder buf) {
        appendDeclareMock(object, buf);
        appendNewLine(buf);

        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);

            Object fieldValue;
            try {
                fieldValue = field.get(object);
            } catch (IllegalAccessException e) {
                continue;
            }

            String fieldName = field.getName();

            appendMockField(buf, object, fieldName, fieldValue);
            appendNewLine(buf);
        }
    }

    private static boolean appendLiteral(StringBuilder buf, Object value) {
        if (value instanceof String) {
            appendStringLiteral(buf, (String) value);
            return true;
        }
        return false;
    }

    private static void appendStringLiteral(StringBuilder buf, String value) {
        buf.append("\"");
        int idx = 0, lastIdx = 0;
        while ((idx = indexOfSpecialChar(value, lastIdx)) != -1) {
            buf.append(value, lastIdx, idx);
            buf.append('\\');
            buf.append(encodeSpecialChar(value.charAt(idx)));
            lastIdx = idx + 1;
        }
        buf.append(value, lastIdx, value.length());
        buf.append("\"");
    }

    private static char encodeSpecialChar(char c) {
        switch (c) {
            case '\n': return 'n';
            case '\r': return 'r';
            case '\"': return '"';
            case '\t': return 't';
            case '\b': return 'b';
            case '\\': return '\\';
        }
        return c;
    }

    private static int indexOfSpecialChar(String str, int fromIndex) {
        for (int i = fromIndex; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '\n' || c == '\r'
                    || c == '\"' || c == '\t'
                    || c == '\b' || c == '\\') {
                return i;
            }
        }
        return -1;
    }

    private static void appendCapitalName(StringBuilder buf, String name) {
        if (name.isEmpty()) {
            return;
        }

        buf.append(Character.toUpperCase(name.charAt(0)));
        buf.append(name, 1, name.length());
    }

    private void appendObjectId(StringBuilder buf, Object object) {
        long id = System.identityHashCode(object) & 0xffffffffL;

        buf.append("obj");
        while (id > 0) {
            char c = (char) (id % 62);
            if (c < 10) {
                c += '0';
            } else if (c < 36) {
                c -= 10;
                c += 'A';
            } else {
                c -= 36;
                c += 'a';
            }
            buf.append(c);
            id /= 62;
        }
    }

    private void appendMockField(StringBuilder buf, Object object, String fieldName, Object fieldValue) {
        buf.append(MOCKITO_CLASSNAME);
        buf.append(".when(");
        appendObjectId(buf, object);
        buf.append(".get");
        appendCapitalName(buf, fieldName);
        buf.append("()).thenReturn(");
        if (!appendLiteral(buf, fieldValue)) {
            appendObjectId(buf, fieldValue);
        }
        buf.append(");");
    }

    private void appendDeclareMock(Object object, StringBuilder buf) {
        Class<?> type = object.getClass();
        String className = type.getName();

        buf.append(className);
        buf.append(" ");
        appendObjectId(buf, object);
        buf.append(" = ");
        buf.append(MOCKITO_CLASSNAME);
        buf.append(".mock(");
        buf.append(className);
        buf.append(".class)");
    }

    private void appendNewLine(StringBuilder buf) {
        buf.append("\n");
    }

}
