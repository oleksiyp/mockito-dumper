package io.github.oleksiyp.mockito_dumper;

import io.github.oleksiyp.mockito_dumper.FieldWriteGateway.StaticRef;
import javassist.ClassPool;
import javassist.Loader;
import javassist.NotFoundException;
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
public class FieldWriteInstrumentationTest {
    public static FieldWriteGateway GATEWAY;
    public static final StaticRef GATEWAY_REF = new StaticRef(FieldWriteInstrumentationTest.class, "GATEWAY");

    @Mock
    FieldWriteGateway gateway;

    FieldWriteInstrumentation instrumentation;

    ClassPool pool;

    Loader loader;

    @Before
    public void setUp() throws Exception {
        pool = new ClassPool();
        pool.appendSystemPath();

        loader = new Loader(Thread.currentThread().getContextClassLoader(), pool);

        loader.delegateLoadingOf(Callable.class.getName());
        loader.delegateLoadingOf(Runnable.class.getName());
        loader.delegateLoadingOf(FieldWriteGateway.class.getName());
        loader.delegateLoadingOf(FieldWriteInstrumentationTest.class.getName());

        instrumentation = new FieldWriteInstrumentation();

        GATEWAY = gateway;
    }

    public static class BooleanFieldTestObject implements Callable<Runnable> {
        boolean field1;

        @Override
        public Runnable call() throws Exception {
            field1 = true;

            return () -> assertThat(field1).isEqualTo(true);
        }
    }

    @Test
    public void shouldInstrumentBooleanFieldWriteRoutedToGateway() throws Throwable {
        Callable<Runnable> fakeObj = constructInstrumentedTestObj(BooleanFieldTestObject.class.getName());

        Runnable checker = fakeObj.call();

        verify(gateway, only())
                .writePrimitiveField(fakeObj, "field1", 'Z', 1);

        checker.run();
    }

    public static class ByteFieldTestObject implements Callable<Runnable> {
        byte field1;

        @Override
        public Runnable call() throws Exception {
            field1 = 5;

            return () -> assertThat(field1).isEqualTo((byte) 5);
        }
    }

    @Test
    public void shouldInstrumentByteFieldWriteRoutedToGateway() throws Throwable {
        Callable<Runnable> fakeObj = constructInstrumentedTestObj(ByteFieldTestObject.class.getName());

        Runnable checker = fakeObj.call();

        verify(gateway, only())
                .writePrimitiveField(fakeObj, "field1", 'B', 5);

        checker.run();
    }

    public static class ShortFieldTestObject implements Callable<Runnable> {
        short field1;

        @Override
        public Runnable call() throws Exception {
            field1 = 5;

            return () -> assertThat(field1).isEqualTo((short) 5);
        }
    }

    @Test
    public void shouldInstrumentShortFieldWriteRoutedToGateway() throws Throwable {
        Callable<Runnable> fakeObj = constructInstrumentedTestObj(ShortFieldTestObject.class.getName());

        Runnable checker = fakeObj.call();

        verify(gateway, only())
                .writePrimitiveField(fakeObj, "field1", 'S', 5);

        checker.run();
    }

    public static class CharFieldTestObject implements Callable<Runnable> {
        char field1;

        @Override
        public Runnable call() throws Exception {
            field1 = 'a';

            return () -> assertThat(field1).isEqualTo('a');
        }
    }

