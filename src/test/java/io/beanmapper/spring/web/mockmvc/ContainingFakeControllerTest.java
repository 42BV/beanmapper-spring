package io.beanmapper.spring.web.mockmvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import io.beanmapper.config.BeanMapperBuilder;
import io.beanmapper.spring.ApplicationConfig;
import io.beanmapper.spring.web.mockmvc.fakedomain.ContainingFake;
import io.beanmapper.spring.web.mockmvc.fakedomain.ContainingFakeController;
import io.beanmapper.spring.web.mockmvc.fakedomain.ContainingFakeForm;
import io.beanmapper.spring.web.mockmvc.fakedomain.ContainingFakeService;
import io.beanmapper.spring.web.mockmvc.fakedomain.Fake;
import io.beanmapper.spring.web.mockmvc.fakedomain.FakeRepository;
import io.beanmapper.spring.web.mockmvc.fakedomain.FakeWebMvcConfig;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class ContainingFakeControllerTest extends AbstractControllerTest {

    @InjectMocks
    private ContainingFakeController controller;

    @MockBean
    private FakeRepository fakeRepository;

    @MockBean
    private ContainingFakeService containingFakeService;

    private Fake fake;

    private ContainingFake containingFake;

    @BeforeEach
    public void setup() {
        controller.beanMapper = new BeanMapperBuilder()
                .addPackagePrefix(ApplicationConfig.class)
                .build();
        initWebClient(controller);
        registerRepository(fakeRepository, Fake.class);
        createWebClient(controller);

        fake = new Fake();
        fake.setName("Henk");

        containingFake = new ContainingFake();
        containingFake.setFake(fake);
    }

    @Test
    public void create() throws Exception {
        when(this.fakeRepository.findById(42L)).thenReturn(Optional.ofNullable(this.fake));
        when(containingFakeService.create(any())).thenAnswer(i -> {
            ContainingFake fake = (ContainingFake) i.getArguments()[0];
            fake.setFake(this.fake);
            return fake;
        });

        ContainingFakeForm containingFakeForm = new ContainingFakeForm();
        containingFakeForm.fakeId = 42L;

        this.webClient.perform(MockMvcRequestBuilders.post("/containing-fake")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new FakeWebMvcConfig().objectMapper().writeValueAsString(containingFakeForm)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.fakeName").value("Henk"));
    }

    @Test
    public void createThrowsValidationExceptionForForm() throws Exception {
        ContainingFakeForm containingFakeForm = new ContainingFakeForm();
        containingFakeForm.fakeId = 42L;

        this.webClient.perform(MockMvcRequestBuilders.post("/containing-fake/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new FakeWebMvcConfig().objectMapper().writeValueAsString(containingFakeForm)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(400));
    }

    @Test
    public void createThrowsValidationExceptionForMappedTarget() throws Exception {
        ContainingFakeForm containingFakeForm = new ContainingFakeForm();
        containingFakeForm.fakeId = 42L;
        containingFakeForm.passMe = "somevalue";

        this.webClient.perform(MockMvcRequestBuilders.post("/containing-fake/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new FakeWebMvcConfig().objectMapper().writeValueAsString(containingFakeForm)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(400));
    }
}
