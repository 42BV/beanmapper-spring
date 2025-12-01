package io.beanmapper.spring.web.mockmvc;

import java.util.Collections;

import io.beanmapper.BeanMapper;
import io.beanmapper.spring.AbstractSpringTest;
import io.beanmapper.spring.web.mockmvc.fakedomain.FakeApplicationConfig;
import io.beanmapper.spring.web.mockmvc.fakedomain.FakeWebMvcConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.Persistable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@AutoConfigureMockMvc
@EnableAutoConfiguration
public class AbstractControllerTest extends AbstractSpringTest {

    private FakeWebMvcConfig config = new FakeWebMvcConfig();

    @Autowired
    protected MockMvc webClient;

    protected MockMvcBeanMapper mockMvcBeanMapper;

    protected void initWebClient(Object controller) {

        this.mockMvcBeanMapper = new MockMvcBeanMapper(
                new FormattingConversionService(),
                Collections.singletonList(config.structuredJsonMessageConverter()),
                new FakeApplicationConfig().beanMapper()
        );
    }

    public void createWebClient(Object controller) {
        this.webClient = MockMvcBuilders.standaloneSetup(controller)
                .setMessageConverters(config.structuredJsonMessageConverter())
                .setCustomArgumentResolvers(mockMvcBeanMapper.createHandlerMethodArgumentResolvers())
                .setConversionService(mockMvcBeanMapper.getConversionService())
                .build();
    }

    public BeanMapper beanMapper() {
        return mockMvcBeanMapper.getBeanMapper();
    }

    public <T extends Persistable> void registerRepository(CrudRepository<T, Long> repository, Class<T> entityClass) {
        mockMvcBeanMapper.registerRepository(repository, entityClass);
    }
}
