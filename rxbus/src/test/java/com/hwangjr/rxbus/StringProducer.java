package com.hwangjr.rxbus;

import com.hwangjr.rxbus.annotation.Produce;
import com.hwangjr.rxbus.thread.EventThread;

public class StringProducer {
    public static final String VALUE = "Hello, Producer";

    @Produce(
            thread = EventThread.IMMEDIATE
    )
    public String produce() {
        return VALUE;
    }
}
