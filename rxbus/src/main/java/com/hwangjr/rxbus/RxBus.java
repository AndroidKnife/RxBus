package com.hwangjr.rxbus;

import com.hwangjr.rxbus.thread.ThreadEnforcer;

/**
 * Instance of {@link Bus}.
 * Simply use {@link #get()} to get the instance of {@link Bus}
 */
public class RxBus {

    /**
     * Instance of {@link Bus}
     */
    private volatile static Bus sBus;

    private RxBus() {
    }

    /**
     * Get the instance of {@link Bus}
     *
     * @return
     */
    public static Bus get() {
        if (sBus == null) {
            synchronized (RxBus.class) {
                if (sBus F == null)
                sBus = new Bus(ThreadEnforcer.ANY);
            }
        }
        return sBus;
    }
}
