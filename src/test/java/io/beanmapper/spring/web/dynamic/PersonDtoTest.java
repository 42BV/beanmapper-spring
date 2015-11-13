package io.beanmapper.spring.web.dynamic;

import static org.junit.Assert.assertEquals;
import io.beanmapper.BeanMapper;
import io.beanmapper.spring.model.Person;
import io.beanmapper.spring.model.PersonDto;

import java.util.Arrays;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class PersonDtoTest extends AbstractDtoTest {

    @Test
    public void mapToStatic() {
        Person person = createPerson();

        PersonDto personDto = new BeanMapper().map(person, PersonDto.class);

        assertEquals((Long)42L, personDto.id);
        assertEquals("Henk", personDto.name);
        assertEquals("Koraalrood", personDto.street);
        assertEquals("11f", personDto.houseNumber);
        assertEquals("Zoetermeer", personDto.city);
    }

    @Test
    public void mapToDynamic() throws Exception {
        Person person = createPerson();
        Object dynPersonDto = dynBeanMapper.map(person, PersonDto.class, Arrays.asList("id", "name"));

        String json = new ObjectMapper().writeValueAsString(dynPersonDto);
        assertEquals("{\"id\":42,\"name\":\"Henk\"}", json);
    }

    private Person createPerson() {
        Person person = new Person();
        person.setId(42L);
        person.setName("Henk");
        person.setStreet("Koraalrood");
        person.setHouseNumber("11f");
        person.setCity("Zoetermeer");
        person.setBankAccount("NLABN123998877665544");
        return person;
    }

}
