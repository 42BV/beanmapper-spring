package io.beanmapper.spring.web;

import io.beanmapper.BeanMapper;
import io.beanmapper.spring.Lazy;
import io.beanmapper.spring.web.converter.StructuredBody;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
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

    private final EntityFinder entityFinder;

    public MergedFormMethodArgumentResolver(List<HttpMessageConverter<?>> messageConverters,
                                            BeanMapper beanMapper, 
                                            ApplicationContext applicationContext) {
        this(messageConverters, beanMapper, new SpringDataEntityFinder(applicationContext));
    }

    public MergedFormMethodArgumentResolver(List<HttpMessageConverter<?>> messageConverters,
                                            BeanMapper beanMapper, 
                                            EntityFinder entityFinder) {
        super(messageConverters);
        this.beanMapper = beanMapper;
        this.entityFinder = entityFinder;
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

        // First check the URI variables (ie, comparable to @PathVariable)
        Map<String, String> uriTemplateVars = getUriTemplateVars(webRequest);
        String mergeIdValue = uriTemplateVars != null ? uriTemplateVars.get(mergeId) : null;

        // If the mergeIdValue was not found, check the query parameters
        if (mergeIdValue == null) {
            mergeIdValue = webRequest.getParameter(mergeId);
        }

        return mergeIdValue != null ? Long.valueOf(mergeIdValue) : null;
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> getUriTemplateVars(NativeWebRequest webRequest) {
        return (Map<String, String>) webRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
    }
    
    private Object resolveEntity(Object form, Long id, Class<?> entityClass, MergedForm annotation) {
        Object data = form;
        Set<String> propertyNames = null;
        if (form instanceof StructuredBody) {
            propertyNames = ((StructuredBody) form).getPropertyNames();
            data = ((StructuredBody) form).getBody();
        }

        if (id == null) {
            // Create a new entity using our form data
            return beanMapper.map(data, entityClass);
        } else {
            // Map our input form on the already persisted entity
            Object entity = entityFinder.find(id, entityClass);
            if (annotation.patch() && propertyNames != null) {
                return beanMapper
                        .wrapConfig().downsizeSource(new ArrayList<>(propertyNames))
                        .build()
                        .map(data, entity);
            } else {
                return beanMapper.map(data, entity);
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
            return resolveEntity(form, id, entityClass, annotation);
        }
        
    }

}
