package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author tz95
 * @project sky-take-out
 * @date 2025/5/28
 * * 自动填充切面类
 * 用于处理自动填充注解
 * 例如：创建时间、更新时间等
 * * 该类将会在方法执行前后进行拦截
 * 并根据注解的类型进行相应的处理
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    /**
     * 切入点
     * 拦截标记了 @AutoFill 注解的方法
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){}

    @Before("com.sky.aspect.AutoFillAspect.autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint){
        log.info("开始公共字段自动填充...");

        // 获取当前被拦截方法上的数据库操作类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature(); // 获取方法签名对象
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class); // 获取方法上的 AutoFill 注解
        OperationType value = autoFill.value(); // 获取注解中的操作类型

        // 获取当前被拦截方法的参数-实体对象
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0){
            return;
        }
        Object arg = args[0];

        // 准备赋值的数据
        LocalDateTime datetime = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        // 根据操作类型，通过反射为对应属性赋值
        if (OperationType.INSERT.equals(value)) {
            // 为4个公共属性赋值
            try {
                Method setCreateTime = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setUpdateTime = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setCreateUser = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateUser = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                // 通过反射调用方法为属性赋值
                setCreateTime.invoke(arg,datetime);
                setUpdateTime.invoke(arg,datetime);
                setCreateUser.invoke(arg,currentId);
                setUpdateUser.invoke(arg,currentId);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        } else if (OperationType.UPDATE.equals(value)) {
            // 为2个公共属性赋值
            Method setUpdateTime = null;
            try {
                setUpdateTime = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                // 通过反射调用方法为属性赋值
                setUpdateTime.invoke(arg,datetime);
                setUpdateUser.invoke(arg,currentId);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }

        }
    }

}
