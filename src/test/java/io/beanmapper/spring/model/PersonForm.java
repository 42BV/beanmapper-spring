package io.beanmapper.spring.model;

import java.util.List;

import io.beanmapper.annotations.BeanCollection;

public class PersonForm {
    
    public String name;
    
    public String city;

    @BeanCollection(elementType = Tag.class)
    public List<String> tags;

}
