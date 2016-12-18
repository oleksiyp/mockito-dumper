package io.github.oleksiyp.mockito_dumper;

public interface ObjectDumper {
    ObjectDumper NOP = object -> {};

    void dump(Object object);
}
