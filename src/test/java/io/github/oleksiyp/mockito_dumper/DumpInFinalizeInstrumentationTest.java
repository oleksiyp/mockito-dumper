package io.github.oleksiyp.mockito_dumper;

import javassist.ClassPool;
import javassist.Loader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Method;

import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class DumpInFinalizeInstrumentationTest {
    public static ObjectDumper DUMPER_INSTANCE;
    public static final String INSTANCE_CLASS_NAME = DumpInFinalizeInstrumentationTest.class.getName();
    public static final String INSTANCE_FIELD_NAME = "DUMPER_INSTANCE";

    public static final String CALL_FINALIZE_METHOD = "callFinalize";

    @Mock
    FinalizeListener dumpListener;

    @Mock
    FinalizeListener superListener;

    @Mock
    FinalizeListener thisListener;

    DumpInFinalizeInstrumentation instrumentation;

    ClassPool pool;

    Loader loader;

    @Before
    public void setUp() throws Exception {
        pool = new ClassPool();
        pool.appendSystemPath();

        loader = new Loader(Thread.currentThread().getContextClassLoader(), pool);

        loader.delegateLoadingOf(FinalizeListener.class.getName());

        instrumentation = new DumpInFinalizeInstrumentation();
    }

    public interface FinalizeListener {
        void finalizeCalled(Object obj);
    }

    @Test
    public void shouldInstrumentFinalizerToDumpValues() throws Throwable {
        String fakeObjectClassName = SimpleFakeObject.class.getName();

        instrumentation.instrument(
                pool.get(fakeObjectClassName),
                INSTANCE_CLASS_NAME,
                INSTANCE_FIELD_NAME
        );

        Class<?> instrumentedFakeObjectCls = loader.loadClass(fakeObjectClassName);

        Object instrumentedFakeObj = instrumentedFakeObjectCls.newInstance();

        Method callFinalizeMethod = instrumentedFakeObjectCls.getMethod(CALL_FINALIZE_METHOD,
                FinalizeListener.class);

        callFinalizeMethod.invoke(instrumentedFakeObj, dumpListener);

        verify(dumpListener).finalizeCalled(instrumentedFakeObj);
    }

    public static class SimpleFakeObject {
        public void callFinalize(final FinalizeListener listener) throws Throwable {
            DUMPER_INSTANCE = new TestDumper(listener);

            finalize();
        }
    }

    @Test
    public void shouldInstrumentFinalizerToDumpValuesIfMethodExists() throws Throwable {
        String fakeObjectClassName = FakeObjectWithFinalize.class.getName();

        instrumentation.instrument(
                pool.get(fakeObjectClassName),
                INSTANCE_CLASS_NAME,
                INSTANCE_FIELD_NAME
        );

        Class<?> instrumentedFakeObjectCls = loader.loadClass(fakeObjectClassName);

        Object instrumentedFakeObj = instrumentedFakeObjectCls.newInstance();

        Method callFinalizeMethod = instrumentedFakeObjectCls.getMethod(CALL_FINALIZE_METHOD,
                FinalizeListener.class,
                FinalizeListener.class);

        callFinalizeMethod.invoke(instrumentedFakeObj, dumpListener, thisListener);

        verify(dumpListener).finalizeCalled(instrumentedFakeObj);

        verify(thisListener).finalizeCalled(instrumentedFakeObj);
    }

    public static class FakeObjectWithFinalize {
        FinalizeListener listener;

        @Override
        protected void finalize() throws Throwable {
            listener.finalizeCalled(this);
        }

        public void callFinalize(FinalizeListener dumperListener,
                                 FinalizeListener listener) throws Throwable {
            DUMPER_INSTANCE = new TestDumper(dumperListener);

            this.listener = listener;

            finalize();
        }

    }


    @Test
    public void shouldInstrumentFinalizerToDumpValuesAndCallSuperFinalize() throws Throwable {
        String fakeObjectClassName = FakeObjectWithSuperFinalize.class.getName();

        instrumentation.instrument(
                pool.get(fakeObjectClassName),
                INSTANCE_CLASS_NAME,
                INSTANCE_FIELD_NAME
        );

        Class<?> instrumentedFakeObjectCls = loader.loadClass(fakeObjectClassName);

        Object instrumentedFakeObj = instrumentedFakeObjectCls.newInstance();

        Method callFinalizeMethod = instrumentedFakeObjectCls.getMethod("callFinalize", FinalizeListener.class, FinalizeListener.class);

        callFinalizeMethod.invoke(instrumentedFakeObj, dumpListener, superListener);

        verify(dumpListener).finalizeCalled(instrumentedFakeObj);

        verify(superListener).finalizeCalled(instrumentedFakeObj);
    }

    public static class FakeObjectWithSuperFinalize extends FakeObjectWithFinalize {
    }

    private static class TestDumper implements ObjectDumper {
        private final FinalizeListener dumperListener;

        public TestDumper(FinalizeListener dumperListener) {
            this.dumperListener = dumperListener;
        }

        public void dump(Object object) {
            dumperListener.finalizeCalled(object);
        }
    }
}
