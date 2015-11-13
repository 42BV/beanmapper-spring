/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.spring;

import io.beanmapper.BeanMapper;
import io.beanmapper.core.BeanFieldMatch;
import io.beanmapper.core.converter.BeanConverter;
import org.springframework.core.convert.ConversionService;

/**
 * Spring based bean converter adapter. Makes use of Spring's
 * build-in conversion service.
 *
 * @author Jeroen van Schagen
 * @since Jun 18, 2015
 */
public class ConversionServiceBeanConverter implements BeanConverter {
    
    /**
     * Performs type conversions in Spring.
     */
    private final ConversionService conversionService;

    /**
     * Construct a new {@link ConversionServiceBeanConverter}.
     * @param conversionService the converter delegate
     */
    public ConversionServiceBeanConverter(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object convert(Object source, Class<?> targetClass, BeanFieldMatch beanFieldMatch) {
        return conversionService.convert(source, targetClass);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean match(Class<?> sourceClass, Class<?> targetClass) {
        return conversionService.canConvert(sourceClass, targetClass);
    }

    @Override
    public void setBeanMapper(BeanMapper beanMapper) {
    }

}
