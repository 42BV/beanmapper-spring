package io.beanmapper.spring.web.dynamic;

import io.beanmapper.BeanMapper;
import io.beanmapper.spring.model.ProductDto;

public class AbstractDtoTest {

    protected static DynBeanMapper dynBeanMapper;

    static {
        BeanMapper beanMapper = new BeanMapper();
        beanMapper.addPackagePrefix(ProductDto.class);
        dynBeanMapper = new DynBeanMapper(beanMapper);
    }

}
