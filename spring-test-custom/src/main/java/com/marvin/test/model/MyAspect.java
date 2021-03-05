package com.marvin.test.model;

//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.springframework.stereotype.Component;

/**
 * @author Marvin
 */
//@Aspect
//@Component
public class MyAspect {

    private Long time;

//    @Pointcut(value = "execution(* com.marvin.test.model.StudentService.*(..))")
//    public void pointCut(){
//
//    }

//    @Around(value = "pointCut()")
//    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
//        long time = System.currentTimeMillis();
//        Object proceed = joinPoint.proceed();
//        time = System.currentTimeMillis()-time;
//        System.out.println("耗时："+time);
//        return proceed;
//    }

/**    @Before(value ="pointCut()")*/
    public void before(){
        this.time = System.currentTimeMillis();
    }

/**    @Before(value ="pointCut()")*/
    public void after(){
        if (time!=null){
            this.time = System.currentTimeMillis()-this.time;
            System.out.println("耗时："+time);
        }
    }
}
