package io.beanmapper.spring.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;

import io.beanmapper.BeanMapper;
import io.beanmapper.spring.AbstractSpringTest;
import io.beanmapper.spring.model.Person;
import io.beanmapper.spring.model.PersonRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
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

    @Test
    public void noRepository() {
        EntityFinder entityFinder = new SpringDataEntityFinder(applicationContext, entityManager);
        assertThrows(EntityNotFoundException.class, () -> entityFinder.find(42L, BeanMapper.class));
    }

    @Test
    public void entityNotFound() {
        EntityFinder entityFinder = new SpringDataEntityFinder(applicationContext, entityManager);
        assertThrows(EntityNotFoundException.class, () -> entityFinder.find(42L, Person.class));
    }

}
