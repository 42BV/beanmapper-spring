package io.beanmapper.spring.config;

import io.beanmapper.core.inspector.PropertyAccessor;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class BeanUtilsBasedPropertyAccessorsTest {

    @Test
    public void testGetAll() {
        List<PropertyAccessor> props = new BeanUtilsBasedPropertyAccessors().getAll(Foo.class);
        //noinspection OptionalGetWithoutIsPresent - failure is expected if none found
        PropertyAccessor strValProperty = props.stream().filter(prop -> prop.getName().equals("strVal")).findFirst().get();
        assertEquals("setStrVal", strValProperty.getWriteMethod().getName());
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