package io.beanmapper.spring.unproxy;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("SpringJavaAutowiringInspection")
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
