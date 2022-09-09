package io.beanmapper.spring.web.mockmvc;

import io.beanmapper.BeanMapper;
import io.beanmapper.core.BeanPropertyMatch;
import io.beanmapper.core.converter.BeanConverter;

import org.springframework.data.domain.Persistable;
import org.springframework.data.repository.CrudRepository;

public class MockIdToEntityBeanConverter implements BeanConverter {

    private final Class<?> targetClass;

    private final CrudRepository<? extends Persistable<Long>, Long> repository;

    public MockIdToEntityBeanConverter(CrudRepository<? extends Persistable<Long>, Long> repository, Class<?> targetClass) {
        this.repository = repository;
        this.targetClass = targetClass;
    }

    @Override
    public Object convert(BeanMapper beanMapper, Object source, Class<?> targetClass, BeanPropertyMatch beanFieldMatch) {
        return repository.findById((Long)source).orElse(null);
    }

    @Override
    public boolean match(Class<?> sourceClass, Class<?> targetClass) {
        return sourceClass.equals(Long.class) && targetClass.equals(this.targetClass);
    }

}
