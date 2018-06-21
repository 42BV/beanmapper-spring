/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.spring.web;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;

import javax.persistence.EntityManager;

import io.beanmapper.BeanMapper;
import io.beanmapper.config.BeanMapperBuilder;
import io.beanmapper.spring.AbstractSpringTest;
import io.beanmapper.spring.ApplicationConfig;
import io.beanmapper.spring.flusher.JpaAfterClearFlusher;
import io.beanmapper.spring.model.Person;
import io.beanmapper.spring.model.PersonRepository;
import io.beanmapper.spring.model.Tag;
import io.beanmapper.spring.web.converter.StructuredJsonMessageConverter;
import mockit.Mocked;
import mockit.StrictExpectations;

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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

public class PersonControllerTest extends AbstractSpringTest {
 
    private MockMvc webClient;

    private BeanMapper beanMapper;

    private MappingJackson2HttpMessageConverter converter;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private PersonRepository personRepository;

    @Mocked
    private EntityManager entityManager;

    @Before
    public void setUp() {
        converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);

        beanMapper = new BeanMapperBuilder()
                .addPackagePrefix(ApplicationConfig.class)
                .build();

        this.webClient = createWebClient(beanMapper);
    }

    private MockMvc createWebClient(BeanMapper beanMapper) {
        return MockMvcBuilders.standaloneSetup(new PersonController())
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
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Henk"));
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
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(person.getId().intValue()))
                .andExpect(jsonPath("$.name").value("Jan"))
                .andExpect(jsonPath("$.city").value("Lisse"));
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
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(person.getId().intValue()))
                .andExpect(jsonPath("$.name").value("Jan"))
                .andExpect(jsonPath("$.city").doesNotExist());
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
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(person.getId().intValue()))
                .andExpect(jsonPath("$.name").value("Jan"))
                .andExpect(jsonPath("$.city").doesNotExist());
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
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(person.getId().intValue()))
                .andExpect(jsonPath("$.name").value("Jan"));
    }

    @Test
    public void testLazyTriggersFlush() throws Exception {

        new StrictExpectations(){{
            entityManager.flush();
        }};

        JpaAfterClearFlusher flusher = new JpaAfterClearFlusher(entityManager);

        this.webClient = createWebClient(new BeanMapperBuilder()
                .addPackagePrefix(ApplicationConfig.class)
                .addAfterClearFlusher(flusher)
                .build());

        Person person = new Person();
        person.setName("Henk");
        person.setTags(Arrays.asList(Tag.CUSTOMER, Tag.UPSELLING));
        personRepository.save(person);

        this.webClient.perform(MockMvcRequestBuilders.put("/person/" + person.getId() + "/lazy")
                .content("{\"name\":\"Jan\",\"tags\":[\"DEBTOR\",\"CUSTOMER\"]}")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(person.getId().intValue()))
                .andExpect(jsonPath("$.name").value("Jan"));
    }

    @Test
    public void testLazyFailFinalValidation() throws Exception {
        Person person = new Person();
        person.setName("Henk");
        personRepository.save(person);

        this.webClient.perform(MockMvcRequestBuilders.put("/person/" + person.getId() + "/lazy")
                .content("{}")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void multipartForm() throws Exception {
        Person person = new Person();
        person.setName("Henk");
        person.setStreet("Stationsplein");
        personRepository.save(person);

        byte[] bytes = "CAFEBABE".getBytes();
        MockMultipartFile photoPart = new MockMultipartFile("photo", "photo.jpeg", "image/jpeg", bytes);
        MockMultipartFile personPart = new MockMultipartFile("personForm", "", "application/json", "{\"name\":\"Jan\"}".getBytes());

        webClient.perform(
                    MockMvcRequestBuilders
                        .multipart("/person/" + person.getId() + "/multipart")
                        .file(personPart)
                        .file(photoPart)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(person.getId().intValue()))
                .andExpect(jsonPath("$.name").value("Jan"))
                .andExpect(jsonPath("$.street").value("Stationsplein"));
    }

    @Test
    public void testUpdateTags() throws Exception {
        Person person = new Person();
        person.setName("Henk");
        person.setCity("Leiden");
        person.setTags(new ArrayList<Tag>() {{
            add(Tag.DEBTOR);
            add(Tag.UPSELLING);
        }});
        personRepository.save(person);

        this.webClient.perform(MockMvcRequestBuilders.put("/person/" + person.getId() + "/no-patch")
                .content("{\"name\":\"Jan\",\"city\":\"Lisse\",\"tags\":[\"DEBTOR\",\"CUSTOMER\"]}")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(person.getId().intValue()))
                .andExpect(jsonPath("$.name").value("Jan"))
                .andExpect(jsonPath("$.city").value("Lisse"))
                .andExpect(jsonPath("$.tags[0]").value("DEBTOR"))
                .andExpect(jsonPath("$.tags[1]").value("CUSTOMER"));
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
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.beforeMerge.id").value(person.getId().intValue()))
                .andExpect(jsonPath("$.beforeMerge.name").value("Henk"))
                .andExpect(jsonPath("$.beforeMerge.street").value("Stationsplein"))
                .andExpect(jsonPath("$.beforeMerge.city").value("Leiden"))
                .andExpect(jsonPath("$.beforeMerge.houseNumber").value("42"))
                .andExpect(jsonPath("$.beforeMerge.bankAccount").value("1234567890"))
                .andExpect(jsonPath("$.afterMerge.id").value(person.getId().intValue()))
                .andExpect(jsonPath("$.afterMerge.name").value("Jan"))
                .andExpect(jsonPath("$.afterMerge.street").value("Stationsplein"))
                .andExpect(jsonPath("$.afterMerge.city").value("Delft"))
                .andExpect(jsonPath("$.afterMerge.houseNumber").value("42"))
                .andExpect(jsonPath("$.afterMerge.bankAccount").value("1234567890"));
    }

    @Test
    public void testPairWithCollection() throws Exception {
        Person person = new Person();
        person.setName("Henk");
        person.setCity("Leiden");
        person.setStreet("Stationsplein");
        person.setHouseNumber("42");
        person.setBankAccount("1234567890");
        person.setTags(new ArrayList<Tag>() {{
            add(Tag.UPSELLING);
        }});
        personRepository.save(person);

        this.webClient.perform(MockMvcRequestBuilders.put("/person/" + person.getId() + "/pair")
                .content("{\"name\":\"Jan\",\"city\":\"Lisse\",\"tags\":[\"CUSTOMER\",\"DEBTOR\"]}")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.beforeMerge.id").value(person.getId().intValue()))
                .andExpect(jsonPath("$.beforeMerge.tags[0]").value("UPSELLING"))
                .andExpect(jsonPath("$.afterMerge.id").value(person.getId().intValue()))
                .andExpect(jsonPath("$.afterMerge.tags[0]").value("CUSTOMER"))
                .andExpect(jsonPath("$.afterMerge.tags[1]").value("DEBTOR"));
    }

}
