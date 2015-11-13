# beanmapper-spring

@Override
public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
    argumentResolvers.add(new FormMethodArgumentResolver(
            Collections.singletonList(mappingJackson2HttpMessageConverter()),
            beanMapper,
            applicationContext
    ));
}

@RequestMapping(value = "/bla/{id}", method = RequestMethod.PUT)
@ResponseBody
public Temp update(@Form(formClass = TempForm.class, mergeId = "id") Temp tempEntity) {
    return tempEntity;
}
