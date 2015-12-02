package com.hwangjr.rxbus.annotation;

import com.hwangjr.rxbus.Bus;
import com.hwangjr.rxbus.finder.AnnotatedFinder;
import com.hwangjr.rxbus.thread.EventThread;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as an event subscriber, as used by {@link AnnotatedFinder} and {@link Bus}.
 * <p/>
 * <p>The method's first (and only) parameter and tag defines the event type.
 * <p>If this annotation is applied to methods with zero parameters or more than one parameter, the object containing
 * the method will not be able to register for event delivery from the {@link Bus}. Bus fails fast by throwing
 * runtime exceptions in these cases.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Subscribe {
    Tag[] tags() default {};

    EventThread thread() default EventThread.MAIN_THREAD;
}