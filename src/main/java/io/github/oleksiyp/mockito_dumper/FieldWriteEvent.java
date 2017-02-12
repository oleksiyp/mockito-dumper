package io.github.oleksiyp.mockito_dumper;

public class FieldWriteEvent {
    private String objectClassName;
    private int objectId;
    private String fieldName;
    private char typeDescriptor;
    private long value;

    public void set(String objectClassName,
                    int objectId,
                    String fieldName,
                    char typeDescriptor,
                    long value) {
        this.objectClassName = objectClassName;
        this.objectId = objectId;
        this.fieldName = fieldName;
        this.typeDescriptor = typeDescriptor;
        this.value = value;
    }

    public String getObjectClassName() {
        return objectClassName;
    }

    public int getObjectId() {
        return objectId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public char getTypeDescriptor() {
        return typeDescriptor;
    }

    public long getValue() {
        return value;
    }
}
