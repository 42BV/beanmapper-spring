package io.beanmapper.spring.web.mockmvc.fakedomain;

public class ContainingFake {

    private Fake fake;

    public Fake getFake() {
        return fake;
    }

    public void setFake(Fake fake) {
        this.fake = fake;
    }

}
