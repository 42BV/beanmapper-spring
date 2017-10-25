package io.beanmapper.spring.flusher;

import java.util.ArrayList;
import java.util.List;

import io.beanmapper.annotations.BeanCollection;

public class CollSource {

    @BeanCollection(elementType = CollTarget.class, flushAfterClear = true)
    public List<String> items = new ArrayList<>();

}
