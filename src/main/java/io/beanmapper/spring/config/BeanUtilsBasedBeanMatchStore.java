package io.beanmapper.spring.config;

import io.beanmapper.config.CollectionHandlerStore;
import io.beanmapper.core.BeanMatchStore;
import io.beanmapper.core.inspector.PropertyAccessors;
import io.beanmapper.core.unproxy.BeanUnproxy;

public class BeanUtilsBasedBeanMatchStore extends BeanMatchStore {

    @SuppressWarnings("WeakerAccess")
    public BeanUtilsBasedBeanMatchStore(CollectionHandlerStore collectionHandlerStore, BeanUnproxy beanUnproxy) {
        super(collectionHandlerStore, beanUnproxy);
    }

    @Override
    protected PropertyAccessors createPropertyAccessors() {
        return new BeanUtilsBasedPropertyAccessors();
    }
}
