package io.beanmapper.spring.web.mockmvc.fakedomain;

import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Component;

@Component
public class Populator {

    private FakeBuilder fakeBuilder;

    @PostConstruct
    public void initData() {
        fakeBuilder.henk();
        fakeBuilder.piet();
    }


}
