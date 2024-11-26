/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.spring.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

/**
 * 
 *
 * @author Jeroen van Schagen
 * @since Jun 18, 2015
 */
class ConversionServiceBeanConverterTest {
    
    private ConversionService conversionService;
    
    private ConversionServiceBeanConverter beanConverter;
    
    @BeforeEach
    void setUp() {
        conversionService = new DefaultConversionService();
        beanConverter = new ConversionServiceBeanConverter(conversionService);
    }

    @Test
    void testCanConvert() {
        assertTrue(beanConverter.match(String.class, Long.class));
    }
    
    @Test
    void testConvert() {
        assertEquals(Long.valueOf(1), beanConverter.convert(null, "1", Long.class, null));
    }

}
