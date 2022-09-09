package io.beanmapper.spring.exceptions;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class PrincipalIsNoInstanceOfUserDetailsExceptionTest {

    @Test
    void testPrincipalIsNoInstanceOfUserDetailsExceptionTest() {
        assertThrows(PrincipalIsNoInstanceOfUserDetailsException.class, () -> { throw new PrincipalIsNoInstanceOfUserDetailsException(); });
    }

}
