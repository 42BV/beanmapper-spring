package io.beanmapper.spring.web.mockmvc;

import io.beanmapper.BeanMapper;
import io.beanmapper.core.BeanFieldMatch;
import io.beanmapper.core.converter.BeanConverter;

import org.springframework.data.domain.Persistable;
import org.springframework.data.repository.CrudRepository;

public class MockIdToEntityBeanConverter implements BeanConverter {

    private Class targetClass;

    private CrudRepository<? extends Persistable, Long> repository;

    public MockIdToEntityBeanConverter(CrudRepository<? extends Persistable, Long> repository, Class targetClass) {
        this.repository = repository;
        this.targetClass = targetClass;
    }

    @Override
    public Object convert(BeanMapper beanMapper, Object source, Class<?> targetClass, BeanFieldMatch beanFieldMatch) {
        return repository.findOne((Long)source);
    }

    @Override
    public boolean match(Class<?> sourceClass, Class<?> targetClass) {
        return sourceClass.equals(Long.class) && targetClass.equals(this.targetClass);
    }

}
