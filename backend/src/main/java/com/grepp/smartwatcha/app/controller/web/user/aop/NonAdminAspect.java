package com.grepp.smartwatcha.app.controller.web.user.aop;

import com.grepp.smartwatcha.app.controller.web.user.annotation.NonAdmin;
import com.grepp.smartwatcha.app.model.auth.CustomUserDetails;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class NonAdminAspect {

    @Around("@annotation(com.grepp.smartwatcha.app.controller.web.user.annotation.NonAdmin)")
    public Object checkNonAdmin(ProceedingJoinPoint joinPoint) throws Throwable {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            if (userDetails.getRole() != null && userDetails.getRole().name().equals("ADMIN")) {
                return "redirect:/admin";
            }
        }
        return joinPoint.proceed();
    }
} 