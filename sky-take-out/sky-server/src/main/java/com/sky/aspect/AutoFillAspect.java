package com.sky.aspect;


import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    //用于实现 数据库实体对象的自动填充功能，即在执行插入（INSERT）或更新（UPDATE）操作时，自动为实体对象中的公共字段赋值。
    /*
    * 切入点
    * */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){}


    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint){
        log.info("开始进行数据填充");
        //获取到当前被拦截的方法上的数据库操作类型
        MethodSignature signature =(MethodSignature) joinPoint.getSignature();//获取到当前被拦截的方法，signature：保存了目标方法的完整定义信息，例如方法名、参数、返回值、注解等
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);//获取到当前方法上的数据库操作类型，方法本身：signature.getMethod()  getAnnotation方法上的注解
        OperationType operationType = autoFill.value();//获取数据库操作类型


        //获取到当前方法的参数--实体对象
        Object[] args = joinPoint.getArgs();//获取方法所有参数，约定employee参数在第一个位置
        if(args == null || args.length == 0){
            return;
        }
        Object entity = args[0];
        //准备赋值数据
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();


        //根据数据库操作类型，为对应的属性赋值
        if(operationType == OperationType.INSERT){
            //为4个公共字段赋值
           try {
               //Method setCreateTime = entity.getClass().getDeclaredMethod("setCreateTime", LocalDateTime.class);换成常量
               Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
               /*
               * entity.getClass()：
                获取当前对象的 Class 类型（即这个对象属于哪个类）。
                例如：Employee、Category 等。
                .getDeclaredMethod("setCreateTime", LocalDateTime.class)：
                获取名为 "setCreateTime" 的方法，并指定参数类型为 LocalDateTime.class。
                这个方法就是实体类中的 setter 方法。
                setCreateTime.invoke(entity, now);：
                调用该方法，等价于执行：
                entity.setCreateTime(now);
               *
               * */

               Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
               Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
               Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
               //通过反射为属性赋值
               /*
               * Java 反射机制 是指在程序运行时（Runtime），可以动态获取类的信息（如类名、方法、属性等），并能调用类的方法或访问/修改类的属性。
               * */
                setCreateTime.invoke(entity,now);
                setUpdateTime.invoke(entity,now);
                setCreateUser.invoke(entity,currentId);
                setUpdateUser.invoke(entity,currentId);

                /*
                *   为什么在这里用反射？
                    在这个例子中，AutoFillAspect.java 是一个 AOP 切面类，它被设计成通用的自动填充逻辑，适用于所有实体类（如 Employee、Category、Setmeal 等）。
                    如果不用反射，就需要为每个实体类单独编写填充逻辑，非常不灵活。
                    而使用反射后，就可以做到：
                    自动识别任意实体类是否有 setCreateTime、setCreateUser 等方法；
                    如果有，就动态调用这些方法进行赋值；
                    实现一次编码，多处复用。
                *
                * */

           } catch (Exception e) {
               e.printStackTrace();
           }
        }else if(operationType == OperationType.UPDATE){
            try {
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                setUpdateTime.invoke(entity,now);
                setUpdateUser.invoke(entity,currentId);

            } catch (Exception e) {
                e.printStackTrace();
            }

    }
    }
}
