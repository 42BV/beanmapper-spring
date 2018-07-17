package io.beanmapper.spring.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.persistence.EntityNotFoundException;

import io.beanmapper.spring.AbstractSpringTest;
import io.beanmapper.spring.model.Person;
import io.beanmapper.spring.model.PersonRepository;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class EntityFinderTest extends AbstractSpringTest {

    @Autowired
    private PersonRepository personRepository;

    @Test
    public void find() {
        Person person = new Person();
        person.setName("Henk");
        person.setCity("Lisse");
        person = personRepository.save(person);

        EntityFinder entityFinder = new EntityFinder() {

            @Override
            public <T> T find(Long id, Class<T> entityClass) throws EntityNotFoundException {
                return (T)personRepository.findById(id).orElse(null);
            }

            @Override
            public <T> T findAndDetach(Long id, Class<T> entityClass) throws EntityNotFoundException {
                return find(id, entityClass);
            }

        };

        Person foundPerson = entityFinder.find(person.getId(), Person.class);
        assertNotNull(foundPerson);
        assertEquals("Henk", foundPerson.getName());
    }

}
