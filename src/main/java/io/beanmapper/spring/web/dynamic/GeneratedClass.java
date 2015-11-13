/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.spring.web.dynamic;

import javassist.CannotCompileException;
import javassist.CtClass;

class GeneratedClass {
    
    public final CtClass ctClass;
    
    public final Class<?> generatedClass;
    
    public GeneratedClass(CtClass ctClass) throws CannotCompileException {
        this.ctClass = ctClass;
        this.generatedClass = ctClass.toClass();
    }
    
}