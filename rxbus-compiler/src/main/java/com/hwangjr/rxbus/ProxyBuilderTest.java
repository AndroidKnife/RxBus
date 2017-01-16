package com.hwangjr.rxbus;

import com.squareup.javapoet.ClassName;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertTrue;

/**
 * Created by trs on 17-1-5.
 */
public class ProxyBuilderTest {
    @Test
    public void testCreateClass() {
        File file = new File("../exampleTest");
        File DemoTestClass = new File(file.getAbsolutePath() + "/com/hwangjr/rxbus/demo/MainActivity$$Proxy.java");
        ProxyBuilder builder = new ProxyBuilder(ClassName.get("com.hwangjr.rxbus.demo", "MainActivity"));
        builder.build(file);

        assertTrue(file.exists());
        assertTrue(DemoTestClass.exists());
    }
}
