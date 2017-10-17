package com.baytsif.rxdynamicbus.entity;

import com.baytsif.rxdynamicbus.thread.EventThread;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import rx.Subscriber;
import rx.functions.Action1;
import rx.observers.TestSubscriber;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

public class EventSubscriberTest {

    private static final Object FIXTURE_ARGUMENT = new Object();

    private boolean methodCalled;
    private Object methodArgument;

    @Before
    public void setUp() throws Exception {
        methodCalled = false;
        methodArgument = null;
    }

    /**
     * Checks that a no-frills, no-issues method call is properly executed.
     *
     * @throws Exception if the aforementioned proper execution is not to be had.
     */
    @Test
    public void basicMethodCall() throws Exception {
        Method method = getRecordingMethod();

        SubscriberEvent subscriber = new SubscriberEvent(this, method, EventThread.IMMEDIATE);

        subscriber.handle(FIXTURE_ARGUMENT);

        assertTrue("Subscriber must call provided method.", methodCalled);
        assertSame("Subscriber argument must be *exactly* the provided object.",
                methodArgument, FIXTURE_ARGUMENT);
    }

    /**
     * Checks that SubscriberEvent's constructor disallows null methods.
     */
    @Test
    public void rejectionOfNullMethods() {
        try {
            new SubscriberEvent(this, null, EventThread.IMMEDIATE);
            fail("SubscriberEvent must immediately reject null methods.");
        } catch (NullPointerException expected) {
            // Hooray!
        }
    }

    /**
     * Checks that SubscriberEvent's constructor disallows null targets.
     */
    @Test
    public void rejectionOfNullTargets() throws NoSuchMethodException {
        Method method = getRecordingMethod();
        try {
            new SubscriberEvent(null, method, EventThread.IMMEDIATE);
            fail("SubscriberEvent must immediately reject null targets.");
        } catch (NullPointerException expected) {
            // Huzzah!
        }
    }

    @Test
    public void exceptionWrapping() throws NoSuchMethodException {
        Method method = getExceptionThrowingMethod();
        SubscriberEvent event = new SubscriberEvent(this, method, EventThread.IMMEDIATE);

        event.getSubject().subscribe(new Action1() {
            @Override
            public void call(Object o) {
                fail("Subscribers whose methods throw must throw RuntimeException");
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                // Expected.
                assertTrue("Expected exception must be wrapped.",
                        throwable.getCause() instanceof IntentionalException);
            }
        });
        event.handle(new Object());
    }

    @Test
    public void errorPassthrough() throws InvocationTargetException, NoSuchMethodException {
        Method method = getErrorThrowingMethod();
        SubscriberEvent event = new SubscriberEvent(this, method, EventThread.IMMEDIATE);

        event.getSubject().subscribe(new Action1() {
            @Override
            public void call(Object o) {
                fail("Subscribers whose methods throw Errors must rethrow them");
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                // Expected.
                assertEquals(throwable.getClass(), JudgmentError.class);
            }
        });
        event.handle(new Object());
    }

    @Test
    public void backPressure() throws NoSuchMethodException {
        Method method = getPrintMethod();
        final SubscriberEvent subscriber = new SubscriberEvent(this, method, EventThread.IO);

        Subject subject = PublishSubject.create();
        TestSubscriber testSubscriber = TestSubscriber.create(new Subscriber() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(Object o) {
                try {
                    if (subscriber.isValid()) {
                        subscriber.handleEvent(0);
                    }
                } catch (InvocationTargetException e) {
                    subscriber.throwRuntimeException("Could not dispatch event: " + o.getClass() + " to subscriber " + subscriber, e);
                }
            }
        });
        subject.onBackpressureBuffer().observeOn(EventThread.getScheduler(EventThread.IO))
                .subscribe(testSubscriber);
        try {
            Field subjectField = subscriber.getClass().getDeclaredField("subject");
            subjectField.setAccessible(true);
            subjectField.set(subscriber, subject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < 2000; i++) {
            System.out.println("back pressure : " + i);
            subscriber.getSubject().onNext(new Object());
        }
        subscriber.getSubject().onCompleted();
        testSubscriber.assertNoErrors();
    }

    private Method getRecordingMethod() throws NoSuchMethodException {
        return getClass().getMethod("recordingMethod", Object.class);
    }

    private Method getExceptionThrowingMethod() throws NoSuchMethodException {
        return getClass().getMethod("exceptionThrowingMethod", Object.class);
    }

    private Method getErrorThrowingMethod() throws NoSuchMethodException {
        return getClass().getMethod("errorThrowingMethod", Object.class);
    }

    private Method getPrintMethod() throws NoSuchMethodException {
        return getClass().getMethod("printMethod", Object.class);
    }

    public void printMethod(Object arg) {
        System.out.print("print arg=" + arg);
    }

    /**
     * Records the provided object in {@link #methodArgument} and sets
     * {@link #methodCalled}.
     *
     * @param arg argument to record.
     */
    public void recordingMethod(Object arg) {
        if (methodCalled) {
            throw new IllegalStateException("Method called more than once.");
        }
        methodCalled = true;
        methodArgument = arg;
    }

    public void exceptionThrowingMethod(Object arg) throws Exception {
        throw new IntentionalException();
    }

    /**
     * Local exception subclass to check variety of exception thrown.
     */
    static class IntentionalException extends Exception {
        private static final long serialVersionUID = -2500191180248181379L;
    }

    public void errorThrowingMethod(Object arg) {
        throw new JudgmentError();
    }

    /**
     * Local Error subclass to check variety of error thrown.
     */
    static class JudgmentError extends Error {
        private static final long serialVersionUID = 634248373797713373L;
    }
}
