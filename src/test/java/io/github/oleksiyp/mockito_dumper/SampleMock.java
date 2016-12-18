package io.github.oleksiyp.mockito_dumper;

import org.mockito.Mockito;

public class SampleMock {

    int value;

    public int getValue() {
        return value;
    }

    {
        SampleMock mock = Mockito.mock(SampleMock.class);
        Mockito.when(mock.getValue()).thenReturn(5);

    }
}
