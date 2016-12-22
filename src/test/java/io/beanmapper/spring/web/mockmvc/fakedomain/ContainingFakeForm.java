package io.beanmapper.spring.web.mockmvc.fakedomain;

import io.beanmapper.annotations.BeanProperty;

import javax.validation.constraints.NotNull;

public class ContainingFakeForm {

    @BeanProperty(name = "fake")
    public Long fakeId;

    @NotNull
    public String passMe;

}
