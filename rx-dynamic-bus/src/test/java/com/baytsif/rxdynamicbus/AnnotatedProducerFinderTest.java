package com.baytsif.rxdynamicbus;

import com.baytsif.rxdynamicbus.annotation.Produce;
import com.baytsif.rxdynamicbus.annotation.Subscribe;
import com.baytsif.rxdynamicbus.thread.EventThread;
import com.baytsif.rxdynamicbus.thread.ThreadEnforcer;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test that Bus finds the correct producers.
 * <p/>
 * This test must be outside the c.g.c.rxbus package to test correctly.
 */
@SuppressWarnings("UnusedDeclaration")
public class AnnotatedProducerFinderTest {

    static class Subscriber {
        final List<Object> events = new ArrayList<Object>();

        @Subscribe(
                thread = EventThread.IMMEDIATE
        )
        public void subscribe(Object o) {
            events.add(o);
        }
    }

    static class SimpleProducer {
        static final Object VALUE = new Object();

        int produceCalled = 0;

        @Produce(
                thread = EventThread.IMMEDIATE
        )
        public Object produceIt() {
            produceCalled += 1;
            return VALUE;
        }
    }

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
}
