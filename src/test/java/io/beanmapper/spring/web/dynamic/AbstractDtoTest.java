package io.beanmapper.spring.web.dynamic;

import io.beanmapper.BeanMapper;
import io.beanmapper.spring.model.ProductDto;

public class AbstractDtoTest {

    protected DynamicBeanMapper dynamicBeanMapper;

    {
        BeanMapper beanMapper = new BeanMapper();
        beanMapper.addPackagePrefix(ProductDto.class);
        dynamicBeanMapper = new DynamicBeanMapper(beanMapper);
    }

}
