/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.spring.web.converter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Set;

import io.beanmapper.spring.util.JsonUtil;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.google.common.io.CharStreams;

/**
 * Wraps the default Jackson2 message converter with read
 * functionality that returns both the message and property
 * names.
 *
 * @author Jeroen van Schagen
 * @since Nov 25, 2015
 */
public class StructuredJsonMessageConverter implements HttpMessageConverter<Object> {
    
    private final MappingJackson2HttpMessageConverter delegate;
    
    public StructuredJsonMessageConverter(MappingJackson2HttpMessageConverter delegate) {
        this.delegate = delegate;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return delegate.canRead(clazz, mediaType);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return delegate.canWrite(clazz, mediaType);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return delegate.getSupportedMediaTypes();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object read(Class<? extends Object> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        String json = CharStreams.toString(new InputStreamReader(inputMessage.getBody()));
        Object body = delegate.read(clazz, new StringHttpInputMessage(inputMessage.getHeaders(), json));
        Set<String> propertyNames = JsonUtil.getPropertyNamesFromJson(json, delegate.getObjectMapper());
        return new StructuredBody(body, propertyNames);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void write(Object t, MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        delegate.write(t, contentType, outputMessage);
    }
    
    private static class StringHttpInputMessage implements HttpInputMessage {
        
        private final HttpHeaders headers;
        
        private final String content;
        
        public StringHttpInputMessage(HttpHeaders headers, String content) {
            this.headers = headers;
            this.content = content;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public HttpHeaders getHeaders() {
            return headers;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public InputStream getBody() throws IOException {
            return new ByteArrayInputStream(content.getBytes());
        }
        
    }

}
