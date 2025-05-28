package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author tz95
 * @project sky-take-out
 * @date 2025/5/28
 * * 自动填充注解
 * 用于标记需要自动填充的字段
 * 例如：创建时间、更新时间等
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {

    // 数据库操作类型: UPDATE 或 INSERT
    OperationType value();

}
