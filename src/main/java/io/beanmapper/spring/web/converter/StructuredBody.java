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
public record StructuredBody(Object body, Set<String> propertyNames) {

}
