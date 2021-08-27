package com.hwangjr.rxbus;

import static junit.framework.Assert.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;

import com.hwangjr.rxbus.annotation.Produce;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.thread.EventThread;
import com.hwangjr.rxbus.thread.ThreadEnforcer;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Test that Bus finds the correct producers.
 * <p/>
 * This test must be outside the c.g.c.rxbus package to test correctly.
 */
@SuppressWarnings("UnusedDeclaration")
public class AnnotatedProducerFinderTest {

    @Test
    public void simpleProducer() {
        Bus bus = new Bus(ThreadEnforcer.ANY);
        Subscriber subscriber = new Subscriber();
        SimpleProducer producer = new SimpleProducer();

        bus.register(producer);
        assertThat(producer.produceCalled).isEqualTo(0);
        bus.register(subscriber);
        assertThat(producer.produceCalled).isEqualTo(1);
        assertEquals(Arrays.asList(SimpleProducer.VALUE), subscriber.events);
    }

    @Test
    public void multipleSubscriptionsCallsProviderEachTime() {
        Bus bus = new Bus(ThreadEnforcer.ANY);
        SimpleProducer producer = new SimpleProducer();

        bus.register(producer);
        bus.register(new Subscriber());
        assertThat(producer.produceCalled).isEqualTo(1);
        bus.register(new Subscriber());
        assertThat(producer.produceCalled).isEqualTo(2);
    }

    static class Subscriber {
        final List<Object> events = new ArrayList<Object>();

        @Subscribe(
                thread = EventThread.SINGLE
        )
        public void subscribe(Object o) {
            events.add(o);
        }
    }

    static class SimpleProducer {
        static final Object VALUE = new Object();

        int produceCalled = 0;

        @Produce(
                thread = EventThread.SINGLE
        )
        public Object produceIt() {
            produceCalled += 1;
            return VALUE;
        }
    }
}
