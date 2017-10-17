package com.baytsif.rxdynamicbus;

import com.baytsif.rxdynamicbus.annotation.Subscribe;
import com.baytsif.rxdynamicbus.thread.EventThread;
import com.baytsif.rxdynamicbus.thread.ThreadEnforcer;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.fail;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test that Bus finds the correct subscribers.
 * <p/>
 * This test must be outside the c.g.c.rxbus package to test correctly.
 */
@RunWith(Enclosed.class)
@SuppressWarnings("UnusedDeclaration")
public class AnnotatedSubscriberFinderTest {

    private static final Object EVENT = new Object();

    @Ignore // Tests are in extending classes.
    public abstract static class AbstractEventBusTest<S> {
        abstract S createSubscriber();

        private S subscriber;

        S getSubscriber() {
            return subscriber;
        }

        @Before
        public void setUp() throws Exception {
            subscriber = createSubscriber();
            Bus bus = new Bus(ThreadEnforcer.ANY);
            bus.register(subscriber);
            bus.post(EVENT);
        }

        @After
        public void tearDown() throws Exception {
            subscriber = null;
        }
    }

    /*
     * We break the tests up based on whether they are annotated or abstract in the superclass.
     */
    public static class BaseSubscriberFinderTest
            extends AbstractEventBusTest<BaseSubscriberFinderTest.Subscriber> {
        static class Subscriber {
            final List<Object> nonSubscriberEvents = new ArrayList<Object>();
            final List<Object> subscriberEvents = new ArrayList<Object>();

            public void notASubscriber(Object o) {
                nonSubscriberEvents.add(o);
            }

            @Subscribe(
                    thread = EventThread.IMMEDIATE
            )
            public void subscriber(Object o) {
                subscriberEvents.add(o);
            }
        }

        @Test
        public void nonSubscriber() {
            assertThat(getSubscriber().nonSubscriberEvents).isEmpty();
        }

        @Test
        public void subscriber() {
            assertThat(getSubscriber().subscriberEvents).containsExactly(EVENT);
        }

        @Override
        Subscriber createSubscriber() {
            return new Subscriber();
        }
    }

    public static class AbstractNotAnnotatedInSuperclassTest
            extends AbstractEventBusTest<AbstractNotAnnotatedInSuperclassTest.SubClass> {
        abstract static class SuperClass {
            public abstract void overriddenInSubclassNowhereAnnotated(Object o);

            public abstract void overriddenAndAnnotatedInSubclass(Object o);
        }

        static class SubClass extends SuperClass {
            final List<Object> overriddenInSubclassNowhereAnnotatedEvents = new ArrayList<Object>();
            final List<Object> overriddenAndAnnotatedInSubclassEvents = new ArrayList<Object>();

            @Override
            public void overriddenInSubclassNowhereAnnotated(Object o) {
                overriddenInSubclassNowhereAnnotatedEvents.add(o);
            }

            @Subscribe(
                    thread = EventThread.IMMEDIATE
            )
            @Override
            public void overriddenAndAnnotatedInSubclass(Object o) {
                overriddenAndAnnotatedInSubclassEvents.add(o);
            }
        }

        @Test
        public void overriddenAndAnnotatedInSubclass() {
            assertThat(getSubscriber().overriddenAndAnnotatedInSubclassEvents).containsExactly(EVENT);
        }

        @Test
        public void overriddenInSubclassNowhereAnnotated() {
            assertThat(getSubscriber().overriddenInSubclassNowhereAnnotatedEvents).isEmpty();
        }

        @Override
        SubClass createSubscriber() {
            return new SubClass();
        }
    }

    public static class NeitherAbstractNorAnnotatedInSuperclassTest
            extends AbstractEventBusTest<NeitherAbstractNorAnnotatedInSuperclassTest.SubClass> {
        static class SuperClass {
            final List<Object> neitherOverriddenNorAnnotatedEvents = new ArrayList<Object>();
            final List<Object> overriddenInSubclassNowhereAnnotatedEvents = new ArrayList<Object>();
            final List<Object> overriddenAndAnnotatedInSubclassEvents = new ArrayList<Object>();

            public void neitherOverriddenNorAnnotated(Object o) {
                neitherOverriddenNorAnnotatedEvents.add(o);
            }

            public void overriddenInSubclassNowhereAnnotated(Object o) {
                overriddenInSubclassNowhereAnnotatedEvents.add(o);
            }

            public void overriddenAndAnnotatedInSubclass(Object o) {
                overriddenAndAnnotatedInSubclassEvents.add(o);
            }
        }

        static class SubClass extends SuperClass {
            @Override
            public void overriddenInSubclassNowhereAnnotated(Object o) {
                super.overriddenInSubclassNowhereAnnotated(o);
            }

            @Subscribe(
                    thread = EventThread.IMMEDIATE
            )
            @Override
            public void overriddenAndAnnotatedInSubclass(Object o) {
                super.overriddenAndAnnotatedInSubclass(o);
            }
        }

        @Test
        public void neitherOverriddenNorAnnotated() {
            assertThat(getSubscriber().neitherOverriddenNorAnnotatedEvents).isEmpty();
        }

        @Test
        public void overriddenInSubclassNowhereAnnotated() {
            assertThat(getSubscriber().overriddenInSubclassNowhereAnnotatedEvents).isEmpty();
        }

        @Test
        public void overriddenAndAnnotatedInSubclass() {
            assertThat(getSubscriber().overriddenAndAnnotatedInSubclassEvents).containsExactly(EVENT);
        }

        @Override
        SubClass createSubscriber() {
            return new SubClass();
        }
    }

    public static class FailsOnInterfaceSubscription {

        static class InterfaceSubscriber {
            @Subscribe(
                    thread = EventThread.IMMEDIATE
            )
            public void whatever(Serializable thingy) {
                // Do nothing.
            }
        }

        @Test
        public void subscribingToInterfacesFails() {
            try {
                new Bus(ThreadEnforcer.ANY).register(new InterfaceSubscriber());
                fail("Annotation finder allowed subscription to illegal interface type.");
            } catch (IllegalArgumentException expected) {
                // Do nothing.
            }
        }
    }

}
