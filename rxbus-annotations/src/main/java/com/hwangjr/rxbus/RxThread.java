package com.hwangjr.rxbus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by trs on 17-1-4.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface RxThread {
    ThreadType value() default ThreadType.MainThread;
}
