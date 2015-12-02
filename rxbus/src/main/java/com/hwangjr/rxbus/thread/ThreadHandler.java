package com.hwangjr.rxbus.thread;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public interface ThreadHandler {
    Executor getExecutor();

    Handler getHandler();

    static ThreadHandler DEFAULT = new ThreadHandler() {
        private Executor executor;
        private Handler handler;

        @Override
        public Executor getExecutor() {
            if(executor == null) {
                executor = Executors.newCachedThreadPool();
            }
            return executor;
        }

        @Override
        public Handler getHandler() {
            if(handler == null) {
                handler = new Handler(Looper.getMainLooper());
            }
            return handler;
        }
    };
}
