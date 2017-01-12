package io.github.oleksiyp.mockito_dumper;

import com.lmax.disruptor.EventSink;

public class PublishingInstrumentationGateway implements InstrumentationGateway {
    private final EventSink<StringBuilder> eventSink;
    private final Formatter formatter;

    public PublishingInstrumentationGateway(EventSink<StringBuilder> eventSink,
                                            Formatter formatter) {
        this.eventSink = eventSink;
        this.formatter = formatter;
    }

    @Override
    public void gwSetIntField(Object object, String field, int value) {
        eventSink.publishEvent((builder, sequence) -> {
            try {
                builder.setLength(0);
                formatter.outputFormatted(
                        object,
                        builder);
                builder.append(".");
                builder.append(field);
                builder.append(" = ");
                formatter.outputFormatted(
                        value,
                        builder);
            } catch (Throwable throwable) {
                // skip any
                builder.setLength(0);
            }
        });
    }
}
