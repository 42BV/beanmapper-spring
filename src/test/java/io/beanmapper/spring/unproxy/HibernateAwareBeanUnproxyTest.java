package io.beanmapper.spring.unproxy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.beanmapper.spring.model.Spoon;
import io.beanmapper.spring.model.SpoonDrawerForm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HibernateAwareBeanUnproxyTest {

    private HibernateAwareBeanUnproxy beanUnproxy;

    @BeforeEach
    void setUp() {
        this.beanUnproxy = new HibernateAwareBeanUnproxy();
    }

    @Test
    void unproxyHibernateProxy() {
        HibernateProxyContainer shopHibernateProxy = new HibernateProxyContainer();
        Object object = shopHibernateProxy.new GeneratedProxy();
        Class<?> unproxyClass = beanUnproxy.unproxy(object.getClass());
        assertEquals(Shop.class, unproxyClass);
    }

    @Test
    void testUnproxy_ShouldNotConsiderStaticNestedClassAHibernateProxy() {
        Object object = new SpoonDrawerForm.SpoonForm();
        Class<?> unproxyClass = beanUnproxy.unproxy(object.getClass());
        assertEquals(SpoonDrawerForm.SpoonForm.class, unproxyClass);
    }

    @Test
    void testUnproxy_ShouldReturnObjectUnchangedWhenNotAHibernateProxy() {
        Object object = new Spoon(true);
        Class<?> unproxyClass = beanUnproxy.unproxy(object.getClass());
        assertEquals(Spoon.class, unproxyClass);
    }

}
