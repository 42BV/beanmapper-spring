package io.beanmapper.spring.web;

import io.beanmapper.BeanMapper;
import io.beanmapper.spring.Lazy;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodArgumentResolver;

public class MergedFormMethodArgumentResolver extends AbstractMessageConverterMethodArgumentResolver {

    private final BeanMapper beanMapper;

    private final Repositories repositories;

    public MergedFormMethodArgumentResolver(List<HttpMessageConverter<?>> messageConverters, BeanMapper beanMapper, ApplicationContext applicationContext) {
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

        MergedForm form = parameter.getParameterAnnotation(MergedForm.class);
        Object input = readWithMessageConverters(webRequest, parameter, form.value());
        Long id = resolveId(webRequest, form.mergeId());

        Class<?> parameterType = parameter.getParameterType();
        if (Lazy.class.isAssignableFrom(parameterType)) {
            ParameterizedType a = (ParameterizedType) parameter.getGenericParameterType();
            String typeName = a.getActualTypeArguments()[0].getTypeName();
            return new LazyResolveEntity(input, id, Class.forName(typeName));
        } else {
            return resolveEntity(input, id, parameterType);
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
    private Object resolveEntity(Object input, Long id, Class<?> entityClass) {
        if (id == null) {
            return beanMapper.map(input, entityClass);
        } else {
            CrudRepository<?, Long> repository = (CrudRepository<?, Long>) repositories.getRepositoryFor(entityClass);
            return beanMapper.map(input, repository.findOne(id));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected <T> Object readWithMessageConverters(NativeWebRequest webRequest, MethodParameter methodParam, Type paramType) throws IOException,
            HttpMediaTypeNotSupportedException {

        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        HttpInputMessage inputMessage = new ServletServerHttpRequest(servletRequest);

        InputStream inputStream = inputMessage.getBody();
        if (inputStream == null) {
            return handleEmptyBody(methodParam);
        } else if (inputStream.markSupported()) {
            inputStream.mark(1);
            if (inputStream.read() == -1) {
                return handleEmptyBody(methodParam);
            }
            inputStream.reset();
        } else {
            final PushbackInputStream pushbackInputStream = new PushbackInputStream(inputStream);
            int b = pushbackInputStream.read();
            if (b == -1) {
                return handleEmptyBody(methodParam);
            } else {
                pushbackInputStream.unread(b);
            }
            inputMessage = new PushbackServletServerHttpRequest(servletRequest, pushbackInputStream);
        }

        return super.readWithMessageConverters(inputMessage, methodParam, paramType);
    }

    private Object handleEmptyBody(MethodParameter param) {
        if (param.getParameterAnnotation(RequestBody.class).required()) {
            throw new HttpMessageNotReadableException("Required request body content is missing: " + param);
        }
        return null;
    }
    
    private static class PushbackServletServerHttpRequest extends ServletServerHttpRequest {
        
        private final InputStream body;

        public PushbackServletServerHttpRequest(HttpServletRequest servletRequest, InputStream body) {
            super(servletRequest);
            this.body = body;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public InputStream getBody() throws IOException {
            return body;
        }
        
    }

    private class LazyResolveEntity implements Lazy<Object> {
        
        private final Object input;
        
        private final Long id;
        
        private final Class<?> entityClass;
        
        public LazyResolveEntity(Object input, Long id, Class<?> entityClass) {
            this.input = input;
            this.id = id;
            this.entityClass = entityClass;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public Object get() {
            return resolveEntity(input, id, entityClass);
        }
        
    }

}
