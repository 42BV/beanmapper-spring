package io.beanmapper.spring.web.mockmvc.fakedomain;

import java.util.Collections;
import java.util.List;

import io.beanmapper.BeanMapper;
import io.beanmapper.spring.web.MergedFormMethodArgumentResolver;
import io.beanmapper.spring.web.converter.StructuredJsonMessageConverter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import tools.jackson.core.Version;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.support.DomainClassConverter;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.converter.HttpMessageConverters.ServerBuilder;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.annotation.JsonInclude;

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
    public void configureMessageConverters(ServerBuilder builder) {
        builder.addCustomConverter(structuredJsonMessageConverter());
    }

    public StructuredJsonMessageConverter structuredJsonMessageConverter() {
        return new StructuredJsonMessageConverter(new JacksonJsonHttpMessageConverter(objectMapper()));
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new MergedFormMethodArgumentResolver(
                Collections.singletonList(structuredJsonMessageConverter()),
                beanMapper,
                applicationContext,
                entityManager
        ));
    }

    @Bean
    public JsonMapper objectMapper() {
        return JsonMapper.builder()
                .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(JsonInclude.Include.NON_NULL))
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .addModule(new SimpleModule("BeanMapperSpring", new Version(1, 0, 0, null, "org.beanmapper", "spring")))
                .build();
    }

    @Bean
    public DomainClassConverter<FormattingConversionService> domainClassConverter() {
        return new DomainClassConverter<>(mvcConversionService);
    }

}
