package io.beanmapper.spring.web;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;

import org.springframework.context.ApplicationContext;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.support.Repositories;

/**
 * The default implementation is based on the Repositories class of Spring Data. It
 * first looks up the repository. If found, it then calls findOne on the repository.
 * @author Robert Bor
 */
public class SpringDataEntityFinder implements EntityFinder {

    private final Repositories repositories;

    private final EntityManager entityManager;

    public SpringDataEntityFinder(ApplicationContext applicationContext, EntityManager entityManager) {
        this.repositories = new Repositories(applicationContext);
        this.entityManager = entityManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T findAndDetach(Long id, Class<T> entityClass) throws EntityNotFoundException {
        T entity = find(id, entityClass);
        entityManager.detach(entity);
        return entity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T find(Long id, Class<T> entityClass) throws EntityNotFoundException {
        CrudRepository<T, Long> repository =
                (CrudRepository<T, Long>) repositories.getRepositoryFor(entityClass)
                .orElseThrow(() -> new EntityNotFoundException("No repository found for " + entityClass.getName())
        );

        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                "Entity with ID " + id + "was not found in repository " + repository.getClass().getName())
        );
    }

}
