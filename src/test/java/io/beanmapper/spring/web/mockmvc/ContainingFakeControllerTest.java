package io.beanmapper.spring.web.mockmvc;

import io.beanmapper.spring.web.mockmvc.fakedomain.*;
import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class ContainingFakeControllerTest extends AbstractControllerTest {

    @Mocked
    private FakeRepository fakeRepository;

    @Mocked
    private ContainingFakeService containingFakeService;

    private Fake fake;

    private ContainingFake containingFake;

    @Before
    public void setup() {
        ContainingFakeController controller = new ContainingFakeController();
        initWebClient(controller);
        Deencapsulation.setField(controller, "containingFakeService", containingFakeService);
        Deencapsulation.setField(controller, "beanMapper", beanMapper());
        registerRepository(fakeRepository, Fake.class);

        fake = new Fake();
        Deencapsulation.setField(fake, "id", 42L);
        fake.setName("Henk");

        containingFake = new ContainingFake();
        containingFake.setFake(fake);
    }

    @Test
    public void create() throws Exception {
        new Expectations(){{
            fakeRepository.findOne(42L);
            result = fake;

            containingFakeService.create((ContainingFake) any);
            result = containingFake;
        }};

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
        new Expectations(){{
            fakeRepository.findOne(42L);
            result = fake;
        }};

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
