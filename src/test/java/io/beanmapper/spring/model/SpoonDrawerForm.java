package io.beanmapper.spring.model;

import java.util.List;

import io.beanmapper.annotations.BeanCollection;

public class SpoonDrawerForm {

    @BeanCollection(elementType = Spoon.class)
    public List<SpoonForm> spoons;

    public static class SpoonForm {
        public Long id;
        public Boolean polished;
    }
}
