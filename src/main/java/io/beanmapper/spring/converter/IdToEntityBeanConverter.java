package io.beanmapper.spring.converter;

import io.beanmapper.core.BeanFieldMatch;
import io.beanmapper.core.converter.BeanConverter;

import java.io.Serializable;

import org.springframework.context.ApplicationContext;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.support.Repositories;

public class IdToEntityBeanConverter implements BeanConverter {
    
    private final Repositories repositories;
    
    public IdToEntityBeanConverter(ApplicationContext applicationContext) {
        this.repositories = new Repositories(applicationContext);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Object convert(Object source, Class<?> targetClass, BeanFieldMatch beanFieldMatch) {
        if (source == null) {
            return null;
        }

        CrudRepository repository = (CrudRepository) repositories.getRepositoryFor(targetClass);
        return repository.findOne((Serializable) source);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean match(Class<?> sourceClass, Class<?> targetClass) {
        boolean match = false;
        EntityInformation<Object, Serializable> information = repositories.getEntityInformationFor(targetClass);
        if (information != null) {
            match = sourceClass.equals(information.getIdType());
        }
        return match;
    }

}
