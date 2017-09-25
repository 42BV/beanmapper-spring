package io.beanmapper.spring.web;

import io.beanmapper.BeanMapper;

public class MergePair<T> {

    private T beforeMerge;
    private T afterMerge;
    private final BeanMapper beanMapper;
    private final EntityFinder entityFinder;
    private final Class<?> entityClass;
    private final MergedForm annotation;

    public MergePair(BeanMapper beanMapper, EntityFinder entityFinder, Class<?> entityClass, MergedForm annotation) {
        this.beanMapper = beanMapper;
        this.entityFinder = entityFinder;
        this.entityClass = entityClass;
        this.annotation = annotation;
    }

    public void initNew(Object source) {
        setAfterMerge(beanMapper.map(source, targetEntityClass()));
    }

    public void merge(BeanMapper customBeanMapper, Object source, Long id) {
        T target = (T)findSource(id);
        setAfterMerge(customBeanMapper.map(source, target));
    }

    public T getBeforeMerge() {
        return beforeMerge;
    }

    public T getAfterMerge() {
        return afterMerge;
    }

    public Object result() {
        return isMergePair() ? this : getAfterMerge();
    }

    private void createBeforeMerge(Object source) {
        setBeforeMerge(beanMapper.map(source, targetEntityClass()));
    }

    private Object findSource(Long id) {
        Object entity = entityFinder.find(id, targetEntityClass());
        if (isMergePair()) {
            createBeforeMerge(entity);
        }
        return entity;
    }

    private boolean isMergePair() {
        return entityClass.equals(MergePair.class);
    }

    private Class<T> targetEntityClass() {
        return isMergePair() ? (Class<T>)annotation.mergePairClass() : (Class<T>)entityClass;
    }

    private void setBeforeMerge(T beforeMerge) {
        this.beforeMerge = beforeMerge;
    }

    private void setAfterMerge(T afterMerge) {
        this.afterMerge = afterMerge;
    }

}
