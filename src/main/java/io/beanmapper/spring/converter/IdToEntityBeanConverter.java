package io.beanmapper.spring.converter;

import java.io.Serializable;

import jakarta.persistence.EntityNotFoundException;

import io.beanmapper.BeanMapper;
import io.beanmapper.core.BeanPropertyMatch;
import io.beanmapper.core.converter.BeanConverter;

import org.springframework.context.ApplicationContext;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.support.Repositories;

public class IdToEntityBeanConverter implements BeanConverter {
    
    private final Repositories repositories;
    
    public IdToEntityBeanConverter(ApplicationContext applicationContext) {
        this.repositories = new Repositories(applicationContext);
    }
    
    @Override
    public <S, T> T convert(BeanMapper beanMapper, S source, Class<T> targetClass, BeanPropertyMatch beanFieldMatch) {
        if (source == null) {
            return null;
        }

        CrudRepository repository = (CrudRepository) repositories.getRepositoryFor(targetClass).orElseThrow(() -> new EntityNotFoundException(
                "No repository found for " + targetClass.getName()
        ));

        return targetClass.cast(repository.findById(source).orElse(null));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean match(Class<?> sourceClass, Class<?> targetClass) {
        if (targetClass.isAssignableFrom(sourceClass)) {
            return false;
        }
        if (!repositories.hasRepositoryFor(targetClass)) {
            return false;
        }
        // No need for a null check. Repository#getEntityInformation fails if null.
        return sourceClass.equals(repositories.getEntityInformationFor(targetClass).getIdType());
    }

}
