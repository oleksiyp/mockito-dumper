package io.github.oleksiyp.mockito_dumper;

public interface Formatter {
    void outputFormatted(Object object,
                         StringBuilder buf);
}
