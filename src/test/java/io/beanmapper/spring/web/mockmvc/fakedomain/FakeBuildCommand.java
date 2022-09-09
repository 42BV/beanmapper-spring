package io.beanmapper.spring.web.mockmvc.fakedomain;

import nl._42.heph.AbstractBuildCommand;

public interface FakeBuildCommand extends AbstractBuildCommand<Fake, FakeRepository> {

    @Override
    default Fake findEntity(Fake entity) {
        return getRepository().findByName(entity.getName()).orElse(null);
    }

    FakeBuildCommand withName(String name);

    FakeBuildCommand withId(Long id);


}
