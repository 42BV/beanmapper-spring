package io.beanmapper.spring.web;

import javax.persistence.EntityNotFoundException;

/**
 * Provides a generic interface for custom entity finders. The entity finder is
 * used within MergedFormMethodArgumentResolver to fetch the entity.
 * @author Robert Bor
 */
public interface EntityFinder {

    /**
     * Returns the entity on the basis of the entity class and its ID.
     * @param entityClass the class of the entity
     * @param id the ID of the entity
     * @return the entity if found
     * @throws javax.persistence.EntityNotFoundException if the repository or the entity
     *         could not be found
     */
    Object find(Long id, Class entityClass) throws EntityNotFoundException;

}
