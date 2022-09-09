package io.beanmapper.spring.web.mockmvc.fakedomain;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import io.beanmapper.BeanMapper;
import io.beanmapper.config.BeanMapperBuilder;
import io.beanmapper.spring.ApplicationConfig;
import io.beanmapper.spring.web.mockmvc.AbstractControllerTest;
import io.beanmapper.spring.web.mockmvc.FakeController;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

public class FakeControllerTest extends AbstractControllerTest {

    @InjectMocks
    private FakeController controller;

    @MockBean
    private FakeService service;

    @MockBean
    private FakeRepository repository;

    @Autowired
    private FakeBuilder fakeBuilder;

    @InjectMocks
    private ObjectMapper objectMapper;

    private BeanMapper beanMapper;

    private MappingJackson2HttpMessageConverter converter;

    @BeforeEach
    public void setup() {
        converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);

        beanMapper = new BeanMapperBuilder()
                .addPackagePrefix(ApplicationConfig.class)
                .build();

        // Made it public due to problems with AutoWire.
        controller.beanMapper = beanMapper;

        initWebClient(controller);
        registerRepository(repository, Fake.class);
        createWebClient(controller);

        when(this.repository.findById(42L)).thenAnswer(i -> Optional.ofNullable(this.fakeBuilder.base().withId(42L).withName("Henk").construct()));
    }

    @Test
    public void find() throws Exception {
        when(this.service.read(any())).thenReturn(this.fakeBuilder.base().withId(42L).withName("Henk").construct());

        this.webClient.perform(MockMvcRequestBuilders.get("/fake/42")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(42))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Henk"));
    }

    @Test
    public void create() throws Exception {
        when(service.create(any())).thenAnswer(i -> {
            Fake fake = (Fake) i.getArguments()[0];
            fake.setId(42L);
            return fake;
        });

        FakeForm fakeForm = new FakeForm();
        fakeForm.name = "Henk";

        this.webClient.perform(MockMvcRequestBuilders.post("/fake")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(new FakeWebMvcConfig().objectMapper().writeValueAsString(fakeForm)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(42))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Henk"));
    }

    @Test
    public void update() throws Exception {
        when(this.service.update(any())).thenAnswer(i -> i.getArguments()[0]);

        FakeForm fakeForm = new FakeForm();
        fakeForm.name = "Henk";

        super.webClient.perform(MockMvcRequestBuilders.put("/fake/42")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new FakeWebMvcConfig().objectMapper().writeValueAsString(fakeForm)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(42))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Henk"));
    }

    @Test
    public void delete() throws Exception {
        when(this.service.delete(any())).thenAnswer(i -> i.getArguments()[0]);

        this.webClient.perform(MockMvcRequestBuilders.delete("/fake/42")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(42))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Henk"));
    }

}
