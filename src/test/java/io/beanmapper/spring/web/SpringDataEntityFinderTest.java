package io.beanmapper.spring.web;

import io.beanmapper.BeanMapper;
import io.beanmapper.spring.AbstractSpringTest;
import io.beanmapper.spring.model.Person;
import io.beanmapper.spring.model.PersonRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.persistence.EntityNotFoundException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SpringDataEntityFinderTest extends AbstractSpringTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private PersonRepository personRepository;

    @Test
    public void find() {
        Person person = new Person();
        person.setName("Henk");
        person.setCity("Lisse");
        person = personRepository.save(person);

        EntityFinder entityFinder = new SpringDataEntityFinder(applicationContext);
        Person foundPerson = (Person)entityFinder.find(person.getId(), Person.class);
        assertNotNull(foundPerson);
        assertEquals("Henk", foundPerson.getName());
    }

    @Test(expected = EntityNotFoundException.class)
    public void noRepository() {
        EntityFinder entityFinder = new SpringDataEntityFinder(applicationContext);
        entityFinder.find(42L, BeanMapper.class);
    }

    @Test(expected = EntityNotFoundException.class)
    public void entityNotFound() {
        EntityFinder entityFinder = new SpringDataEntityFinder(applicationContext);
        entityFinder.find(42L, Person.class);
    }

}
