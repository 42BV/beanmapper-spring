# beanmapper-spring

## Input merging argument resolver

### Configuration

```java
@Override
public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
    argumentResolvers.add(new MergeFormMethodArgumentResolver(
            Collections.singletonList(mappingJackson2HttpMessageConverter()),
            beanMapper,
            applicationContext
    ));
}
```

### Usage

```java
@RequestMapping(value = "/bla", method = RequestMethod.POST)
@ResponseBody
public Temp create(@MergeForm(TempForm.class) Temp tempEntity) {
    return tempEntity;
}

@RequestMapping(value = "/bla/{id}", method = RequestMethod.PUT)
@ResponseBody
public Temp update(@MergeForm(value = TempForm.class, id = "id") Temp tempEntity) {
    return tempEntity;
}
```