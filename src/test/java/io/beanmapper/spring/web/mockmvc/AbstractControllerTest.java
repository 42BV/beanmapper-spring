package io.beanmapper.spring.web.mockmvc;

import io.beanmapper.BeanMapper;
import io.beanmapper.spring.web.mockmvc.fakedomain.FakeApplicationConfig;
import io.beanmapper.spring.web.mockmvc.fakedomain.FakeWebMvcConfig;
import org.springframework.data.domain.Persistable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

public class AbstractControllerTest {

    private FakeWebMvcConfig config = new FakeWebMvcConfig();

    protected MockMvc webClient;

    protected MockMvcBeanMapper mockMvcBeanMapper;

    protected void initWebClient(Object controller) {

        this.mockMvcBeanMapper = new MockMvcBeanMapper(
                new FormattingConversionService(),
                Collections.singletonList(config.mappingJackson2HttpMessageConverter()),
                new FakeApplicationConfig().beanMapper()
        );

        this.webClient = MockMvcBuilders.standaloneSetup(controller)
                .setMessageConverters(config.mappingJackson2HttpMessageConverter())
                .setCustomArgumentResolvers(mockMvcBeanMapper.createHandlerMethodArgumentResolvers())
                .setConversionService(mockMvcBeanMapper.getConversionService())
                .build();
    }

    public BeanMapper beanMapper() {
        return mockMvcBeanMapper.getBeanMapper();
    }

    public void registerRepository(CrudRepository<? extends Persistable, Long> repository, Class<?> entityClass) {
        mockMvcBeanMapper.registerRepository(repository, entityClass);
    }
}
