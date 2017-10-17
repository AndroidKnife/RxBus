package com.baytsif.rxdynamicbus;

import com.baytsif.rxdynamicbus.thread.ThreadEnforcer;

/**
 * Instance of {@link Bus}.
 * Simply use {@link #getTest()} to getTest the instance of {@link Bus}
 */
public class RxBus {

    /**
     * Instance of {@link Bus}
     */
    private static Bus sBus;

    /**
     * Get the instance of {@link Bus}
     *
     * @return
     */
    public static synchronized Bus getTest() {
        if (sBus == null) {
            sBus = new Bus(ThreadEnforcer.ANY);
        }
        return sBus;
    }
}