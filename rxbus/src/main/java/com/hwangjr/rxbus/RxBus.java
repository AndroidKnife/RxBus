package com.hwangjr.rxbus;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by trs on 16-10-20.
 */
public final class RxBus {

    private static final Map<Class, Constructor<? extends BusProxy>> CONSTRUCTOR_CACHE = new HashMap<>();
    private static final Map<Class, BusProxy> PROXY_CACHE = new HashMap<>();
    private static final Map<String, Set<BusProxy.SubjectFucker>> SUBJECTS = new ConcurrentHashMap<>();

    public static void register(Object entity) {
        BusProxy proxy = createProxy(entity);
        proxy.register(entity);
        proxy.mount(SUBJECTS);
    }

    public static void unregister(Object entity) {
        Class entityClass = entity.getClass();
        BusProxy proxy = getProxy4Class(entityClass);
        if (proxy != null)
            proxy.unregister(entity);
    }

    public static void post(Object value) {
        post(Subscribe.DEFAULT, value);
    }

    public static void post(String tag, Object value) {
        Set<BusProxy.SubjectFucker> subjects = SUBJECTS.get(tag);
        if (subjects != null) {
            for (BusProxy.SubjectFucker s : subjects) {
                if (!s.subscription.isUnsubscribed())
                    s.subject.onNext(value);
                else
                    subjects.remove(s);
            }
        }
    }

    private static BusProxy createProxy(Object entity) {
        Class entityClass = entity.getClass();
        BusProxy proxy = getProxy4Class(entityClass);
        if (proxy == null) {
            Constructor<? extends BusProxy> constructor = getConstructor4Class(entityClass);
            try {
                proxy = constructor.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return proxy;
    }

    private static BusProxy getProxy4Class(Class c) {
        return PROXY_CACHE.get(c);
    }

    private static Constructor<? extends BusProxy> getConstructor4Class(Class c) {
        Constructor<? extends BusProxy> constructor = CONSTRUCTOR_CACHE.get(c);
        if (constructor == null) {
            String targetName = c.getName();
            try {
                Class targetProxy = Class.forName(targetName + "$$Proxy");
                constructor = targetProxy.getConstructor();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            CONSTRUCTOR_CACHE.put(c, constructor);
        }
        return constructor;
    }
}
