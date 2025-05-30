package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.BaseException;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealNotFoundException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    @ExceptionHandler
    public Result sqlExceptionHandler(SQLIntegrityConstraintViolationException ex) {
        // Duplicate entry 'xxx' for key 'employee.idx_username'
        String message = ex.getMessage();
        log.error("SQL异常信息：{}", message);
        if (message.contains("Duplicate entry")){
            String[] split = message.split(" ");
            String username = split[2];
            String msg = username + MessageConstant.ACCOUNT_EXISTS;
            return Result.error(msg);
        }else {
            return Result.error("未知错误");
        }
    }

    @ExceptionHandler
    public Result accountNotFoundExceptionHandler(AccountNotFoundException ex) {
        log.error("业务异常信息：{}", ex.getMessage());
        return Result.error(MessageConstant.ACCOUNT_NOT_FOUND);
    }

    @ExceptionHandler
    public Result fileUploadFailureHandler(FileUploadException ex){
        log.error("文件上传失败: {}", ex.getMessage());
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }

    @ExceptionHandler
    public Result setmealNotFoundExceptionHandler(SetmealNotFoundException ex) {
        log.error("套餐未找到异常信息：{}", ex.getMessage());
        return Result.error(MessageConstant.SETMEAL_NOTFOUND_FAILED);
    }

    @ExceptionHandler
    public Result deletionNotAllowedExceptionHandler(DeletionNotAllowedException ex) {
        log.error("删除失败: {}", ex.getMessage());
        return Result.error(ex.getMessage());
    }
}
