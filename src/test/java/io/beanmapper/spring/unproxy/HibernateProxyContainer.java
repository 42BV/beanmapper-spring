package io.beanmapper.spring.unproxy;

import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;

public class HibernateProxyContainer {

    public class GeneratedProxy extends Shop implements HibernateProxy {

        @Override
        public Object writeReplace() {
            return null;
        }

        @Override
        public LazyInitializer getHibernateLazyInitializer() {
            return null;
        }
    }
}
