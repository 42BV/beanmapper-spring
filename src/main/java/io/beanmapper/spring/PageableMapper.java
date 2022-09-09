/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.spring;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.beanmapper.BeanMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * Mapping utilities for pages.
 *
 * @author Jeroen van Schagen
 * @since Nov 13, 2015
 */
public class PageableMapper {

    /**
     * Private constructor to hide the implicit public constructor of
     * utility-class.
     */
    private PageableMapper() {}
    
    /**
     * Converts a page into the desired target type.
     * 
     * @param source the source page
     * @param targetClass the target type
     * @param beanMapper the bean mapper used to perform mappings
     * @return the same page, but with result type
     */
    public static <S, T> Page<T> map(Page<S> source, Class<T> targetClass, BeanMapper beanMapper) {
        List<T> transformed;
        if (source.hasContent()) {
            List<S> content = new ArrayList<>(source.getContent());
            transformed = beanMapper.map(content, targetClass);
        } else {
            transformed = Collections.emptyList();
        }
        Pageable pageable = PageRequest.of(source.getNumber(), source.getSize(), source.getSort());
        return new PageImpl<>(transformed, pageable, source.getTotalElements());
    }

}
