package com.baytsif.rxdynamicbus;

import com.baytsif.rxdynamicbus.annotation.Produce;
import com.baytsif.rxdynamicbus.annotation.Subscribe;
import com.baytsif.rxdynamicbus.entity.EventType;
import com.baytsif.rxdynamicbus.entity.ProducerEvent;
import com.baytsif.rxdynamicbus.entity.SubscriberEvent;
import com.baytsif.rxdynamicbus.finder.Finder;
import com.baytsif.rxdynamicbus.thread.EventThread;
import com.baytsif.rxdynamicbus.thread.ThreadEnforcer;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

/**
 * Test case for subscribers which unregister while handling an event.
 */
public class UnregisteringSubscriberTest {

    private static final String EVENT = "Hello";
    private static final String BUS_IDENTIFIER = "test-bus";

    private Bus bus;

    @Before
    public void setUp() throws Exception {
        bus = new Bus(ThreadEnforcer.ANY, BUS_IDENTIFIER, new SortedSubscriberFinder());
    }

    @Test
    public void unregisterInSubscriber() {
        UnregisteringStringCatcher catcher = new UnregisteringStringCatcher(bus);
        bus.register(catcher);
        bus.post(EVENT);

        assertEquals("One correct event should be delivered.", Arrays.asList(EVENT), catcher.getEvents());

        bus.post(EVENT);
        bus.post(EVENT);
        assertEquals("Shouldn't catch any more events when unregistered.", Arrays.asList(EVENT), catcher.getEvents());
    }

    @Test
    public void unregisterInSubscriberWhenEventProduced() throws Exception {
        UnregisteringStringCatcher catcher = new UnregisteringStringCatcher(bus);

        bus.register(new StringProducer());
        bus.register(catcher);
        assertEquals(Arrays.asList(StringProducer.VALUE), catcher.getEvents());

        bus.post(EVENT);
        bus.post(EVENT);
        assertEquals("Shouldn't catch any more events when unregistered.",
                Arrays.asList(StringProducer.VALUE), catcher.getEvents());
    }

    @Test
    public void unregisterProducerInSubscriber() throws Exception {
        final Object producer = new Object() {
            private int calls = 0;

            @Produce(
                    thread = EventThread.IMMEDIATE
            )
            public String produceString() {
                calls++;
                if (calls > 1) {
                    fail("Should only have been called once, then unregistered and never called again.");
                }
                return "Please enjoy this hand-crafted String.";
            }
        };
        bus.register(producer);
        bus.register(new Object() {
            @Subscribe(
                    thread = EventThread.IMMEDIATE
            )
            public void firstUnsubscribeTheProducer(String produced) {
                bus.unregister(producer);
            }

            @Subscribe(
                    thread = EventThread.IMMEDIATE
            )
            public void shouldNeverBeCalled(String uhoh) {
                fail("Shouldn't receive events from an unregistered producer.");
            }
        });
    }

    /**
     * Delegates to {@code Finder.ANNOTATED}, then sorts results by {@code SubscriberEvent#toString}
     */
    static class SortedSubscriberFinder implements Finder {

        static Comparator<SubscriberEvent> subscriberComparator = new Comparator<SubscriberEvent>() {
            @Override
            public int compare(SubscriberEvent eventSubscriber, SubscriberEvent eventSubscriber1) {
                return eventSubscriber.toString().compareTo(eventSubscriber1.toString());
            }
        };

        @Override
        public Map<EventType, ProducerEvent> findAllProducers(Object listener) {
            return Finder.ANNOTATED.findAllProducers(listener);
        }

        @Override
        public Map<EventType, Set<SubscriberEvent>> findAllSubscribers(Object listener) {
            Map<EventType, Set<SubscriberEvent>> found = Finder.ANNOTATED.findAllSubscribers(listener);
            Map<EventType, Set<SubscriberEvent>> sorted = new HashMap<>();
            for (Map.Entry<EventType, Set<SubscriberEvent>> entry : found.entrySet()) {
                SortedSet<SubscriberEvent> subscribers = new TreeSet<>(subscriberComparator);
                subscribers.addAll(entry.getValue());
                sorted.put(entry.getKey(), subscribers);
            }
            return sorted;
        }
    }
}
