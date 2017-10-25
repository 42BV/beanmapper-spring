package io.beanmapper.spring.flusher;

import javax.persistence.EntityManager;

import io.beanmapper.config.AfterClearFlusher;

/**
 * Specific AfterClearFlusher for flushing JPA's EntityManager. This is called by
 * BeanMapper after calling clear on a collection.
 */
public class JpaAfterClearFlusher implements AfterClearFlusher {

    private final EntityManager entityManager;

    public JpaAfterClearFlusher(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void flush() {
        entityManager.flush();
    }

}
