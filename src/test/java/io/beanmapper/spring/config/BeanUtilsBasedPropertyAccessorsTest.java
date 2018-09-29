package io.beanmapper.spring.config;

import io.beanmapper.core.inspector.PropertyAccessor;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class BeanUtilsBasedPropertyAccessorsTest {

    @Test
    public void testGetAll() {
        List<PropertyAccessor> props = new BeanUtilsBasedPropertyAccessors().getAll(Foo.class);
        assertEquals(1, props.size());
        assertEquals("setStrVal", props.get(0).getWriteMethod().getName());
    }

    static class Foo {
        private String strVal;

        public String getStrVal() {
            return strVal;
        }

        public Foo setStrVal(String strVal) {
            this.strVal = strVal;
            return this;
        }
    }

}