package io.beanmapper.spring.unproxy;

import java.util.Arrays;

import io.beanmapper.core.unproxy.BeanUnproxy;

import org.hibernate.proxy.HibernateProxy;

public class HibernateAwareBeanUnproxy implements BeanUnproxy {

    /**
     * {@inheritDoc}
     */
    public Class<?> unproxy(Class<?> beanClass) {
        if (beanClass.getName().contains("$")
                && Arrays.asList(beanClass.getInterfaces()).contains(HibernateProxy.class)) {
            return beanClass.getSuperclass();
        }
        return beanClass;
    }

}
