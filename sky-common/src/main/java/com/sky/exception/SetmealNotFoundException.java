package com.sky.exception;

/**
 * @author tz95
 * @project sky-take-out
 * @date 2025/5/30
 * * 套餐未找到异常
 */
public class SetmealNotFoundException extends BaseException {

    public SetmealNotFoundException() {}

    public SetmealNotFoundException(String message) {
        super(message);
    }
}
