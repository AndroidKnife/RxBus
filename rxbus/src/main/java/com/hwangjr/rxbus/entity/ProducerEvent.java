package com.hwangjr.rxbus.entity;

import com.hwangjr.rxbus.thread.EventThread;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import rx.Observable;
import rx.Subscriber;

/**
 * Wraps a 'producer' method on a specific object.
 * <p/>
 * <p> This class only verifies the suitability of the method and event type if something fails.  Callers are expected
 * to verify their uses of this class.
 */
public class ProducerEvent extends Event {

    /**
     * Object sporting the producer method.
     */
    private final Object target;
    /**
     * Producer method.
     */
    private final Method method;
    /**
     * Producer thread
     */
    private final EventThread thread;
    /**
     * Object hash code.
     */
    private final int hashCode;
    /**
     * Should this producer produce events
     */
    private boolean valid = true;

    public ProducerEvent(Object target, Method method, EventThread thread) {
        if (target == null) {
            throw new NullPointerException("EventProducer target cannot be null.");
        }
        if (method == null) {
            throw new NullPointerException("EventProducer method cannot be null.");
        }

        this.target = target;
        this.thread = thread;
        this.method = method;
        method.setAccessible(true);

        // Compute hash code eagerly since we know it will be used frequently and we cannot estimate the runtime of the
        // target's hashCode call.
        final int prime = 31;
        hashCode = (prime + method.hashCode()) * prime + target.hashCode();
    }

    public boolean isValid() {
        return valid;
    }

    /**
     * If invalidated, will subsequently refuse to produce events.
     * <p/>
     * Should be called when the wrapped object is unregistered from the Bus.
     */
    public void invalidate() {
        valid = false;
    }

    /**
     * Invokes the wrapped producer method and produce a {@link Observable}.
     */
    public Observable produce() {
        return Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                try {
                    subscriber.onNext(produceEvent());
                    subscriber.onCompleted();
                } catch (InvocationTargetException e) {
                    throwRuntimeException("Producer " + ProducerEvent.this + " threw an exception.", e);
                }
            }
        }).subscribeOn(EventThread.getScheduler(thread));
    }

    /**
     * Invokes the wrapped producer method.
     *
     * @throws IllegalStateException     if previously invalidated.
     * @throws InvocationTargetException if the wrapped method throws any {@link Throwable} that is not
     *                                   an {@link Error} ({@code Error}s are propagated as-is).
     */
    private Object produceEvent() throws InvocationTargetException {
        if (!valid) {
            throw new IllegalStateException(toString() + " has been invalidated and can no longer produce events.");
        }
        try {
            return method.invoke(target);
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
        return "[EventProducer " + method + "]";
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
        final ProducerEvent other = (ProducerEvent) obj;
        return method.equals(other.method) && target == other.target;
    }

    public Object getTarget() {
        return target;
    }
}
