package com.baytsif.rxdynamicbus.app;

import android.app.Application;
import android.os.Build;
import android.os.StrictMode;

import timber.log.Timber;

/**
 * Application to init timber and strict mode etc in debug mode.
 */
public class AppApplication extends Application {

    /**
     * Init timber and strict mode in debug mode.
     */
    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
            StrictMode.ThreadPolicy.Builder threadPolicyBuilder = new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                threadPolicyBuilder.penaltyDeathOnNetwork();
            }
            StrictMode.setThreadPolicy(threadPolicyBuilder.build());
        }
    }
}
