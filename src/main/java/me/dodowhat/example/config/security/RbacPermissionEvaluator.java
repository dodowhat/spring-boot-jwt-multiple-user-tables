package me.dodowhat.example.config.security;

import org.casbin.jcasbin.main.Enforcer;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component("RbacPermissionEvaluator")
public class RbacPermissionEvaluator {
    private final Enforcer enforcer;

    public RbacPermissionEvaluator(Enforcer enforcer) {
        this.enforcer = enforcer;
    }

    public boolean check(Authentication authentication, HttpServletRequest request) {
        return enforcer.enforce(
                authentication.getName(),
                request.getRequestURI(),
                request.getMethod());
    }

}
