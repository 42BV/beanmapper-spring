package io.beanmapper.spring.web.mockmvc.fakedomain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FakeRepository extends JpaRepository<Fake, Long> {
}
