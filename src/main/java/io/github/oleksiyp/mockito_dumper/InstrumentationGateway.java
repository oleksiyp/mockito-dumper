package io.github.oleksiyp.mockito_dumper;

public interface InstrumentationGateway {

    InstrumentationGateway NOP = (object, field, value) -> {};

    String GW_SET_INT_FIELD = "gwSetIntField";
    void gwSetIntField(Object object, String field, int value);

    class StaticRef {
        private final String instanceClassName;
        private final String instanceFieldName;

        public StaticRef(Class<?> instanceClassName,
                         String instanceFieldName) {
            this.instanceClassName = instanceClassName.getName();
            this.instanceFieldName = instanceFieldName;
        }

        public String getSetIntFieldMethod() {
            return instanceClassName + "." +
                    instanceFieldName + "." +
                    GW_SET_INT_FIELD;
        }
    }
}
