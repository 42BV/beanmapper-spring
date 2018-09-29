package io.beanmapper.spring.config;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BeanUtilsBasedConfigBuilderTest {

    @Test
    public void testCopy() {

        BeanUtilsBasedConfigBuilder builder = new BeanUtilsBasedConfigBuilder();
        Bar bar = builder.build().map(new Foo().setStrVal("mee"), Bar.class);

        assertEquals("mee", bar.getStrVal());

    }

    @SuppressWarnings({"SameParameterValue", "unused"})
    static class Foo {
        private String strVal;

        public String getStrVal() {
            return strVal;
        }

        Foo setStrVal(String strVal) {
            this.strVal = strVal;
            return this;
        }
    }

    @SuppressWarnings("unused")
    public static class Bar {
        private String strVal;

        String getStrVal() {
            return strVal;
        }

        public Bar setStrVal(String strVal) {
            this.strVal = strVal;
            return this;
        }

    }

}