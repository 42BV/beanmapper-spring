package io.beanmapper.spring.web.mockmvc.fakedomain;

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import io.beanmapper.BeanMapper;
import io.beanmapper.spring.web.MergedFormMethodArgumentResolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.support.DomainClassConverter;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

@EnableWebMvc
@EnableSpringDataWebSupport
@ComponentScan(basePackageClasses = FakeApplicationConfig.class,
        includeFilters = @ComponentScan.Filter({ ControllerAdvice.class, Controller.class, RestController.class }),
        excludeFilters = @ComponentScan.Filter({ Configuration.class, Service.class, Repository.class }))
public class FakeWebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private FormattingConversionService mvcConversionService;

    @Autowired
    private BeanMapper beanMapper;

    @Autowired
    private ApplicationContext applicationContext;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(mappingJackson2HttpMessageConverter());
    }

    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper());
        return converter;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new MergedFormMethodArgumentResolver(
                Collections.singletonList(mappingJackson2HttpMessageConverter()),
                beanMapper,
                applicationContext,
                entityManager
        ));
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();

        SimpleModule module = new SimpleModule("BeanMapperSpring", new Version(1, 0, 0, null, "org.beanmapper", "spring"));
        mapper.registerModule(module);

        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

    @Bean
    public DomainClassConverter<FormattingConversionService> domainClassConverter() {
        return new DomainClassConverter<>(mvcConversionService);
    }

}
