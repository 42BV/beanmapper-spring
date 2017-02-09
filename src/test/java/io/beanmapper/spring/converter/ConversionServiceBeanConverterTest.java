/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.spring.converter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

/**
 * 
 *
 * @author Jeroen van Schagen
 * @since Jun 18, 2015
 */
public class ConversionServiceBeanConverterTest {
    
    private ConversionService conversionService;
    
    private ConversionServiceBeanConverter beanConverter;
    
    @Before
    public void setUp() {
        conversionService = new DefaultConversionService();
        beanConverter = new ConversionServiceBeanConverter(conversionService);
    }

    @Test
    public void testCanConvert() {
        Assert.assertTrue(beanConverter.match(String.class, Long.class));
    }
    
    @Test
    public void testConvert() {
        Assert.assertEquals(Long.valueOf(1), beanConverter.convert(null, "1", Long.class, null));
    }

}
