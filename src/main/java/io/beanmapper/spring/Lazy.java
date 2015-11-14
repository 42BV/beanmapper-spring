/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.spring;

/**
 * Wrapper on object to retrieve only when desired.
 *
 * @author Jeroen van Schagen
 * @since Nov 13, 2015
 */
public interface Lazy<T> {
    
    /**
     * Retrieve the entity instance.
     * @return the entity instance
     */
    T get();

}
