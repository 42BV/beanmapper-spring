/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.spring.web;

import io.beanmapper.BeanMapper;
import io.beanmapper.spring.AbstractSpringTest;
import io.beanmapper.spring.ApplicationConfig;
import io.beanmapper.spring.model.Person;
import io.beanmapper.spring.model.PersonRepository;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
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
        
        BeanMapper beanMapper = new BeanMapper();
        beanMapper.addPackagePrefix(ApplicationConfig.class);
        
        List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>();
        converters.add(converter);
        
        this.webClient = MockMvcBuilders.standaloneSetup(new PersonController())
                .setCustomArgumentResolvers(new MergedFormMethodArgumentResolver(
                        converters,
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

    // @TODO patching temporarily disabled. Not used for current business case of customer
//    @Test
//    public void testUpdatePatch() throws Exception {
//        Person person = new Person();
//        person.setName("Henk");
//        person.setCity("Lisse");
//        personRepository.save(person);
//
//        this.webClient.perform(MockMvcRequestBuilders.put("/person/" + person.getId() + "/patch")
//                .content("{\"name\":\"Jan\"}")
//                .contentType(MediaType.APPLICATION_JSON))
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(person.getId().intValue()))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Jan"))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.city").value("Lisse"));
//    }
    
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

}
