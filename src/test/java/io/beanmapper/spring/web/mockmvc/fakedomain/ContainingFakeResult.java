package io.beanmapper.spring.web.mockmvc.fakedomain;

import io.beanmapper.annotations.BeanProperty;

public class ContainingFakeResult {

    @BeanProperty(name = "fake.name")
    public String fakeName;

}
