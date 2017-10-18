package io.beanmapper.spring.web;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.beanmapper.BeanMapper;
import io.beanmapper.spring.Lazy;
import io.beanmapper.spring.web.converter.StructuredBody;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Conventions;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestPartMethodArgumentResolver;

public class MergedFormMethodArgumentResolver extends AbstractMessageConverterMethodArgumentResolver {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final BeanMapper beanMapper;

    private final EntityFinder entityFinder;

    private final RequestPartMethodArgumentResolver multiPartResolver;


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
        this.multiPartResolver = new RequestPartMethodArgumentResolver(messageConverters);
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

        final Object form;
        if (annotation.multiPart().length() > 0) {
            form = readFromMultiPartForm(parameter, mavContainer, webRequest, binderFactory, annotation);
        } else {
            form = readWithMessageConverters(webRequest, parameter, annotation.value());
        }

        // Check for @Valid on the mapped target and apply the validation rules to form
        validateObject(parameter, mavContainer, webRequest, binderFactory, getBody(form));

        if (Lazy.class.isAssignableFrom(parameterType)) {
            ParameterizedType genericType = (ParameterizedType) parameter.getGenericParameterType();
            Type entityType = genericType.getActualTypeArguments()[0];
            return new LazyResolveEntity(form, id, (Class<?>) entityType, annotation);
        } else {
            Object mappedTarget = resolveEntity(form, id, parameterType, annotation);
            // Check for @Valid on the mapped target and apply the validation rules to the mapped target
            validateObject(parameter, mavContainer, webRequest, binderFactory, mappedTarget);
            return mappedTarget;
        }
    }

    private Object readFromMultiPartForm(
            MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory,
            MergedForm annotation) throws Exception {
        MethodParameter formParameter = new MethodParameter(parameter);
        Field f1 = formParameter.getClass().getDeclaredField("parameterType");
        f1.setAccessible(true);
        f1.set(formParameter, annotation.value());
        try {
            Field f2 = formParameter.getClass().getDeclaredField("genericParameterType");
            f2.setAccessible(true);
            f2.set(formParameter, annotation.value());
        } catch (NoSuchFieldException err) {
            logger.warn("Older Spring version? Update to at least 4.3.10.RELEASE, see https://github.com/42BV/beanmapper-spring/issues/19");
        }
        return multiPartResolver.resolveArgument(formParameter, mavContainer, webRequest, binderFactory);
    }

    private void validateObject(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory, Object objectToValidate) throws Exception {
        String name = Conventions.getVariableNameForParameter(parameter);
        WebDataBinder binder = binderFactory.createBinder(webRequest, objectToValidate, name);
        if (objectToValidate != null) {
            validateIfApplicable(binder, parameter);
            if (binder.getBindingResult().hasErrors() && isBindExceptionRequired(binder, parameter)) {
                throw new MethodArgumentNotValidException(parameter, binder.getBindingResult());
            }
        }
        mavContainer.addAttribute(BindingResult.MODEL_KEY_PREFIX + name, binder.getBindingResult());
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

    private Object getBody(Object form) {
        if (form == null) return null;
        return form instanceof StructuredBody ? ((StructuredBody) form).getBody() : form;
    }

    private Set<String> getPropertyNames(Object form) {
        if (form == null) return null;
        return form instanceof StructuredBody ? ((StructuredBody) form).getPropertyNames() : null;
    }

    private Object resolveEntity(Object form, Long id, Class<?> entityClass, MergedForm annotation) {
        Object data = getBody(form);
        Set<String> propertyNames = getPropertyNames(form);
        final MergePair mergePair = new MergePair(beanMapper, entityFinder, entityClass, annotation);

        if (id == null) {
            // Create a new entity using our form data
            mergePair.initNew(data);
        } else {
            // Map our input form on the already persisted entity
            BeanMapper customBeanMapper = beanMapper;
            if (annotation.patch() && propertyNames != null) {
                customBeanMapper = beanMapper
                        .wrapConfig()
                        .downsizeSource(new ArrayList<>(propertyNames))
                        .build();
            }
            mergePair.merge(customBeanMapper, data, id);
        }
        return mergePair.result();
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
