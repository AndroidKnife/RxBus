package com.baytsif.rxdynamicbus;

import com.baytsif.rxdynamicbus.thread.ThreadEnforcer;

import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class ThreadEnforcerTest {

  private static class RecordingThreadEnforcer implements ThreadEnforcer {
    boolean called = false;

    @Override public void enforce(Bus bus) {
      called = true;
    }
  }

  @Test public void enforerCalledForRegister() {
    RecordingThreadEnforcer enforcer = new RecordingThreadEnforcer();
    Bus bus = new Bus(enforcer);

    assertFalse(enforcer.called);
    bus.register(this);
    assertTrue(enforcer.called);
  }

  @Test public void enforcerCalledForPost() {
    RecordingThreadEnforcer enforcer = new RecordingThreadEnforcer();
    Bus bus = new Bus(enforcer);

    assertFalse(enforcer.called);
    bus.post(this);
    assertTrue(enforcer.called);
  }

  @Test public void enforcerCalledForUnregister() {
    RecordingThreadEnforcer enforcer = new RecordingThreadEnforcer();
    Bus bus = new Bus(enforcer);

    assertFalse(enforcer.called);
    bus.unregister(this);
    assertTrue(enforcer.called);
  }

}
