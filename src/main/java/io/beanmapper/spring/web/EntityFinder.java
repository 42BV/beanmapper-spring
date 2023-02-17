package io.beanmapper.spring.web;

import jakarta.persistence.EntityNotFoundException;

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
     * @throws jakarta.persistence.EntityNotFoundException if the repository or the entity
     *         could not be found
     */
    <T> T find(Long id, Class<T> entityClass) throws EntityNotFoundException;

    /**
     * Returns the entity on the basis of the entity class and its ID bypassing the
     * Hibernate cache
     * @param entityClass the class of the entity
     * @param id the ID of the entity
     * @return the entity if found
     * @throws jakarta.persistence.EntityNotFoundException if the repository or the entity
     *         could not be found
     */
    <T> T findAndDetach(Long id, Class<T> entityClass) throws EntityNotFoundException;

}
