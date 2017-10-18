/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.spring;

import java.util.Arrays;

import io.beanmapper.BeanMapper;
import io.beanmapper.config.BeanMapperBuilder;
import io.beanmapper.spring.model.Person;
import io.beanmapper.spring.model.PersonForm;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageableMapperTest {
    
    private final Pageable pageable = new PageRequest(0, 10, new Sort("id"));

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
        
        Assert.assertNotNull(target);
        Assert.assertEquals(pageable.getPageNumber(), target.getNumber());
        Assert.assertEquals(pageable.getPageSize(), target.getSize());
        Assert.assertEquals(pageable.getSort(), target.getSort());
        Assert.assertEquals(1, target.getTotalElements());
        
        Person person = target.getContent().get(0);
        Assert.assertEquals(Person.class, person.getClass());
        Assert.assertEquals("Henk", person.getName());
    }
    
    @Test
    public void testMapEmpty() {
        Page<PersonForm> source = new PageImpl<PersonForm>(Arrays.<PersonForm> asList(), pageable, 0);
        Page<Person> target = PageableMapper.map(source, Person.class, beanMapper);

        Assert.assertNotNull(target);
        Assert.assertEquals(pageable.getPageNumber(), target.getNumber());
        Assert.assertEquals(pageable.getPageSize(), target.getSize());
        Assert.assertEquals(pageable.getSort(), target.getSort());
        Assert.assertEquals(0, target.getTotalElements());
    }

}
