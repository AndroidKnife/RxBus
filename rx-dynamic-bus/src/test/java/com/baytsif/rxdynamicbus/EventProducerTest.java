package com.baytsif.rxdynamicbus;

import com.baytsif.rxdynamicbus.entity.ProducerEvent;
import com.baytsif.rxdynamicbus.thread.EventThread;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import rx.functions.Action1;

import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

public class EventProducerTest {

    private static final Object FIXTURE_RETURN_VALUE = new Object();

    private boolean methodCalled;
    private Object methodReturnValue;

    @Before
    public void setUp() throws Exception {
        methodCalled = false;
        methodReturnValue = FIXTURE_RETURN_VALUE;
    }

    /**
     * Checks that a no-frills, no-issues method call is properly executed.
     *
     * @throws Exception if the aforementioned proper execution is not to be had.
     */
    @Test
    public void basicMethodCall() throws Exception {
        Method method = getRecordingMethod();
        ProducerEvent producer = new ProducerEvent(this, method, EventThread.MAIN_THREAD);
        producer.produce().subscribe(new Action1() {
            @Override
            public void call(Object methodResult) {
                assertTrue("Producer must call provided method.", methodCalled);
                assertSame("Producer result must be *exactly* the specified return value.", methodResult, FIXTURE_RETURN_VALUE);
            }
        });
    }

    /**
     * Checks that EventProducer's constructor disallows null methods.
     */
    @Test
    public void rejectionOfNullMethods() {
        try {
            new ProducerEvent(this, null, EventThread.MAIN_THREAD);
            fail("EventProducer must immediately reject null methods.");
        } catch (NullPointerException expected) {
            // Hooray!
        }
    }

    /**
     * Checks that EventProducer's constructor disallows null targets.
     */
    @Test
    public void rejectionOfNullTargets() throws NoSuchMethodException {
        Method method = getRecordingMethod();
        try {
            new ProducerEvent(null, method, EventThread.MAIN_THREAD);
            fail("EventProducer must immediately reject null targets.");
        } catch (NullPointerException expected) {
            // Huzzah!
        }
    }

    @Test
    public void testExceptionWrapping() throws NoSuchMethodException {
        Method method = getExceptionThrowingMethod();
        ProducerEvent producer = new ProducerEvent(this, method, EventThread.MAIN_THREAD);

        try {
            producer.produce();
            fail("Producers whose methods throw must throw RuntimeException");
        } catch (RuntimeException e) {
            assertTrue("Expected exception must be wrapped.",
                    e.getCause() instanceof IntentionalException);
        }
    }

    @Test
    public void errorPassthrough() throws InvocationTargetException, NoSuchMethodException {
        Method method = getErrorThrowingMethod();
        ProducerEvent producer = new ProducerEvent(this, method, EventThread.MAIN_THREAD);

        try {
            producer.produce();
            fail("Producers whose methods throw Errors must rethrow them");
        } catch (JudgmentError expected) {
            // Expected.
        }
    }

    @Test
    public void returnValueNotCached() throws Exception {
        Method method = getRecordingMethod();
        ProducerEvent producer = new ProducerEvent(this, method, EventThread.MAIN_THREAD);
        producer.produce().subscribe();
        methodReturnValue = new Object();
        methodCalled = false;
        producer.produce().subscribe(new Action1() {
            @Override
            public void call(Object secondReturnValue) {
                assertTrue("Producer must call provided method twice.", methodCalled);
                assertSame("Producer result must be *exactly* the specified return value on each invocation.",
                        secondReturnValue, methodReturnValue);
            }
        });
    }

    private Method getRecordingMethod() throws NoSuchMethodException {
        return getClass().getMethod("recordingMethod");
    }

    private Method getExceptionThrowingMethod() throws NoSuchMethodException {
        return getClass().getMethod("exceptionThrowingMethod");
    }

    private Method getErrorThrowingMethod() throws NoSuchMethodException {
        return getClass().getMethod("errorThrowingMethod");
    }

    /**
     * Records the invocation in {@link #methodCalled} and returns the value in
     * {@link #FIXTURE_RETURN_VALUE}.
     */
    public Object recordingMethod() {
        if (methodCalled) {
            throw new IllegalStateException("Method called more than once.");
        }
        methodCalled = true;
        return methodReturnValue;
    }

    public Object exceptionThrowingMethod() throws Exception {
        throw new IntentionalException();
    }

    /**
     * Local exception subclass to check variety of exception thrown.
     */
    static class IntentionalException extends Exception {
        private static final long serialVersionUID = -2500191180248181379L;
    }

    public Object errorThrowingMethod() {
        throw new JudgmentError();
    }

    /**
     * Local Error subclass to check variety of error thrown.
     */
    static class JudgmentError extends Error {
        private static final long serialVersionUID = 634248373797713373L;
    }
}
