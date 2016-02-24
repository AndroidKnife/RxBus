package com.hwangjr.rxbus.app;

import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.thread.EventThread;

/**
 * Created by hwangjr on 2/23/16.
 */
public interface Cat {
    void caught(Mouse mouse);
}