package io.github.oleksiyp.mockito_dumper;

import com.lmax.disruptor.EventSink;

public class PublishingObjectDumper implements ObjectDumper {
    private final EventSink<StringBuilder> eventSink;
    private final Formatter formatter;

    public PublishingObjectDumper(EventSink<StringBuilder> eventSink,
                                  Formatter formatter) {
        this.eventSink = eventSink;
        this.formatter = formatter;
    }

    @Override
    public void dump(Object object) {
        eventSink.publishEvent((builder, sequence) -> {
            try {
                builder.setLength(0);
                formatter.outputFormatted(
                        object,
                        builder);
            } catch (Throwable throwable) {
                // skip any
                builder.setLength(0);
            }
        });
    }
}
