[![Build Status](https://github.com/42BV/beanmapper-spring/workflows/Java%20CI%20with%20Maven/badge.svg)](https://github.com/42BV/beanmapper-spring/actions?query=workflow%3A%22Java+CI+with+Maven%22)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/e2784c9a7592423390df7888a9dc30e0)](https://www.codacy.com/gh/42BV/beanmapper-spring/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=42BV/beanmapper-spring&amp;utm_campaign=Badge_Grade)
[![BCH compliance](https://bettercodehub.com/edge/badge/42BV/beanmapper-spring?branch=master)](https://bettercodehub.com/)
[![codecov](https://codecov.io/gh/42BV/beanmapper-spring/branch/master/graph/badge.svg)](https://codecov.io/gh/42BV/beanmapper-spring)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.beanmapper/beanmapper-spring/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.beanmapper/beanmapper-spring)
[![Javadocs](https://www.javadoc.io/badge/io.beanmapper/beanmapper-spring.svg)](https://www.javadoc.io/doc/io.beanmapper/beanmapper-spring)
[![Apache 2](http://img.shields.io/badge/license-Apache%202-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)

# BeanMapper Spring

## Input merging argument resolver

### Configuration

```java
@Override
public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
    argumentResolvers.add(new MergeFormMethodArgumentResolver(
            Collections.singletonList(mappingJackson2HttpMessageConverter()),
            beanMapper,
            applicationContext,
            entityManager
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

## License

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
