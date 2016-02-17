package io.beanmapper.spring.web.mockmvc.fakedomain;

import io.beanmapper.BeanMapper;
import io.beanmapper.spring.converter.IdToEntityBeanConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

public class FakeApplicationConfig {

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public BeanMapper beanMapper() {
        BeanMapper bm = new BeanMapper();
        bm.addPackagePrefix(FakeApplicationConfig.class);
//        bm.setBeanUnproxy(new AdvancedBeanUnproxy());
        if (applicationContext != null) {
            bm.addConverter(new IdToEntityBeanConverter(applicationContext));
        }
        return bm;
    }


}
