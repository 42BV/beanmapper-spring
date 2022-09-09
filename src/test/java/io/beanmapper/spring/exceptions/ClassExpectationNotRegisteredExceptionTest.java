package io.beanmapper.spring.exceptions;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ClassExpectationNotRegisteredExceptionTest {

    @Test
    void classExpectationNotRegisteredExceptionTest() {
        assertThrows(ClassExpectationNotRegisteredException.class, () -> {throw new ClassExpectationNotRegisteredException("Test");});
    }

}
