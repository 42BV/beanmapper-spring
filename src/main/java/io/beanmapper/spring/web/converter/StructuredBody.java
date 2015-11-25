/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.spring.web.converter;

import java.util.Set;

/**
 * Wrapper for a read request body.
 *
 * @author Jeroen van Schagen
 * @since Nov 25, 2015
 */
public class StructuredBody {
    
    private final Object body;
    
    private final Set<String> propertyNames;

    public StructuredBody(Object body, Set<String> propertyNames) {
        this.body = body;
        this.propertyNames = propertyNames;
    }
    
    public Object getBody() {
        return body;
    }
    
    public Set<String> getPropertyNames() {
        return propertyNames;
    }

}
