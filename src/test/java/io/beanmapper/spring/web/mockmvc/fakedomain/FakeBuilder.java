package io.beanmapper.spring.web.mockmvc.fakedomain;

import nl._42.heph.AbstractBuilder;

import org.springframework.stereotype.Component;

@Component
public class FakeBuilder extends AbstractBuilder<Fake, FakeBuildCommand> {

    @Override
    public FakeBuildCommand base() {
        return blank();
    }

    public Fake henk() {
        return base()
                .withName("Henk")
                .create();
    }

    public Fake piet() {
        return base()
                .withName("Piet")
                .create();
    }

}
