package io.beanmapper.spring.flusher;

import java.util.ArrayList;
import java.util.List;

import io.beanmapper.annotations.BeanCollection;
import io.beanmapper.utils.Trinary;

public class CollSource {

    @BeanCollection(elementType = CollTarget.class, flushAfterClear = Trinary.ENABLED)
    public List<String> items = new ArrayList<>();

}
