package io.beanmapper.spring.unproxy;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

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
