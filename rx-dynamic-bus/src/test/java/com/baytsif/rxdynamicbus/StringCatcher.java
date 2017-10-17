package com.baytsif.rxdynamicbus;

import com.baytsif.rxdynamicbus.annotation.Subscribe;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple SubscriberEvent mock that records Strings.
 * <p/>
 * For testing fun, also includes a landmine method that Bus tests are
 * required <em>not</em> to call ({@link #methodWithoutAnnotation(String)}).
 */
public class StringCatcher {
    private List<String> events = new ArrayList<String>();

    @Subscribe
    public void hereHaveAString(String string) {
        events.add(string);
    }

    public void methodWithoutAnnotation(String string) {
        Assert.fail("Event bus must not call methods without @Subscribe!");
    }

    public List<String> getEvents() {
        return events;
    }
}
