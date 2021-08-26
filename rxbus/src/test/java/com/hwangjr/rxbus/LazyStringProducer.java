package com.hwangjr.rxbus;

import com.hwangjr.rxbus.annotation.Produce;

public class LazyStringProducer {
    public String value = null;

    @Produce
    public String produce() {
        return value;
    }
}
