package io.github.oleksiyp.mockito_dumper;

import com.lmax.disruptor.EventHandler;
import io.github.oleksiyp.mockito_dumper.FieldWriteGateway.Type;

public class FormattingFieldWriteEventHandler implements EventHandler<FieldWriteEvent> {
    private final StringBuilder builder;
    private final EventHandler<StringBuilder> handler;

    public FormattingFieldWriteEventHandler(EventHandler<StringBuilder> handler) {
        this.handler = handler;
        builder = new StringBuilder(1024);
    }

    @Override
    public void onEvent(FieldWriteEvent event, long sequence, boolean endOfBatch) throws Exception {
        builder.setLength(0);
        builder.append(event.getObjectClassName());
        builder.append(":");
        builder.append("obj");
        FormatUtils.appendObjId(builder, event.getObjectId());
        builder.append(".");
        builder.append(event.getFieldName());
        builder.append(" = ");
        builder.append(event.getTypeDescriptor());
        builder.append(":");
        appendValue(builder, event.getTypeDescriptor(), event.getValue());
        builder.append("\n");

        handler.onEvent(builder, sequence, endOfBatch);
    }

    private void appendValue(StringBuilder builder, char typeDescriptor, long value) {
        switch (Type.of(typeDescriptor)) {
            case BOOLEAN:
                builder.append(value == 1 ? "true" : "false");
                break;
            case CHAR:
                builder.append("'");
                builder.append((char) value);
                builder.append("'");
                return;
            case BYTE:
            case SHORT:
            case INT:
            case LONG:
                builder.append(value);
                break;
            case FLOAT:
                builder.append(Float.intBitsToFloat((int) value));
                break;
            case DOUBLE:
                builder.append(Double.longBitsToDouble(value));
                break;
            case OBJECT:
                builder.append("obj");
                FormatUtils.appendObjId(builder, value);
                break;
        }
    }
}
