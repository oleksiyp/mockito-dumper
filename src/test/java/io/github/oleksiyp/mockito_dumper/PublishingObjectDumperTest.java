package io.github.oleksiyp.mockito_dumper;

import com.lmax.disruptor.EventSink;
import com.lmax.disruptor.EventTranslator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PublishingObjectDumperTest {
    PublishingObjectDumper dumper;

    @Mock
    EventSink<StringBuilder> eventSink;

    @Mock
    Formatter formatter;

    @Before
    public void setUp() throws Exception {
        dumper = new PublishingObjectDumper(eventSink, formatter);
    }

    @Test
    public void shouldPublishAnyObject() throws Exception {
        dumper.dump(new TestObject());

        verify(eventSink).publishEvent(any());
    }

    @Test
    public void shouldPublishAndFormatInTranslator() throws Exception {
        ArgumentCaptor<EventTranslator> captor = ArgumentCaptor.forClass(EventTranslator.class);

        StringBuilder buf = new StringBuilder();

        doAnswer(invocation -> {
            EventTranslator<StringBuilder> translator = (EventTranslator<StringBuilder>) invocation.getArguments()[0];

            translator.translateTo(buf, 0);

            return null;
        }).when(eventSink).publishEvent(any());

        TestObject object = new TestObject();

        dumper.dump(object);

        verify(eventSink).publishEvent(any());

        verify(formatter).outputFormatted(same(object), same(buf));
    }

    private static class TestObject {
    }
}