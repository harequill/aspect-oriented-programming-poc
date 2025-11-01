# Aspect-Oriented Programming (AOP) - Proof of Concept

A practical demonstration of Aspect-Oriented Programming using AspectJ, showing how to separate cross-cutting concerns from business logic.

## Table of Contents
- [What is Aspect-Oriented Programming?](#what-is-aspect-oriented-programming)
- [The Problem AOP Solves](#the-problem-aop-solves)
- [Project Overview](#project-overview)
- [Core AOP Concepts](#core-aop-concepts)
- [Code Walkthrough](#code-walkthrough)
- [How It Works](#how-it-works)
- [Output Example](#output-example)
- [Key Takeaways](#key-takeaways)
- [Running the Project](#running-the-project)

## What is Aspect-Oriented Programming?

Aspect-Oriented Programming (AOP) is a programming paradigm that allows you to separate **cross-cutting concerns** from your business logic. Cross-cutting concerns are functionalities that affect multiple parts of your application, such as:

- Logging
- Security
- Transaction management
- Error handling
- Performance monitoring
- Caching

Instead of scattering this code throughout your application, AOP lets you modularize these concerns into separate units called **aspects**.

## The Problem AOP Solves

Imagine you want to log every method call in your application. Without AOP, you'd have to do this:

```java
public class CalculatorService {
    public int sum(int a, int b) {
        System.out.println("Before execution: sum");
        System.out.println("Args: " + a + ", " + b);
        
        int result = a + b;
        
        System.out.println("Method returned successfully.");
        System.out.println("Return: " + result);
        
        return result;
    }

    public int multiply(int a, int b) {
        System.out.println("Before execution: multiply");
        System.out.println("Args: " + a + ", " + b);
        
        int result = a * b;
        
        System.out.println("Method returned successfully.");
        System.out.println("Return: " + result);
        
        return result;
    }
    
    // ... and so on for every method
}
```

**Problems:**
- Code duplication: logging code repeated in every method
- Mixed concerns: business logic buried in logging code
- Hard to maintain: changing logging requires modifying every method
- Error-prone: easy to forget logging in new methods

**With AOP**, your service class stays clean:

```java
public class CalculatorService {
    public int sum(int a, int b) {
        return a + b;
    }

    public int multiply(int a, int b) {
        return a * b;
    }
}
```

And logging is handled automatically by an aspect.

## Project Overview

This project demonstrates AOP with a simple calculator service that performs basic arithmetic operations. The logging functionality is completely separated from the business logic using AspectJ.

### Project Structure

```
src/main/
├── java/com/example/
│   ├── Main.java                          # Entry point
│   ├── service/
│   │   └── CalculatorService.java         # Business logic 
│   └── aspect/
│       └── LoggingAspect.java             # Cross-cutting concern
└── resources/META-INF/
    └── aop.xml                             # AspectJ configuration
```

## Core AOP Concepts

This project demonstrates the following AOP concepts:

### 1. **Aspect**
A module that encapsulates a cross-cutting concern.

```java
@Aspect
public class LoggingAspect {
    // This class is an aspect
}
```

### 2. **Join Point**
A point in your program's execution where an aspect can be applied (e.g., method execution, field access).

In this project: Every method execution in `CalculatorService` is a join point.

### 3. **Pointcut**
An expression that selects which join points to apply advice to.

```java
@Pointcut("execution(public * com.example.service.*.*(..))")
public void methodService() {}
```

This pointcut matches:
- `public` - any public method
- `*` - with any return type
- `com.example.service.*` - in any class within the service package
- `.*(..)` - any method name with any parameters

### 4. **Advice**
The action taken at a join point. This project uses three types:

#### **@Before** - Runs before the method executes
```java
@Before("methodService()")
public void beforeExecution(JoinPoint joinPoint) {
    System.out.println("<<<ASPECT>>> Before execution: " + joinPoint.getSignature().getName());
}
```

#### **@AfterReturning** - Runs after the method completes successfully
```java
@AfterReturning(pointcut = "methodService()", returning = "result")
public void afterSuccess(JoinPoint joinPoint, Object result) {
    System.out.println("<<<ASPECT>>> Method returned succesfully.");
    System.out.println("<<<ASPECT>>> Return: " + result);
}
```

#### **@AfterThrowing** - Runs when the method throws an exception
```java
@AfterThrowing(pointcut = "methodService()", throwing = "exception")
public void afterException(JoinPoint joinPoint, Exception exception) {
    System.out.println("<<<ASPECT>>> Exception caught: " + exception.getMessage());
}
```

### 5. **Weaving**
The process of applying aspects to your code. Configured in `aop.xml`:

```xml
<aspectj>
    <aspects>
        <aspect name="com.example.aspect.LoggingAspect"/>
    </aspects>
    <weaver options="-verbose -showWeaveInfo">
        <include within="com.example..*"/>
    </weaver>
</aspectj>
```

## Code Walkthrough

### 1. The Business Logic (CalculatorService.java)

```java
package com.example.service;

public class CalculatorService {
    public int sum(int a, int b) {
        return a + b;
    }

    public int multiply(int a, int b){
        return a * b;
    }

    public int divide(int a, int b) {
        if( b == 0 ) {
            throw new IllegalArgumentException("Can't divide by 0");
        }
        return (int)(a/b);
    }
}
```

**Notice:** This class contains ONLY business logic. No logging, no error handling output, just pure calculation logic. All of this is handled by Aspect

### 2. The Aspect (LoggingAspect.java)

```java
package com.example.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;

@Aspect
public class LoggingAspect {

    // Define which methods to intercept
    @Pointcut("execution(public * com.example.service.*.*(..))")
    public void methodService() {}

    // Log before method execution
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

    // Log after successful execution
    @AfterReturning(pointcut = "methodService()", returning = "result")
    public void afterSuccess(JoinPoint joinPoint, Object result) {
        System.out.println("<<<ASPECT>>> Method returned succesfully.");
        System.out.println("<<<ASPECT>>> Return: " + result);
    }

    // Log when an exception occurs
    @AfterThrowing(pointcut = "methodService()", throwing = "exception")
    public void afterException(JoinPoint joinPoint, Exception exception){
        System.out.println("<<<ASPECT>>> Exception caught: " + exception.getMessage());
    }
}
```

**Key Points:**
- `JoinPoint` parameter gives access to method information (name, arguments, etc.)
- The aspect automatically intercepts all methods matching the pointcut
- No changes needed in `CalculatorService` to enable logging

### 3. The Main Application

```java
package com.example;

import com.example.service.CalculatorService;

public class Main {
    public static void main(String[] args) {
        System.out.println("Demo - Aspects\n");

        CalculatorService calc = new CalculatorService();

        // Aspect will log it automatically
        int sum = calc.sum(5, 3);
        System.out.println("Result: " + sum);

        int mult = calc.multiply(4, 7);
        System.out.println("Result: " + mult);

        int divi = calc.divide(4, 2);
        System.out.println("result: " + divi);

        try {
            calc.divide(2, 0);
        } catch (Exception err) {
            System.out.println("Exception: " + err.getMessage());
        }
    }
}
```

## How It Works

Here's the execution flow when you call `calc.sum(5, 3)`:

```
1. Method call: calc.sum(5, 3)
   |
   v
2. @Before advice executes
   => Logs: "<<<ASPECT>>> Before execution: sum"
   => Logs: "<<<ASPECT>>> Args: 5, 3"
   |
   v
3. Actual method executes
   => Calculates: 5 + 3 = 8
   |
   v
4. @AfterReturning advice executes
   => Logs: "<<<ASPECT>>> Method returned succesfully."
   => Logs: "<<<ASPECT>>> Return: 8"
   |
   v
5. Control returns to Main
   => Prints: "Result: 8"
```

### Exception Flow

When calling `calc.divide(2, 0)`:

```
1. Method call: calc.divide(2, 0)
   |
   v
2. @Before advice executes
   => Logs method name and arguments
   |
   v
3. Actual method executes
   => Throws IllegalArgumentException
   |
   v
4. @AfterThrowing advice executes (instead of @AfterReturning)
   => Logs: "<<<ASPECT>>> Exception caught: Can't divide by 0"
   |
   v
5. Exception propagates to Main
   => Caught in try-catch block
```

## Output Example

When you run the program, you'll see:

```
Demo - Aspects

<<<ASPECT>>> Before execution: sum
<<<ASPECT>>> Args: 5, 3
<<<ASPECT>>> Method returned succesfully.
<<<ASPECT>>> Return: 8
Result: 8

<<<ASPECT>>> Before execution: multiply
<<<ASPECT>>> Args: 4, 7
<<<ASPECT>>> Method returned succesfully.
<<<ASPECT>>> Return: 28
Result: 28

<<<ASPECT>>> Before execution: divide
<<<ASPECT>>> Args: 4, 2
<<<ASPECT>>> Method returned succesfully.
<<<ASPECT>>> Return: 2
result: 2

<<<ASPECT>>> Before execution: divide
<<<ASPECT>>> Args: 2, 0
<<<ASPECT>>> Exception caught: Can't divide by 0
Exception: Can't divide by 0
```

**Notice:**
- Every method call is automatically logged
- Arguments and return values are captured
- Exceptions are intercepted and logged
- The `CalculatorService` class has no logging code.

## Key Takeaways

### Benefits Demonstrated

1. **Separation of Concerns**
   - Business logic in `CalculatorService` is clean and focused
   - Logging logic is isolated in `LoggingAspect`

2. **Code Reusability**
   - One aspect handles logging for ALL service methods
   - Logging works automatically for any new methods.

3. **Maintainability**
   - To change logging format: Modify only `LoggingAspect`
   - To disable logging: Remove the aspect configuration

4. **Non-Invasive**
   - `CalculatorService` has no knowledge of logging
   - No dependencies on logging frameworks in business code

### Real-World Applications

This same pattern can be applied to:

- **Security**: Check user permissions before method execution
- **Transactions**: Begin/commit/rollback database transactions
- **Caching**: Cache method results to improve performance
- **Performance Monitoring**: Measure execution time
- **Audit Logging**: Track who called what and when
- **Error Handling**: Centralized exception handling and recovery

### When to Use AOP

**Good Use Cases:**
- Logging and monitoring
- Security and authentication
- Transaction management
- Caching strategies
- Performance metrics

**Not Recommended:**
- Core business logic
- One-off operations
- Simple helper methods
- Complex control flow

## Running the Project

### Prerequisites
- Java JDK 8 or higher
- AspectJ runtime library
- Maven or Gradle (for dependency management)

### Compile and Run

```bash
# Compile with AspectJ compiler
ajc -d bin src/com/example/**/*.java

# Run with AspectJ weaver
java -javaagent:path/to/aspectjweaver.jar com.example.Main
```

Or use load-time weaving with the provided `aop.xml`:

```bash
java -javaagent:aspectjweaver.jar \
     -Daj.weaving.loadtime.configuration=file:META-INF/aop.xml \
     com.example.Main
```

Easy way (Works on my machine, if it doesn't in yours, find your own way. I'm not a Java developer.):

```bash
./gradlew clean run
```

## Conclusion

This project demonstrates how Aspect-Oriented Programming can dramatically improve code organization by separating cross-cutting concerns from business logic. The `CalculatorService` remains clean and focused on calculations, while `LoggingAspect` handles all logging concerns automatically.

**Why using AOP:**
- Write cross-cutting logic once, apply it everywhere
- Keep business logic clean and maintainable
- Easily add, modify, or remove aspects without touching business code
- Create more modular and testable applications

AOP is not a replacement for Object-Oriented Programming. It's a complement that helps you write cleaner, more maintainable code when dealing with concerns that span multiple modules.

## Further Reading

- [AspectJ Documentation](https://www.eclipse.org/aspectj/doc/released/progguide/index.html)

---

**License:** The Unlicense

Feel free to use this project as a learning resource or starting point for your own AOP implementations.
