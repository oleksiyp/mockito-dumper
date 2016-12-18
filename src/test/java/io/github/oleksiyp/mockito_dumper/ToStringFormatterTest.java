package io.github.oleksiyp.mockito_dumper;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ToStringFormatterTest {
    @Test
    public void testCheckToString() throws Exception {
        assertThat(ToStringFormatter.checkHasToString(new ObjWithToString()))
                .isTrue();
    }

    @Test
    public void testCheckNoToString() throws Exception {
        assertThat(ToStringFormatter.checkHasToString(new ObjWithoutToString()))
                .isFalse();
    }

    class ObjWithToString {
        @Override
        public String toString() {
            return "abc";
        }
    }

    class ObjWithoutToString {

    }
}