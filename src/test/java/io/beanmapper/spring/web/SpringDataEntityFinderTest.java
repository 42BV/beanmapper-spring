package io.beanmapper.spring.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import io.beanmapper.BeanMapper;
import io.beanmapper.spring.AbstractSpringTest;
import io.beanmapper.spring.model.Person;
import io.beanmapper.spring.model.PersonRepository;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;

public class SpringDataEntityFinderTest extends AbstractSpringTest {

    @Autowired
    private ApplicationContext applicationContext;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PersonRepository personRepository;

    @Test
    public void find() {
        Person person = new Person();
        person.setName("Henk");
        person.setCity("Lisse");
        person = personRepository.save(person);

        EntityFinder entityFinder = new SpringDataEntityFinder(applicationContext, entityManager);
        Person foundPerson = (Person) entityFinder.find(person.getId(), Person.class);
        assertNotNull(foundPerson);
        assertEquals("Henk", foundPerson.getName());
    }

    @Test
    @Transactional
    public void findAndDetach() {
        Person person = new Person();
        person.setName("Henk");
        person.setCity("Lisse");
        person = personRepository.save(person);

        EntityFinder entityFinder = new SpringDataEntityFinder(applicationContext, entityManager);
        Person person1 = entityFinder.find(person.getId(), Person.class);
        Person person2 = entityFinder.find(person.getId(), Person.class);
        assertEquals(person1, person2);

        person1 = entityFinder.findAndDetach(person.getId(), Person.class);
        person2 = entityFinder.find(person.getId(), Person.class);

        assertNotEquals(person1, person2);
    }

    @Test(expected = EntityNotFoundException.class)
    public void noRepository() {
        EntityFinder entityFinder = new SpringDataEntityFinder(applicationContext, entityManager);
        entityFinder.find(42L, BeanMapper.class);
    }

    @Test(expected = EntityNotFoundException.class)
    public void entityNotFound() {
        EntityFinder entityFinder = new SpringDataEntityFinder(applicationContext, entityManager);
        entityFinder.find(42L, Person.class);
    }

}
