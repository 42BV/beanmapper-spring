package io.beanmapper.spring.web.mockmvc;

import io.beanmapper.spring.web.mockmvc.fakedomain.Fake;
import io.beanmapper.spring.web.mockmvc.fakedomain.FakeController;
import io.beanmapper.spring.web.mockmvc.fakedomain.FakeForm;
import io.beanmapper.spring.web.mockmvc.fakedomain.FakeRepository;
import io.beanmapper.spring.web.mockmvc.fakedomain.FakeService;
import io.beanmapper.spring.web.mockmvc.fakedomain.FakeWebMvcConfig;
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

public class FakeControllerTest extends AbstractControllerTest {

    @Mocked
    private FakeRepository fakeRepository;

    @Mocked
    private FakeService fakeService;

    private Fake fake;

    @Before
    public void setup() {
        FakeController controller = new FakeController();
        initWebClient(controller);
        Deencapsulation.setField(controller, "fakeService", fakeService);
        Deencapsulation.setField(controller, "beanMapper", beanMapper());
        registerRepository(fakeRepository, Fake.class);
        createWebClient(controller);

        fake = new Fake();
        Deencapsulation.setField(fake, "id", 42L);
        fake.setName("Henk");

        new NonStrictExpectations() {{
            fakeRepository.findOne(42L);
            result = fake;
        }};

    }

    @Test
    public void find() throws Exception {

        new Expectations(){{
            fakeService.read((Fake)any);
            result = new ReturnPassedArgument<Fake>();
        }};

        this.webClient.perform(MockMvcRequestBuilders.get("/fake/42")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(42))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Henk"));
    }

    @Test
    public void create() throws Exception {
        new Expectations(){{
            fakeService.create((Fake)any);
            result = new ReturnPassedArgument<Fake>(42L);
        }};

        FakeForm fakeForm = new FakeForm();
        fakeForm.name = "Henk";

        this.webClient.perform(MockMvcRequestBuilders.post("/fake")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new FakeWebMvcConfig().objectMapper().writeValueAsString(fakeForm)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(42))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Henk"));
    }

    @Test
    public void update() throws Exception {
        new Expectations(){{
            fakeService.update((Fake)any);
            result = new ReturnPassedArgument<Fake>();
        }};

        FakeForm fakeForm = new FakeForm();
        fakeForm.name = "Henk";

        this.webClient.perform(MockMvcRequestBuilders.put("/fake/42")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new FakeWebMvcConfig().objectMapper().writeValueAsString(fakeForm)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(42))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Henk"));
    }

    @Test
    public void delete() throws Exception {
        new Expectations(){{
            fakeService.delete((Fake)any);
            result = new ReturnPassedArgument<Fake>();
        }};

        this.webClient.perform(MockMvcRequestBuilders.delete("/fake/42")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(42))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Henk"));
    }

}
