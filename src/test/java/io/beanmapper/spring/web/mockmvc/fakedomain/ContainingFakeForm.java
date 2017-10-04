package io.beanmapper.spring.web.mockmvc.fakedomain;

import io.beanmapper.annotations.BeanIgnore;
import io.beanmapper.annotations.BeanProperty;

public class ContainingFakeForm {

    @BeanProperty(name = "fake")
    public Long fakeId;

    @BeanIgnore
    public String passMe;

}
