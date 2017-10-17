package com.baytsif.rxdynamicbus;

import com.baytsif.rxdynamicbus.annotation.Subscribe;
import com.baytsif.rxdynamicbus.thread.EventThread;

import java.util.ArrayList;
import java.util.List;

/**
 * An SubscriberEvent mock that records a String and unregisters itself in the subscriber.
 */
public class UnregisteringStringCatcher {
    private final Bus bus;

    private List<String> events = new ArrayList<String>();

    public UnregisteringStringCatcher(Bus bus) {
        this.bus = bus;
    }

    @Subscribe(
            thread = EventThread.IMMEDIATE
    )
    public void unregisterOnString(String event) {
        bus.unregister(this);
        events.add(event);
    }

    @Subscribe(
            thread = EventThread.IMMEDIATE
    )
    public void zzzSleepinOnStrings(String event) {
        events.add(event);
    }

    @Subscribe(
            thread = EventThread.IMMEDIATE
    )
    public void haveAnInteger(Integer event) {
    }

    @Subscribe(
            thread = EventThread.IMMEDIATE
    )
    public void enjoyThisLong(Long event) {
    }

    @Subscribe(
            thread = EventThread.IMMEDIATE
    )
    public void perhapsATastyDouble(Double event) {
    }

    public List<String> getEvents() {
        return events;
    }
}
