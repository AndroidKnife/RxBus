package com.hwangjr.rxbus;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.thread.EventThread;
import com.hwangjr.rxbus.thread.ThreadEnforcer;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Validate that {@link Bus} behaves carefully when listeners publish
 * their own events.
 */
public class ReentrantEventsTest {

    static final String FIRST = "one";
    static final Double SECOND = 2.0d;

    final Bus bus = new Bus(ThreadEnforcer.ANY);

    @Test
    public void noReentrantEvents() {
        ReentrantEventsHater hater = new ReentrantEventsHater();
        bus.register(hater);

        bus.post(FIRST);

        assertEquals("ReentrantEventHater expected 2 events",
                Arrays.<Object>asList(FIRST, SECOND), hater.eventsReceived);
    }

    @Test
    public void eventOrderingIsPredictable() {
        EventProcessor processor = new EventProcessor();
        bus.register(processor);

        EventRecorder recorder = new EventRecorder();
        bus.register(recorder);

        bus.post(FIRST);

        assertEquals("EventRecorder expected events in order",
                Arrays.<Object>asList(FIRST, SECOND), recorder.eventsReceived);
    }

    public static class EventRecorder {
        List<Object> eventsReceived = new ArrayList<Object>();

        @Subscribe(
                thread = EventThread.SINGLE
        )
        public void listenForEverything(Object event) {
            eventsReceived.add(event);
        }
    }

    public class ReentrantEventsHater {
        boolean ready = true;
        List<Object> eventsReceived = new ArrayList<Object>();

        @Subscribe(
                thread = EventThread.SINGLE
        )
        public void listenForStrings(String event) {
            eventsReceived.add(event);
            ready = false;
            try {
                bus.post(SECOND);
            } finally {
                ready = true;
            }
        }

        @Subscribe(
                thread = EventThread.SINGLE
        )
        public void listenForDoubles(Double event) {
            assertTrue("I received an event when I wasn't ready!", ready);
            eventsReceived.add(event);
        }
    }

    public class EventProcessor {
        @Subscribe(
                thread = EventThread.SINGLE
        )
        public void listenForStrings(String event) {
            bus.post(SECOND);
        }
    }
}
