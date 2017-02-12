package io.github.oleksiyp.mockito_dumper;

public interface FieldWriteGateway {
    void writePrimitiveField(Object object, String field, char typeDesc, long value);

    void writeObjectField(Object object, String field, Object value);

    enum Type {
        BOOLEAN('Z'),
        CHAR('C'),
        BYTE('B'),
        SHORT('S'),
        INT('I'),
        LONG('J'),
        FLOAT('F'),
        DOUBLE('D'),
        OBJECT('L');

        private char descriptor;

        public static Type of(char typeDescriptor) {
            switch (typeDescriptor) {
                case 'Z': return Type.BOOLEAN;
                case 'C': return Type.CHAR;
                case 'B': return Type.BYTE;
                case 'S': return Type.SHORT;
                case 'I': return Type.INT;
                case 'J': return Type.LONG;
                case 'F': return Type.FLOAT;
                case 'D': return Type.DOUBLE;
                case 'L': return Type.OBJECT;
                default: return null;
            }
        }

        Type(char descriptor) {
            this.descriptor = descriptor;
        }

        public char getDescriptor() {
            return descriptor;
        }

    }

    class StaticRef {
        private final String instanceClassName;
        private final String instanceFieldName;

        public StaticRef(Class<?> instanceClassName,
                         String instanceFieldName) {
            this.instanceClassName = instanceClassName.getName();
            this.instanceFieldName = instanceFieldName;
        }

        public String writePrimitiveFieldMethod() {
            return writeFieldMethod("Primitive");
        }

        public String writeObjectFieldMethod() {
            return writeFieldMethod("Object");
        }

        private String writeFieldMethod(String type) {
            return instanceClassName + "." +
                    instanceFieldName + "." +
                    "write" + type + "Field";
        }
    }
}
