package io.github.oleksiyp.mockito_dumper;

import com.lmax.disruptor.EventSink;

public class PublishingFieldWriteGateway implements FieldWriteGateway {
    private final EventSink<FieldWriteEvent> eventSink;

    public PublishingFieldWriteGateway(EventSink<FieldWriteEvent> eventSink) {
        this.eventSink = eventSink;
    }

    @Override
    public void writePrimitiveField(Object object, String field, char typeDesc, long value) {
        eventSink.publishEvent((event, sequence) -> {
            long id = System.identityHashCode(object) & 0xffffffffL;

            event.set(object.getClass().getName(),
                    (int) id,
                    field,
                    typeDesc,
                    value);
        });
    }

    @Override
    public void writeObjectField(Object object, String field, Object value) {
        eventSink.publishEvent((event, sequence) -> {
            long id = System.identityHashCode(object) & 0xffffffffL;
            long valId = System.identityHashCode(value) & 0xffffffffL;

            event.set(object.getClass().getName(),
                    (int) id,
                    field,
                    Type.OBJECT.getDescriptor(),
                    (int) valId);
        });
    }
}
