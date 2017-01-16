package com.hwangjr.rxbus.demo;

/**
 * Created by trs on 16-11-22.
 */

public class Entity {
    String param1, param2;

    Entity(String param1, String param2) {
        this.param1 = param1;
        this.param2 = param2;
    }

    @Override
    public String toString() {
        return "Entity{" +
                "param1='" + param1 + '\'' +
                ", param2='" + param2 + '\'' +
                '}';
    }
}
