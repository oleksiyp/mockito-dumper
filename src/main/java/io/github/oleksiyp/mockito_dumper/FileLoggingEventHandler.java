package io.github.oleksiyp.mockito_dumper;

import com.lmax.disruptor.EventHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public class FileLoggingEventHandler implements EventHandler<StringBuilder> {
    PrintStream stream;

    FileLoggingEventHandler(File file) throws FileNotFoundException {
        stream = new PrintStream(file);
    }

    @Override
    public void onEvent(StringBuilder event,
                        long sequence,
                        boolean endOfBatch) throws Exception {
        stream.append(event);
    }
}
