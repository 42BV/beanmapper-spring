package io.beanmapper.spring.web.mockmvc.fakedomain;

import javax.validation.constraints.NotNull;

public class ContainingFake {

    private Fake fake;

    @NotNull
    private String mustNotBeNull;

    public Fake getFake() {
        return fake;
    }

    public void setFake(Fake fake) {
        this.fake = fake;
    }

}
