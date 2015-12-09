package com.hwangjr.rxbus;

/**
 * Instance of {@link Bus}.
 * Simply use {@link #get()} to get the instance of {@link Bus}
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
    public static synchronized Bus get() {
        if (sBus == null) {
            sBus = new Bus();
        }
        return sBus;
    }
}