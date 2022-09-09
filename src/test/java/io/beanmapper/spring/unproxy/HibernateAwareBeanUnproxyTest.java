package io.beanmapper.spring.unproxy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class HibernateAwareBeanUnproxyTest {

    @Test
    public void unproxyHibernateProxy() {
        HibernateProxyContainer shopHibernateProxy = new HibernateProxyContainer();
        Object object = shopHibernateProxy.new GeneratedProxy();
        HibernateAwareBeanUnproxy beanUnproxy = new HibernateAwareBeanUnproxy();
        Class unproxyClass = beanUnproxy.unproxy(object.getClass());
        assertEquals(Shop.class, unproxyClass);
    }

}
