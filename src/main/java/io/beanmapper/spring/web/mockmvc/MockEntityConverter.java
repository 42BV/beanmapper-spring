package io.beanmapper.spring.web.mockmvc;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Persistable;
import org.springframework.data.repository.CrudRepository;

public class MockEntityConverter<T extends Persistable> implements Converter<String, T> {

    private final CrudRepository<T, Long> repository;

    public MockEntityConverter(CrudRepository<T, Long> respository) {
        this.repository = respository;
    }

    @Override
    public T convert(String id) {
        return repository.findById(Long.valueOf(id)).orElse(null);
    }

}
