package edu.ucsal.fiadopay.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import edu.ucsal.fiadopay.annotations.LoggedOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
@Component
public class LoggingAspect {
    private final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("@annotation(edu.ucsal.fiadopay.annotations.LoggedOperation)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        MethodSignature sig = (MethodSignature) pjp.getSignature();
        LoggedOperation ann = sig.getMethod().getAnnotation(LoggedOperation.class);
        String name = ann != null && !ann.value().isEmpty() ? ann.value() : sig.getMethod().getName();
        log.info("START op={} args={}", name, pjp.getArgs());
        try {
            Object result = pjp.proceed();
            long dur = System.currentTimeMillis() - start;
            log.info("END op={} durationMs={}", name, dur);
            return result;
        } catch (Throwable t) {
            long dur = System.currentTimeMillis() - start;
            log.error("ERROR op={} durationMs={} ex={}", name, dur, t.toString());
            throw t;
        }
    }
}
