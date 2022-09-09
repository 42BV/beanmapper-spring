/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.spring.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.beanmapper.spring.AbstractSpringTest;
import io.beanmapper.spring.model.Artist;
import io.beanmapper.spring.model.Asset;
import io.beanmapper.spring.model.Person;
import io.beanmapper.spring.model.PersonRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public class IdToEntityBeanConverterTest extends AbstractSpringTest {

    @Autowired
    private ApplicationContext applicationContext;
    
    @Autowired
    private PersonRepository personRepository;

    private IdToEntityBeanConverter beanConverter;
    
    @BeforeEach
    public void setUp() {
        beanConverter = new IdToEntityBeanConverter(applicationContext);
    }

    @Test
    public void testCanConvert() {
        assertTrue(beanConverter.match(Long.class, Person.class));
    }
    
    @Test
    public void testCannotConvertInvalidSource() {assertFalse(beanConverter.match(String.class, Person.class));
    }
    
    @Test
    public void testCannotConvertInvalidTarget() {
        assertFalse(beanConverter.match(Long.class, Asset.class));
    }
    
    @Test
    public void testConvert() {
        Person person = new Person();
        person.setName("Henk");
        personRepository.save(person);

        assertEquals(person.getId(), ((Person) beanConverter.convert(null, person.getId(), Person.class, null)).getId());
    }
    
    @Test
    public void testSameClassNoMatch() {
        Person person = new Person();
        person.setName("Henk");
        assertFalse(beanConverter.match(person.getClass(), Person.class));
    }
    
    @Test
    public void noRepoForArtist() {
        assertFalse(beanConverter.match(Long.class, Artist.class));
    }
    
    @Test
    public void testConvertNull() {
        assertNull(beanConverter.convert(null, null, Person.class, null));
    }

}
