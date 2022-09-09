/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.spring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;

import io.beanmapper.BeanMapper;
import io.beanmapper.config.BeanMapperBuilder;
import io.beanmapper.spring.model.Person;
import io.beanmapper.spring.model.PersonForm;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageableMapperTest {
    
    private final Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));

    private BeanMapper beanMapper;

    {
        beanMapper = new BeanMapperBuilder()
                .addPackagePrefix(ApplicationConfig.class)
                .build();
    }

    @Test
    public void testMap() {
        PersonForm form = new PersonForm();
        form.name = "Henk";
        
        Page<PersonForm> source = new PageImpl<PersonForm>(Arrays.asList(form), pageable, 1);
        Page<Person> target = PageableMapper.map(source, Person.class, beanMapper);
        
        assertNotNull(target);
        assertEquals(pageable.getPageNumber(), target.getNumber());
        assertEquals(pageable.getPageSize(), target.getSize());
        assertEquals(pageable.getSort(), target.getSort());
        assertEquals(1, target.getTotalElements());
        
        Person person = target.getContent().get(0);
        assertEquals(Person.class, person.getClass());
        assertEquals("Henk", person.getName());
    }
    
    @Test
    public void testMapEmpty() {
        Page<PersonForm> source = new PageImpl<PersonForm>(Arrays.<PersonForm> asList(), pageable, 0);
        Page<Person> target = PageableMapper.map(source, Person.class, beanMapper);

        assertNotNull(target);
        assertEquals(pageable.getPageNumber(), target.getNumber());
        assertEquals(pageable.getPageSize(), target.getSize());
        assertEquals(pageable.getSort(), target.getSort());
        assertEquals(0, target.getTotalElements());
    }

}
