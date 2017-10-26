package io.beanmapper.spring.web;

import org.springframework.core.Conventions;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

public class WebRequestParameters {

    private final MethodParameter parameter;
    private final ModelAndViewContainer mavContainer;
    private final NativeWebRequest webRequest;
    private final WebDataBinderFactory binderFactory;

    public WebRequestParameters(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) {
        this.parameter = parameter;
        this.mavContainer = mavContainer;
        this.webRequest = webRequest;
        this.binderFactory = binderFactory;
    }

    public WebDataBinder createBinder(Object objectToValidate) throws Exception {
        String name = Conventions.getVariableNameForParameter(parameter);
        return binderFactory.createBinder(webRequest, objectToValidate, name);
    }

    public void setBindingResult(BindingResult bindingResult) {
        String name = Conventions.getVariableNameForParameter(parameter);
        mavContainer.addAttribute(BindingResult.MODEL_KEY_PREFIX + name, bindingResult);
    }

    public MethodParameter getParameter() {
        return parameter;
    }

}
