package com.baytsif.rxdynamicbus;

import com.baytsif.rxdynamicbus.annotation.Produce;

public class LazyStringProducer {
  public String value = null;

  @Produce
  public String produce() {
    return value;
  }
}
