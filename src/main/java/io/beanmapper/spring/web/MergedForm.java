/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.spring.web;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Capable of reading an input form and mapping this
 * into an existing/new entity. The entity is declared 
 * as parameter in our handler mapping method.
 *
 * @since Nov 15, 2015
 */
@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface MergedForm {
    
    /**
     * Type of input form.
     * @return the input form class
     */
    Class<?> value();
    
    /**
     * When patch, we only map the provided properties
     * from our input form to the entity.
     * @return the patch
     */
    boolean patch() default false;
    
    /**
     * Entity identifier variable in our path mapping.
     * @return the identifier variable
     */
    String mergeId() default "";

    /**
     * Class types of the before-/afterMerge instances maintained in MergePair
     */
    Class<?> mergePairClass() default Object.class;

}
