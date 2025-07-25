/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.spring;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hibernate.dialect.HSQLDialect;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;

@ComponentScan(
    basePackageClasses = ApplicationConfig.class,
    excludeFilters = {
        @ComponentScan.Filter({ ControllerAdvice.class, Controller.class, RestController.class })
    }
)
@EnableTransactionManagement
@EnableJpaRepositories(basePackageClasses = ApplicationConfig.class)
@Configuration
public class ApplicationConfig {
    
    @Autowired
    private DataSource dataSource;
    
    @Autowired
    @Qualifier("hibernateDialect")
    private String hibernateDialect;
    
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource);
        entityManagerFactoryBean.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        entityManagerFactoryBean.setPackagesToScan(ApplicationConfig.class.getPackage().getName());
        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setGenerateDdl(false);
        jpaVendorAdapter.setDatabasePlatform(hibernateDialect);
        entityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);

        Map<String, Object> jpaProperties = getHibernatePropertyMap();
        entityManagerFactoryBean.setJpaPropertyMap(jpaProperties);
        entityManagerFactoryBean.setEntityManagerFactoryInterface(EntityManagerFactory.class);

        return entityManagerFactoryBean;
    }

    private Map<String, Object> getHibernatePropertyMap() {
        Map<String, Object> jpaProperties = new HashMap<>();
        jpaProperties.put("hibernate.dialect", hibernateDialect);
        jpaProperties.put("hibernate.hbm2ddl.auto", "create-drop");
        jpaProperties.put("hibernate.jdbc.use_get_generated_keys", true);
        jpaProperties.put("hibernate.id.new_generator_mappings", true);
        jpaProperties.put("hibernate.generate_statistics", false);
        return jpaProperties;
    }

    @Bean
    public JpaTransactionManager transactionManager(@Autowired LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
        return transactionManager;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        return mapper;
    }

    @Configuration
    public static class HsqlConfig {
        
        @Bean
        public DataSource dataSource() {
            return new EmbeddedDatabaseBuilder().setName("dev").setType(HSQL).build();
        }
        
        @Bean
        public String hibernateDialect() {
            return HSQLDialect.class.getName();
        }
        
    }

}
