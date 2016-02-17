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

### MockMvcBeanMapper

Since mapping to an Entity is done at an early stage, your Spring MVC controller level tests must be configured to deal with the usage of repositories. The MockMvcBeanMapper is configured at the level of your abstract controller test, ie the class your controller tests all extend from.

The reason why you need to do this is because:
* Spring's DomainClassConverter (working on @RequestParam and @PathVariable) makes use of your repositories
* BeanMapper @MergedForm makes use of your repositories
* BeanMapper IdToEntityBeanConverter makes use of your repositories

Each of these vectors need to be addressed to set up controller tests that can deal with repositories.

Assuming you use Spring's MockMvcBuilders and assuming you have a web configuration class called WebMvcConfig, this is what you could do:

```java
public abstract class AbstractControllerTest {

    private WebMvcConfig config = new WebMvcConfig();

    protected MockMvc webClient;

    protected MockMvcBeanMapper mockMvcBeanMapper;

    protected void initWebClient(Object controller) {

        this.mockMvcBeanMapper = new MockMvcBeanMapper(
                new FormattingConversionService(),
                Collections.singletonList(config.mappingJackson2HttpMessageConverter()),
                new ApplicationConfig().beanMapper()
        );

        this.webClient = MockMvcBuilders.standaloneSetup(controller)
                .setMessageConverters(config.mappingJackson2HttpMessageConverter())
                .setCustomArgumentResolvers(mockMvcBeanMapper.createHandlerMethodArgumentResolvers())
                .setConversionService(mockMvcBeanMapper.getConversionService())
                .build();
    }

    public BeanMapper beanMapper() {
        return mockMvcBeanMapper.getBeanMapper();
    }

    public void registerRepository(CrudRepository<? extends BaseEntity, Long> repository, Class<?> entityClass) {
        mockMvcBeanMapper.registerRepository(repository, entityClass);
    }

}
```

In your controller test, you will have to register all the repositories (presumably mock classes) that need to be added, ostensibly in a @Before method..

```java
registerRepository(ownerRepository, Owner.class);
```

You can take program your mock repositories as you normally would, for example in JMockit:

```java
new NonStrictExpectations() {{
    ownerRepository.findOne(1138L);
    result = new Owner();
}};
```

