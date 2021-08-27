package com.hwangjr.rxbus;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import com.hwangjr.rxbus.thread.ThreadEnforcer;

import org.junit.Test;

public class ThreadEnforcerTest {

    @Test
    public void enforerCalledForRegister() {
        RecordingThreadEnforcer enforcer = new RecordingThreadEnforcer();
        Bus bus = new Bus(enforcer);

        assertFalse(enforcer.called);
        bus.register(this);
        assertTrue(enforcer.called);
    }

    @Test
    public void enforcerCalledForPost() {
        RecordingThreadEnforcer enforcer = new RecordingThreadEnforcer();
        Bus bus = new Bus(enforcer);

        assertFalse(enforcer.called);
        bus.post(this);
        assertTrue(enforcer.called);
    }

    @Test
    public void enforcerCalledForUnregister() {
        RecordingThreadEnforcer enforcer = new RecordingThreadEnforcer();
        Bus bus = new Bus(enforcer);

        assertFalse(enforcer.called);
        bus.unregister(this);
        assertTrue(enforcer.called);
    }

    private static class RecordingThreadEnforcer implements ThreadEnforcer {
        boolean called = false;

        @Override
        public void enforce(Bus bus) {
            called = true;
        }
    }

}
