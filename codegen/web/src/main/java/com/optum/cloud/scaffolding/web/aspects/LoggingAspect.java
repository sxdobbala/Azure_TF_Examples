package com.optum.cloud.scaffolding.web.aspects;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    @Around("within(com.optum.cloud.scaffolding..*)")
    public Object logMethodSurround(final ProceedingJoinPoint joinPoint) throws Throwable {
        final Log logger = LogFactory.getLog(joinPoint.getTarget().getClass());

        final StopWatch stopWatch = new StopWatch();

        logger.debug(String.format("%s - entering", joinPoint.getSignature().getName()));
        try {
            stopWatch.start();
            return joinPoint.proceed();
        } finally {
            stopWatch.stop();
            logger.debug(String.format("%s - leaving", joinPoint.getSignature().getName()));
            logger.info(String.format("%s - Total Execution Time: %s", joinPoint.getSignature().getName(), stopWatch.toString()));
        }
    }
}
