package io.beanmapper.spring.web;

import org.springframework.context.ApplicationContext;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.support.Repositories;

import javax.persistence.EntityNotFoundException;

/**
 * The default implementation is based on the Repositories class of Spring Data. It
 * first looks up the repository. If found, it then calls findOne on the repository.
 * @author Robert Bor
 */
public class SpringDataEntityFinder implements EntityFinder {

    private final Repositories repositories;

    public SpringDataEntityFinder(ApplicationContext applicationContext) {
        this.repositories = new Repositories(applicationContext);
    }

    @Override
    public Object find(Long id, Class entityClass) throws EntityNotFoundException {
        CrudRepository<?, Long> repository = (CrudRepository<?, Long>) repositories.getRepositoryFor(entityClass);
        if (repository == null) {
            throw new EntityNotFoundException(
                    "No repository found for " + entityClass.getName());
        }
        Object entity = repository.findOne(id);
        if (entity == null) {
            throw new EntityNotFoundException(
                    "Entity with ID " + id + "was not found in repository " + repository.getClass().getName());
        }
        return entity;
    }

}
