package io.beanmapper.spring.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import io.beanmapper.spring.AbstractSpringTest;
import io.beanmapper.spring.model.Person;
import io.beanmapper.spring.model.PersonRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@ExtendWith(MockitoExtension.class)
public class EntityFinderTest extends AbstractSpringTest {

    @Mock
    private PersonRepository personRepository;

    @Test
    public void find() {
        Person person = new Person();
        person.setName("Henk");
        person.setCity("Lisse");
        person.setId(42L);

        when(this.personRepository.findById(person.getId())).thenReturn(Optional.of(person));

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