    @Test
    public void shouldInstrumentCharFieldWriteRoutedToGateway() throws Throwable {
        Callable<Runnable> fakeObj = constructInstrumentedTestObj(CharFieldTestObject.class.getName());

        Runnable checker = fakeObj.call();

        verify(gateway, only())
                .writePrimitiveField(fakeObj, "field1", 'C', (int) 'a');

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

    @Test
    public void shouldInstrumentIntFieldWriteRoutedToGateway() throws Throwable {
        Callable<Runnable> fakeObj = constructInstrumentedTestObj(IntFieldTestObject.class.getName());

        Runnable checker = fakeObj.call();

        verify(gateway, only())
                .writePrimitiveField(fakeObj, "field1", 'I', 5);

        checker.run();
    }

    public static class LongFieldTestObject implements Callable<Runnable> {
        long field1;

        @Override
        public Runnable call() throws Exception {
            field1 = 5;

            return () -> assertThat(field1).isEqualTo(5);
        }
    }

    @Test
    public void shouldInstrumentLongFieldWriteRoutedToGateway() throws Throwable {
        Callable<Runnable> fakeObj = constructInstrumentedTestObj(LongFieldTestObject.class.getName());

        Runnable checker = fakeObj.call();

        verify(gateway, only())
                .writePrimitiveField(fakeObj, "field1", 'J', 5);

        checker.run();
    }

    public static class FloatFieldTestObject implements Callable<Runnable> {
        float field1;

        @Override
        public Runnable call() throws Exception {
            field1 = 5;

            return () -> assertThat(field1).isEqualTo(5);
        }
    }

    @Test
    public void shouldInstrumentFloatFieldWriteRoutedToGateway() throws Throwable {
        Callable<Runnable> fakeObj = constructInstrumentedTestObj(FloatFieldTestObject.class.getName());

        Runnable checker = fakeObj.call();

        verify(gateway, only())
                .writePrimitiveField(fakeObj, "field1", 'F', Float.floatToRawIntBits(5));

        checker.run();
    }

    public static class DoubleFieldTestObject implements Callable<Runnable> {
        double field1;

        @Override
        public Runnable call() throws Exception {
            field1 = 5;

            return () -> assertThat(field1).isEqualTo(5);
        }
    }

    @Test
    public void shouldInstrumentDoubleFieldWriteRoutedToGateway() throws Throwable {
        Callable<Runnable> fakeObj = constructInstrumentedTestObj(DoubleFieldTestObject.class.getName());

        Runnable checker = fakeObj.call();

        verify(gateway, only())
                .writePrimitiveField(fakeObj, "field1", 'D', Double.doubleToRawLongBits(5));

        checker.run();
    }

    public static class ObjectFieldTestObject implements Callable<Runnable> {
        Object field1;

        @Override
        public Runnable call() throws Exception {
            field1 = 5d;

            return () -> assertThat(field1).isEqualTo(5d);
        }
    }

    @Test
    public void shouldInstrumentObjectFieldWriteRoutedToGateway() throws Throwable {
        Callable<Runnable> fakeObj = constructInstrumentedTestObj(ObjectFieldTestObject.class.getName());

        Runnable checker = fakeObj.call();

        verify(gateway, only())
                .writeObjectField(fakeObj, "field1", 5d);

        checker.run();
    }

    @Test
    public void shouldInstrumentAnonymousInnerClass() throws Throwable {
        instrument(AnonymousInnerClassTestObject.class.getName() + "$1");

        Callable<Runnable> fakeObj = constructInstrumentedTestObj(AnonymousInnerClassTestObject.class.getName());


        Runnable checker = fakeObj.call();

        verify(gateway, only())
                .writePrimitiveField(fakeObj, "field1", 'I', 33);

        checker.run();
    }

    public static class AnonymousInnerClassTestObject implements Callable<Runnable> {
        int field1;

        @Override
        public Runnable call() throws Exception {
            new Runnable() {
                @Override
                public void run() {
                    field1 = 33;
                }
            }.run();

            return () -> {
                assertThat(field1).isEqualTo(33);
            };
        }
    }

    private Callable<Runnable> constructInstrumentedTestObj(String fakeObjectClassName) throws NotFoundException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        instrument(fakeObjectClassName);

        Class<?> instrumentedFakeObjectCls = loader.loadClass(fakeObjectClassName);

        return (Callable<Runnable>) instrumentedFakeObjectCls.newInstance();
    }

    private void instrument(String fakeObjectClassName) throws NotFoundException {
        instrumentation.instrument(
                pool.get(fakeObjectClassName),
                GATEWAY_REF);
    }

}
