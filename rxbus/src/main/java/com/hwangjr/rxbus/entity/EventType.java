package com.hwangjr.rxbus.entity;

import androidx.annotation.NonNull;

public class EventType {

    /**
     * Event Tag
     */
    private final String tag;

    /**
     * Event Clazz
     */
    private final Class<?> clazz;
    /**
     * Object hash code.
     */
    private final int hashCode;


    public EventType(String tag, Class<?> clazz) {
        if (tag == null) {
            throw new NullPointerException("EventType Tag cannot be null.");
        }
        if (clazz == null) {
            throw new NullPointerException("EventType Clazz cannot be null.");
        }

        this.tag = tag;
        this.clazz = getRealClass(clazz);

        // Compute hash code eagerly since we know it will be used frequently and we cannot estimate the runtime of the
        // target's hashCode call.
        final int prime = 31;
        hashCode = (prime + tag.hashCode()) * prime + clazz.hashCode();
    }

    @NonNull
    @Override
    public String toString() {
        return "[EventType " + tag + " && " + clazz + "]";
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final EventType other = (EventType) obj;

        return tag.equals(other.tag) && clazz == other.clazz;
    }

    /**
     * getRealClass, compat to kotlin base type
     *
     * @param cls
     * @return
     */
    private Class<?> getRealClass(Class<?> cls) {
        String clsName = cls.getName();
        if (int.class.getName().equals(clsName)) {
            cls = Integer.class;
        } else if (double.class.getName().equals(clsName)) {
            cls = Double.class;
        } else if (float.class.getName().equals(clsName)) {
            cls = Float.class;
        } else if (long.class.getName().equals(clsName)) {
            cls = Long.class;
        } else if (byte.class.getName().equals(clsName)) {
            cls = Byte.class;
        } else if (short.class.getName().equals(clsName)) {
            cls = Short.class;
        } else if (boolean.class.getName().equals(clsName)) {
            cls = Boolean.class;
        } else if (char.class.getName().equals(clsName)) {
            cls = Character.class;
        }
        return cls;
    }
}
