package io.beanmapper.spring.web;

import io.beanmapper.BeanMapper;
import io.beanmapper.core.rule.MappableFields;
import io.beanmapper.spring.Lazy;
import io.beanmapper.spring.util.JsonUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodArgumentResolver;

public class MergedFormMethodArgumentResolver extends AbstractMessageConverterMethodArgumentResolver {

    private final BeanMapper beanMapper;

    private final Repositories repositories;

    public MergedFormMethodArgumentResolver(List<HttpMessageConverter<?>> messageConverters,
                                            BeanMapper beanMapper, ApplicationContext applicationContext) {
        super(messageConverters);
        this.beanMapper = beanMapper;
        this.repositories = new Repositories(applicationContext);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(MergedForm.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        MergedForm annotation = parameter.getParameterAnnotation(MergedForm.class);
        Class<?> parameterType = parameter.getParameterType();
        Long id = resolveId(webRequest, annotation.mergeId());
        Object form = readWithMessageConverters(webRequest, parameter, annotation.value());

        if (Lazy.class.isAssignableFrom(parameterType)) {
            ParameterizedType genericType = (ParameterizedType) parameter.getGenericParameterType();
            Type entityType = genericType.getActualTypeArguments()[0];
            return new LazyResolveEntity(form, id, (Class<?>) entityType, annotation);
        } else {
            return resolveEntity(form, id, parameterType, annotation);
        }
    }

    private Long resolveId(NativeWebRequest webRequest, String mergeId) {
        if (StringUtils.isEmpty(mergeId)) {
            return null;
        }

        Map<String, String> uriTemplateVars = getUriTemplateVars(webRequest);
        return uriTemplateVars != null ? Long.valueOf(uriTemplateVars.get(mergeId)) : null;
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> getUriTemplateVars(NativeWebRequest webRequest) {
        return (Map<String, String>) webRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
    }
    
    @SuppressWarnings("unchecked")
    private Object resolveEntity(Object form, Long id, Class<?> entityClass, MergedForm annotation) throws Exception {

        if (id == null) {
            return beanMapper.map(form, entityClass);
        } else {
            Object entity = ((CrudRepository<?, Long>) repositories.getRepositoryFor(entityClass)).findOne(id);
            if (annotation.patch()) {
                Set<String> propertyNames = JsonUtil.getPropertyNamesFromBody(form);
                return beanMapper.map(form, entity, new MappableFields(propertyNames));
            } else {
                return beanMapper.map(form, entity);
            }
        }
    }

    private class LazyResolveEntity implements Lazy<Object> {

        private final Object form;

        private final Long id;
        
        private final Class<?> entityClass;
        
        private final MergedForm annotation;
        
        public LazyResolveEntity(Object form, Long id, Class<?> entityClass, MergedForm annotation) {
            this.form = form;
            this.id = id;
            this.entityClass = entityClass;
            this.annotation = annotation;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public Object get() {
            try {
                return resolveEntity(form, id, entityClass, annotation);
            } catch (Exception e) {
                throw new IllegalStateException("Could not map entity from request body.", e);
            }
        }
        
    }

}
