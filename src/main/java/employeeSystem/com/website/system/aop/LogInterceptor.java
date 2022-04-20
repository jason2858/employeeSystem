package com.yesee.gov.website.aop;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Service;

@Aspect
@Service
public class LogInterceptor {

    private static final Logger logger = LogManager.getLogger(LogInterceptor.class);
    Exception exc = null;

    /*
     * @Before("execution(* com..*(..))")
     * public void invoke(JoinPoint joinPoint) throws Throwable {
     * logger.info("class: " + joinPoint.getTarget().getClass().getName() + "." +
     * joinPoint.getSignature().getName());
     * }
     */

    // If exception occur, log message in /apache-tomcat-7.0.88/logs/YeseeGov
    @AfterThrowing(value = "execution(* com..*(..))", throwing = "e")
    public void afterThrowingAdvice(JoinPoint joinPoint, Exception e) {
        if (exc != e)
            logger.info(e.getMessage());
        exc = e;
    }
}
