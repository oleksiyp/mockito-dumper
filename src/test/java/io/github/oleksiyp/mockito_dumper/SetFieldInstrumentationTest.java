package io.github.oleksiyp.mockito_dumper;

import io.github.oleksiyp.mockito_dumper.InstrumentationGateway.StaticRef;
import javassist.ClassPool;
import javassist.Loader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.concurrent.Callable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class SetFieldInstrumentationTest {
    public static InstrumentationGateway GATEWAY;
    public static final StaticRef GATEWAY_REF = new StaticRef(SetFieldInstrumentationTest.class, "GATEWAY");

    @Mock
    InstrumentationGateway gateway;

    SetFieldInstrumentation instrumentation;

    ClassPool pool;

    Loader loader;

    @Before
    public void setUp() throws Exception {
        pool = new ClassPool();
        pool.appendSystemPath();

        loader = new Loader(Thread.currentThread().getContextClassLoader(), pool);

        loader.delegateLoadingOf(Callable.class.getName());
        loader.delegateLoadingOf(Runnable.class.getName());
        loader.delegateLoadingOf(InstrumentationGateway.class.getName());
        loader.delegateLoadingOf(SetFieldInstrumentationTest.class.getName());

        instrumentation = new SetFieldInstrumentation();

        GATEWAY = gateway;
    }

    @Test
    public void shouldInstrumentIntFieldWriteRoutedToGateway() throws Throwable {
        String fakeObjectClassName = IntFieldTestObject.class.getName();

        instrumentation.instrument(
                pool.get(fakeObjectClassName),
                GATEWAY_REF);

        Class<?> instrumentedFakeObjectCls = loader.loadClass(fakeObjectClassName);

        Callable<Runnable> instrumentedFakeObj = (Callable<Runnable>) instrumentedFakeObjectCls.newInstance();

        Runnable checker = instrumentedFakeObj.call();

        verify(gateway, only()).gwSetIntField(instrumentedFakeObj, "field1", 5);

        checker.run();
    }

    public static class IntFieldTestObject implements Callable<Runnable> {
        int field1;

        @Override
        public Runnable call() throws Exception {
            field1 = 5;

            return () -> assertThat(field1).isEqualTo(5);
        }
    }
}
