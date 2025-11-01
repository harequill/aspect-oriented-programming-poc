package com.example.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;

@Aspect
public class LoggingAspect {

    // it captures the execution of any public method in service package
    @Pointcut("execution(public * com.example.service.*.*(..))")
    public void methodService() {}

    // before advice: runs before method
    @Before("methodService()")
    public void beforeExecution(JoinPoint joinPoint) {
        System.out.println("<<<ASPECT>>> Before execution: " + joinPoint.getSignature().getName());
        
        Object[] args = joinPoint.getArgs();
        
        if (args.length > 0) {
            System.out.print("<<<ASPECT>>> Args: ");

            for (int i = 0; i < args.length; i++) {
                System.out.print(args[i]);

                if (i < args.length - 1) {
                    System.out.print(", ");
                }
            }

            System.out.println();
        }
    }


    // after return advice: runs after method's succesfull return
    @AfterReturning(pointcut = "methodService()", returning = "result")
    public void afterSuccess(JoinPoint joinPoint, Object result) {
        System.out.println("<<<ASPECT>>> Method returned succesfully.");
        System.out.println("<<<ASPECT>>> Return: " + result);
    }

    // after throwing advice: runs when there is an exception
    @AfterThrowing(pointcut = "methodService()", throwing = "exception")
    public void afterException(JoinPoint joinPoint, Exception exception){
        System.out.println("<<<ASPECT>>> Exception caught: " + exception.getMessage());
    }
}
