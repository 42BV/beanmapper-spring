package io.beanmapper.spring.web.mockmvc;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityNotFoundException;

import io.beanmapper.exceptions.BeanConstructException;
import io.beanmapper.spring.exceptions.ClassExpectationNotRegisteredException;
import io.beanmapper.spring.web.EntityFinder;

import org.springframework.data.domain.Persistable;
import org.springframework.data.repository.CrudRepository;

public class MockEntityFinder implements EntityFinder {

    private final Map<Class<?>, CrudRepository<? extends Persistable<Long>, Long>> repositories = new HashMap<>();

    @Override
    public <T> T find(Long id, Class<T> entityClass) throws EntityNotFoundException, BeanConstructException {
        CrudRepository<T, Long> repository = (CrudRepository<T, Long>)repositories.get(entityClass);
        if (repository == null) {
            throw new BeanConstructException(entityClass, new ClassExpectationNotRegisteredException("No constructor found for " + entityClass.getSimpleName() +
                    ". Make sure to register the class in addConverters.registerExpectation."));
        }
        return repository.findById(id).orElse(null);
    }

    @Override
    public <T> T findAndDetach(Long id, Class<T> entityClass) throws EntityNotFoundException, BeanConstructException {
        // Not supported, same as find
        return find(id, entityClass);
    }

    public void addRepository(CrudRepository<? extends Persistable<Long>, Long> repository, Class<?> entityClass) {
        repositories.put(entityClass, repository);
    }

}
