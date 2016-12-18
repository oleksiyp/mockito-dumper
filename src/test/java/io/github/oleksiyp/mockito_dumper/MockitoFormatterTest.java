package io.github.oleksiyp.mockito_dumper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MockitoFormatterTest {

    @Test
    public void shouldDumpValue() throws Exception {
        Formatter dumper = new MockitoFormatter();

        TestObject testObj = new TestObject();

        StringBuilder builder = new StringBuilder();

        dumper.outputFormatted(testObj, builder);

        System.out.println(builder);

        builder.setLength(0);

        dumper.outputFormatted(testObj.obj2, builder);

        System.out.println(builder);
    }

    class Obj2 {

    }

    class TestObject {
        String abc = "va\nlue";

        Obj2 obj2 = new Obj2();
    }

}