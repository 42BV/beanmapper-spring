package io.beanmapper.spring.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public abstract class AbstractSpringSecuredCheck {

    protected Object getPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getPrincipal();
    }

    protected UserDetails getUserDetails() {
        Object principal = getPrincipal();
        if (!(getPrincipal() instanceof UserDetails)) {
            throw new PrincipalIsNoInstanceOfUserDetailsException();
        }
        return (UserDetails)principal;
    }

    public boolean hasRole(String... roles) {
        if (hasNoRequiredRoles(roles)) {
            return true;
        }
        UserDetails userDetails = getUserDetails();
        for (String role : roles) {
            for (GrantedAuthority authority : userDetails.getAuthorities()) {
                if (hasRole(authority, role)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasRole(GrantedAuthority authority, String role) {
        String prefixedRole = "ROLE_" + role;
        return  prefixedRole.equals(authority.getAuthority()) ||
                role.equals(authority.getAuthority());
    }

    private boolean hasNoRequiredRoles(String[] roles) {
        return roles.length == 0;
    }

}
