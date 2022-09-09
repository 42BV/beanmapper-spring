package io.beanmapper.spring.web.mockmvc.fakedomain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FakeRepository extends JpaRepository<Fake, Long> {

    Optional<Fake> findByName(String name);

}
