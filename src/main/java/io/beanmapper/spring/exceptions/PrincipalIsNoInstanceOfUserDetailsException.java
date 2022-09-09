package io.beanmapper.spring.exceptions;

public class PrincipalIsNoInstanceOfUserDetailsException extends RuntimeException {

    private static final String MESSAGE = "The Security Principal is not an instance of UserDetails";

    public PrincipalIsNoInstanceOfUserDetailsException() {
        super(MESSAGE);
    }

}
