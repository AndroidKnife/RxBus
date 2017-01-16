package com.hwangjr.rxbus.demo;

import com.hwangjr.rxbus.BusProxy;

import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by trs on 17-1-4.
 */
public final class MainActivity$$Proxy_test extends BusProxy<MainActivity> {
    public MainActivity$$Proxy_test() {
        createMethod("test1", AndroidSchedulers.mainThread()
                , new ProxyAction<MainActivity, Integer>() {
                    @Override
                    public void toDo(MainActivity mainActivity, Integer v) {
                        mainActivity.test(v);
                    }
                });
    }
}
