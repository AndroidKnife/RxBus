package com.baytsif.rxdynamicbus;

import com.baytsif.rxdynamicbus.annotation.Subscribe;
import com.baytsif.rxdynamicbus.thread.EventThread;
import com.baytsif.rxdynamicbus.thread.ThreadEnforcer;

import org.junit.Test;

import static junit.framework.Assert.assertTrue;

/**
 * Stress test of {@link Bus} against inner classes. The anon inner class tests
 * were broken when we switched to weak references.
 */
public class EventBusInnerClassStressTest {
    public static final int REPS = 1000000;
    boolean called;

    class Sub {
        @Subscribe(
                thread = EventThread.IMMEDIATE
        )
        public void in(Object o) {
            called = true;
        }
    }

    Sub sub = new Sub();

    @Test
    public void eventBusOkayWithNonStaticInnerClass() {
        Bus eb = new Bus(ThreadEnforcer.ANY);
        eb.register(sub);
        int i = 0;
        while (i < REPS) {
            called = false;
            i++;
            eb.post(nextEvent(i));
            assertTrue("Failed at " + i, called);
        }
    }

    @Test
    public void eventBusFailWithAnonInnerClass() {
        Bus eb = new Bus(ThreadEnforcer.ANY);
        eb.register(new Object() {
            @Subscribe(
                    thread = EventThread.IMMEDIATE
            )
            public void in(String o) {
                called = true;
            }
        });
        int i = 0;
        while (i < REPS) {
            called = false;
            i++;
            eb.post(nextEvent(i));
            assertTrue("Failed at " + i, called);
        }
    }

    @Test
    public void eventBusNpeWithAnonInnerClassWaitingForObject() {
        Bus eb = new Bus(ThreadEnforcer.ANY);
        eb.register(new Object() {
            @Subscribe(
                    thread = EventThread.IMMEDIATE
            )
            public void in(Object o) {
                called = true;
            }
        });
        int i = 0;
        while (i < REPS) {
            called = false;
            i++;
            eb.post(nextEvent(i));
            assertTrue("Failed at " + i, called);
        }
    }

    private static String nextEvent(int i) {
        return "" + i;
    }
}
