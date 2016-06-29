package com.hwangjr.rxbus.entity;

import com.hwangjr.rxbus.thread.EventThread;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * Wraps a single-argument 'subscriber' method on a specific object.
 * <p/>
 * <p>This class only verifies the suitability of the method and event type if something fails.  Callers are expected to
 * verify their uses of this class.
 * <p/>
 * <p>Two SubscriberEvent are equivalent when they refer to the same method on the same object (not class).   This
 * property is used to ensure that no subscriber method is registered more than once.
 */
public class SubscriberEvent extends Event {

    /**
     * Object sporting the method.
     */
    private final Object target;
    /**
     * Subscriber method.
     */
    private final Method method;
    /**
     * Subscriber thread
     */
    private final EventThread thread;
    /**
     * RxJava {@link Subject}
     */
    private Subject subject;
    /**
     * Object hash code.
     */
    private final int hashCode;
    /**
     * Should this Subscriber receive events?
     */
    private boolean valid = true;

    public SubscriberEvent(Object target, Method method, EventThread thread) {
        if (target == null) {
            throw new NullPointerException("SubscriberEvent target cannot be null.");
        }
        if (method == null) {
            throw new NullPointerException("SubscriberEvent method cannot be null.");
        }
        if (thread == null) {
            throw new NullPointerException("SubscriberEvent thread cannot be null.");
        }

        this.target = target;
        this.method = method;
        this.thread = thread;
        method.setAccessible(true);
        initObservable();

        // Compute hash code eagerly since we know it will be used frequently and we cannot estimate the runtime of the
        // target's hashCode call.
        final int prime = 31;
        hashCode = (prime + method.hashCode()) * prime + target.hashCode();
    }

    private void initObservable() {
        subject = PublishSubject.create();
        subject.onBackpressureBuffer().observeOn(EventThread.getScheduler(thread))
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object event) {
                        try {
                            if (valid) {
                                handleEvent(event);
                            }
                        } catch (InvocationTargetException e) {
                            throwRuntimeException("Could not dispatch event: " + event.getClass() + " to subscriber " + SubscriberEvent.this, e);
                        }
                    }
                });
    }

    public boolean isValid() {
        return valid;
    }

    /**
     * If invalidated, will subsequently refuse to handle events.
     * <p/>
     * Should be called when the wrapped object is unregistered from the Bus.
     */
    public void invalidate() {
        valid = false;
    }

    public void handle(Object event) {
        subject.onNext(event);
    }

    public Subject getSubject() {
        return subject;
    }

    /**
     * Invokes the wrapped subscriber method to handle {@code event}.
     *
     * @param event event to handle
     * @throws IllegalStateException     if previously invalidated.
     * @throws InvocationTargetException if the wrapped method throws any {@link Throwable} that is not
     *                                   an {@link Error} ({@code Error}s are propagated as-is).
     */
    protected void handleEvent(Object event) throws InvocationTargetException {
        if (!valid) {
            throw new IllegalStateException(toString() + " has been invalidated and can no longer handle events.");
        }
        try {
            method.invoke(target, event);
        } catch (IllegalAccessException e) {
            throw new AssertionError(e);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof Error) {
                throw (Error) e.getCause();
            }
            throw e;
        }
    }

    @Override
    public String toString() {
        return "[SubscriberEvent " + method + "]";
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final SubscriberEvent other = (SubscriberEvent) obj;

        return method.equals(other.method) && target == other.target;
    }

}
