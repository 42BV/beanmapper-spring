package io.beanmapper.spring.web.mockmvc;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityNotFoundException;

import io.beanmapper.spring.web.EntityFinder;

import org.springframework.data.domain.Persistable;
import org.springframework.data.repository.CrudRepository;

public class MockEntityFinder implements EntityFinder {

    private Map<Class<?>, CrudRepository<? extends Persistable, Long>> repositories = new HashMap<>();

    @Override
    public Object find(Long id, Class<?> entityClass) throws EntityNotFoundException {
        CrudRepository<? extends Persistable, Long> repository = repositories.get(entityClass);
        if (repository == null) {
            throw new RuntimeException("No constructor found for " + entityClass.getSimpleName() +
                    ". Make sure to register the class in addConverters.registerExpectation");
        }
        return repository.findById(id).orElse(null);
    }

    public void addRepository(CrudRepository<? extends Persistable, Long> repository, Class<?> entityClass) {
        repositories.put(entityClass, repository);
    }

}
