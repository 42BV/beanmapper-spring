/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.spring.util;

import java.util.HashSet;
import java.util.Set;

/**
 * Utilities for working with JSON.
 *
 * @author Jeroen van Schagen
 * @since Nov 13, 2015
 */
public class JsonUtil {

    /**
     * Retrieve the property names mentioned in a JSON content.
     *
     * @param body the JSON content
     * @return set of the property names
     */
    public static Set<String> getPropertyNamesFromBody(Object body) {
        Set<String> propertyNames = new HashSet<String>();
        return propertyNames;
    }

}
