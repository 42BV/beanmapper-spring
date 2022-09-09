package io.beanmapper.spring.web.mockmvc.fakedomain;

import org.springframework.stereotype.Service;

@Service
public class FakeService {

    public Fake read(Fake fake) {
        return fake;
    }

    public Fake create(Fake fake) {
        return fake;
    }

    public Fake update(Fake fake) {
        return fake;
    }

    public Fake delete(Fake fake) {
        return fake;
    }

}
