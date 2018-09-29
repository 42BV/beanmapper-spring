package io.beanmapper.spring.config;

import io.beanmapper.config.BeanMapperBuilder;
import io.beanmapper.config.Configuration;

public class BeanUtilsBasedConfigBuilder extends BeanMapperBuilder {

    public BeanUtilsBasedConfigBuilder() {
        super(BeanUtilsBasedBeanMatchStore::new);
    }

    public BeanUtilsBasedConfigBuilder(Configuration configuration) {
        super(configuration);
    }
}
