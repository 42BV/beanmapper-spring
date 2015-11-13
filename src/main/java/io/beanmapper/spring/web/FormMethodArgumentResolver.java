package io.beanmapper.spring.web;

import io.beanmapper.BeanMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
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
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodArgumentResolver;

public class FormMethodArgumentResolver extends AbstractMessageConverterMethodArgumentResolver {

    private final BeanMapper beanMapper;

    private final Repositories repositories;

    public FormMethodArgumentResolver(List<HttpMessageConverter<?>> messageConverters, BeanMapper beanMapper,
                                      ApplicationContext applicationContext) {
        super(messageConverters);
        this.beanMapper = beanMapper;
        this.repositories = new Repositories(applicationContext);
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Form.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        Form form = parameter.getParameterAnnotation(Form.class);
        Object formResult = readWithMessageConverters(webRequest, parameter, form.formClass());
        Long id = retrieveMergeId(webRequest, form.mergeId());

        if (id == null) {
            // Create
            return beanMapper.map(formResult, parameter.getParameterType());
        } else {
            // Fetch and merge
            CrudRepository<?, Long> repository = (CrudRepository<?, Long>) repositories.getRepositoryFor(parameter.getParameterType());
            return beanMapper.map(formResult, repository.findOne(id));
        }
    }

    @SuppressWarnings("unchecked")
    private Long retrieveMergeId(NativeWebRequest webRequest, String mergeId) {
        if (mergeId == null || mergeId.length() == 0) {
            return null;
        }
        Map<String, String> uriTemplateVars =
                (Map<String, String>) webRequest.getAttribute(
                        HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
        return uriTemplateVars != null ? Long.valueOf(uriTemplateVars.get(mergeId)) : null;
    }

    @Override
    protected <T> Object readWithMessageConverters(NativeWebRequest webRequest, MethodParameter methodParam,
                                                  Type paramType) throws IOException, HttpMediaTypeNotSupportedException {

        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        HttpInputMessage inputMessage = new ServletServerHttpRequest(servletRequest);

        InputStream inputStream = inputMessage.getBody();
        if (inputStream == null) {
            return handleEmptyBody(methodParam);
        }
        else if (inputStream.markSupported()) {
            inputStream.mark(1);
            if (inputStream.read() == -1) {
                return handleEmptyBody(methodParam);
            }
            inputStream.reset();
        }
        else {
            final PushbackInputStream pushbackInputStream = new PushbackInputStream(inputStream);
            int b = pushbackInputStream.read();
            if (b == -1) {
                return handleEmptyBody(methodParam);
            } else {
                pushbackInputStream.unread(b);
            }
            
            inputMessage = new ServletServerHttpRequest(servletRequest) {
                
                @Override
                public InputStream getBody() {
                    // Form POST should not get here
                    return pushbackInputStream;
                }
                
            };
        }

        return super.readWithMessageConverters(inputMessage, methodParam, paramType);
    }

    private Object handleEmptyBody(MethodParameter param) {
        if (param.getParameterAnnotation(RequestBody.class).required()) {
            throw new HttpMessageNotReadableException("Required request body content is missing: " + param);
        }
        return null;
    }

}
