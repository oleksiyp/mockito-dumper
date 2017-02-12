package io.github.oleksiyp.mockito_dumper;

import com.lmax.disruptor.EventSink;
import com.lmax.disruptor.EventTranslator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PublishingFieldWriteGatewayTest {
    PublishingFieldWriteGateway gateway;

    @Mock
    EventSink<FieldWriteEvent> eventSink;

    @Mock
    FieldWriteEvent event;

    @Before
    public void setUp() throws Exception {
        gateway = new PublishingFieldWriteGateway(eventSink);

        doAnswer(answerWithTranslateEvent())
                .when(eventSink)
                .publishEvent(any());
    }

    @Test
    public void shouldPublishPrimitiveTypeSetEvent() throws Exception {
        TestObject obj = new TestObject();

        gateway.writePrimitiveField(obj, "fld", 'c', 5);

        verify(event)
                .set(obj.getClass().getName(), objId(obj), "fld", 'c', 5);
    }

    @Test
    public void shouldPublishObjectTypeSetEvent() throws Exception {
        TestObject obj1 = new TestObject();
        TestObject obj2 = new TestObject();

        gateway.writeObjectField(obj1, "fld", obj2);

        verify(event)
                .set(obj1.getClass().getName(),
                        objId(obj1),
                        "fld",
                        'L',
                        objId(obj2));
    }

    private static int objId(Object obj) {
        return System.identityHashCode(obj);
    }

    private Answer answerWithTranslateEvent() {
        return invocation -> {
            ((EventTranslator) invocation.getArguments()[0])
                    .translateTo(event, 0);
            return null;
        };
    }

    private static class TestObject {
    }
}