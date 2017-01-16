package com.hwangjr.rxbus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by trs on 16-10-20.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface Subscribe {
    /**
     * Tag
     *
     * @return
     */
    String[] value() default {DEFAULT};

    String DEFAULT = "__default__";
}
