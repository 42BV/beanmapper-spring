/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.spring.web;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.beanmapper.BeanMapper;
import io.beanmapper.config.BeanMapperBuilder;
import io.beanmapper.spring.AbstractSpringTest;
import io.beanmapper.spring.ApplicationConfig;
import io.beanmapper.spring.model.Person;
import io.beanmapper.spring.model.PersonRepository;
import io.beanmapper.spring.web.converter.StructuredJsonMessageConverter;

import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

public class PersonControllerTest extends AbstractSpringTest {
 
    private MockMvc webClient;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private ApplicationContext applicationContext;
    
    @Autowired
    private PersonRepository personRepository;
    
    @Before
    public void setUp() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);
        
        BeanMapper beanMapper = new BeanMapperBuilder()
                .addPackagePrefix(ApplicationConfig.class)
                .build();

        this.webClient = MockMvcBuilders.standaloneSetup(new PersonController())
                .setCustomArgumentResolvers(new MergedFormMethodArgumentResolver(
                        Arrays.<HttpMessageConverter<?>> asList(new StructuredJsonMessageConverter(converter)),
                        beanMapper,
                        applicationContext))
                .setMessageConverters(converter)
                .setConversionService(new FormattingConversionService())
                .build();
    }
    
    @Test
    public void testCreate() throws Exception {
        this.webClient.perform(MockMvcRequestBuilders.post("/person")
                .content("{\"name\":\"Henk\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Henk"));
    }

    @Test
    public void testUpdatePatch() throws Exception {
        Person person = new Person();
        person.setName("Henk");
        person.setCity("Lisse");
        personRepository.save(person);

        this.webClient.perform(MockMvcRequestBuilders.put("/person/" + person.getId() + "/patch")
                .content("{\"name\":\"Jan\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(person.getId().intValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Jan"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.city").value("Lisse"));
    }
    
    @Test
    public void testUpdateNoPatch() throws Exception {
        Person person = new Person();
        person.setName("Henk");
        person.setCity("Lisse");
        personRepository.save(person);
        
        this.webClient.perform(MockMvcRequestBuilders.put("/person/" + person.getId() + "/no-patch")
                .content("{\"name\":\"Jan\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(person.getId().intValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Jan"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.city").doesNotExist());
    }
    
    @Test
    public void testUpdateNoPatchMergeIdAsRequestParam() throws Exception {
        Person person = new Person();
        person.setName("Henk");
        person.setCity("Lisse");
        personRepository.save(person);

        this.webClient.perform(MockMvcRequestBuilders.put("/person/query-param")
                .param("id", person.getId().toString())
                .content("{\"name\":\"Jan\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(person.getId().intValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Jan"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.city").doesNotExist());
    }

    @Test
    public void testLazy() throws Exception {
        Person person = new Person();
        person.setName("Henk");
        personRepository.save(person);
        
        this.webClient.perform(MockMvcRequestBuilders.put("/person/" + person.getId() + "/lazy")
                .content("{\"name\":\"Jan\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(person.getId().intValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Jan"));
    }

    @Test
    public void multipartForm() throws Exception {
        Person person = new Person();
        person.setName("Henk");
        person.setStreet("Stationsplein");
        personRepository.save(person);

        byte[] bytes = IOUtils.toByteArray("CAFEBABE");
        MockMultipartFile photoPart = new MockMultipartFile("photo", "photo.jpeg", "image/jpeg", bytes);
        MockMultipartFile personPart = new MockMultipartFile("person", "", "application/json", "{\"name\":\"Jan\"}".getBytes());

        webClient.perform(
                    MockMvcRequestBuilders
                        .fileUpload("/person/" + person.getId() + "/multipart")
                        .file(personPart)
                        .file(photoPart)
                )
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(person.getId().intValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Jan"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.street").value("Stationsplein"));
    }

    @Test
    public void testPair() throws Exception {
        Person person = new Person();
        person.setName("Henk");
        person.setCity("Leiden");
        person.setStreet("Stationsplein");
        person.setHouseNumber("42");
        person.setBankAccount("1234567890");
        personRepository.save(person);

        this.webClient.perform(MockMvcRequestBuilders.put("/person/" + person.getId() + "/pair")
                .content("{\"name\":\"Jan\",\"city\":\"Delft\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.beforeMerge.id").value(person.getId().intValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.beforeMerge.name").value("Henk"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.beforeMerge.street").value("Stationsplein"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.beforeMerge.city").value("Leiden"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.beforeMerge.houseNumber").value("42"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.beforeMerge.bankAccount").value("1234567890"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.afterMerge.id").value(person.getId().intValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.afterMerge.name").value("Jan"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.afterMerge.street").value("Stationsplein"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.afterMerge.city").value("Delft"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.afterMerge.houseNumber").value("42"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.afterMerge.bankAccount").value("1234567890"));

    }

}
