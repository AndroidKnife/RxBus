package com.baytsif.rxdynamicbus;

import com.baytsif.rxdynamicbus.annotation.Produce;
import com.baytsif.rxdynamicbus.thread.EventThread;

public class StringProducer {
    public static final String VALUE = "Hello, Producer";

    @Produce(
            thread = EventThread.IMMEDIATE
    )
    public String produce() {
        return VALUE;
    }
}
