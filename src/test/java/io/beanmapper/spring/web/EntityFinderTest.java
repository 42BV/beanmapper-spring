package io.beanmapper.spring.web;

import io.beanmapper.spring.AbstractSpringTest;
import io.beanmapper.spring.model.Person;
import io.beanmapper.spring.model.PersonRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityNotFoundException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
            public Object getOne(Class entityClass, Long id) throws EntityNotFoundException {
                return personRepository.findOne(id);
            }
        };
        Person foundPerson = (Person)entityFinder.getOne(Person.class, person.getId());
        assertNotNull(foundPerson);
        assertEquals("Henk", foundPerson.getName());
    }

}
