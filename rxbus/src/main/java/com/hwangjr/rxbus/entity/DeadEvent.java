package com.hwangjr.rxbus.entity;

import com.hwangjr.rxbus.Bus;

/**
 * Wraps an event that was posted, but which had no subscribers and thus could not be delivered.
 * <p/>
 * <p>Subscribing a DeadEvent is useful for debugging or logging, as it can detect misconfigurations in a
 * system's event distribution.
 */
public class DeadEvent {

    public final Object source;
    public final Object event;

    /**
     * Creates a new DeadEvent.
     *
     * @param source object broadcasting the DeadEvent (generally the {@link Bus}).
     * @param event  the event that could not be delivered.
     */
    public DeadEvent(Object source, Object event) {
        this.source = source;
        this.event = event;
    }

}
