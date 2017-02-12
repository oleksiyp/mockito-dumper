package io.github.oleksiyp.mockito_dumper;

public class FormatUtils {
    public static boolean appendLiteral(StringBuilder buf, Object value) {
        if (value instanceof String) {
            appendStringLiteral(buf, (String) value);
            return true;
        }
        return false;
    }

    public static void appendStringLiteral(StringBuilder buf, String value) {
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

    public static void appendObjId(StringBuilder buf, long id) {
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
}
