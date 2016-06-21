/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.spring.converter;

import io.beanmapper.spring.AbstractSpringTest;
import io.beanmapper.spring.model.Artist;
import io.beanmapper.spring.model.Asset;
import io.beanmapper.spring.model.Person;
import io.beanmapper.spring.model.PersonRepository;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public class IdToEntityBeanConverterTest extends AbstractSpringTest {

    @Autowired
    private ApplicationContext applicationContext;
    
    @Autowired
    private PersonRepository personRepository;

    private IdToEntityBeanConverter beanConverter;
    
    @Before
    public void setUp() {
        beanConverter = new IdToEntityBeanConverter(applicationContext);
    }

    @Test
    public void testCanConvert() {
        Assert.assertTrue(beanConverter.match(Long.class, Person.class));
    }
    
    @Test
    public void testCannotConvertInvalidSource() {
        Assert.assertFalse(beanConverter.match(String.class, Person.class));
    }
    
    @Test
    public void testCannotConvertInvalidTarget() {
        Assert.assertFalse(beanConverter.match(Long.class, Asset.class));
    }
    
    @Test
    public void testConvert() {
        Person person = new Person();
        person.setName("Henk");
        personRepository.save(person);

        Assert.assertEquals(person.getId(), ((Person) beanConverter.convert(person.getId(), Person.class, null)).getId());
    }
    
    @Test
    public void testSameClassNoMatch() {
        Person person = new Person();
        person.setName("Henk");
        Assert.assertFalse(beanConverter.match(person.getClass(), Person.class));
    }
    
    @Test
    public void noRepoForArtist() {
        Assert.assertFalse(beanConverter.match(Long.class, Artist.class));
    }
    
    @Test
    public void testConvertNull() {
        Assert.assertNull(beanConverter.convert(null, Person.class, null));
    }

}
