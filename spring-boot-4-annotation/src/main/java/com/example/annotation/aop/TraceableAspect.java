package com.example.annotation.aop;

import com.example.annotation.Traceable;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TraceableAspect {

    private static final Logger log = LoggerFactory.getLogger(TraceableAspect.class);

    @Around("@annotation(traceable) || @within(traceable)")
    public Object around(ProceedingJoinPoint joinPoint, Traceable traceable) throws Throwable {
        Traceable resolved = traceable;
        if (resolved == null) {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            resolved = AnnotationUtils.findAnnotation(signature.getMethod(), Traceable.class);
            if (resolved == null) {
                resolved = AnnotationUtils.findAnnotation(signature.getDeclaringType(), Traceable.class);
            }
        }

        String operation = (resolved == null || resolved.value().isBlank())
                ? joinPoint.getSignature().toShortString()
                : resolved.value();

        long start = System.currentTimeMillis();
        log.info("[trace] start: {}", operation);
        try {
            Object result = joinPoint.proceed();
            long durationMs = System.currentTimeMillis() - start;
            log.info("[trace] end: {} ({} ms)", operation, durationMs);
            return result;
        } catch (Throwable ex) {
            long durationMs = System.currentTimeMillis() - start;
            log.warn("[trace] error: {} ({} ms)", operation, durationMs, ex);
            throw ex;
        }
    }
}
