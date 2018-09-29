package io.beanmapper.spring.config;

import io.beanmapper.core.inspector.PropertyAccessors;
import org.springframework.beans.BeanUtils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

public class BeanUtilsBasedPropertyAccessors extends PropertyAccessors {
    @Override
    protected PropertyDescriptor[] extractPropertyDescriptors(Class<?> clazz) throws IntrospectionException {
        return BeanUtils.getPropertyDescriptors(clazz);
    }
}
