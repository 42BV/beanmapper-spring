package io.beanmapper.spring.security;

import io.beanmapper.config.SecuredPropertyHandler;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SpringSecuredPropertyHandler implements SecuredPropertyHandler {

    @Override
    public boolean hasRole(String... roles) {
        if (roles.length == 0) {
            return true;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails)authentication.getPrincipal();
            for (String role : roles) {
                String prefixedRole = "ROLE_" + role;
                for (GrantedAuthority authority : userDetails.getAuthorities()) {
                    if (    prefixedRole.equals(authority.getAuthority()) ||
                            role.equals(authority.getAuthority())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
