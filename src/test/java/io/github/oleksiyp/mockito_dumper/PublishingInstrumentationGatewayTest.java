package io.github.oleksiyp.mockito_dumper;

import com.lmax.disruptor.EventSink;
import com.lmax.disruptor.EventTranslator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PublishingInstrumentationGatewayTest {
    PublishingInstrumentationGateway dumper;

    @Mock
    EventSink<StringBuilder> eventSink;

    @Mock
    Formatter formatter;

    @Before
    public void setUp() throws Exception {
        dumper = new PublishingInstrumentationGateway(eventSink, formatter);
    }

    @Test
    public void shouldPublishAnyObject() throws Exception {
        dumper.gwSetIntField(new TestObject(), "fld", 5);

        verify(eventSink).publishEvent(any());
    }

    @Test
    public void shouldFormatInTranslator() throws Exception {
        ArgumentCaptor<EventTranslator> captor = ArgumentCaptor.forClass(EventTranslator.class);

        StringBuilder buf = new StringBuilder();

        doAnswer(invocation -> {
            EventTranslator<StringBuilder> translator = (EventTranslator<StringBuilder>) invocation.getArguments()[0];

            translator.translateTo(buf, 0);

            return null;
        }).when(eventSink).publishEvent(any());

        TestObject object = new TestObject();

        dumper.gwSetIntField(object, "fld", 33);

        verify(formatter).outputFormatted(same(object), same(buf));
    }

    private static class TestObject {
    }
}