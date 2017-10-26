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
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.StringUtils;
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

        WebRequestParameters webRequestParameters = new WebRequestParameters(parameter, mavContainer, webRequest, binderFactory);

        // Check for @Valid on the mapped target and apply the validation rules to form
        validateObject(webRequestParameters, getBody(form));

        if (Lazy.class.isAssignableFrom(parameterType)) {
            ParameterizedType genericType = (ParameterizedType) parameter.getGenericParameterType();
            Type entityType = genericType.getActualTypeArguments()[0];
            return new LazyResolveEntity(form, id, (Class<?>) entityType, annotation, webRequestParameters);
        } else {
            return resolveEntity(form, id, parameterType, annotation, webRequestParameters);
        }
    }

    private Object readFromMultiPartForm(
            MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory,
            MergedForm annotation) throws Exception {
        MethodParameter formParameter = new MethodParameter(parameter);
        setMethodParameterField(formParameter, "parameterType", annotation.value());
        setMethodParameterField(formParameter, "genericParameterType", annotation.value());
        setMethodParameterField(formParameter, "parameterName", annotation.multiPart());
        setMethodParameterField(formParameter, "parameterNameDiscoverer", null);
        return multiPartResolver.resolveArgument(formParameter, mavContainer, webRequest, binderFactory);
    }

    private void setMethodParameterField(MethodParameter formParameter, String fieldName, Object value) throws IllegalAccessException {
        try {
            Field f = formParameter.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(formParameter, value);
        } catch (NoSuchFieldException err) {
            logger.warn("Older Spring version? Update to at least 4.3.10.RELEASE, see https://github.com/42BV/beanmapper-spring/issues/19");
        }
    }

    private void validateObject(WebRequestParameters webRequestParameters, Object objectToValidate) throws Exception {
        WebDataBinder binder = webRequestParameters.createBinder(objectToValidate);
        if (objectToValidate != null) {
            validateIfApplicable(binder, webRequestParameters.getParameter());
            if (binder.getBindingResult().hasErrors() && isBindExceptionRequired(binder, webRequestParameters.getParameter())) {
                throw new MethodArgumentNotValidException(webRequestParameters.getParameter(), binder.getBindingResult());
            }
        }
        webRequestParameters.setBindingResult(binder.getBindingResult());
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

    private Object resolveEntity(
            Object form, Long id, Class<?> entityClass, MergedForm annotation,
            WebRequestParameters webRequestParameters) throws Exception {
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
        Object mappedTarget = mergePair.result();
        // Check for @Valid on the mapped target and apply the validation rules to the mapped target
        validateObject(webRequestParameters, mappedTarget);
        return mappedTarget;
    }

    private class LazyResolveEntity implements Lazy<Object> {

        private final Object form;
        private final Long id;
        private final Class<?> entityClass;
        private final MergedForm annotation;
        private final WebRequestParameters webRequestParameters;
        
        public LazyResolveEntity(
                Object form, Long id, Class<?> entityClass,
                MergedForm annotation, WebRequestParameters webRequestParameters) {
            this.form = form;
            this.id = id;
            this.entityClass = entityClass;
            this.annotation = annotation;
            this.webRequestParameters = webRequestParameters;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public Object get() throws Exception {
            return resolveEntity(form, id, entityClass, annotation, webRequestParameters);
        }
        
    }

}
